package com.student.scheduler.ui.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.student.scheduler.R
import com.student.scheduler.model.StatisticsUi
import com.student.scheduler.viewmodel.StatisticsViewModel

/** "Статистика" — completion rate (pie) and per-subject progress (bar), from Room via the ViewModel. */
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textTotal = view.findViewById<TextView>(R.id.text_total)
        val textDone = view.findViewById<TextView>(R.id.text_done)
        val textOverdue = view.findViewById<TextView>(R.id.text_overdue)
        val chartCompletion = view.findViewById<PieChart>(R.id.chart_completion)
        val chartBySubject = view.findViewById<BarChart>(R.id.chart_by_subject)
        val textNoData = view.findViewById<TextView>(R.id.text_no_data)

        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            textTotal.text = stats.totalTasks.toString()
            textDone.text = stats.doneTasks.toString()
            textOverdue.text = stats.overdueTasks.toString()
            bindCompletionPieChart(chartCompletion, stats)
            bindSubjectBarChart(chartBySubject, textNoData, stats)
        }

        viewModel.load()
    }

    private fun bindCompletionPieChart(chart: PieChart, stats: StatisticsUi) {
        val done = stats.doneTasks
        val pending = stats.totalTasks - stats.doneTasks
        val entries = listOfNotNull(
            if (done > 0) PieEntry(done.toFloat(), "Выполнено") else null,
            if (pending > 0) PieEntry(pending.toFloat(), "В работе") else null
        ).ifEmpty { listOf(PieEntry(1f, "Нет данных")) }

        val colors = listOf(
            requireContext().getColor(R.color.accent_green),
            requireContext().getColor(R.color.accent_coral)
        )

        val dataSet = PieDataSet(entries, "").apply {
            setColors(if (entries.size == 1) listOf(Color.LTGRAY) else colors)
            valueTextSize = 13f
            valueTextColor = Color.WHITE
        }

        chart.data = PieData(dataSet)
        chart.description.isEnabled = false

        // Подписи секторов дублируют легенду и налезают на значения — отключаем.
        chart.setDrawEntryLabels(false)

        // Больше «дырка» и без полупрозрачного кольца, чтобы центральный текст помещался.
        chart.setHoleRadius(68f)
        chart.setTransparentCircleRadius(0f)
        chart.setHoleColor(Color.TRANSPARENT)
        chart.centerText = "${stats.completionPercent}%\nвыполнено"
        chart.setCenterTextSize(13f)
        chart.setUsePercentValues(false)

        chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        chart.setExtraOffsets(8f, 8f, 8f, 8f)
        chart.invalidate()
    }

    private fun bindSubjectBarChart(chart: BarChart, textNoData: TextView, stats: StatisticsUi) {
        if (stats.bySubject.isEmpty()) {
            chart.visibility = View.GONE
            textNoData.visibility = View.VISIBLE
            return
        }
        chart.visibility = View.VISIBLE
        textNoData.visibility = View.GONE

        val entries = stats.bySubject.mapIndexed { index, subject -> BarEntry(index.toFloat(), subject.percent.toFloat()) }
        val colors = stats.bySubject.map { requireContext().getColor(it.accentColor) }
        // Длинные названия обрезаем «…», чтобы подписи не слипались и не вылезали за диаграмму.
        val maxChars = when {
            stats.bySubject.size <= 3 -> 11
            stats.bySubject.size == 4 -> 9
            else -> 7
        }
        val labels = stats.bySubject.map { name ->
            val title = name.subjectName
            if (title.length > maxChars) title.take(maxChars - 1).trimEnd() + "…" else title
        }

        val dataSet = BarDataSet(entries, "Успеваемость, %").apply {
            setColors(colors)
            valueTextSize = 11f
        }

        chart.data = BarData(dataSet).apply { barWidth = 0.55f }
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.axisLeft.axisMinimum = 0f
        chart.axisLeft.axisMaximum = 100f

        // Подписи предметов — снизу, горизонтально (повёрнутый текст обрезался под осью).
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(labels)
            granularity = 1f
            setLabelCount(labels.size, false)
            labelRotationAngle = 0f
            textSize = 11f
            setDrawGridLines(false)
        }
        chart.setExtraBottomOffset(8f)

        // Место сверху под значение над столбцом «100», без зума пальцами.
        chart.setExtraTopOffset(20f)
        chart.setDrawValueAboveBar(true)
        chart.setScaleEnabled(false)
        chart.invalidate()
    }
}