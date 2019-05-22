package com.example.fitday

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_add_meal.*

class NewMealDialogFragment : DialogFragment() {

    var meal: MealModel? = null
    internal lateinit var listener: NewMealDialogListener
    interface NewMealDialogListener {
        fun onDialogPositiveClick(dialog: NewMealDialogFragment)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog+s
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_add_meal, null))
                // Add action buttons
                .setPositiveButton("Dodaj",
                    DialogInterface.OnClickListener { dialog, id ->
                        meal = MealModel()
                        meal?.mealName = nameEditText.text.toString()
                        meal?.kcal = kcalEditText.text.toString().toInt()
                        meal?.protein = proteinEditText.text.toString().toInt()
                        meal?.carbs = carbsEditText.text.toString().toInt()
                    })


            builder.setTitle("Dodaj własny produkt")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as NewMealDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }
}