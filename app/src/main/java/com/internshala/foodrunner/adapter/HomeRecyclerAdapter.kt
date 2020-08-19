package com.internshala.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activity.RestaurantDetailsActivity
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.fragment.HomeFragment
import com.internshala.foodrunner.model.Restaurant
import com.internshala.foodrunner.model.RestaurantDetail
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_home_single_row.view.*
import com.internshala.foodrunner.database.RestaurantEntity as RestaurantEntity

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName : TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtFoodRating)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        val imgFavouriteClicked: ImageView = view.findViewById(R.id.imgFavouriteClicked)
        val llContext: LinearLayout =view.findViewById(R.id.llContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)



        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        val restaurantId = restaurant.restaurantId
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantPrice.text = "₹ " + restaurant.restaurantPrice + "/person"
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.default_food)
            .into(holder.imgRestaurantImage)


        val restaurantEntity = RestaurantEntity(
            restaurantId.toInt(),
            restaurant.userId,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.restaurantPrice,
            restaurant.restaurantImage
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()
        if(isFav){
            holder.imgFavouriteClicked.visibility = View.VISIBLE
            holder.imgFavourite.visibility = View.INVISIBLE
        }
        else{
            holder.imgFavourite.visibility = View.VISIBLE
            holder.imgFavouriteClicked.visibility = View.INVISIBLE
        }


        holder.imgFavourite.setOnClickListener {
            holder.imgFavouriteClicked.visibility = View.VISIBLE
            holder.imgFavourite.visibility = View.INVISIBLE

            val restaurantEntity = RestaurantEntity(
                restaurantId.toInt(),
                restaurant.userId,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantPrice,
                restaurant.restaurantImage
            )
            if (!DBAsyncTask(context,restaurantEntity,1).execute().get()){
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if(result)
                {
                    Toast.makeText(context,"Added to Favourite Restaurants",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context,"Some error occurred!",Toast.LENGTH_SHORT).show()
                }
            }

        }
        holder.imgFavouriteClicked.setOnClickListener {
            holder.imgFavourite.visibility = View.VISIBLE
            holder.imgFavouriteClicked.visibility = View.INVISIBLE
            val restaurantEntity = RestaurantEntity(
                restaurantId.toInt(),
                restaurant.userId,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantPrice,
                restaurant.restaurantImage
            )
            if (DBAsyncTask(context,restaurantEntity,1).execute().get()){
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if(result)
                {
                    Toast.makeText(context,"Removed from Favourite Restaurants",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context,"Some error occurred!",Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.llContext.setOnClickListener {
            val intent = Intent(context,RestaurantDetailsActivity::class.java)
            intent.putExtra("id",restaurant.restaurantId)
            intent.putExtra("user_id",restaurant.userId)
            intent.putExtra("name",restaurant.restaurantName)
            intent.putExtra("rating",restaurant.restaurantRating)
            intent.putExtra("price",restaurant.restaurantPrice)
            intent.putExtra("image_url",restaurant.restaurantImage)
            context.startActivity(intent)
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

                2-> {
//                    Mode 2 -> Save the restaurant into DB as favourite
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3->{
//                    Remove the restaurant from favourite
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

            }

            return false
        }

    }
}