package com.example.fitday.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.fitday.BuildConfig
import com.example.fitday.R
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.content_gallery.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

var imagePaths : ArrayList<String> =  ArrayList()

const val IMAGE_FOLDER = "FitDay"
const val CAPTURE_PHOTO = 11122
private const val TIME_OUT = 800

class Gallery : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setSupportActionBar(toolbar)
        Handler().postDelayed(
            {
                // check if user has grannted permission to access device external storage.
                // if not ask user for access to external storage.
                if (!checkSelfPermission()) {
                    requestPermission()
                } else {
                    // if permission granted read images from storage.
                    //  source code for this function can be found below.
                    loadAllImages()
                }
            }, TIME_OUT.toLong())
        this.window.setBackgroundDrawableResource(R.drawable.fitness__silownia_007_kobieta__pompki)
        adapter = GalleryAdapter(this, imagePaths)
        gallery.adapter = adapter
        loadAllImages()

        adapter.notifyDataSetChanged()

        fab_gallery.setOnClickListener { _ ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        null
                    }

                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            this, BuildConfig.APPLICATION_ID + ".fileprovider",it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,MediaStore.Images.Media.ORIENTATION)
                        startActivityForResult(takePictureIntent, CAPTURE_PHOTO)

                    }
                }
            }

        }
    }

    private lateinit var  adapter : GalleryAdapter

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            6036 -> {
                if (grantResults.isNotEmpty()) {
                    val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (permissionGranted) {


                        loadAllImages()
                    } else {
                        Toast.makeText(this, "Permission Denied! Cannot load images.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA), 6036)
    }
    private fun checkSelfPermission(): Boolean {

        return ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
    }
    private fun getAllShownImagesPath(filePath : ArrayList<String>): ArrayList<String> {

        //var filePath: ArrayList<String> = ArrayList<String>()
        val path = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path,IMAGE_FOLDER)
        if (path.exists()) {

            Log.i("mymemory","reading")

            for (i in path.list().iterator()) {
                if(i.contains(".jpg")) {
                    filePath.add("$path/$i")
                    //val x = File(path.toString() + "/" + i)
                    //x.delete()
                    Log.i("mymemory","$path/$i")
                }
            }
        }
        else
        {
            if(!path.mkdir()){
                Toast.makeText(this, "$IMAGE_FOLDER can't be created.", Toast.LENGTH_SHORT).show()
            }
            Log.i("mymemory","Creating folder")
        }

        //path.delete()
        return filePath
    }
    private fun loadAllImages()
    {
        imagePaths.clear()
        getAllShownImagesPath(imagePaths)
        imagePaths.sort()
        imagePaths.reverse()
    }
    @Throws(IOException::class)
    private fun createFile() : File{

        val myDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path+"/"+ IMAGE_FOLDER)
        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val date = Date()
        val formatter = SimpleDateFormat("||MMM_dd_yyyy||",Locale.US)
        val answer: String = formatter.format(date)
        return File.createTempFile(
            "Image-$answer",
            ".jpg",
            myDir
        )

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAPTURE_PHOTO)
            {
                loadAllImages()
                adapter.notifyDataSetChanged()
            }
        }
    }
}
