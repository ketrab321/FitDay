package com.example.fitday

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
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
        return if(sex){
            66.47f + (13.7f * weight) + (5.0f * height) - (6.76f * age)
        } else{
            655.1f + (9.567f * weight) + (1.85f * height) - (4.68f * age)
        }
    }

    private fun setUserValues() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val radioGroup = findViewById<RadioGroup>(R.id.radioGroup_Sex)

                if(dataSnapshot.hasChild("users/$userId")) {
                    snp_height.value = dataSnapshot.child("users/$userId/height").value.toString().toInt()
                    snp_weight.value = dataSnapshot.child("users/$userId/weight").value.toString().toInt()
                    snp_age.value = dataSnapshot.child("users/$userId/age").value.toString().toInt()
                    if (dataSnapshot.child("users/$userId/sex").value == true) {
                        radioGroup.male.isChecked = true
                    }
                    if (dataSnapshot.child("users/$userId/sex").value == false) {
                        radioGroup.female.isChecked = true
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("FitDayError", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addListenerForSingleValueEvent(postListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_params_form)

        val buttonConfirm = findViewById<Button>(R.id.confirm_body_params)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup_Sex)

        if (user == null) {
            Toast.makeText(this, "User not signed", Toast.LENGTH_SHORT).show()
            finish()
        }

        setUserValues()

        buttonConfirm.setOnClickListener {
            database.child("users/$userId/height").setValue(snp_height.value.toFloat())
            database.child("users/$userId/weight").setValue(snp_weight.value.toFloat())
            database.child("users/$userId/age").setValue(snp_age.value.toFloat())
            if (radioGroup.checkedRadioButtonId == R.id.male) {database.child("users/$userId/sex").setValue(true)}
            if (radioGroup.checkedRadioButtonId == R.id.female) {database.child("users/$userId/sex").setValue(false)}
            finish()
            }
        }
    }
