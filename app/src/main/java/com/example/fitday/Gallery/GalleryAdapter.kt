package com.example.fitday.Gallery

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.fitday.R
import java.io.File

class GalleryAdapter(private var activity: Activity, private var items: ArrayList<String>) : BaseAdapter() {
    private class ViewHolder(row: View?) {
        var date: TextView? = null
        var image: ImageView? = null

        init {
            this.date = row?.findViewById<TextView>(R.id.date_gallery)
            this.image = row?.findViewById<ImageView>(R.id.image_gallery)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.gallery_row, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var imgFile = File(items[position])
        var parts = items[position].subSequence(42,53)
        viewHolder.date?.text = parts
        val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath().toString() )
        viewHolder.image?.setImageBitmap(myBitmap)

        return view as View
    }

    override fun getItem(i: Int): String {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}
