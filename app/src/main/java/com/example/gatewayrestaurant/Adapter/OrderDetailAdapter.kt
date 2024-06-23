package com.example.gatewayrestaurant.Adapter

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.OrderListLayoutItemBinding
import com.example.gatewayrestaurant.model.OrderModel
import java.util.*

class OrderDetailAdapter(
    var mContext: Context,
    private val orderDetailsList: ArrayList<OrderModel>,
    var callback: CallBack

) : RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder>() {
    var orderCartItemAdapter: OrderCartItemAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: OrderListLayoutItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.order_list_layout_item, parent, false
        )

        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setIsRecyclable(false)
        val orderDetailsItem = orderDetailsList[position]

        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = orderDetailsItem.timestamp!!.toLong()
        val time = DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString()
        holder.mBinder.tvOrderDateTime.text = time

        holder.mBinder.tvOrderId.text = "Order : ${orderDetailsItem.randomUid}"
        holder.mBinder.tvOrderTotal.text = "₹ ${orderDetailsItem.totalAmount}/-"


        if (orderDetailsItem.confirmation.isNullOrEmpty()){
            holder.mBinder.btnOrderStatus.text = "Pending"
            holder.mBinder.btnOrderStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext,R.drawable.ic_pending_icon), null)

        }else if(orderDetailsItem.confirmation.lowercase().contains("order accepted")){
            holder.mBinder.btnOrderStatus.text = "Confirmed"
            holder.mBinder.btnOrderStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext,R.drawable.ic_correct), null)
        }else if(orderDetailsItem.confirmation.lowercase().contains("order cancelled")){
            holder.mBinder.btnOrderStatus.text = "Cancelled"
            holder.mBinder.btnOrderStatus.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext,R.drawable.ic_vector_cancel_order), null)
        }

        if (orderDetailsItem.rating.isNullOrEmpty()) {
            holder.mBinder.clRateOrder.visibility = View.VISIBLE
            holder.mBinder.viewRating.visibility = View.VISIBLE
        } else {
            holder.mBinder.clRateOrder.visibility = View.GONE
            holder.mBinder.viewRating.visibility = View.GONE
        }

        holder.mBinder.clRateOrder.setOnClickListener {
            orderDetailsItem.randomUid?.let { it1 -> callback.feedbackDialogSetup(it1) }
        }

        if (orderDetailsItem.deliverytype == "homeDelivery"){
            holder.mBinder.tvDeliveryOption.text = mContext.getString(R.string.home_delivery)
        }else{
            holder.mBinder.tvDeliveryOption.text = mContext.getString(R.string.takeaway)
        }
        holder.mBinder.rvOrderItem.layoutManager = LinearLayoutManager(mContext)
        orderCartItemAdapter = OrderCartItemAdapter(
            mContext, orderDetailsItem.Order!!
        )
        holder.mBinder.rvOrderItem.adapter = orderCartItemAdapter

    }

    override fun getItemCount(): Int {
        return orderDetailsList.size
    }


    interface CallBack {
        fun feedbackDialogSetup(orderNo: String)
    }

    class MyViewHolder(var mBinder: OrderListLayoutItemBinding) :
        RecyclerView.ViewHolder(mBinder.root)
}