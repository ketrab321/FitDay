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
import android.widget.Toast
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.exercise_list.*

class ExerciseListFragment : Fragment() {
    lateinit var adapter: FirebaseListAdapter<ExerciseModel>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.exercise_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dbRef = FirebaseDatabase.getInstance().reference
        val currentFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
        addExerciseButton.setOnClickListener {
            val newFragment = NewExerciseDialogFragment()
            newFragment.show(fragmentManager, "newExercise")
        }
        val l = R.mipmap.ic_launcher
        /*
         * Create a DatabaseReference to the data; works with standard DatabaseReference methods
         * like limitToLast() and etc.
         */
        val query = dbRef.child("exercises/$currentFirebaseUserId")

        // Now set the adapter with a given layout
        val options = FirebaseListOptions.Builder<ExerciseModel>()
            .setQuery(query, ExerciseModel::class.java)
            .setLayout(R.layout.exercise_item)
            .build()

        adapter = object : FirebaseListAdapter<ExerciseModel>(options) {
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

                val exerciseName = v.findViewById<TextView>(R.id.exerciseName)
                val calories = v.findViewById<TextView>(R.id.caloriesExercise)
                val caloriesIn1minute = v.findViewById<TextView>(R.id.calories_in_one_minute)
                val bold = StyleSpan(Typeface.BOLD)
                var label = SpannableStringBuilder()

                // Exercise name
                exerciseName.text = model.exerciseName
                // Calories
                calories.text = model.kcal.toString()


                // Calories in 100 g
                label = SpannableStringBuilder()
                label.append("Calories burnt in ")
                label.append("one rep", Color.BLACK, bold)
                caloriesIn1minute.text = label

                v.setOnClickListener {
                    val newFragment = AddExerciseDialogFragment()
                    val args = Bundle()
                    args.putString("exerciseName", model.exerciseName)
                    args.putInt("kcal", model.kcal!!)
                    newFragment.arguments = args
                    newFragment.show(fragmentManager, "addExercise")
                }

                v.setOnLongClickListener {
                    val deleteQuery = getRef(position)
                    deleteQuery.removeValue()
                    true
                }
            }
        }

        exerciseList.adapter = adapter
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
