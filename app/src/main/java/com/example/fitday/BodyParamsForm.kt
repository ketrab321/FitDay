package com.example.fitday

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_body_params_form.*
import kotlinx.android.synthetic.main.activity_body_params_form.view.*

//Activity is responsible for enabling user entering his/her body parameters
class BodyParamsForm : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser
    val userId: String? = user?.uid
    var database = FirebaseDatabase.getInstance().reference

    private fun dailyCalorieConsumption(sex: Boolean, height: Float, weight: Float, age: Float): Float {
        if(sex){return  66.47f + (13.7f * weight) + (5.0f * height) - (6.76f * age)}
        else{return 655.1f + (9.567f * weight) + (1.85f * height) - (4.68f * age)}
    }

    private fun setUserValues() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var radioGroup = findViewById<RadioGroup>(R.id.radioGroup_Sex)
                // Get Post object and use the values to update the UI
                snp_height.value = dataSnapshot.child("users/$userId/height").getValue().toString().toInt()
                snp_weight.value = dataSnapshot.child("users/$userId/weight").getValue().toString().toInt()
                snp_age.value = dataSnapshot.child("users/$userId/age").getValue().toString().toInt()
                if (dataSnapshot.child("users/$userId/sex").getValue() == true) {radioGroup.male.isChecked = true}
                if (dataSnapshot.child("users/$userId/sex").getValue() == false) {radioGroup.female.isChecked = true}
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("FitDayError", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addListenerForSingleValueEvent(postListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_params_form)

        var button_confirm = findViewById<Button>(R.id.confirm_body_params)
        var radioGroup = findViewById<RadioGroup>(R.id.radioGroup_Sex)

        if (user == null) {
            finish()
        }

        setUserValues()

        button_confirm.setOnClickListener { view ->
            database.child("users/$userId/height").setValue(snp_height.value.toFloat())
            database.child("users/$userId/weight").setValue(snp_weight.value.toFloat())
            database.child("users/$userId/age").setValue(snp_age.value.toFloat())
            if (radioGroup.checkedRadioButtonId == R.id.male) {database.child("users/$userId/sex").setValue(true)}
            if (radioGroup.checkedRadioButtonId == R.id.female) {database.child("users/$userId/sex").setValue(false)}

                var ppm = dailyCalorieConsumption(
                    radioGroup.male.isChecked,
                    snp_height.value.toFloat(),
                    snp_weight.value.toFloat(),
                    snp_age.value.toFloat()
                )
                val returnIntent = Intent()
                returnIntent.putExtra("PPM", ppm)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }
    }
