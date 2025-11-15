package com.fridgefairy.android.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fridgefairy.android.R
import com.fridgefairy.android.databinding.ActivityAnalyticsBinding
import com.fridgefairy.android.ui.viewmodels.AnalyticsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch

/**
 * Analytics Dashboard Activity
 * ADDITIONAL FEATURE: Shows money saved, waste reduced, and usage charts
 */
class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private val viewModel: AnalyticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCharts()
        observeAnalytics()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.analytics)
        }
    }

    private fun setupCharts() {
        setupLineChart(binding.usageLineChart)
        setupPieChart(binding.categoryPieChart)
        setupBarChart(binding.savingsBarChart)
    }

    private fun observeAnalytics() {
        lifecycleScope.launch {
            viewModel.analytics.collect { analytics ->
                updateStatCards(analytics)
                updateUsageChart(analytics.dailyUsage)
                updateCategoryChart(analytics.categoryBreakdown)
                updateSavingsChart(analytics.monthlySavings)
            }
        }
    }

    private fun updateStatCards(analytics: com.fridgefairy.android.data.models.Analytics) {
        binding.apply {
            tvMoneySaved.text = getString(R.string.currency_format, analytics.moneySaved)
            tvMoneySavedLabel.text = getString(R.string.money_saved)

            tvWasteReduced.text = getString(R.string.kg_format, analytics.wasteKg)
            tvWasteReducedLabel.text = getString(R.string.waste_reduced)

            tvItemsConsumed.text = analytics.itemsConsumed.toString()
            tvItemsConsumedLabel.text = getString(R.string.items_consumed)

            tvSavingsThisMonth.text = getString(R.string.currency_format, analytics.savingsThisMonth)
            tvSavingsThisMonthLabel.text = getString(R.string.savings_this_month)
        }
    }

    private fun setupLineChart(chart: LineChart) {
        chart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = getColor(R.color.text_secondary)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = getColor(R.color.divider)
                textColor = getColor(R.color.text_secondary)
            }
            axisRight.isEnabled = false

            legend.apply {
                textColor = getColor(R.color.text_primary)
                textSize = 12f
            }

            animateX(1000, Easing.EaseInOutCubic)
        }
    }

    private fun setupPieChart(chart: PieChart) {
        chart.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawHoleEnabled(true)
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = getString(R.string.category_breakdown)
            setCenterTextSize(16f)
            setCenterTextColor(getColor(R.color.text_primary))
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true

            legend.apply {
                textColor = getColor(R.color.text_primary)
                textSize = 12f
            }

            animateY(1000, Easing.EaseInOutQuad)
        }
    }

    private fun setupBarChart(chart: BarChart) {
        chart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = getColor(R.color.text_secondary)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = getColor(R.color.divider)
                textColor = getColor(R.color.text_secondary)
                axisMinimum = 0f
            }
            axisRight.isEnabled = false

            legend.isEnabled = false

            animateY(1000, Easing.EaseInOutCubic)
        }
    }

    private fun updateUsageChart(dailyUsage: List<Float>) {
        val entries = dailyUsage.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        val dataSet = LineDataSet(entries, getString(R.string.usage_chart)).apply {
            color = getColor(R.color.primary)
            setCircleColor(getColor(R.color.primary))
            lineWidth = 2f
            circleRadius = 3f
            setDrawCircleHole(false)
            valueTextSize = 9f
            setDrawFilled(true)
            fillColor = getColor(R.color.primary_light)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.usageLineChart.apply {
            data = LineData(dataSet)
            xAxis.valueFormatter = object : ValueFormatter() {
                private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                override fun getFormattedValue(value: Float): String {
                    return days.getOrNull(value.toInt()) ?: ""
                }
            }
            invalidate()
        }
    }

    private fun updateCategoryChart(categoryBreakdown: Map<String, Float>) {
        val entries = categoryBreakdown.map { (category, percentage) ->
            PieEntry(percentage, category)
        }

        val colors = listOf(
            getColor(R.color.primary),
            getColor(R.color.secondary),
            getColor(R.color.accent),
            getColor(R.color.info),
            getColor(R.color.success)
        )

        val dataSet = PieDataSet(entries, "").apply {
            setColors(colors)
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            sliceSpace = 3f
            selectionShift = 5f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${value.toInt()}%"
                }
            }
        }

        binding.categoryPieChart.apply {
            data = PieData(dataSet)
            invalidate()
        }
    }

    private fun updateSavingsChart(monthlySavings: List<Float>) {
        val entries = monthlySavings.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = getColor(R.color.secondary)
            valueTextColor = getColor(R.color.text_primary)
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "R${value.toInt()}"
                }
            }
        }

        binding.savingsBarChart.apply {
            data = BarData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(
                listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
            )
            invalidate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}