package com.example.fitday

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_add_meal.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddMealDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            // Inflate and set the layout for the dialog+s
            // Pass null as the parent view because its going in the dialog layout
            val inputView = inflater.inflate(R.layout.dialog_add_meal, null)
            builder.setView(inputView)

            val dbRef = FirebaseDatabase.getInstance().reference
            val currentFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid

            val mealTime = inputView.findViewById<Spinner>(R.id.mealTime)
            val weightInput = inputView.findViewById<EditText>(R.id.weightInput)

            builder
                // Add action buttons
                .setPositiveButton("Add"
                ) { _, _ ->
                    var mealTimeString = mealTime.selectedItem as String
                    mealTimeString = mealTimeString.toLowerCase()

                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formatted = current.format(formatter)

                    Log.d("data", formatted)
                    val meal = MealModel()
                    meal.mealName = arguments?.getString("mealName")
                    meal.kcal = arguments?.getInt("kcal")
                    meal.protein = arguments?.getInt("protein")
                    meal.carbs = arguments?.getInt("carbs")
                    meal.fat = arguments?.getInt("fat")
                    meal.weight = weightInput.text.toString().toInt()

                    dbRef.child("daily/$currentFirebaseUserId/$formatted/$mealTimeString").push().setValue(meal)
                    activity?.findViewById<ViewPager>(R.id.viewPager)?.currentItem = 0
                }


            builder.setTitle("Add to calendar")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
