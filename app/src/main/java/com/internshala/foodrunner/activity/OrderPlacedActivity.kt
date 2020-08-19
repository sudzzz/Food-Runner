package com.internshala.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.internshala.foodrunner.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        btnOk = findViewById(R.id.btnOk)

        btnOk.setOnClickListener {
            val intent = Intent(this@OrderPlacedActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onBackPressed() {
        btnOk.callOnClick()
    }
}