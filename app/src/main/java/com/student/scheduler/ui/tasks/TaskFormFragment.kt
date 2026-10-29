package com.student.scheduler.ui.tasks

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.student.scheduler.R
import com.student.scheduler.data.database.SubjectEntity
import com.student.scheduler.data.database.TagEntity
import com.student.scheduler.data.database.TaskEntity
import com.student.scheduler.utils.ColorPalette
import com.student.scheduler.viewmodel.TaskFormViewModel
import java.util.Calendar

/**
 * Add / edit form for a task. `taskId` == -1 means "create new"; any other
 * value loads that task from Room and pre-fills the form for editing.
 */
class TaskFormFragment : Fragment(R.layout.fragment_task_form) {

    private val viewModel: TaskFormViewModel by viewModels()
    private var subjects: List<SubjectEntity> = emptyList()
    private var allTags: List<TagEntity> = emptyList()
    private var selectedTagIds: MutableSet<Long> = mutableSetOf()
    private var dueCalendar: Calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
    private var editingTaskId: Long = -1
    private var editingIsDone: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editingTaskId = arguments?.getLong("taskId", -1L) ?: -1L

        val textFormTitle = view.findViewById<TextView>(R.id.text_form_title)
        val layoutTitle = view.findViewById<TextInputLayout>(R.id.layout_title)
        val inputTitle = view.findViewById<TextInputEditText>(R.id.input_title)
        val inputDescription = view.findViewById<TextInputEditText>(R.id.input_description)
        val spinnerSubject = view.findViewById<Spinner>(R.id.spinner_subject)
        val chipGroupTags = view.findViewById<ChipGroup>(R.id.chip_group_tags)
        val buttonPickDate = view.findViewById<Button>(R.id.button_pick_date)
        val buttonPickTime = view.findViewById<Button>(R.id.button_pick_time)
        val textDuePreview = view.findViewById<TextView>(R.id.text_due_preview)
        val buttonSave = view.findViewById<Button>(R.id.button_save)
        val buttonCancel = view.findViewById<Button>(R.id.button_cancel)

        textFormTitle.text = if (editingTaskId > 0) "Редактировать задание" else "Новое задание"
        updateDuePreview(textDuePreview)

        viewModel.subjects.observe(viewLifecycleOwner) { list ->
            subjects = list
            val names = listOf("Без предмета") + list.map { it.name }
            spinnerSubject.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
        }
        viewModel.loadSubjects()

        viewModel.tags.observe(viewLifecycleOwner) { list ->
            allTags = list
            renderTagChips(chipGroupTags)
        }
        viewModel.loadTags()

        if (editingTaskId > 0) {
            viewModel.loadTask(editingTaskId)
            viewModel.loadedTagIds.observe(viewLifecycleOwner) { ids ->
                selectedTagIds = ids.toMutableSet()
                renderTagChips(chipGroupTags)
            }
            viewModel.loadedTask.observe(viewLifecycleOwner) { task ->
                if (task == null) return@observe
                inputTitle.setText(task.title)
                inputDescription.setText(task.description)
                editingIsDone = task.isDone
                dueCalendar.timeInMillis = task.dueDateMillis
                updateDuePreview(textDuePreview)
                val subjectPosition = subjects.indexOfFirst { it.id == task.subjectId }
                spinnerSubject.setSelection(if (subjectPosition >= 0) subjectPosition + 1 else 0)
            }
        }

        buttonPickDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    dueCalendar.set(year, month, day)
                    updateDuePreview(textDuePreview)
                },
                dueCalendar.get(Calendar.YEAR), dueCalendar.get(Calendar.MONTH), dueCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        buttonPickTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    dueCalendar.set(Calendar.HOUR_OF_DAY, hour)
                    dueCalendar.set(Calendar.MINUTE, minute)
                    updateDuePreview(textDuePreview)
                },
                dueCalendar.get(Calendar.HOUR_OF_DAY), dueCalendar.get(Calendar.MINUTE), true
            ).show()
        }

        buttonCancel.setOnClickListener { findNavController().popBackStack() }

        buttonSave.setOnClickListener {
            val title = inputTitle.text?.toString().orEmpty().trim()
            if (title.isEmpty()) {
                layoutTitle.error = "Укажите название задания"
                return@setOnClickListener
            }
            layoutTitle.error = null

            val subjectPosition = spinnerSubject.selectedItemPosition
            val subjectId = if (subjectPosition == 0) null else subjects.getOrNull(subjectPosition - 1)?.id

            val task = TaskEntity(
                id = if (editingTaskId > 0) editingTaskId else 0,
                subjectId = subjectId,
                title = title,
                description = inputDescription.text?.toString().orEmpty().trim(),
                dueDateMillis = dueCalendar.timeInMillis,
                isDone = editingIsDone
            )
            viewModel.save(task, selectedTagIds.toList())
            findNavController().popBackStack()
        }
    }

    /** Redraws the tag ChipGroup: one filter chip per existing tag, plus a "+" chip to create a new one. */
    private fun renderTagChips(chipGroup: ChipGroup) {
        chipGroup.removeAllViews()
        allTags.forEach { tag ->
            val chip = Chip(requireContext()).apply {
                text = tag.name
                isCheckable = true
                isChecked = selectedTagIds.contains(tag.id)
                chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    requireContext().getColor(ColorPalette.colorRes(tag.colorIndex))
                )
                setTextColor(requireContext().getColor(android.R.color.white))
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) selectedTagIds.add(tag.id) else selectedTagIds.remove(tag.id)
                }
            }
            chipGroup.addView(chip)
        }

        val addChip = Chip(requireContext()).apply {
            text = "+ Новый тег"
            isCheckable = false
            setOnClickListener { promptNewTag() }
        }
        chipGroup.addView(addChip)
    }

    /** Small inline prompt for creating a tag without leaving the form — keeps the many-to-many UI lightweight. */
    private fun promptNewTag() {
        val input = TextInputEditText(requireContext())
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Новый тег")
            .setView(input)
            .setPositiveButton("Добавить") { _, _ ->
                val name = input.text?.toString()?.trim().orEmpty()
                if (name.isNotEmpty()) {
                    viewModel.createTag(name, colorIndex = allTags.size) { newId ->
                        selectedTagIds.add(newId)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun updateDuePreview(textView: TextView) {
        val format = java.text.SimpleDateFormat("d MMMM, HH:mm", java.util.Locale("ru"))
        textView.text = format.format(dueCalendar.time)
    }
}
