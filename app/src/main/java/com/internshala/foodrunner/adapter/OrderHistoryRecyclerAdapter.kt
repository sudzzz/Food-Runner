package com.internshala.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.model.FoodItem
import com.internshala.foodrunner.model.Order

class OrderHistoryRecyclerAdapter(val context: Context, val orderHistory: List<Order>) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryRecyclerViewHolder>() {

    class OrderHistoryRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        val llFoodItems: LinearLayout = view.findViewById(R.id.llFoodItems)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_single_row,parent,false)

        return OrderHistoryRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistory.size
    }

    override fun onBindViewHolder(holder: OrderHistoryRecyclerViewHolder, position: Int) {



        holder.setIsRecyclable(false)

        val order = orderHistory[position]

        holder.txtRestaurantName.text = order.restaurant_name

        val date = order.order_placed_at.subSequence(0, 8) as String
        val myDate = date.replace("-", "/")
        holder.txtOrderDate.text = myDate

        for (i in 0 until order.food_items.size){
            val inflater: LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

            val llSingleItem = inflater?.inflate(R.layout.recycler_cart_single_row, null) as LinearLayout

            val txtNumber: TextView = llSingleItem.findViewById(R.id.txtNumber)
            val txtFoodName: TextView = llSingleItem.findViewById(R.id.txtFoodName)
            val txtPrice: TextView = llSingleItem.findViewById(R.id.txtPrice)

            txtNumber.text = (i+1).toString()
            txtFoodName.text = order.food_items[i].name
            txtPrice.text = "₹ ${order.food_items[i].cost}"

            holder.llFoodItems.addView(llSingleItem)
        }

    }
}