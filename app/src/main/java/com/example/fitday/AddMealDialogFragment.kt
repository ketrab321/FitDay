package com.example.fitday

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import kotlinx.android.synthetic.main.app_bar_main.*

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
                .setPositiveButton("Add"
                ) { _, _ ->
                    activity?.findViewById<ViewPager>(R.id.viewPager)?.currentItem = 0
                }


            builder.setTitle("Add to calendar")
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
