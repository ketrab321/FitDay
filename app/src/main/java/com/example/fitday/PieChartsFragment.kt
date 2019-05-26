package com.example.fitday

import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.accordion_item.view.*
import kotlinx.android.synthetic.main.pie_charts.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PieChartsFragment : Fragment() {

    var totalCalories = 0
    var totalProtein = 0
    var totalCarbs = 0
    var totalFat = 0
    var BMR = 2000

    private val dbRef = FirebaseDatabase.getInstance().reference
    private val currentFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pie_charts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updatePieChart()
        updatePieLabels()
        setupAccordion()
    }

    fun notifyDataSetChanged() {
        updatePieLabels()
        updatePieChart()
    }

    private fun setupAccordion() {

        // Accordion 1 (Exercises)
        accordion1.title.text = "Ćwiczenia"
        accordion1.kcal.text = "${-250}"
        accordion1.addButton.text = "Dodaj ćwiczenie"
        accordion1.addButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_button_exercise, 0, 0, 0)

        accordion1.addButton.setOnClickListener{ switchToPage(2) }

        fun setupAccordion(accordion: View, title: String, from: String, endpoint: String) {
            accordion.findViewById<TextView>(R.id.title).text = title
            accordion.findViewById<TextView>(R.id.kcal).text = "${0}"

            val query = dbRef.child(endpoint)
            val options = FirebaseListOptions.Builder<MealModel>()
                .setQuery(query, MealModel::class.java)
                .setLayout(R.layout.accordion_meal_item)
                .build()
            val adapter = AccordionMealAdapter(options, accordion, this)
            accordion.findViewById<ListView>(R.id.mealsList).adapter = adapter
            adapter.startListening()
            accordion.findViewById<Button>(R.id.addButton).setOnClickListener { switchToPage(1, from) }
        }

        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        // Accordion 2
        setupAccordion(accordion2, "Breakfast", "breakfast",
            "daily/$currentFirebaseUserId/$today/breakfast")

        // Accordion 3
        setupAccordion(accordion3, "Dinner", "dinner",
            "daily/$currentFirebaseUserId/$today/dinner")

        // Accordion 4
        setupAccordion(accordion4, "Supper", "supper",
            "daily/$currentFirebaseUserId/$today/supper")

        // Accordion 5
        setupAccordion(accordion5, "Other", "other",
            "daily/$currentFirebaseUserId/$today/other")
    }

    private fun switchToPage(pageId: Int, from: String? = null) {
        val mainActivity = context as MainActivity
        mainActivity.intent.putExtra("page", pageId)
        mainActivity.intent.putExtra("from", from)
        mainActivity.switchPage()
    }

    private fun onAddMealClicked(view: View) {
        val mealsList = view.rootView.findViewById<ListView>(R.id.mealsList)
        val adapter = mealsList.adapter as AccordionMealAdapter

        //adapter.addMeal(MealModel("Łzy studentów", 0, 10, 5, 2))
    }

    private fun updatePieLabels() {

        BMRLabel.text = "${totalCalories*100/BMR}"

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
        val totalNutrients = totalCarbs + totalFat + totalProtein

        val caloriesColor = ResourcesCompat.getColor(resources, R.color.colorCalories, null)
        label.append("■ ", caloriesColor, null)
        label.append("Calories: ")
        label.append("$totalCalories", caloriesColor, bold)
        caloriesLabel.text = label

        label = SpannableStringBuilder()
        val proteinColor = ResourcesCompat.getColor(resources, R.color.colorProtein, null)
        label.append("■ ", proteinColor, null)
        label.append("Protein: ")
        label.append("$totalProtein", proteinColor, bold)
        label.append(" g")
        label.append(" ${if(totalNutrients==0) 0 else totalProtein*100/totalNutrients}%", proteinColor, null)
        proteinLabel.text = label

        label = SpannableStringBuilder()
        val carbsColor = ResourcesCompat.getColor(resources, R.color.colorCarbs, null)
        label.append("■ ", carbsColor, null)
        label.append("Carbs: ")
        label.append("$totalCarbs", carbsColor, bold)
        label.append(" g")
        label.append(" ${if(totalNutrients==0) 0 else totalCarbs*100/totalNutrients}%", carbsColor, null)
        carbsLabel.text = label


        label = SpannableStringBuilder()
        val fatColor = ResourcesCompat.getColor(resources, R.color.colorFat, null)
        label.append("■ ", fatColor, null)
        label.append("Fat: ")
        label.append("$totalFat", fatColor, bold)
        label.append(" g")
        label.append(" ${if(totalNutrients==0) 0 else totalFat*100/totalNutrients}%", fatColor, null)
        fatLabel.text = label

        label = SpannableStringBuilder()
        val leftColor = Color.parseColor("#bababa")
        label.append("■ ", leftColor, null)
        label.append("Left: ")
        label.append("${BMR - totalCalories}", proteinColor, bold)
        label.append(" kcal")
        leftLabel.text = label
    }

    private fun updatePieChart() {
        val caloriesPercentage = totalCalories*100f/BMR
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

        val protein = totalProtein.toFloat()
        val carbs = totalCarbs.toFloat()
        val fat = totalFat.toFloat()
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
