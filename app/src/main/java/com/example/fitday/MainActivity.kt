package com.example.fitday

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener
import com.example.fitday.retrofit.InspirationAPI
import com.example.fitday.retrofit.InspirationDTO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val CHANGE_BODY_PARAMETERS_REQUEST_CODE = 1212
var PPM = 0.0f
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        createPieChart()
        addPieLabels()
    }

    override fun onStart() {
        super.onStart()
        getQuote()

    }
    private fun addPieLabels() {

        fun SpannableStringBuilder.append(str: String, color: Int, style: Any?) {
            val start = length
            val col = ForegroundColorSpan(color)
            append(str)
            setSpan(col, start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            if (style != null)
                setSpan(style, start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        }
        val bold = StyleSpan(Typeface.BOLD)
        var label = SpannableStringBuilder()
        val ex = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE

        val caloriesColor = ResourcesCompat.getColor(resources, R.color.colorCalories, null)
        label.append("■ ", caloriesColor, null)
        label.append("Kalorie: ")
        label.append("${432.5}", caloriesColor, bold)
        caloriesLabel.text = label

        label = SpannableStringBuilder()
        val proteinColor = ResourcesCompat.getColor(resources, R.color.colorProtein, null)
        label.append("■ ", proteinColor, null)
        label.append("Białka: ")
        label.append("${46.2}", proteinColor, bold)
        label.append(" g")
        label.append(" ${42}%", proteinColor, null)
        proteinLabel.text = label

        label = SpannableStringBuilder()
        val carbsColor = ResourcesCompat.getColor(resources, R.color.colorCarbs, null)
        label.append("■ ", carbsColor, null)
        label.append("Węglowodany: ")
        label.append("${56.1}", carbsColor, bold)
        //label.append(" g")
        label.append(" ${52}%", carbsColor, null)
        carbsLabel.text = label


        label = SpannableStringBuilder()
        val fatColor = ResourcesCompat.getColor(resources, R.color.colorFat, null)
        label.append("■ ", fatColor, null)
        label.append("Tłuszcze: ")
        label.append("${5.5}", fatColor, bold)
        label.append(" g")
        label.append(" ${5}%", fatColor, null)
        fatLabel.text = label

        label = SpannableStringBuilder()
        val leftColor = Color.parseColor("#bababa")
        label.append("■ ", leftColor, null)
        label.append("Pozostało: ")
        label.append("${1465.8}", proteinColor, bold)
        label.append(" kcal")
        leftLabel.text = label
    }

    private fun createPieChart() {
        val caloriesPercentage = 23f
        val caloriesData = ArrayList<SliceValue>()
        val emptyColor = Color.parseColor("#bababa")
        val caloriesColor = ResourcesCompat.getColor(resources, R.color.colorCalories, null)
        caloriesData.add(SliceValue(caloriesPercentage, caloriesColor))
        caloriesData.add(SliceValue(100-caloriesPercentage, emptyColor))

        val caloriesChartData = PieChartData(caloriesData)
        caloriesChartData.setHasCenterCircle(true)
        caloriesChartData.slicesSpacing = 0
        outerChart.pieChartData = caloriesChartData
        outerChart.setChartRotation(-90, true)

        val protein = 18f
        val carbs = 75f
        val fat = 7f
        val proteinColor = ResourcesCompat.getColor(resources, R.color.colorProtein, null)
        val carbsColor = ResourcesCompat.getColor(resources, R.color.colorCarbs, null)
        val fatColor = ResourcesCompat.getColor(resources, R.color.colorFat, null)
        val backgroundColor = ResourcesCompat.getColor(resources, R.color.backgroundGray, null)

        val nutrientsData = ArrayList<SliceValue>()
        nutrientsData.add(SliceValue(protein, proteinColor))
        nutrientsData.add(SliceValue(carbs, carbsColor))
        nutrientsData.add(SliceValue(fat, fatColor))

        val nutrientsChartData = PieChartData(nutrientsData)
        nutrientsChartData.slicesSpacing = 0
        nutrientsChartData.setHasCenterCircle(true)
        nutrientsChartData.centerCircleScale = 0.9f
        nutrientsChartData.centerCircleColor = backgroundColor

        innerChart.pieChartData = nutrientsChartData
        innerChart.setChartRotation(-90, true)
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
                var resultPPM = data!!.getFloatExtra("PPM",0.0f)
                PPM = resultPPM
                Toast.makeText(this,"Return $resultPPM",Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
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

            override fun onResponse(call: Call<InspirationDTO>, response: Response<InspirationDTO>) {
                val mybody = response.body()
                var quote = mybody!!.contents.quotes.get(0).quote
                //Toast.makeText(this@MainActivity,"$quote",Toast.LENGTH_LONG).show()
                var dialog = TTFancyGifDialog.Builder(this@MainActivity)
                    .setTitle("Your daily quote")
                    .setMessage("$quote")
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
        })
    }
}
