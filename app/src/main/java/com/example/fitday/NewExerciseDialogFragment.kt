package com.example.fitday

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class NewExerciseDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            val dbRef = FirebaseDatabase.getInstance().reference
            val currentFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid
            // Inflate and set the layout for the dialog+s
            // Pass null as the parent view because its going in the dialog layout
            val inputView = inflater.inflate(R.layout.dialog_new_exercise, null)
            builder.setView(inputView)

            val exerciseName = inputView.findViewById<EditText>(R.id.nameEditText)
            val kcal = inputView.findViewById<EditText>(R.id.kcalEditText)


            builder
                // Add action buttons
                .setPositiveButton("Dodaj"
                ) { _, _ ->
                    val exercise = ExerciseModel()
                    exercise.exerciseName= exerciseName.text.toString()
                    exercise.kcal = kcal.text.toString().toInt()


                    dbRef.child("exercises/$currentFirebaseUserId").push().setValue(exercise)
                }


            builder.setTitle("Add your own exercise")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}