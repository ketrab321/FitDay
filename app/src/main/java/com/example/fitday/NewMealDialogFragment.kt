package com.example.fitday

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_add_meal.*

class NewMealDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            val dbRef = FirebaseDatabase.getInstance().reference
            val currentFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
            // Inflate and set the layout for the dialog+s
            // Pass null as the parent view because its going in the dialog layout
            val inputView = inflater.inflate(R.layout.dialog_add_meal, null)
            builder.setView(inputView)

            val mealName = inputView.findViewById<EditText>(R.id.nameEditText)
            val kcal = inputView.findViewById<EditText>(R.id.kcalEditText)
            val protein = inputView.findViewById<EditText>(R.id.proteinEditText)
            val carbs = inputView.findViewById<EditText>(R.id.carbsEditText)
            val fat = inputView.findViewById<EditText>(R.id.fatEditText)

            builder
                // Add action buttons
                .setPositiveButton("Dodaj"
                ) { _, _ ->
                    val meal = MealModel()
                    meal.mealName = mealName.text.toString()
                    meal.kcal = kcal.text.toString().toInt()
                    meal.protein = protein.text.toString().toInt()
                    meal.carbs = carbs.text.toString().toInt()  
                    meal.fat = fat.text.toString().toInt()

                    dbRef.child("meals/$currentFirebaseUserId").push().setValue(meal)
                }


            builder.setTitle("Dodaj własny produkt")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}