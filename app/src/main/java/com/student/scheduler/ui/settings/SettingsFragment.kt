package com.student.scheduler.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.student.scheduler.R
import com.student.scheduler.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

/** "Настройки" — theme, notification toggles and default task sorting, all persisted via SharedPreferences. */
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchRemindLessons = view.findViewById<Switch>(R.id.switch_remind_lessons)
        val switchRemindTasks = view.findViewById<Switch>(R.id.switch_remind_tasks)
        val switchSound = view.findViewById<Switch>(R.id.switch_sound)
        val switchDarkTheme = view.findViewById<Switch>(R.id.switch_dark_theme)
        val spinnerSort = view.findViewById<Spinner>(R.id.spinner_default_sort)
        val rowExport = view.findViewById<LinearLayout>(R.id.row_export)
        val rowClearData = view.findViewById<LinearLayout>(R.id.row_clear_data)

        val prefs = viewModel.preferences

        switchRemindLessons.isChecked = prefs.remindLessons
        switchRemindTasks.isChecked = prefs.remindTasks
        switchSound.isChecked = prefs.notificationSound
        switchDarkTheme.isChecked = prefs.darkTheme

        val sortOptions = listOf("По сроку", "По названию", "По приоритету")
        spinnerSort.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sortOptions)
        spinnerSort.setSelection(prefs.defaultSortMode)

        switchRemindLessons.setOnCheckedChangeListener { _, isChecked -> prefs.remindLessons = isChecked }
        switchRemindTasks.setOnCheckedChangeListener { _, isChecked -> prefs.remindTasks = isChecked }
        switchSound.setOnCheckedChangeListener { _, isChecked -> prefs.notificationSound = isChecked }

        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            prefs.darkTheme = isChecked
            // Triggers an Activity recreate through AppCompat, applying the new theme immediately.
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        spinnerSort.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, v: View?, position: Int, id: Long) {
                prefs.defaultSortMode = position
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }

        rowExport.setOnClickListener { exportSchedule() }
        rowClearData.setOnClickListener { confirmClearData() }
    }

    private fun exportSchedule() {
        viewLifecycleOwner.lifecycleScope.launch {
            val text = viewModel.buildScheduleExportText()
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Расписание")
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(intent, "Экспорт расписания"))
        }
    }

    private fun confirmClearData() {
        AlertDialog.Builder(requireContext())
            .setTitle("Очистить данные?")
            .setMessage("Все задания будут удалены без возможности восстановления. Расписание пар останется без изменений.")
            .setPositiveButton("Очистить") { _, _ ->
                viewModel.clearTasks {
                    Toast.makeText(requireContext(), "Задания удалены", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
