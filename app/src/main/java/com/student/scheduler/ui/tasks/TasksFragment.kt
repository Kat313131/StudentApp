package com.student.scheduler.ui.tasks

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.student.scheduler.R
import com.student.scheduler.adapters.TaskAdapter
import com.student.scheduler.data.database.TaskEntity
import com.student.scheduler.model.TaskUi
import com.student.scheduler.viewmodel.TasksViewModel

/** "Задания" — full task list with CRUD: add (FAB), edit (tap), delete (long-press, with confirmation). */
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()
    private var currentTasks: List<TaskUi> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_tasks)
        val textEmpty = view.findViewById<TextView>(R.id.text_empty)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_task)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            currentTasks = tasks
            recycler.adapter = TaskAdapter(
                tasks = tasks,
                onCheckedChange = { taskId, isChecked -> viewModel.onTaskCheckedChanged(taskId, isChecked) },
                onItemClick = { taskId -> openDetail(taskId) },
                onItemLongClick = { taskId -> confirmDelete(taskId) }
            )
            val hasTasks = tasks.isNotEmpty()
            recycler.visibility = if (hasTasks) View.VISIBLE else View.GONE
            textEmpty.visibility = if (hasTasks) View.GONE else View.VISIBLE
        }

        fabAdd.setOnClickListener { openForm(taskId = -1) }
    }

    private fun openDetail(taskId: Long) {
        findNavController().navigate(
            R.id.action_tasksFragment_to_taskDetailFragment,
            bundleOf("taskId" to taskId)
        )
    }

    private fun openForm(taskId: Long) {
        findNavController().navigate(
            R.id.action_tasksFragment_to_taskFormFragment,
            bundleOf("taskId" to taskId)
        )
    }

    /** Required by the assignment: deletion always asks for confirmation first. */
    private fun confirmDelete(taskId: Long) {
        val task = currentTasks.firstOrNull { it.id == taskId } ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Удалить задание?")
            .setMessage("«${task.title}» будет удалено без возможности восстановления.")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteTask(
                    TaskEntity(
                        id = task.id,
                        subjectId = task.subjectId,
                        title = task.title,
                        description = task.description,
                        dueDateMillis = task.dueDateMillis,
                        isDone = task.isDone
                    )
                )
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
