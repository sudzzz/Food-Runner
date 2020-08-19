package com.internshala.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurant WHERE user_id = :userId")
    fun getAllRestaurant(userId: String): List<RestaurantEntity>

    @Query("SELECT * FROM restaurant WHERE res_id = :restaurantId AND user_id = :userId")
    fun getRestaurantById(restaurantId: String, userId: String): RestaurantEntity
}