package com.internshala.foodrunner.model

data class Order(
    val order_id: String,
    val restaurant_name: String,
    val total_cost: String,
    val order_placed_at: String,
    val food_items: List<FoodItem>
)