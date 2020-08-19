package com.internshala.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.internshala.foodrunner.R

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_login), Context.MODE_PRIVATE)
        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        val name = sharedPreferences.getString("user_email","user")

        if(isLoggedIn){
            Handler().postDelayed({
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
            },1000)

        }
        else{
            Handler().postDelayed({
                val intent = Intent(this@MainActivity,
                    LoginActivity::class.java)
                startActivity(intent)
                finish()
            },1000)
        }



    }
}
