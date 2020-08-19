package com.internshala.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
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

class LoginActivity : AppCompatActivity() {

    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister: TextView
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var rlLogin: RelativeLayout
    lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_login), Context.MODE_PRIVATE)

        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        rlLogin = findViewById(R.id.progressLayoutLogin)
        progressBar = findViewById(R.id.progressBar)



        rlLogin.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnLogin.visibility = View.INVISIBLE

        //Underlining the forgot password and register yourself.
        txtForgotPassword.paintFlags = txtForgotPassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        txtRegister.paintFlags = txtRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        if (ConnectionManager().checkConnectivity(this@LoginActivity)){
            rlLogin.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            btnLogin.visibility = View.VISIBLE

            btnLogin.setOnClickListener {
                //Storing the entered values of EditText

                val mobileNumber = etMobileNumber.text.toString()
                val password = etPassword.text.toString()

                if((!mobileNumber.isEmpty()) && (!password.isEmpty())){
                    val queue = Volley.newRequestQueue(this@LoginActivity)
                    val url = "http://13.235.250.119/v2/login/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password", password)

                    val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener{

                        try {
                            val jsonObject=it.getJSONObject("data")
                            val success = jsonObject.getBoolean("success")

                            if(success){
                                val response = jsonObject.getJSONObject("data")
                                sharedPreferences.edit().putString("user_id",response.getString("user_id")).apply()
                                sharedPreferences.edit().putString("user_name",response.getString("name")).apply()
                                sharedPreferences.edit().putString("user_email",response.getString("email")).apply()
                                sharedPreferences.edit().putString("user_mobile",response.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("user_address",response.getString("address")).apply()

                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()

                                val intent = Intent(this@LoginActivity,
                                    HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else
                            {
                                Toast.makeText(this@LoginActivity, "Invalid Credentials!", Toast.LENGTH_SHORT).show()
                            }

                        }catch (e: JSONException){
                            Toast.makeText(this@LoginActivity, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                        }
                    },Response.ErrorListener {
                        Toast.makeText(this@LoginActivity, "Some Volley error occurred!", Toast.LENGTH_SHORT).show()
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
                else
                {
                    Toast.makeText(this@LoginActivity, "All the fields are mandatory. Please fill them!", Toast.LENGTH_SHORT).show()
                }


            }

            txtRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity,
                    RegisterActivity::class.java)
                startActivity(intent)
            }

            txtForgotPassword.setOnClickListener {
                val intent = Intent(this@LoginActivity,
                    ForgotPassword::class.java)
                startActivity(intent)
            }

        }
        else
        {
            val dialog = AlertDialog.Builder(this@LoginActivity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@LoginActivity.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(this@LoginActivity as Activity)
            }
            dialog.create()
            dialog.show()

        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}