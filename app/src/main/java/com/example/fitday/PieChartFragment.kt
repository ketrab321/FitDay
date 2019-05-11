package com.example.fitday

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_main.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue

class PieChartFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createPieChart()
        addPieLabels()
    }
    
    private fun addPieLabels() {

        fun SpannableStringBuilder.append(str: String, color: Int, style: Any?) {
            val start = length
            val col = ForegroundColorSpan(color)
            append(str)
            setSpan(col, start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (style != null)
                setSpan(style, start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        }
        val bold = StyleSpan(Typeface.BOLD)
        var label = SpannableStringBuilder()
        val ex = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

        val caloriesColor = ResourcesCompat.getColor(resources, R.color.colorCalories, null)
        label.append("■ ", caloriesColor, null)
        label.append("Kalorie: ")
        label.append("${432.5}", caloriesColor, bold)
        caloriesLabel.text = label

        label = SpannableStringBuilder()
        val proteinColor = ResourcesCompat.getColor(resources, R.color.colorProtein, null)
        label.append("■ ", proteinColor, null)
        label.append("Białka: ")
        label.append("${46.2}", proteinColor, bold)
        label.append(" g")
        label.append(" ${42}%", proteinColor, null)
        proteinLabel.text = label

        label = SpannableStringBuilder()
        val carbsColor = ResourcesCompat.getColor(resources, R.color.colorCarbs, null)
        label.append("■ ", carbsColor, null)
        label.append("Węglowodany: ")
        label.append("${56.1}", carbsColor, bold)
        //label.append(" g")
        label.append(" ${52}%", carbsColor, null)
        carbsLabel.text = label


        label = SpannableStringBuilder()
        val fatColor = ResourcesCompat.getColor(resources, R.color.colorFat, null)
        label.append("■ ", fatColor, null)
        label.append("Tłuszcze: ")
        label.append("${5.5}", fatColor, bold)
        label.append(" g")
        label.append(" ${5}%", fatColor, null)
        fatLabel.text = label

        label = SpannableStringBuilder()
        val leftColor = Color.parseColor("#bababa")
        label.append("■ ", leftColor, null)
        label.append("Pozostało: ")
        label.append("${1465.8}", proteinColor, bold)
        label.append(" kcal")
        leftLabel.text = label
    }
    
    private fun createPieChart() {
        val caloriesPercentage = 23f
        val caloriesData = ArrayList<SliceValue>()
        val emptyColor = Color.parseColor("#bababa")
        val caloriesColor = ResourcesCompat.getColor(resources, R.color.colorCalories, null)
        caloriesData.add(SliceValue(caloriesPercentage, caloriesColor))
        caloriesData.add(SliceValue(100-caloriesPercentage, emptyColor))

        val caloriesChartData = PieChartData(caloriesData)
        caloriesChartData.setHasCenterCircle(true)
        caloriesChartData.slicesSpacing = 0
        outerChart.pieChartData = caloriesChartData
        outerChart.setChartRotation(-90, true)

        val protein = 18f
        val carbs = 7f
        val fat = 75f
        val proteinColor = ResourcesCompat.getColor(resources, R.color.colorProtein, null)
        val carbsColor = ResourcesCompat.getColor(resources, R.color.colorCarbs, null)
        val fatColor = ResourcesCompat.getColor(resources, R.color.colorFat, null)
        val backgroundColor = ResourcesCompat.getColor(resources, R.color.backgroundGray, null)

        val nutrientsData = ArrayList<SliceValue>()
        nutrientsData.add(SliceValue(protein, proteinColor))
        nutrientsData.add(SliceValue(carbs, carbsColor))
        nutrientsData.add(SliceValue(fat, fatColor))

        val nutrientsChartData = PieChartData(nutrientsData)
        nutrientsChartData.slicesSpacing = 0
        nutrientsChartData.setHasCenterCircle(true)
        nutrientsChartData.centerCircleScale = 0.9f
        nutrientsChartData.centerCircleColor = backgroundColor

        innerChart.pieChartData = nutrientsChartData
        innerChart.setChartRotation(-90, true)
    }
}
