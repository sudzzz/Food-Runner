package com.internshala.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
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
import androidx.room.Room
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.FavouriteRecyclerAdapter
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.database.RestaurantEntity
import com.internshala.foodrunner.util.ConnectionManager


class FavouriteRestaurantsFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var rlMessage: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences
    var dbRestaurantList = listOf<RestaurantEntity>()
    var user_id:String = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restraunts, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        rlMessage = view.findViewById(R.id.rlMessage)
        layoutManager = LinearLayoutManager(activity)
        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_login),Context.MODE_PRIVATE)
        user_id = sharedPreferences.getString("user_id","John").toString()

        dbRestaurantList = RetrieveFavourites(activity as Context,user_id).execute().get()

        showFavourites()

        return view
    }

    fun showFavourites(){
        if (ConnectionManager().checkConnectivity(activity as Context)){


            if(dbRestaurantList.isNotEmpty()){
                if (activity != null) {
                    progressLayout.visibility = View.GONE
                    rlMessage.visibility = View.INVISIBLE
                    recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbRestaurantList)
                    recyclerFavourite.adapter = recyclerAdapter
                    recyclerFavourite.layoutManager = layoutManager
                }
                else{
                    Toast.makeText(activity as Context, "Some error occurred!", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                progressLayout.visibility = View.GONE
                rlMessage.visibility = View.VISIBLE
            }
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
    }

    class RetrieveFavourites(val context: Context,val user: String) : AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

            return db.restaurantDao().getAllRestaurant(user)
        }

    }

}