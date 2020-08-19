package com.internshala.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.database.OrderEntity

class CartRecyclerAdapter(val context: Context, val orderList: List<OrderEntity>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtNumber: TextView = view.findViewById(R.id.txtNumber)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)

        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
       return orderList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orderList[position]
        val pos = (position+1).toString()
        holder.txtNumber.text = pos
        holder.txtFoodName.text = order.foodName
        holder.txtPrice.text = "₹ "+order.foodCost
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}