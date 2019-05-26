package com.example.fitday

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_add_meal.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddExerciseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            // Inflate and set the layout for the dialog+s
            // Pass null as the parent view because its going in the dialog layout
            val inputView = inflater.inflate(R.layout.dialog_add_exercise, null)
            builder.setView(inputView)

            val dbRef = FirebaseDatabase.getInstance().reference
            val currentFirebaseUserId = FirebaseAuth.getInstance().currentUser?.uid

            val repsInput = inputView.findViewById<EditText>(R.id.repsInput)

            builder
                // Add action buttons
                .setPositiveButton("Add"
                ) { _, _ ->
                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val formatted = current.format(formatter)

                    Log.d("data", formatted)
                    val exercise = ExerciseModel()
                    exercise.exerciseName = arguments?.getString("exerciseName")
                    exercise.kcal = arguments?.getInt("kcal")
                    exercise.reps = repsInput.text.toString().toInt()

                    dbRef.child("daily/$currentFirebaseUserId/$formatted/exercises").push().setValue(exercise)
                    activity?.findViewById<ViewPager>(R.id.viewPager)?.currentItem = 0

                    Thread.sleep(1000)
                    var dialog = TTFancyGifDialog.Builder(activity)
                        .setTitle("GREAT JOB")
                        .setMessage("Chuck Norris is proud of you")
                        .setPositiveBtnText("I am THE BEST")
                        .setPositiveBtnBackground("#000000")
                        .setGifResource(R.drawable.chuck)      //pass your gif, png or jpg
                        .isCancellable(true)
                        .OnPositiveClicked( TTFancyGifDialogListener() {
                            fun OnClick() {
                                Toast.makeText(activity,"Ok", Toast.LENGTH_SHORT).show();
                            }
                        })
                    dialog.build()
                }


            builder.setTitle("Add to calendar")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
