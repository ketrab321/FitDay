package com.example.fitday

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
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
