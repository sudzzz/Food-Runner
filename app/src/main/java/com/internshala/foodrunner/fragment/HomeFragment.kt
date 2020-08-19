package com.internshala.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodrunner.R
import com.internshala.foodrunner.adapter.HomeRecyclerAdapter
import com.internshala.foodrunner.model.Restaurant
import com.internshala.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var customDialog: CustomDialog
    lateinit var sharedPreferences: SharedPreferences

    var ratingComparator = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantRating.compareTo(res2.restaurantRating, true) == 0) {
            //sort according to name if rating is same
            res2.restaurantName.compareTo(res1.restaurantName, true)
        } else {
            res1.restaurantRating.compareTo(res2.restaurantRating, true)
        }
    }

    var costComparatorLowToHigh = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantPrice.compareTo(res2.restaurantPrice, true) == 0) {
            //sort according to name if price is same
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            res1.restaurantPrice.compareTo(res2.restaurantPrice, true)
        }
    }

    var costComparatorHighToLow = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantPrice.compareTo(res2.restaurantPrice, true) == 0) {
            //sort according to name if price is same
            res2.restaurantName.compareTo(res1.restaurantName, true)
        } else {
            res1.restaurantPrice.compareTo(res2.restaurantPrice, true)
        }
    }


    val restaurantInfoList = arrayListOf<Restaurant>()
    val displayRestaurantInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        customDialog = CustomDialog(activity as Context)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        sharedPreferences = (activity as FragmentActivity).getSharedPreferences(getString(R.string.preference_login),Context.MODE_PRIVATE)
        val user = sharedPreferences.getString("user_id","0").toString()


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context))
        {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener{

                // Here we will handle the response
                try {
                    val jsonObject=it.getJSONObject("data")
                    progressLayout.visibility = View.GONE
                    val success = jsonObject.getBoolean("success")

                    if (success){

                        val data = jsonObject.getJSONArray("data")
                        for (i in 0 until data.length()){
                            val restaurantJsonObject = data.getJSONObject(i)

                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id"),
                                user,
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )

                            displayRestaurantInfoList.add(restaurantObject)
                            restaurantInfoList.add(restaurantObject)



                            recyclerAdapter = HomeRecyclerAdapter(activity as Context, displayRestaurantInfoList)

                            recyclerHome.adapter = recyclerAdapter

                            recyclerHome.layoutManager = layoutManager

                        }

                    } else {
                        Toast.makeText(activity as Context, "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(activity as Context, "Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
                }


            },Response.ErrorListener{
                //Here we will handle the errors
                if (activity != null){
                    Toast.makeText(activity as Context, "Volley error occurred!", Toast.LENGTH_SHORT).show()
                }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
        }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.actionSort) {
            //open dialog box
            customDialog.show()
            customDialog.setOnDismissListener {
                when (customDialog.selectedOption) {
                    R.id.sortLowToHigh -> {
                        Collections.sort(displayRestaurantInfoList, costComparatorLowToHigh)
                    }
                    R.id.sortHighToLow -> {
                        Collections.sort(displayRestaurantInfoList, costComparatorHighToLow)
                        displayRestaurantInfoList.reverse()
                    }
                    R.id.sortRating -> {
                        Collections.sort(displayRestaurantInfoList, ratingComparator)
                        displayRestaurantInfoList.reverse()
                    }
                    else -> {
                        //Do nothing
                    }
                }
                recyclerAdapter.notifyDataSetChanged()
            }


        }

        if(id == R.id.searchBar){
            val searchView = item.actionView as SearchView
            searchView.queryHint = "Search Restaurants"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText!!.isNotEmpty()){
                        displayRestaurantInfoList.clear()

                        val search = newText.toLowerCase(Locale.getDefault())
                        restaurantInfoList.forEach {

                            if(it.restaurantName.toLowerCase(Locale.getDefault()).contains(search)){
                                displayRestaurantInfoList.add(it)
                            }
                        }
                        recyclerAdapter.notifyDataSetChanged()

                    }
                    else{
                        displayRestaurantInfoList.clear()
                        displayRestaurantInfoList.addAll(restaurantInfoList)
                        recyclerAdapter.notifyDataSetChanged()
                    }

                    return true
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }


    class CustomDialog(
        context: Context
    ) : Dialog(context), android.view.View.OnClickListener {

        var dialog: Dialog? = null
        var positiveButton: Button? = null
        var negativeButton: Button? = null
        var radioGroup: RadioGroup? = null

        var selectedOption: Int = -1

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.radio_button_options)
            positiveButton = findViewById(R.id.btnOK)
            negativeButton = findViewById(R.id.btnCancel)
            radioGroup = findViewById(R.id.group_Menu)
            positiveButton!!.setOnClickListener(this)
            negativeButton!!.setOnClickListener(this)

            //keep previously selectedOption checked
            if (selectedOption != -1) {
                radioGroup!!.check(selectedOption)
            }
        }


        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.btnCancel -> {
                    dismiss()
                }
                R.id.btnOK -> {
                    when (radioGroup!!.checkedRadioButtonId) {
                        R.id.sortLowToHigh -> {
                            selectedOption = R.id.sortLowToHigh
                        }
                        R.id.sortHighToLow -> {
                            selectedOption = R.id.sortHighToLow
                        }
                        R.id.sortRating -> {
                            selectedOption = R.id.sortRating
                        }
                    }
                    dismiss()
                }

            }
        }
    }
}