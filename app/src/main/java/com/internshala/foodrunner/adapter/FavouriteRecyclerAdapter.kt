package com.internshala.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activity.RestaurantDetailsActivity
import com.internshala.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso


class FavouriteRecyclerAdapter(val context: Context, val restaurantList: List<RestaurantEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtFavRestaurantName : TextView = view.findViewById(R.id.txtFavRestaurantName)
        val txtFavRestaurantPrice: TextView = view.findViewById(R.id.txtFavFoodPrice)
        val txtFavRestaurantRating: TextView = view.findViewById(R.id.txtFavFoodRating)
        val imgFavRestaurantImage: ImageView = view.findViewById(R.id.imgFavFoodImage)
        val llFavContent: LinearLayout =view.findViewById(R.id.llFavContent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favourite_single_row,parent,false)

        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        val restaurantId = restaurant.restaurant_id.toString()
        holder.txtFavRestaurantName.text = restaurant.restaurantName
        holder.txtFavRestaurantPrice.text = "₹ ${restaurant.restaurantPrice}/person"
        holder.txtFavRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.default_food)
            .into(holder.imgFavRestaurantImage)

        holder.llFavContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("id",restaurantId)
            intent.putExtra("user_id",restaurant.user_id)
            intent.putExtra("name",restaurant.restaurantName)
            intent.putExtra("rating",restaurant.restaurantRating)
            intent.putExtra("price",restaurant.restaurantPrice)
            intent.putExtra("image_url",restaurant.restaurantImage)
            context.startActivity(intent)
        }

    }
}