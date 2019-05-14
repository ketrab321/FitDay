package com.example.fitday

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener
import com.example.fitday.Gallery.Gallery
import com.example.fitday.retrofit.InspirationAPI
import com.example.fitday.retrofit.InspirationDTO
import android.support.v7.app.AppCompatDelegate
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//test change

//Version: 0.1
const val CHANGE_BODY_PARAMETERS_REQUEST_CODE = 1212
var PPM = 0.0f
private const val TIME_OUT = 800
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val database = FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                    Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()

                }
            }, TIME_OUT.toLong())

        val intent = Intent(this, SignInActivity::class.java).apply {}
        startActivity(intent)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        nav_view.setNavigationItemSelectedListener(this)

        val pagerAdapter = MainTabsPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2

        tabs.setupWithViewPager(viewPager)
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA), 6666)
    }
    private fun checkSelfPermission(): Boolean {

        return ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)&&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED))
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            6666 -> {
                if (grantResults.size > 0) {
                    var permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (!permissionGranted) {
                        Toast.makeText(this, "Permission Denied! Cannot load images.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onStart() {
        super.onStart()
        getQuote()
    }

    override fun onResume() {
        super.onResume()
        setUserDataOnHeader()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.action_change_body_parameters -> {
                val myintent = Intent(this, BodyParamsForm::class.java )
                startActivityForResult(myintent, CHANGE_BODY_PARAMETERS_REQUEST_CODE)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {
                var intent = Intent(this,Gallery::class.java)
                startActivity(intent)
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CHANGE_BODY_PARAMETERS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val resultPPM = data!!.getFloatExtra("PPM",0.0f)
                PPM = resultPPM
                Toast.makeText(this,"Return $resultPPM",Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private fun setUserDataOnHeader(){
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            val navView = findViewById<NavigationView>(R.id.nav_view)
            val v = navView.getHeaderView(0)
            val nameContainer = v.findViewById<TextView>(R.id.userName)
            val emailContainer = v.findViewById<TextView>(R.id.userEmail)
            val avatarContainer = v.findViewById<ImageView>(R.id.userAvatar)
            nameContainer.text = user.displayName.toString()
            emailContainer.text = user.email
            //Todo: Proper avatar resize
            Picasso.get().load(user.photoUrl)
                .resize(200, 200)
                .into(avatarContainer)
        }

    }

    private fun getQuote()
    {
        var interceptor = HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        var client =  OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build();

        val retrofit = Retrofit.Builder()
            .baseUrl("http://quotes.rest")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val inspiration = retrofit.create(InspirationAPI::class.java)

        var call = inspiration.getQuote()

        call.enqueue(object : Callback<InspirationDTO> {
            override fun onFailure(call: Call<InspirationDTO>, t: Throwable) {
                Log.d("inspiration", "ups ${call} $t")
                defaultQuote()
            }

            override fun onResponse(call: Call<InspirationDTO>, response: Response<InspirationDTO>) {
                if(response.isSuccessful) {
                    Log.i("retrofit","success")
                    val mybody = response.body()
                    var quote = mybody!!.contents.quotes[0].quote
                    var dialog = TTFancyGifDialog.Builder(this@MainActivity)
                        .setTitle("Your daily quote")
                        .setMessage("$quote")
                        .setPositiveBtnText("Lets go")
                        .setPositiveBtnBackground("#22b573")
                        .setGifResource(R.drawable.strength)      //pass your gif, png or jpg
                        .isCancellable(true)
                        .OnPositiveClicked(TTFancyGifDialogListener() {

                            fun OnClick() {
                                Toast.makeText(this@MainActivity, "Ok", Toast.LENGTH_SHORT).show();
                            }
                        })
                    dialog.build()
                }
                else
                {
                    defaultQuote()
                }
            }
        })
    }

    private fun defaultQuote(){
        var dialog = TTFancyGifDialog.Builder(this@MainActivity)
            .setTitle("Your daily quote")
            .setMessage("Never give up and keep moving forward!!!")
            .setPositiveBtnText("Lets go")
            .setPositiveBtnBackground("#22b573")
            .setGifResource(R.drawable.strength)      //pass your gif, png or jpg
            .isCancellable(true)
            .OnPositiveClicked( TTFancyGifDialogListener() {

                fun OnClick() {
                    Toast.makeText(this@MainActivity,"Ok",Toast.LENGTH_SHORT).show();
                }
            })
            .build()
    }
}
