package com.example.fitday

import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions


class AccordionMealAdapter( o : FirebaseListOptions<MealModel>,
                            private val accordion: View,
                            private val context: PieChartsFragment)
    : FirebaseListAdapter<MealModel>(o) {

    var totalCalories = 0
    var totalProtein = 0
    var totalCarbs = 0
    var totalFat = 0

    override fun populateView(v: View, model: MealModel, position: Int) {

        // Helper function for formatting text
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

        val mealName = v.findViewById<TextView>(R.id.mealName)
        val calories = v.findViewById<TextView>(R.id.calories)
        val weight = v.findViewById<TextView>(R.id.weight)

        mealName.text = model.mealName

        val caloriesColor = ResourcesCompat.getColor(context.resources, R.color.colorCalories, null)
        label.append("Calories: ")
        label.append("${model.kcal!! * model.weight!! / 100}", caloriesColor, bold)
        calories.text = label

        label = SpannableStringBuilder()
        label.append("Weight: ")
        label.append("${model.weight} g", Color.BLACK, bold)
        weight.text = label

        v.setOnLongClickListener {
            val deleteQuery = getRef(position)
            deleteQuery.removeValue()
            true
        }
    }

    override fun onDataChanged() {
        val caloriesLabel = accordion.findViewById<TextView>(R.id.kcal)
        updateTotalCalories(caloriesLabel)

        val listView = accordion.findViewById<ListView>(R.id.mealsList)
        updateTotalHeightOfListView(listView)
    }

    private fun updateTotalCalories(label: TextView) {

        context.totalCalories -= totalCalories
        context.totalCarbs -= totalCarbs
        context.totalFat -= totalFat
        context.totalProtein -= totalProtein
        totalCalories = 0
        totalCarbs = 0
        totalFat = 0
        totalProtein = 0

        for( i in 0 until count ) {
            val item = getItem(i)
            val weight = item.weight!!
            totalCalories += weight * (item.kcal ?: 0) / 100
            totalCarbs += weight * (item.carbs ?: 0) / 100
            totalFat += weight * (item.fat ?: 0) / 100
            totalProtein += weight * (item.protein ?: 0) / 100
        }

        label.text = totalCalories.toString()

        context.totalCalories += totalCalories
        context.totalCarbs += totalCarbs
        context.totalFat += totalFat
        context.totalProtein += totalProtein
        context.notifyDataSetChanged()
    }

    // This helper function increases listView's height property so all items are visible
    private fun updateTotalHeightOfListView(listView: ListView) {
        var totalHeight = 0

        for( i in 0 until count ) {
            val mView = getView(i, null, listView)
            mView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            totalHeight += mView.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }
}
