package com.internshala.foodrunner.activity

import android.annotation.SuppressLint
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
import com.google.gson.Gson
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.DetailRecyclerAdapter
import com.internshala.foodrunner.database.OrderDatabase
import com.internshala.foodrunner.database.OrderEntity
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.database.RestaurantEntity
import com.internshala.foodrunner.fragment.FavouriteRestaurantsFragment
import com.internshala.foodrunner.model.RestaurantDetail
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONException

class RestaurantDetailsActivity : AppCompatActivity() {

    //Declaring variables
    lateinit var toolbarDetail: Toolbar
    lateinit var imgFav: ImageView
    lateinit var imgFavClicked: ImageView
    lateinit var recyclerDetail: RecyclerView
    lateinit var recyclerAdapter: DetailRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayoutDetail: RelativeLayout
    lateinit var progressBarDetail: ProgressBar
    lateinit var btnCart: Button
    lateinit var sharedPreferences: SharedPreferences



    val restaurantMenu = arrayListOf<RestaurantDetail>()
    val order = arrayListOf<RestaurantDetail>()

        var id: String = "100"
        var user: String = "0"
        var name: String = "xyz"
        var rating: String = "xyz"
        var price: String = "xyz"
        var image_url: String = "xyz"


    fun showCart(){
        btnCart.visibility = View.VISIBLE
    }

    fun showNoCart(){
        btnCart.visibility = View.GONE
    }


    override fun onBackPressed() {
        if (Cart(this@RestaurantDetailsActivity, 2).execute().get()) {
            //create alert dialog

            val dialog = AlertDialog.Builder(this@RestaurantDetailsActivity)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will reset cart items. Do you still want to proceed?")

            dialog.setPositiveButton("YES") { text, listener ->
                //clear cart and go back
                if (Cart(this@RestaurantDetailsActivity, 1).execute().get()) {
                    super.onBackPressed()
                } else {
                    Toast.makeText(
                        this@RestaurantDetailsActivity,
                        "Cart database not cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            dialog.setNegativeButton("NO") { text, listener ->
                //clear cart and go back
                dialog.create().dismiss()
            }

            dialog.create()
            dialog.show()

        } else {
            super.onBackPressed()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        imgFav = findViewById(R.id.imgFav)
        imgFavClicked = findViewById(R.id.imgFavClicked)
        progressLayoutDetail = findViewById(R.id.progressLayoutDetail)
        progressBarDetail = findViewById(R.id.progressBarDetail)
        toolbarDetail = findViewById(R.id.toolbarDetail)
        btnCart = findViewById(R.id.btnCart)
        btnCart.visibility = View.GONE

        btnCart.setOnClickListener {
            proceedTOCart()
        }

        recyclerDetail = findViewById(R.id.recyclerDetail)
        layoutManager = LinearLayoutManager(this@RestaurantDetailsActivity)

        progressLayoutDetail.visibility = View.VISIBLE
        progressBarDetail.visibility = View.VISIBLE

        if (intent != null) {
            id = intent.getStringExtra("id").toString()
            user = intent.getStringExtra("user_id").toString()
            name = intent.getStringExtra("name").toString()
            rating = intent.getStringExtra("rating").toString()
            price = intent.getStringExtra("price").toString()
            image_url = intent.getStringExtra("image_url").toString()

        } else {
            finish()
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        if(id == "100"){
            finish()
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        val restaurantEntity = RestaurantEntity(
            id.toInt(),
            user,
            name,
            rating,
            price,
            image_url
        )

        val checkFav = DBAsyncTask(applicationContext, restaurantEntity, 1).execute()
        val isFav = checkFav.get()
        if(isFav){
            imgFavClicked.visibility = View.VISIBLE
            imgFav.visibility = View.INVISIBLE
        }
        else{
            imgFav.visibility = View.VISIBLE
            imgFavClicked.visibility = View.INVISIBLE
        }

        setUpToolbar()

        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"+id

        if (ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity))
        {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener{
                try {
                    progressLayoutDetail.visibility = View.GONE
                    progressBarDetail.visibility = View.GONE
                    val jsonObject=it.getJSONObject("data")
                    val success = jsonObject.getBoolean("success")

                    if (success) {

                        val data = jsonObject.getJSONArray("data")
                        for (i in 0 until data.length())
                        {
                            val MenuJsonObject = data.getJSONObject(i)
                            val MenuObject = RestaurantDetail(
                                MenuJsonObject.getString("id"),
                                MenuJsonObject.getString("name"),
                                MenuJsonObject.getString("cost_for_one"),
                                MenuJsonObject.getString("restaurant_id")
                            )
                            restaurantMenu.add(MenuObject)

                            recyclerAdapter = DetailRecyclerAdapter(this@RestaurantDetailsActivity, restaurantMenu)
                            recyclerDetail.adapter = recyclerAdapter
                            recyclerDetail.layoutManager = layoutManager

                        }

                    }else{
                        Toast.makeText(this@RestaurantDetailsActivity, "Some error occurred!", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(this@RestaurantDetailsActivity, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }
            },Response.ErrorListener{
                Toast.makeText(this@RestaurantDetailsActivity, "Some Volley error occurred!", Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(this@RestaurantDetailsActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this@RestaurantDetailsActivity.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantDetailsActivity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbarDetail)
        supportActionBar?.title = name
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

    fun proceedTOCart() {
        val intent = Intent(this@RestaurantDetailsActivity,CartActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("resId", id)
        startActivity(intent)
    }

    class Cart(val context: Context, val mode: Int): AsyncTask<Void, Void, Boolean>(){

        override fun doInBackground(vararg p0: Void?): Boolean{

            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()

            when(mode){

                1 -> {
                    db.orderDao().deleteOrders()
                    db.close()
                    return true
                }
                //check if cart is empty or not
                2 -> {
                    val cartItems = db.orderDao().getAllOrders()
                    db.close()
                    return cartItems.size > 0
                }
                3 ->{
                    //Get all orders and check if the length of list is 0 or not
                    val cartItems = db.orderDao().getAllOrders()
                    db.close()
                    return cartItems.size > 0
                }
            }
            return false
        }
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg p0: Void?): Boolean {
            /*
       Mode 1 -> Check DB if the restaurant is favourite or not
       Mode 2 -> Save the restaurant into DB as favourite
       Mode 3 -> Remove the restaurant from favourite
       * */

            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

            when(mode){

                1-> {
//                    Check DB if the restaurant is favourite or not
                    val restaurant: RestaurantEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.restaurant_id.toString(),restaurantEntity.user_id)
                    db.close()
                    return restaurant != null

                }
            }

            return false
        }

    }
}