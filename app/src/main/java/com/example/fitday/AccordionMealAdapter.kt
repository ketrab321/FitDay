package com.example.fitday

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions


class AccordionMealAdapter( o : FirebaseListOptions<MealModel>,
                            private val listView: ListView)
    : FirebaseListAdapter<MealModel>(o) {

//    init {
//        getTotalHeightOfListView(listView)
//    }

    override fun populateView(v: View, model: MealModel, position: Int) {
        val mealName = v.findViewById<TextView>(R.id.mealName)

        mealName.text = model.mealName

        Log.d("omg", "populateView: ${model.mealName}")
//        getTotalHeightOfListView(listView)
    }

//    override fun notifyDataSetChanged() {
//        super.notifyDataSetChanged()
//        getTotalHeightOfListView(listView)
//    }

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

class AccordionMealAdapter2 (
    private val context: Activity,
    private val resource: Int,
    private val meals: ArrayList<MealModel>,
    private val listView: ListView
)
    : ArrayAdapter<MealModel>(context, resource, meals),
    View.OnClickListener{

    private class MealViewHolder {
        lateinit var item: MealModel
        lateinit var mealName: TextView
        lateinit var calories: TextView
        lateinit var weight: TextView
    }

    init {
        getTotalHeightOfListView(listView)
    }

    fun addMeal(item: MealModel) {
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
        viewHolder.mealName.text = item.mealName

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