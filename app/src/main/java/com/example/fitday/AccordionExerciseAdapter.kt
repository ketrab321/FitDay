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


class AccordionExerciseAdapter( o : FirebaseListOptions<ExerciseModel>,
                            private val accordion: View,
                            private val context: PieChartsFragment)
    : FirebaseListAdapter<ExerciseModel>(o) {

    var totalCalories = 0

    override fun populateView(v: View, model: ExerciseModel, position: Int) {

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

        val exerciseName = v.findViewById<TextView>(R.id.mealName)
        val calories = v.findViewById<TextView>(R.id.calories)
        val reps = v.findViewById<TextView>(R.id.weight)

        exerciseName.text = model.exerciseName

        val exerciseColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimaryDark, null)
        label.append("Burn: ")
        label.append("${model.kcal}", exerciseColor, bold)
        label.append(" kcal")
        calories.text = label

        label = SpannableStringBuilder()
        label.append("Reps: ")
        label.append("${model.reps}", Color.BLACK, bold)
        reps.text = label
    }

    override fun onDataChanged() {
        val caloriesLabel = accordion.findViewById<TextView>(R.id.kcal)
        updateTotalCalories(caloriesLabel)

        val listView = accordion.findViewById<ListView>(R.id.mealsList)
        updateTotalHeightOfListView(listView)
    }

    private fun updateTotalCalories(label: TextView) {

        context.totalCalories -= totalCalories
        totalCalories = 0

        for( i in 0 until count ) {
            val item = getItem(i)
            totalCalories -= (item.kcal ?: 0) * (item.reps ?: 0)
        }

        label.text = totalCalories.toString()

        context.totalCalories += totalCalories
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
