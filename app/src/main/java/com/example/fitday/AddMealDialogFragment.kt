package com.example.fitday

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

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


            builder
                // Add action buttons
                .setPositiveButton("Dodaj"
                ) { _, _ ->

                }


            builder.setTitle("Dodaj własny produkt")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
