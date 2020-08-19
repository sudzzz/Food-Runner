package com.internshala.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodrunner.R
import com.internshala.foodrunner.activity.RestaurantDetailsActivity
import com.internshala.foodrunner.database.OrderDatabase
import com.internshala.foodrunner.database.OrderEntity
import com.internshala.foodrunner.database.RestaurantDatabase
import com.internshala.foodrunner.model.RestaurantDetail
import kotlinx.android.synthetic.main.recycler_detail_single_row.view.*

class DetailRecyclerAdapter(val context: Context, val itemList: ArrayList<RestaurantDetail>):RecyclerView.Adapter<DetailRecyclerAdapter.DetailViewHolder>() {

    companion object {
        var isCartEmpty = true
    }
    class DetailViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtNumber: TextView = view.findViewById(R.id.txtNumber)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodCost: TextView = view.findViewById(R.id.txtFoodCost)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove: Button = view.findViewById(R.id.btnRemove)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_detail_single_row,parent,false)

        return DetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }



    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val menu = itemList[position]

        holder.txtFoodName.text = menu.foodName
        holder.txtFoodCost.text = "₹ "+ menu.foodCost

        val pos = (position+1).toString()
        holder.txtNumber.text = pos

        val orderEntity = OrderEntity(
            menu.foodId.toInt(),
            menu.foodName,
            menu.foodCost,
            menu.RestaurantId
        )
        
        holder.btnAdd.setOnClickListener {

            holder.btnAdd.visibility = View.INVISIBLE
            holder.btnRemove.visibility = View.VISIBLE
            //add in cart
            val async = DBAsyncTask(holder.btnAdd.context, orderEntity, 1).execute()
            val result = async.get()
            if(!result){
                Toast.makeText(context,
                    "Some Error Occurred",
                    Toast.LENGTH_SHORT).show()
            }


            if(context is RestaurantDetailsActivity){
                val myActivity = context
                val check = DBAsyncTask(holder.btnAdd.context,orderEntity,4).execute().get()
                if(check){
                    myActivity.showCart()
                }

            }
        }
        holder.btnRemove.setOnClickListener {

            holder.btnAdd.visibility = View.VISIBLE
            holder.btnRemove.visibility = View.INVISIBLE

            //add in cart
            val async = DBAsyncTask(holder.btnRemove.context, orderEntity, 2).execute()
            val result = async.get()
            if (!result) {
                Toast.makeText(
                    context,
                    "Some Error Occurred",
                    Toast.LENGTH_SHORT
                ).show()
            }


            if (context is RestaurantDetailsActivity) {
                val myActivity = context
                val check = DBAsyncTask(holder.btnRemove.context, orderEntity, 4).execute().get()
                if (!check) {
                    myActivity.showNoCart()
                }
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    class DBAsyncTask(val context: Context, val orderEntity: OrderEntity, val mode: Int): AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg p0: Void?): Boolean {

            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()

                /*
            Mode1->Check DB if order is in cart or not
            Mode2->Add the order into cart
            Mode3->Remove the order from cart
            Mode4->Check size of the list
             */

            when(mode){

                1 -> {
//                    Add order to cart
                    db.orderDao().insertOrder(orderEntity)
                    db.close()
                    return true
                }
                2 -> {
//                    Remove the order from cart
                    db.orderDao().deleteOrder(orderEntity)
                    db.close()
                    return true
                }

                3->{
                    val item: OrderEntity? = db.orderDao().getOrderById(orderEntity.foodId.toString())
                    db.close()
                    return item != null
                }

                4 -> {
//                    Get all orders and check if the length of list is 0 or not
                    val cartItems = db.orderDao().getAllOrders()
                    db.close()
                    return cartItems.size > 0
                }
            }
            return false
        }

    }


}