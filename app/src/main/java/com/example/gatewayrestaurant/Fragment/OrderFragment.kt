package com.example.gatewayrestaurant.Fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.LoginActivity
import com.example.gatewayrestaurant.Adapter.OrderDetailAdapter
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.FragmentOrderBinding
import com.example.gatewayrestaurant.model.OrderListModel
import com.example.gatewayrestaurant.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderFragment : BaseFragment() {

    private lateinit var mBinding: FragmentOrderBinding
    var userUid = ""
    var orderList: ArrayList<OrderModel> = java.util.ArrayList()
    private var orderDetailAdapter: OrderDetailAdapter? = null
    private val user = FirebaseAuth.getInstance().currentUser


    private var callBack: OrderDetailAdapter.CallBack = object : OrderDetailAdapter.CallBack {
        override fun feedbackDialogSetup(orderNo: String) {
            val dialogFeedback = AlertDialog.Builder(requireContext())
            val layoutInflater = layoutInflater
            val customView = layoutInflater.inflate(R.layout.feedback_dialog, null)
            val comment = customView.findViewById<EditText>(R.id.comment)

            val rating = customView.findViewById<RatingBar>(R.id.rbRating)
            val send = customView.findViewById<Button>(R.id.send)
            val cancel = customView.findViewById<Button>(R.id.cancel)
            val feeback_sub_label = customView.findViewById<TextView>(R.id.feeback_sub_label)
            feeback_sub_label.setTextColor(
                ContextCompat.getColor(
                    mActivity, R.color.black
                )
            )
            dialogFeedback.setCancelable(false)
            dialogFeedback.setView(customView)
            val alertDialog = dialogFeedback.create()

            if (alertDialog.window != null) {
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            send.setOnClickListener {
                if (rating.rating.toDouble() == 0.0) {
                    showShortToast("Please")
                    return@setOnClickListener
                } else {
                    val query =
                        FirebaseDatabase.getInstance().reference.child("Order").child(orderNo)

                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            snapshot.ref.child("rating").setValue(rating.rating.toString())
                            snapshot.ref.child("review").setValue(comment.text.toString())
                            alertDialog.dismiss()
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                }
            }
            cancel.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)


        if (user != null) {
            mBinding.shimmerMenu.visibility = View.VISIBLE
            mBinding.rvOrderList.visibility = View.GONE
            setUpOrderList()
            userUid = FirebaseAuth.getInstance().currentUser!!.uid
        } else {
            mBinding.shimmerMenu.visibility = View.GONE
            mBinding.rvOrderList.visibility = View.GONE
            mBinding.noLogIn.root.visibility = View.VISIBLE
            mBinding.clParent.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))

        }
        setUpToolbar()
        mBinding.noLogIn.btnLogIn.setOnClickListener {
            startActivity(Intent(mActivity, LoginActivity::class.java))
        }

        return mBinding.root
    }

    private fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = "Order History"
        mBinding.toolbar.ivBack.visibility = View.GONE
        mBinding.toolbar.shoppingCart.visibility = View.GONE

    }

    private fun setRatingReview(orderNumber: String) {
        Log.e("orderNumber", orderNumber)


    }

    private fun setUpOrderList() {
        val query = FirebaseDatabase.getInstance().reference.child("Order")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val orderMenuList: ArrayList<OrderListModel> = java.util.ArrayList()

                    if (postSnapshot.child("uid").value.toString() == userUid) {

                        val order = postSnapshot.child("Order").value as HashMap<String, Any?>

                        for (i in order) {
                            val individualOrder = i.value as HashMap<String, Any?>
                            orderMenuList.add(
                                OrderListModel(
                                    individualOrder.get("name").toString(),
                                    individualOrder.get("price").toString(),
                                    individualOrder.get("quantity").toString(),
                                    individualOrder.get("quantityprice").toString(),
                                    individualOrder.get("image").toString()
                                )
                            )
                        }

                        orderList.add(
                            OrderModel(
                                randomUid = postSnapshot.child("randomUid").value.toString(),
                                deliverytype = postSnapshot.child("deliverytype").value.toString(),
                                instruction = postSnapshot.child("instruction").value.toString(),
                                cancellationReason = postSnapshot.child("cancellationReason").value.toString(),
                                timestamp = postSnapshot.child("timestamp").value.toString(),
                                uid = postSnapshot.child("uid").value.toString(),
                                confirmation = postSnapshot.child("confirmation").value.toString(),
                                totalAmount = postSnapshot.child("totalAmount").value.toString(),
                                rating = postSnapshot.child("rating").value.toString(),
                                review = postSnapshot.child("review").value.toString(),
                                Order = orderMenuList
                            )
                        )

                    }
                }

                Log.e("orderList", orderList.toString())

                if (orderList.isEmpty()) {
                    mBinding.shimmerMenu.visibility = View.GONE
                    mBinding.rvOrderList.visibility = View.GONE
                    mBinding.ivNoOrder.visibility = View.VISIBLE
                    mBinding.tvNoOrder.visibility = View.VISIBLE
                } else {
                    mBinding.shimmerMenu.visibility = View.GONE
                    mBinding.rvOrderList.visibility = View.VISIBLE

                    mBinding.rvOrderList.layoutManager = LinearLayoutManager(mActivity)
                    mBinding.rvOrderList.isNestedScrollingEnabled = false


//                    val orders = orderList.sortedByDescending { it.timestamp }.toCollection(ArrayList())
                    val orders = orderList
                        .distinctBy { it.randomUid } // Remove duplicates based on orderId
                        .sortedByDescending { it.timestamp } // Sort by timestamp in descending order
                        .toCollection(ArrayList())

                    orderDetailAdapter = OrderDetailAdapter(mActivity, orders, callBack)
                    mBinding.rvOrderList.adapter = orderDetailAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }


}