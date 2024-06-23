package com.example.gatewayrestaurant.Adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.OrderCartLayoutItemBinding
import com.example.gatewayrestaurant.databinding.OrderListLayoutItemBinding
import com.example.gatewayrestaurant.model.OrderListModel
import com.example.gatewayrestaurant.model.OrderModel
import com.example.gatewayrestaurant.model.model
import java.util.*
import kotlin.collections.ArrayList

class OrderCartItemAdapter(
    var mContext: Context,
    private val orderCartItemList: kotlin.collections.List<OrderListModel>

) :
    RecyclerView.Adapter<OrderCartItemAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: OrderCartLayoutItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.order_cart_layout_item,
                parent,
                false
            )

        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setIsRecyclable(false)
        val orderCartListItem = orderCartItemList[position]


        holder.mBinder.tvDishName.text = orderCartListItem.name
        holder.mBinder.tvQuantity.text = orderCartListItem.quantity
//        holder.mBinder.tvTotalDishPrice.text =  "\u20B9 " + model.price
        holder.mBinder.tvTotalDishPrice.text = "\u20B9 ${orderCartListItem.price}"

    }

    override fun getItemCount(): Int {
        return orderCartItemList.size
    }

    interface CallBack {
        fun onItemClicked(
            profileId: String,
        )
    }

    class MyViewHolder(var mBinder: OrderCartLayoutItemBinding) :
        RecyclerView.ViewHolder(mBinder.root)
}