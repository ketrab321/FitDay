package com.example.fitday

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView


class AccordionMealAdapter (
    private val context: Activity,
    private val resource: Int,
    private val meals: ArrayList<String>,
    private val listView: ListView
)
    : ArrayAdapter<String>(context, resource, meals),
    View.OnClickListener{

    private class MealViewHolder {
        lateinit var item: String
        lateinit var mealName: TextView
    }

    init {
        getTotalHeightOfListView(listView)
    }

    fun addMeal(item: String) {
        meals.add( item )
        notifyDataSetChanged()
    }

    override fun onClick(v: View) {

        val viewHolder = v.tag as MealViewHolder
        meals.remove( viewHolder.item )

        notifyDataSetChanged()
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        getTotalHeightOfListView(listView)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        val item = getItem(position)!!
        var viewHolder: MealViewHolder

        if (view == null ) {
            view = LayoutInflater.from(context)
                .inflate(resource, parent, false)!!
            viewHolder = MealViewHolder()

            viewHolder.mealName = view.findViewById(R.id.mealName)!!
            view.tag = viewHolder

            view.setOnClickListener( ::onClick )
        } else {
            viewHolder = view.tag as MealViewHolder
        }

        viewHolder.item = item
        viewHolder.mealName.text = item

        return view
    }

    // This helper function increases listView's height property so all items are visible
    private fun getTotalHeightOfListView(listView: ListView) {
        var totalHeight = 0

        for( i in 0 until count ) {
            val mView = getView(i, null, listView)
            mView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            totalHeight += mView.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }
}