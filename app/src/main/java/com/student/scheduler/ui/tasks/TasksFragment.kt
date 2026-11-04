package com.student.scheduler.ui.tasks

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.student.scheduler.R
import com.student.scheduler.adapters.TaskAdapter
import com.student.scheduler.data.database.SubjectEntity
import com.student.scheduler.data.database.TaskEntity
import com.student.scheduler.model.TaskUi
import com.student.scheduler.viewmodel.TasksViewModel

/**
 * "Задания" — full task list: CRUD (add via FAB, view/edit/delete via item),
 * plus search, subject filter, "only pending" filter and sorting (week 8).
 */
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()
    private var currentTasks: List<TaskUi> = emptyList()
    private var subjects: List<SubjectEntity> = emptyList()
    private val sortLabels = listOf("Сортировка: по сроку", "Сортировка: по названию", "Сортировка: по приоритету")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_tasks)
        val textEmpty = view.findViewById<TextView>(R.id.text_empty)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_task)
        val inputSearch = view.findViewById<EditText>(R.id.input_search)
        val spinnerSubjectFilter = view.findViewById<Spinner>(R.id.spinner_subject_filter)
        val chipOnlyPending = view.findViewById<Chip>(R.id.chip_only_pending)
        val buttonSort = view.findViewById<Button>(R.id.button_sort)
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

        viewModel.subjects.observe(viewLifecycleOwner) { list ->
            subjects = list
            val names = listOf("Все предметы") + list.map { it.name }
            spinnerSubjectFilter.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
            spinnerSubjectFilter.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, v: View?, position: Int, id: Long) {
                    viewModel.setSubjectFilter(if (position == 0) null else subjects.getOrNull(position - 1)?.id)
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
            }
        }

        inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s?.toString().orEmpty())
            }
        })

        chipOnlyPending.setOnCheckedChangeListener { _, isChecked -> viewModel.setOnlyPending(isChecked) }

        var sortMode = 0
        buttonSort.text = sortLabels[sortMode]
        buttonSort.setOnClickListener {
            PopupMenu(requireContext(), buttonSort).apply {
                sortLabels.forEachIndexed { index, label -> menu.add(0, index, index, label) }
                setOnMenuItemClickListener { item ->
                    sortMode = item.itemId
                    buttonSort.text = sortLabels[sortMode]
                    viewModel.setSortMode(sortMode)
                    true
                }
            }.show()
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
