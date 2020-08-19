package com.internshala.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert
    fun insertOrder(orderEntity: OrderEntity)

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)

    @Query("SELECT * FROM orderList")
    fun getAllOrders(): List<OrderEntity>

    @Query("DELETE FROM orderList")
    fun deleteOrders()

    @Query("SELECT * FROM orderList WHERE foodId = :foodId ")
    fun getOrderById(foodId: String): OrderEntity
}

