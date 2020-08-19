package com.internshala.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import com.internshala.foodrunner.R
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var btnRegister: Button
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobile: EditText
    lateinit var etDelivery: EditText
    lateinit var etPass: EditText
    lateinit var etConfirmPass: EditText
    lateinit var rlRegister: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_register), Context.MODE_PRIVATE)
        btnRegister = findViewById(R.id.btnRegister)
        coordinatorLayout = findViewById(R.id.coordinatorLayoutRegister)
        toolbar = findViewById(R.id.toolbarRegister)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobile = findViewById(R.id.etMobile)
        etDelivery = findViewById(R.id.etDelivery)
        etPass = findViewById(R.id.etPass)
        etConfirmPass = findViewById(R.id.etConfirmPass)
        rlRegister = findViewById(R.id.progressLayoutRegister)
        progressBar = findViewById(R.id.progressBar)
        rlRegister.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        btnRegister.visibility=View.INVISIBLE

        setUpToolbar()



        var ePattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

        if (ConnectionManager().checkConnectivity(this@RegisterActivity)){
            rlRegister.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            btnRegister.visibility=View.VISIBLE

            btnRegister.setOnClickListener {

                if((!etName.text.toString().isEmpty()) && (!etEmail.text.toString().isEmpty()) && (!etMobile.text.toString().isEmpty())
                    && (!etDelivery.text.toString().isEmpty()) && (!etPass.text.toString().isEmpty())
                    && (!etConfirmPass.text.toString().isEmpty())){
                    if (etName.text.toString().length>=3)
                    {
                        etName.error = null
                        if((etEmail).text.trim().matches(ePattern))
                        {
                            etEmail.error=null
                            if(etMobile.text.toString().length==10)
                            {
                                etMobile.error=null

                                if (etPass.text.toString().length>=4)
                                {
                                    etPass.error = null
                                    if(etConfirmPass.text.toString()==etPass.text.toString())
                                    {
                                        etPass.error = null
                                        etConfirmPass.error = null
                                        sendRegisterRequest(
                                            etName.text.toString(),
                                            etMobile.text.toString(),
                                            etPass.text.toString(),
                                            etDelivery.text.toString(),
                                            etEmail.text.toString()
                                        )
                                        Toast.makeText(this@RegisterActivity,
                                            "User Registered Successfully!",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                    else
                                    {
                                        etPass.error = "Passwords don't match"
                                        etConfirmPass.error = "Passwords don't match"
                                        Toast.makeText(this@RegisterActivity,
                                            "Passwords don't match",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else
                                {
                                    etPass.error = "Password should be more than or equal 4 digits"
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Password should be more than or equal 4 digits",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            else
                            {
                                etMobile.error = "Invalid Mobile number"
                                Toast.makeText(this@RegisterActivity, "Invalid Mobile number", Toast.LENGTH_SHORT).show()
                            }

                        }
                        else
                        {
                            etEmail.error = "Invalid Email"
                            Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        etName.error = "Invalid Name"
                        Toast.makeText(this@RegisterActivity, "Invalid Name", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@RegisterActivity, "All the fields are mandatory. Please fill them!", Toast.LENGTH_SHORT).show()
                }


            }

        }
        else{
            val dialog = AlertDialog.Builder(this@RegisterActivity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@RegisterActivity.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(this@RegisterActivity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun sendRegisterRequest(name: String, mobile: String, password: String, address: String, email: String) {
        val queue = Volley.newRequestQueue(this@RegisterActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result"
        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener{

            try {
                val jsonObject=it.getJSONObject("data")
                val success = jsonObject.getBoolean("success")

                if (success) {

                    val response = jsonObject.getJSONObject("data")
                    sharedPreferences.edit().putString("user_id",response.getString("user_id")).apply()
                    sharedPreferences.edit().putString("user_name",response.getString("name")).apply()
                    sharedPreferences.edit().putString("user_email",response.getString("email")).apply()
                    sharedPreferences.edit().putString("user_mobile",response.getString("mobile_number")).apply()
                    sharedPreferences.edit().putString("user_address",response.getString("address")).apply()



                    val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this@RegisterActivity, "Some error occurred!", Toast.LENGTH_SHORT).show()
                }

            }catch (e: JSONException){
                Toast.makeText(this@RegisterActivity, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
            }
        },Response.ErrorListener{
            Toast.makeText(this@RegisterActivity, "Some Volley error occurred!", Toast.LENGTH_SHORT).show()
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

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id==android.R.id.home)
        {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}