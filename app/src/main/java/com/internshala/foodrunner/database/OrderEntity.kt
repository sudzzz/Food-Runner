package com.internshala.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orderList")
data class OrderEntity (
    @PrimaryKey val foodId: Int,
    @ColumnInfo(name = "food_name") val foodName: String,
    @ColumnInfo(name = "food_cost") val foodCost: String,
    @ColumnInfo(name = "restaurant_id") val restaurantId: String
)