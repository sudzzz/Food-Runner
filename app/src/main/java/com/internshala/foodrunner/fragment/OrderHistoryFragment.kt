package com.internshala.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.OrderHistoryRecyclerAdapter
import com.internshala.foodrunner.model.FoodItem
import com.internshala.foodrunner.model.Order
import com.internshala.foodrunner.model.Restaurant
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONException


class OrderHistoryFragment : Fragment() {
    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var rlOrderHistory: RelativeLayout
    lateinit var rlOrder: RelativeLayout
    lateinit var rlMessage: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var sharedPreferences: SharedPreferences


    val orderList = arrayListOf<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        rlOrderHistory = view.findViewById(R.id.progressLayoutOrderHistory)
        rlOrder = view.findViewById(R.id.rlOrder)
        rlMessage =view.findViewById(R.id.rlMessage)
        progressBar = view.findViewById(R.id.progressBar)
        layoutManager = LinearLayoutManager(activity)
        sharedPreferences =  (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_login), Context.MODE_PRIVATE)

        rlOrderHistory.visibility = View.VISIBLE
        rlMessage.visibility = View.INVISIBLE
        rlOrder.visibility = View.INVISIBLE


        if (ConnectionManager().checkConnectivity(activity as Context)){
            rlOrderHistory.visibility = View.INVISIBLE


            val queue = Volley.newRequestQueue(activity as Context)

            val user_id = sharedPreferences.getString("user_id", "0")
            val url = "http://13.235.250.119/v2/orders/fetch_result/${user_id}"


            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener{

                try {
                    val jsonObject=it.getJSONObject("data")
                    val success = jsonObject.getBoolean("success")

                    if(success){
                        val data = jsonObject.getJSONArray("data")
                        if(data.length()>0)
                        {
                            rlOrder.visibility = View.VISIBLE


                            val gson = GsonBuilder().create()
                            val orderHistory = gson.fromJson(jsonObject.toString(), OrderHistory::class.java)


                            recyclerAdapter = OrderHistoryRecyclerAdapter(activity as Context,orderHistory.data)
                            recyclerOrderHistory.adapter = recyclerAdapter
                            recyclerOrderHistory.layoutManager = layoutManager

                        }
                        else{
                            rlMessage.visibility = View.VISIBLE
                        }
                    }
                }catch (e: JSONException){
                    Toast.makeText(activity as Context, "${e}", Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {
                Toast.makeText(activity as Context, "Some Volley error occurred!", Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") {text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }
    class OrderHistory(
        val data: List<Order>
    )

}