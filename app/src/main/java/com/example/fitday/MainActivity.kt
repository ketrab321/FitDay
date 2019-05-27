package com.example.fitday

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener
import com.example.fitday.Gallery.Gallery
import com.example.fitday.retrofit.InspirationAPI
import com.example.fitday.retrofit.InspirationDTO
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
const val TOGGLE_QUOTE_CODE = 2221
var BMR = 0
private const val TIME_OUT = 600

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

     val database = FirebaseDatabase.getInstance().reference
    private lateinit var pagerAdapter: MainTabsPagerAdapter

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
        startActivityForResult(intent,TOGGLE_QUOTE_CODE)

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

        pagerAdapter = MainTabsPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 2
        // Change theme color when exercises page is active
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {

                // Example how to pass data to fragment when swiped on it.
//                val fragment = pagerAdapter.getItem(viewPager.currentItem)
//                if (fragment is ExerciseListFragment)
//                    fragment.boo()

                val bar = findViewById<TabLayout>(R.id.tabs)!!
                val toolbar = findViewById<android.support.v7.widget.Toolbar>(R.id.toolbar)
                if (p0 == 2) {
                    bar.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.colorPrimaryDark))
                    toolbar.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.colorPrimaryDark))
                } else {
                    bar.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                    toolbar.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                }
            }
        })
        tabs.setupWithViewPager(viewPager)
    }

    fun switchPage() {
        val newPage = intent.getIntExtra("page", 0)
        tabs.getTabAt(newPage)?.select()
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
                if (grantResults.isNotEmpty()) {
                    val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (!permissionGranted) {
                        Toast.makeText(this, "Permission Denied! Cannot load images.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private lateinit var  sharedPref : SharedPreferences

    override fun onStart() {
        super.onStart()
        sharedPref = this.getPreferences(Context.MODE_PRIVATE)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_gallery -> {
                val intent = Intent(this,Gallery::class.java)
                startActivity(intent)
            }
            R.id.nav_manage -> {
                val intent = Intent(this,BodyParamsForm::class.java)
                startActivityForResult(intent, CHANGE_BODY_PARAMETERS_REQUEST_CODE)
            }
            R.id.nav_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_signout -> {
                if(FirebaseAuth.getInstance().currentUser == null) {
                    Toast.makeText(this, "User already signed out", Toast.LENGTH_SHORT).show()
                }
                // Firebase sign out
                FirebaseAuth.getInstance().signOut()
                setUserDataOnHeader()
               // Toast.makeText(this, "${FirebaseAuth.getInstance().currentUser}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java).apply {}
                startActivity(intent)
            }

            R.id.nav_signin -> {

                val intent = Intent(this, SignInActivity::class.java).apply {}
                if(FirebaseAuth.getInstance().currentUser != null) {
                    Toast.makeText(this, "User already signed in", Toast.LENGTH_SHORT).show()
                }
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == CHANGE_BODY_PARAMETERS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val resultBMR = data!!.getIntExtra("BMR",0)
                BMR = resultBMR
                sharedPref.edit().putInt("BMR", BMR).apply()
                Log.d("savedBMR","$BMR")
                Toast.makeText(this,"New BMR $resultBMR",Toast.LENGTH_SHORT).show()
                val pieChartsFragment = pagerAdapter.getItem(0) as PieChartsFragment
                pieChartsFragment.BMR = BMR
                //pieChartsFragment.updatePieLabels()
            }
        }

        if(requestCode == TOGGLE_QUOTE_CODE)
        {
            Handler().postDelayed(
                {
                    getQuote()
                }, TIME_OUT.toLong())
        }

    }

    private fun setUserDataOnHeader(){
        val user = FirebaseAuth.getInstance().currentUser
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val v = navView.getHeaderView(0)
        val nameContainer = v.findViewById<TextView>(R.id.userName)
        val emailContainer = v.findViewById<TextView>(R.id.userEmail)
        val avatarContainer = v.findViewById<ImageView>(R.id.userAvatar)
        if(user != null){
            nameContainer.text = user.displayName.toString()
            emailContainer.text = user.email
            //Todo: Proper avatar resize
            Picasso.get().load(user.photoUrl)
                .centerCrop()
                .resize(200, 200)
                .into(avatarContainer)
        }
        else{
            nameContainer.text = "Your name"
            emailContainer.text = "youremail@domain.com"
            Picasso.get().load(R.drawable.muscle)
                .centerCrop()
                .resize(200, 200)
                .into(avatarContainer)
        }

    }

    private fun getQuote()
    {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client =  OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://quotes.rest")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val inspiration = retrofit.create(InspirationAPI::class.java)

        val call = inspiration.getQuote()

        call.enqueue(object : Callback<InspirationDTO> {
            override fun onFailure(call: Call<InspirationDTO>, t: Throwable) {
                Log.d("inspiration", "ups $call $t")
                defaultQuote()
            }

            override fun onResponse(call: Call<InspirationDTO>, response: Response<InspirationDTO>) {
                if(response.isSuccessful) {
                    Log.i("retrofit","success")
                    val myBody = response.body()
                    val quote = myBody!!.contents.quotes[0].quote
                    val dialog = TTFancyGifDialog.Builder(this@MainActivity)
                        .setTitle("Your daily quote")
                        .setMessage(quote)
                        .setPositiveBtnText("Lets go")
                        .setPositiveBtnBackground("#22b573")
                        .setGifResource(R.drawable.strength)      //pass your gif, png or jpg
                        .isCancellable(true)
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
        TTFancyGifDialog.Builder(this@MainActivity)
            .setTitle("Your daily quote")
            .setMessage("Never give up and keep moving forward!!!")
            .setPositiveBtnText("Lets go")
            .setPositiveBtnBackground("#22b573")
            .setGifResource(R.drawable.strength)      //pass your gif, png or jpg
            .isCancellable(true)
            .build()
    }
}
