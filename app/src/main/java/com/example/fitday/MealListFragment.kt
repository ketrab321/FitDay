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
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.meal_list.*

class MealListFragment : Fragment() {
    private lateinit var adapter: FirebaseListAdapter<MealModel>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.meal_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dbRef = FirebaseDatabase.getInstance().reference
        val currentFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        addButton.setOnClickListener {
            val newFragment = NewMealDialogFragment()
            newFragment.show(fragmentManager, "newMeal")
        }
        /*
         * Create a DatabaseReference to the data; works with standard DatabaseReference methods
         * like limitToLast() and etc.
         */
        val query = dbRef.child("meals/$currentFirebaseUserId")

        // Now set the adapter with a given layout
        val options = FirebaseListOptions.Builder<MealModel>()
            .setQuery(query, MealModel::class.java)
            .setLayout(R.layout.meal_item)
            .build()

        adapter = object : FirebaseListAdapter<MealModel>(options) {
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

                val mealName = v.findViewById<TextView>(R.id.mealName)
                val calories = v.findViewById<TextView>(R.id.calories)
                val protein = v.findViewById<TextView>(R.id.protein)
                val carbs = v.findViewById<TextView>(R.id.carbs)
                val fat = v.findViewById<TextView>(R.id.fat)
                val caloriesIn100g = v.findViewById<TextView>(R.id.calories_in_100_g)
                val bold = StyleSpan(Typeface.BOLD)
                var label = SpannableStringBuilder()

                // Meal name
                mealName.text = model.mealName
                // Calories
                calories.text = model.kcal.toString()

                // Protein
                val proteinColor = ResourcesCompat.getColor(resources, R.color.colorProtein, null)
                label.append("Protein: ")
                label.append("${model.protein}", proteinColor, bold)
                label.append(" g")
                protein.text = label

                // Carbs
                val carbsColor = ResourcesCompat.getColor(resources, R.color.colorCarbs, null)
                label = SpannableStringBuilder()
                label.append("Carbs: ")
                label.append("${model.carbs}", carbsColor, bold)
                label.append(" g")
                carbs.text = label

                // Fat
                val fatColor = ResourcesCompat.getColor(resources, R.color.colorFat, null)
                label = SpannableStringBuilder()
                label.append("Fat: ")
                label.append("${model.fat}", fatColor, bold)
                label.append(" g")
                fat.text = label

                // Calories in 100 g
                label = SpannableStringBuilder()
                label.append("Calories in ")
                label.append("100 g", Color.BLACK, bold)
                caloriesIn100g.text = label

                v.setOnClickListener {
                    val newFragment = AddMealDialogFragment()
                    val args = Bundle()
                    args.putString("mealName", model.mealName)
                    args.putInt("kcal", model.kcal!!)
                    args.putInt("carbs", model.carbs!!)
                    args.putInt("protein", model.protein!!)
                    args.putInt("fat", model.fat!!)
                    newFragment.arguments = args
                    newFragment.show(fragmentManager, "addMeal")
                }

                v.setOnLongClickListener {
                    val deleteQuery = getRef(position)
                    deleteQuery.removeValue()
                    true
                }
            }
        }

        listView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}
