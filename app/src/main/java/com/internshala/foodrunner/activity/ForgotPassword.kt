package com.internshala.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {

    lateinit var btnNext: Button
    lateinit var etPhone: EditText
    lateinit var etEmailAddress: EditText
    lateinit var rlForgotPass: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        sharedPref = getSharedPreferences(getString(R.string.preference_forgot_pass), Context.MODE_PRIVATE)

        btnNext = findViewById(R.id.btnNext)
        rlForgotPass = findViewById(R.id.progressLayoutForgotPass)
        progressBar = findViewById(R.id.progressBar)

        rlForgotPass.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnNext.visibility = View.INVISIBLE

        var ePattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        val OTP = sharedPref.getBoolean("otp",false)

        if (ConnectionManager().checkConnectivity(this@ForgotPassword)){
            rlForgotPass.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            btnNext.visibility = View.VISIBLE

            btnNext.setOnClickListener {
                etPhone = findViewById(R.id.etPhone)
                etEmailAddress = findViewById(R.id.etEmailAddress)


                val mobile = etPhone.text.toString()
                val email = etEmailAddress.text.toString()

                if((!mobile.isEmpty()) && (!email.isEmpty())) {
                    if(mobile.length==10) {
                        etPhone.error = null
                        if((!email.isEmpty())&&(email.trim().matches(ePattern))){
                            etEmailAddress.error = null

                            val queue = Volley.newRequestQueue(this@ForgotPassword)
                            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                            val jsonParams =JSONObject()
                            jsonParams.put("mobile_number", mobile)
                            jsonParams.put("email", email)
                            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener{

                                try {

                                    val jsonObject=it.getJSONObject("data")
                                    val success = jsonObject.getBoolean("success")

                                    if (success){
                                        sharedPref.edit().putBoolean("otp",jsonObject.getBoolean("first_try")).apply()

                                        if(!OTP)
                                        {
                                            Toast.makeText(this@ForgotPassword, "OTP send to your email is valid for 24 hours", Toast.LENGTH_LONG).show()
                                        }
                                        val bundle = Bundle()
                                        bundle.putString("phone",mobile)
                                        val intent = Intent(this@ForgotPassword,ResetPassword::class.java)
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                    else{

                                        Toast.makeText(this@ForgotPassword, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                                    }
                                }catch (e: JSONException){
                                    Toast.makeText(this@ForgotPassword, "Some Unexpected error occurred!", Toast.LENGTH_SHORT).show()
                                }
                            },Response.ErrorListener {
                                Toast.makeText(this@ForgotPassword, "Some Volley error occurred!", Toast.LENGTH_SHORT).show()
                            }){
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "0df4ac9c107b7f"
                                    return headers
                                }

                            }
                            queue.add(jsonObjectRequest)
                        }
                        else{
                            etEmailAddress.error = "Inavalid Email"
                            Toast.makeText(this@ForgotPassword, "Invalid Email!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        etPhone.error="Invalid Mobile Number"
                        Toast.makeText(this@ForgotPassword, "Invalid Mobile Number!", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@ForgotPassword, "All the fields are mandatory. Please fill them!", Toast.LENGTH_SHORT).show()
                }


            }

        }
        else
        {
            val dialog = AlertDialog.Builder(this@ForgotPassword as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@ForgotPassword.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(this@ForgotPassword  as Activity)
            }
            dialog.create()
            dialog.show()

        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ForgotPassword,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}