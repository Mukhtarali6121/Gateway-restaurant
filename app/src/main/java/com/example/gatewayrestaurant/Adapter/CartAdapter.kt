package com.example.gatewayrestaurant.Adapter

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gatewayrestaurant.Activity.HomePageActivity
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.databinding.LayoutCartItemBinding
import com.example.gatewayrestaurant.model.Cartmodel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    options1: FirebaseRecyclerOptions<Cartmodel?>?,
    private val mContext: Context,
    var itemCountCallback: ItemCountCallBack
) :

    FirebaseRecyclerAdapter<Cartmodel, CartAdapter.MyViewHolder>(
        options1!!
    ) {
    val progressDialog = ProgressDialog(mContext)


    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Cartmodel) {

        progressDialog.setMessage("Updating..., Please wait!")

        Glide.with(mContext).load(model.image).placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder).into(holder.mBinder.ivItem)

//        Glide.with(holder.mBinder.ivItem.context).load(model.image).into(holder.mBinder.ivItem)
        holder.mBinder.tvItemName.text = model.name
        holder.mBinder.tvIRealPrice.text = "\u20B9 " + model.price.toString()
        holder.mBinder.tvIQuantityPrice.text = "\u20B9 " + model.quantityprice.toString()
        holder.mBinder.tvQty.text = model.quantity



        holder.mBinder.ivPlus1.setOnClickListener {
            progressDialog.show()

            var count = holder.mBinder.tvQty.text.toString().toInt()
            count++
            holder.mBinder.tvQty.text = "" + count
            Log.d("count", count.toString())

            val dishName = model.name
            val mFirebaseDatabase = FirebaseDatabase.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(uid)
            val query = databaseReference.orderByChild("name").equalTo(dishName)

            Log.d("query", query.toString())

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val quantity = postSnapshot.child("quantity").value as String?
                        val quantityprice = postSnapshot.child("quantityprice").value as String?
                        val price = postSnapshot.child("price").value as String?

                        //increase quantity
                        val quantityCount = holder.mBinder.tvQty.text as String
                        Log.d("quantitycount", quantityCount)
                        postSnapshot.ref.child("quantity").setValue(quantityCount)

                        //increase price according to quantity
                        val countPrice = (price!!.toInt() * quantityCount.toInt()).toString()
                        Log.d("countPrice", countPrice)
                        postSnapshot.ref.child("quantityprice").setValue(countPrice)
                    }
                    val databaseRef = FirebaseDatabase.getInstance().reference.child("cartTotal")
                    val query1 = databaseRef.child(uid)
                    query1.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val amountFromFirebase = snapshot.child("totalAmount").value.toString()
                            val totalAmount = amountFromFirebase.toInt() + model.price.toInt()
                            snapshot.ref.child("totalAmount").setValue(totalAmount)
                            getCartCount()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                override fun onCancelled(error: DatabaseError) {}
            })


        }



        holder.mBinder.ivMinus1.setOnClickListener {
            var count = holder.mBinder.tvQty.text.toString().toInt()

            count -= 1

            holder.mBinder.tvQty.text = "" + count


            Log.d("count", count.toString())
            val dishName = model.name
            val mFirebaseDatabase = FirebaseDatabase.getInstance()
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(uid)
            val query = databaseReference.orderByChild("name").equalTo(dishName)
            Log.d("query", query.toString())
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val quantity = postSnapshot.child("quantity").value as String?
                        val quantityprice = postSnapshot.child("quantityprice").value as String?
                        val price = postSnapshot.child("price").value as String?


                        //remove item from cart when it becomes 0
                        val quantityCount = holder.mBinder.tvQty.text as String

                        if (quantityCount.toInt() < 1) {
                            postSnapshot.ref.removeValue()
                            notifyDataSetChanged()
                        } else {

                            //increase quantity
                            Log.d("quantitycount", quantityCount)
                            postSnapshot.ref.child("quantity").setValue(quantityCount)

                            //increase price according to quantity
                            val countPrice = (price!!.toInt() * quantityCount.toInt()).toString()
                            Log.d("countPrice", countPrice)
                            postSnapshot.ref.child("quantityprice").setValue(countPrice)


                        }
                    }
                    val databaseRef = FirebaseDatabase.getInstance().reference.child("cartTotal")
                    val query1 = databaseRef.child(uid)
                    query1.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val amountFromFirebase = snapshot.child("totalAmount").value.toString()
                            val totalAmount = amountFromFirebase.toInt() - model.price.toInt()
                            snapshot.ref.child("totalAmount").setValue(totalAmount)
                            getCartCount()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val mBinding: LayoutCartItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.layout_cart_item, parent, false
        )
        return MyViewHolder(mBinding)

    }

    fun getCartCount() {
        val mFirebaseDatabase = FirebaseDatabase.getInstance()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = mFirebaseDatabase.reference.child("cart").child(uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartCount = snapshot.childrenCount.toInt()
                (mContext as BaseActivity).setCartCount(cartCount)

//                AppSettingsPref.saveInt(mContext, AppSettingsPref.CART_COUNT, cartCount)
                Log.e("setCartCount", "$cartCount")
                (mContext as HomePageActivity).refreshBadge()
                progressDialog.hide()


            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDataChanged() {
        itemCount
        super.onDataChanged()
        itemCountCallback.itemCount(itemCount)
    }

    interface ItemCountCallBack {
        fun itemCount(itemCount: Int)
    }

    class MyViewHolder(var mBinder: LayoutCartItemBinding) : RecyclerView.ViewHolder(mBinder.root)
}