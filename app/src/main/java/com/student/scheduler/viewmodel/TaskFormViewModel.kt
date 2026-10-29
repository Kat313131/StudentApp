package com.student.scheduler.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.student.scheduler.StudentApplication
import com.student.scheduler.data.database.SubjectEntity
import com.student.scheduler.data.database.TagEntity
import com.student.scheduler.data.database.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Backs the add/edit task form. Loads the task being edited (if any) and saves on submit. */
class TaskFormViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as StudentApplication
    private val taskRepository = app.taskRepository
    private val scheduleRepository = app.scheduleRepository

    private val _subjects = MutableLiveData<List<SubjectEntity>>(emptyList())
    val subjects: LiveData<List<SubjectEntity>> = _subjects

    private val _tags = MutableLiveData<List<TagEntity>>(emptyList())
    val tags: LiveData<List<TagEntity>> = _tags

    private val _loadedTask = MutableLiveData<TaskEntity?>(null)
    val loadedTask: LiveData<TaskEntity?> = _loadedTask

    /** Ids of the tags already attached to the task being edited, once loaded. */
    private val _loadedTagIds = MutableLiveData<List<Long>>(emptyList())
    val loadedTagIds: LiveData<List<Long>> = _loadedTagIds

    private val _saved = MutableLiveData<Boolean>(false)
    val saved: LiveData<Boolean> = _saved

    fun loadSubjects() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = scheduleRepository.getSubjects()
            withContext(Dispatchers.Main) { _subjects.value = list }
        }
    }

    fun loadTags() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = taskRepository.getTags()
            withContext(Dispatchers.Main) { _tags.value = list }
        }
    }

    fun loadTask(taskId: Long) {
        if (taskId <= 0) return
        viewModelScope.launch(Dispatchers.IO) {
            val details = taskRepository.getAllTasksOnce().firstOrNull { it.task.id == taskId }
            withContext(Dispatchers.Main) {
                _loadedTask.value = details?.task
                _loadedTagIds.value = details?.tags?.map { it.id }.orEmpty()
            }
        }
    }

    /** Creates a new tag on the fly (used by the "+" chip in the tag picker). */
    fun createTag(name: String, colorIndex: Int, onCreated: (Long) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = taskRepository.createTag(name, colorIndex)
            val updated = taskRepository.getTags()
            withContext(Dispatchers.Main) {
                _tags.value = updated
                onCreated(id)
            }
        }
    }

    fun save(task: TaskEntity, tagIds: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.saveTask(task, tagIds)
            withContext(Dispatchers.Main) { _saved.value = true }
        }
    }
}
