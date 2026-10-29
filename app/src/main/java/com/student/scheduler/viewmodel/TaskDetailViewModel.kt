package com.student.scheduler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.student.scheduler.StudentApplication
import com.student.scheduler.data.database.TaskEntity
import com.student.scheduler.model.TaskUi
import com.student.scheduler.model.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Backs the read-only task detail screen: shows one task and lets the user edit/delete it. */
class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = (application as StudentApplication).taskRepository

    private val _taskId = MutableLiveData<Long>()

    val task: LiveData<TaskUi?> = _taskId.switchMap { id ->
        taskRepository.observeTask(id).map { it?.toUi() }
    }

    fun setTaskId(taskId: Long) {
        if (taskId == _taskId.value) return
        _taskId.value = taskId
    }

    fun delete(onDeleted: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = task.value ?: return@launch
            taskRepository.deleteTask(
                TaskEntity(
                    id = current.id,
                    subjectId = current.subjectId,
                    title = current.title,
                    description = current.description,
                    dueDateMillis = current.dueDateMillis,
                    isDone = current.isDone
                )
            )
            kotlinx.coroutines.withContext(Dispatchers.Main) { onDeleted() }
        }
    }
}
