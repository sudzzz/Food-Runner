package com.internshala.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant", primaryKeys = ["res_id","user_id"])
data class RestaurantEntity(
    @ColumnInfo(name = "res_id") val restaurant_id: Int,
    @ColumnInfo(name = "user_id")val user_id: String,
    @ColumnInfo(name = "restaurant_name")val restaurantName: String,
    @ColumnInfo(name = "restaurant_rating")val restaurantRating: String,
    @ColumnInfo(name = "restaurant_price")val restaurantPrice: String,
    @ColumnInfo(name = "restaurant_image")val restaurantImage: String
)