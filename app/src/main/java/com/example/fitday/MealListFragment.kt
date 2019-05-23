package com.example.fitday

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseListAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.meal_list.*
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseListOptions



class MealListFragment : Fragment() {
    lateinit var adapter: FirebaseListAdapter<MealModel>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.meal_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dbRef = FirebaseDatabase.getInstance().reference
        addMealButton.setOnClickListener {
            val newFragment = NewMealDialogFragment()
            newFragment.show(fragmentManager, "newMeal")
        }

        /*
         * Create a DatabaseReference to the data; works with standard DatabaseReference methods
         * like limitToLast() and etc.
         */
        val query = dbRef.child("meals")
        // Now set the adapter with a given layout
        val options = FirebaseListOptions.Builder<MealModel>()
            .setQuery(query, MealModel::class.java)
            .setLayout(R.layout.temp_meal_item)
            .build()

        adapter = object : FirebaseListAdapter<MealModel>(options) {
            override fun populateView(v: View, model: MealModel, position: Int) {
                val textView = v.findViewById<TextView>(R.id.mealName)
                textView.text = model.mealName
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
