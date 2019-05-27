package com.example.fitday.gallery

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import com.bumptech.glide.Glide
import com.example.fitday.R
import kotlinx.android.synthetic.main.photo_dialog.*
import java.io.File

class PhotoDialog(context : Context, _imgFile: File,_date : String ) : Dialog(context),View.OnClickListener{

    var imageFile = _imgFile
    var date = _date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.photo_dialog)
        Glide.with(context)
            .asBitmap()
            .load(Uri.fromFile(imageFile))
            .into(fullSizeImage)
        dialog_date.append(date)
    }


    override fun onClick(v: View?) {
        dismiss()
    }
}