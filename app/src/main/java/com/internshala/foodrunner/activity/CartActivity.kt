package com.internshala.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.CartRecyclerAdapter
import com.internshala.foodrunner.database.OrderDatabase
import com.internshala.foodrunner.database.OrderEntity
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var txtRestaurant: TextView
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnOrder: Button
    lateinit var rlCart: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences
    var cartItemList = listOf<OrderEntity>()

    var name: String = "xyz"
    var resId: String = "00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbarCart)
        txtRestaurant = findViewById(R.id.txtRestaurant)
        recyclerCart = findViewById(R.id.recyclerCart)
        layoutManager = LinearLayoutManager(this@CartActivity)
        btnOrder = findViewById(R.id.btnOrder)
        rlCart = findViewById(R.id.progressLayoutCart)
        progressBar = findViewById(R.id.progressBarCart)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_login), Context.MODE_PRIVATE)

        setUpToolbar()

        rlCart.visibility = View.VISIBLE
        btnOrder.visibility = View.INVISIBLE

        if(intent!=null){
            name = intent.getStringExtra("name").toString()
            resId = intent.getStringExtra("resId").toString()
        }

        txtRestaurant.text = name

        if (ConnectionManager().checkConnectivity(this@CartActivity)){
            rlCart.visibility = View.INVISIBLE
            btnOrder.visibility = View.VISIBLE

            cartItemList = GetOrders(this@CartActivity).execute().get()

            val btnText = "Place Order(Total: ₹ ${totalCost(cartItemList)} )"
            btnOrder.text = btnText
            recyclerAdapter = CartRecyclerAdapter(this@CartActivity, cartItemList)
            recyclerCart.adapter = recyclerAdapter
            recyclerCart.layoutManager = layoutManager

            btnOrder.setOnClickListener {

                val user_id = sharedPreferences.getString("user_id","0").toString()
                val total_cost = totalCost(cartItemList).toString()

                val queue = Volley.newRequestQueue(this@CartActivity)
                val url = "http://13.235.250.119/v2/place_order/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("user_id",user_id)
                jsonParams.put("restaurant_id",resId)
                jsonParams.put("total_cost",total_cost)
                val foodArray = JSONArray()
                for (i in 0 until cartItemList.size) {
                    val foodId = JSONObject()
                    foodId.put("food_item_id", cartItemList[i].foodId.toString())
                    foodArray.put(i, foodId)
                }
                jsonParams.put("food", foodArray)

                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams, Response.Listener{

                        try {
                            val jsonObject=it.getJSONObject("data")
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                if (DBAsyncTask(this@CartActivity).execute().get()) {

                                    super.onBackPressed()
                                }

                                //move to order placed screen
                                val intent =
                                    Intent(this@CartActivity, OrderPlacedActivity::class.java)
                                startActivity(intent)
                                finishAffinity()

                            } else {
                                Toast.makeText(this@CartActivity, "Payment did not succeed", Toast.LENGTH_SHORT).show()
                            }

                        }catch (e: JSONException){
                            Toast.makeText(this@CartActivity, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                        }

                    },Response.ErrorListener {
                        Toast.makeText(this@CartActivity, "Some Volley error occurred!", Toast.LENGTH_SHORT).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "0df4ac9c107b7f"
                        return headers
                    }
                }
                queue.add(jsonRequest)

            }



        }
        else{
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@CartActivity.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(this@CartActivity as Activity)
            }
            dialog.create()
            dialog.show()
        }

    }

    fun totalCost(orderEntity: List<OrderEntity>):Int{
        var total = 0
        for(item in orderEntity){
            total += item.foodCost.toInt()
        }
        return total
    }

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
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

    class GetOrders(val context: Context): AsyncTask<Void, Void, List<OrderEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()

            return db.orderDao().getAllOrders()
        }
    }

    class DBAsyncTask(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders()
            db.close()
            return true
        }

    }

}



