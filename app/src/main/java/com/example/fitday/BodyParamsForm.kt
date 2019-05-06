package com.example.fitday

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.activity_body_params_form.*

//Activity is responsible for enabling user entering his/her body parameters
class BodyParamsForm : AppCompatActivity() {

    fun dailyCalorieConsumptionMale(height :Float,weight : Float, age : Float) : Float{
        var ret =66.47f + (13.7f * weight) + (5.0f * height) - (6.76f * age)
        return ret
    }

    fun dailyCalorieConsumptionFemale(height :Float,weight : Float, age : Float) : Float{
        var ret =655.1f + (9.567f * weight) + (1.85f * height)-(4.68f * age)
        return ret
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_params_form)


        var button_confirm = findViewById<Button>(R.id.confirm_body_params)
        var radioGroup = findViewById<RadioGroup>(R.id.radioGroup_Sex)
        button_confirm.setOnClickListener { view ->
            when (radioGroup.checkedRadioButtonId){
                R.id.male -> {
                    var ppm = dailyCalorieConsumptionMale(snp_height.value.toFloat(),snp_weight.value.toFloat(),snp_age.value.toFloat())
                    val returnIntent = Intent()
                    returnIntent.putExtra("PPM", ppm)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
                R.id.female ->{
                    var ppm = dailyCalorieConsumptionFemale(snp_height.value.toFloat(),snp_weight.value.toFloat(),snp_age.value.toFloat())
                    val returnIntent = Intent()
                    returnIntent.putExtra("PPM", ppm)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
                else ->{
                }
            }
        }
    }
}
