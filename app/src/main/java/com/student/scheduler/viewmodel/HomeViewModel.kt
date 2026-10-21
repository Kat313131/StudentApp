package com.student.scheduler.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.student.scheduler.R
import com.student.scheduler.model.LessonUi
import com.student.scheduler.model.TaskUi
import com.student.scheduler.model.WeekDay

class HomeViewModel : ViewModel() {

    private val _weekDays = MutableLiveData<List<WeekDay>>()
    val weekDays: LiveData<List<WeekDay>> = _weekDays

    private val _todayLessons = MutableLiveData<List<LessonUi>>()
    val todayLessons: LiveData<List<LessonUi>> = _todayLessons

    private val _lessonsHeader = MutableLiveData<String>()
    val lessonsHeader: LiveData<String> = _lessonsHeader

    private val _todayTasks = MutableLiveData<List<TaskUi>>()
    val todayTasks: LiveData<List<TaskUi>> = _todayTasks

    private var selectedDayIndex = 2
    private val actualTodayIndex = 2

    private val fullDayNames = listOf(
        "Понедельник", "Вторник", "Среду", "Четверг", "Пятницу", "Субботу"
    )

    private val lessonsByDay: Map<Int, List<LessonUi>> = mapOf(
        0 to listOf(
            LessonUi("Физика (Практика)", "08:30–10:00", "Ауд. 110", "Проф. Петрова Е.В.", R.color.accent_coral),
            LessonUi("Иностранный язык", "10:15–11:45", "Ауд. 202", "Доц. Кузнецова Л.И.", R.color.accent_green)
        ),
        1 to listOf(
            LessonUi("Математический анализ", "09:00–10:30", "Ауд. 305", "Проф. Иванов А.М.", R.color.accent_blue),
            LessonUi("Программирование", "10:45–12:15", "Ауд. 408", "Доц. Сидоров К.П.", R.color.accent_green)
        ),
        2 to listOf(
            LessonUi("Математический анализ", "09:00–10:30", "Ауд. 305", "Проф. Иванов А.М.", R.color.accent_blue),
            LessonUi("Физика (Лекция)", "10:45–12:15", "Ауд. 112", "Проф. Петрова Е.В.", R.color.accent_coral),
            LessonUi("Программирование", "13:00–14:30", "Ауд. 408", "Доц. Сидоров К.П.", R.color.accent_green)
        ),
        3 to listOf(
            LessonUi("Иностранный язык", "09:00–10:30", "Ауд. 202", "Доц. Кузнецова Л.И.", R.color.accent_green),
            LessonUi("Математический анализ", "10:45–12:15", "Ауд. 305", "Проф. Иванов А.М.", R.color.accent_blue)
        ),
        4 to listOf(
            LessonUi("Программирование", "09:00–10:30", "Ауд. 408", "Доц. Сидоров К.П.", R.color.accent_green),
            LessonUi("Физика (Практика)", "10:45–12:15", "Ауд. 110", "Проф. Петрова Е.В.", R.color.accent_coral)
        ),
        5 to emptyList()
    )

    init {
        loadMockWeekDays()
        loadMockTasks()
        showLessonsForSelectedDay()
    }

    private fun loadMockWeekDays() {
        val names = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб")
        val numbers = listOf(11, 12, 13, 14, 15, 16)
        _weekDays.value = names.indices.map { index ->
            WeekDay(names[index], numbers[index], isSelected = index == selectedDayIndex)
        }
    }

    private fun loadMockTasks() {
        _todayTasks.value = listOf(
            TaskUi(
                title = "Лабораторная работа №3",
                dueLabel = "Срок: Сегодня, 18:00",
                tagName = "Физика",
                tagColor = R.color.accent_coral
            ),
            TaskUi(
                title = "Домашнее задание №5",
                dueLabel = "Срок: До завтра",
                tagName = "Мат. анализ",
                tagColor = R.color.accent_blue
            )
        )
    }

    private fun showLessonsForSelectedDay() {
        _todayLessons.value = lessonsByDay[selectedDayIndex].orEmpty()
        _lessonsHeader.value = if (selectedDayIndex == actualTodayIndex) {
            "Пары сегодня"
        } else {
            "Пары — ${fullDayNames[selectedDayIndex]}"
        }
    }

    fun selectDay(position: Int) {
        if (position == selectedDayIndex) return
        selectedDayIndex = position
        loadMockWeekDays()
        showLessonsForSelectedDay()
    }

    fun onTaskCheckedChanged(position: Int, isChecked: Boolean) {
        val current = _todayTasks.value.orEmpty().toMutableList()
        if (position !in current.indices) return
        current[position] = current[position].copy(isDone = isChecked)
        _todayTasks.value = current
    }
}