package com.student.scheduler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.student.scheduler.StudentApplication
import com.student.scheduler.data.database.SubjectEntity
import com.student.scheduler.data.database.TaskEntity
import com.student.scheduler.model.TaskUi
import com.student.scheduler.model.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Backs the "Задания" screen: full CRUD plus search / filter / sort
 * (search and filters land in week 8, the underlying query already
 * supports them so this class only exposes the state holders here).
 */
class TasksViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as StudentApplication
    private val taskRepository = app.taskRepository
    private val scheduleRepository = app.scheduleRepository

    private val _searchQuery = MutableLiveData("")
    private val _subjectFilter = MutableLiveData<Long?>(null)
    private val _onlyPending = MutableLiveData(false)
    private val _sortMode = MutableLiveData(0)

    val subjects: LiveData<List<SubjectEntity>> = scheduleRepository.observeSubjects()

    /** Re-runs the Room query whenever search/filter/sort state changes. */
    val tasks: LiveData<List<TaskUi>> = _searchQuery.switchMap { query ->
        _subjectFilter.switchMap { subjectId ->
            _onlyPending.switchMap { onlyPending ->
                taskRepository
                    .observeTasks(query.orEmpty(), subjectId, onlyPending, _sortMode.value ?: 0)
                    .map { list -> list.map { it.toUi() } }
            }
        }
    }

    fun setSearchQuery(query: String) {
        if (query == _searchQuery.value) return
        _searchQuery.value = query
    }

    fun setSubjectFilter(subjectId: Long?) {
        _subjectFilter.value = subjectId
    }

    fun setOnlyPending(onlyPending: Boolean) {
        _onlyPending.value = onlyPending
    }

    fun setSortMode(mode: Int) {
        _sortMode.value = mode
        // Re-trigger the chain by nudging the query LiveData (sort isn't a switchMap source on its own).
        _searchQuery.value = _searchQuery.value
    }

    fun onTaskCheckedChanged(taskId: Long, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.setDone(taskId, isChecked) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) { taskRepository.deleteTask(task) }
    }
}
