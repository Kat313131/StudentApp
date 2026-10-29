package com.student.scheduler.ui.tasks

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.student.scheduler.R
import com.student.scheduler.viewmodel.TaskDetailViewModel

/** Read-only detail view for one task, with Edit / Delete actions (per the assignment's screen table). */
class TaskDetailFragment : Fragment(R.layout.fragment_task_detail) {

    private val viewModel: TaskDetailViewModel by viewModels()
    private var taskId: Long = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskId = arguments?.getLong("taskId", -1L) ?: -1L
        viewModel.setTaskId(taskId)

        val textTag = view.findViewById<TextView>(R.id.text_tag)
        val textTitle = view.findViewById<TextView>(R.id.text_title)
        val textDue = view.findViewById<TextView>(R.id.text_due)
        val textDescription = view.findViewById<TextView>(R.id.text_description)
        val buttonEdit = view.findViewById<Button>(R.id.button_edit)
        val buttonDelete = view.findViewById<Button>(R.id.button_delete)

        viewModel.task.observe(viewLifecycleOwner) { task ->
            if (task == null) return@observe
            textTag.text = task.tagName
            textTag.background.setTint(requireContext().getColor(task.tagColor))
            textTitle.text = task.title
            textDue.text = task.dueLabel
            textDescription.text = task.description.ifBlank { "Без описания" }
        }

        buttonEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_taskDetailFragment_to_taskFormFragment,
                bundleOf("taskId" to taskId)
            )
        }

        buttonDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Удалить задание?")
                .setMessage("Вы уверены, что хотите удалить это задание?")
                .setPositiveButton("Удалить") { _, _ ->
                    viewModel.delete { findNavController().popBackStack() }
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
}
