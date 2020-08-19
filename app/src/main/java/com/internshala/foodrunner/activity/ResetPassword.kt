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

class ResetPassword : AppCompatActivity() {
    lateinit var btnSubmit: Button
    lateinit var etOtp: EditText
    lateinit var etNewPass: EditText
    lateinit var etNewConfirmPass: EditText
    lateinit var rlResetPass: RelativeLayout
    lateinit var progressBar: ProgressBar


    var mobile: String? = "00"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)


        rlResetPass = findViewById(R.id.progressLayoutResetPass)
        progressBar = findViewById(R.id.progressBar)
        btnSubmit = findViewById(R.id.btnSubmit)
        etOtp = findViewById(R.id.etOtp)
        etNewPass = findViewById(R.id.etNewPass)
        etNewConfirmPass = findViewById(R.id.etNewConfirmPass)

        rlResetPass.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnSubmit.visibility = View.INVISIBLE

        if(intent!=null){

            val bundle:Bundle? = intent.extras
            mobile = bundle?.getString("phone")
        }

        if (ConnectionManager().checkConnectivity(this@ResetPassword)){

            rlResetPass.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            btnSubmit.visibility = View.VISIBLE

            btnSubmit.setOnClickListener {

                val otp = etOtp.text.toString()
                val pass = etNewPass.text.toString()
                val confirmPass = etNewConfirmPass.text.toString()

                if((!otp.isEmpty()) && (!pass.isEmpty()) && (!confirmPass.isEmpty())){

                    if(otp.length==4){
                        etOtp.error = null
                        if(pass.length>=4)
                        {
                            etNewPass.error = null
                            if(pass==confirmPass)
                            {
                                etNewPass.error=null
                                etNewConfirmPass.error = null

                                val queue = Volley.newRequestQueue(this@ResetPassword)
                                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                val jsonParams = JSONObject()
                                jsonParams.put("mobile_number", mobile)
                                jsonParams.put("password", pass)
                                jsonParams.put("otp",otp)

                                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,url,jsonParams, Response.Listener{
                                    try {

                                        val jsonObject=it.getJSONObject("data")
                                        val success = jsonObject.getBoolean("success")
                                        val msg = jsonObject.getString("successMessage")
                                        if(success){
                                            Toast.makeText(this@ResetPassword, msg, Toast.LENGTH_SHORT).show()
                                            val intent = Intent(this@ResetPassword,LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }

                                    }catch (e: JSONException){
                                        Toast.makeText(this@ResetPassword, "$e", Toast.LENGTH_SHORT).show()
                                    }
                                },Response.ErrorListener {
                                    Toast.makeText(this@ResetPassword, "Some Volley error occurred!", Toast.LENGTH_SHORT).show()
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
                                etNewPass.error="Passwords don't match"
                                etNewConfirmPass.error="Passwords don't match"
                                Toast.makeText(this@ResetPassword, "Passwords don't match!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            etNewPass.error = "Password minimum length should 4"
                            Toast.makeText(this@ResetPassword, "Password minimum length should 4!", Toast.LENGTH_SHORT).show()

                        }
                    }
                    else
                    {
                        etOtp.error = "4 characters only"
                        Toast.makeText(this@ResetPassword, "OTP should of 4 characters!", Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    Toast.makeText(this@ResetPassword, "All the fields are mandatory. Please fill them!", Toast.LENGTH_SHORT).show()
                }


            }
        }
        else
        {
            val dialog = AlertDialog.Builder(this@ResetPassword as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@ResetPassword.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(this@ResetPassword  as Activity)
            }
            dialog.create()
            dialog.show()

        }


    }
}