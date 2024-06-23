package com.example.gatewayrestaurant.Adapter

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gatewayrestaurant.Activity.HotItemActivity
import com.example.gatewayrestaurant.Activity.LoginActivity
import com.example.gatewayrestaurant.Activity.OfferForYouActivity
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.Fragment.*
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Session.SessionManager
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.Utils.CommonSingleton
import com.example.gatewayrestaurant.databinding.SouthindiancardviewBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MenuAdapter(
    options: FirebaseRecyclerOptions<MenuModel?>, private val mContext: Context, var callback: CallBack
) :

    FirebaseRecyclerAdapter<MenuModel, MenuAdapter.MyViewHolder>(options) {

    val progressDialog = ProgressDialog(mContext)
    private var session: AppSettingsPref? = null

    val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private val mFirebaseDatabase = FirebaseDatabase.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var nextAvailableFrom: String = ""
    private var nextAvailableTo: String = ""


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: MenuModel) {

        holder.setIsRecyclable(false)
        session = AppSettingsPref(mContext)

        val sdf = SimpleDateFormat("hh:mm a")
        val currentTime = sdf.format(Date())

        val fromAvailable = model.availableFrom!!.trimStart('0')
        val toAvailable = model.availableTo!!.trimStart('0')

        if (!model.nextAvailableFrom.isNullOrEmpty()) {
            nextAvailableFrom = model.nextAvailableFrom.trimStart('0')
            nextAvailableTo = model.nextAvailableTo!!.trimStart('0')
        }

        if (model.nextAvailableFrom.isNullOrEmpty()) {
            try {
                val time1 = SimpleDateFormat("h:mm a").parse(fromAvailable)
                val time2 = SimpleDateFormat("h:mm a").parse(toAvailable)
                val d = SimpleDateFormat("h:mm a").parse(currentTime)
                if (time1.before(d) && time2.after(d)) {

                    holder.mBinder.tvTimeStatus.visibility = View.GONE
                    holder.mBinder.llAddPlusMinus.visibility = View.VISIBLE
                } else {

                    holder.mBinder.tvTimeStatus.visibility = View.VISIBLE
                    holder.mBinder.llAddPlusMinus.visibility = View.GONE

                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            try {
                val availableFromTime = SimpleDateFormat("h:mm a").parse(fromAvailable)
                val availableToTime = SimpleDateFormat("h:mm a").parse(toAvailable)
                val d = SimpleDateFormat("h:mm a").parse(currentTime)
                if (availableFromTime!!.before(d) && availableToTime!!.after(d)) {
                    holder.mBinder.tvTimeStatus.visibility = View.GONE
                    holder.mBinder.llAddPlusMinus.visibility = View.VISIBLE

                } else {
                    val nextAvailableFromTime = SimpleDateFormat("h:mm a").parse(nextAvailableFrom)
                    val nextAvailableToTime = SimpleDateFormat("h:mm a").parse(nextAvailableTo)
                    val diff = SimpleDateFormat("h:mm a").parse(currentTime)
                    if (nextAvailableFromTime.before(diff) && nextAvailableToTime.after(diff)) {
                        holder.mBinder.tvTimeStatus.visibility = View.GONE
                        holder.mBinder.llAddPlusMinus.visibility = View.VISIBLE
                    } else {
                        holder.mBinder.tvTimeStatus.visibility = View.VISIBLE
                        holder.mBinder.llAddPlusMinus.visibility = View.GONE
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }


        Glide.with(mContext).load(model.image).placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder).into(holder.mBinder.ivDish)

        holder.mBinder.tvDishName.text = model.name

        if (model.offerPrice.isNullOrEmpty()) {
            holder.mBinder.tvAmount.text = "\u20B9 ${model.price}"
        } else {
            holder.mBinder.tvOfferAmount.visibility = View.VISIBLE
            holder.mBinder.tvOfferAmount.text = "\u20B9 ${model.price}"
            holder.mBinder.tvAmount.text = "\u20B9 ${model.offerPrice}"
            holder.mBinder.tvAmount.setBackgroundResource(R.drawable.line)

        }


        if (user != null) {
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)
            val query = databaseReference.orderByChild("name").equalTo(model.name)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val quantity = postSnapshot.child("quantity").value as String?
                            val quantityInInt = Integer.valueOf(quantity)
                            if (quantityInInt <= 0) {
                                holder.mBinder.btnAddToCart.visibility = View.VISIBLE
                                holder.mBinder.llPlusMinus.visibility = View.GONE
                            } else {
                                holder.mBinder.btnAddToCart.visibility = View.GONE
                                holder.mBinder.llPlusMinus.visibility = View.VISIBLE
                                holder.mBinder.tvQty.text = quantity
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } else {
            holder.mBinder.btnAddToCart.visibility = View.VISIBLE
            holder.mBinder.llPlusMinus.visibility = View.GONE
        }

        holder.mBinder.btnAddToCart.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {

                // if user is logged in
                holder.mBinder.btnAddToCart.visibility = View.GONE
                holder.mBinder.llPlusMinus.visibility = View.VISIBLE
                holder.mBinder.tvQty.text = "1"
                addToCart(model, holder)
            } else {

                // if user is not logged in
                val warning = AlertDialog.Builder(mContext).setTitle("Login!")
                    .setMessage("Please login to add item in cart.")
                    .setPositiveButton("Login") { dialogInterface, i ->
                        mContext.startActivity(
                            Intent(mContext, LoginActivity::class.java)
                        )
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
                warning.show()
            }
        }

        holder.mBinder.ivPlus.setOnClickListener {

            progressDialog.setMessage("Updating..., Please wait!")
            progressDialog.show()

            var count = holder.mBinder.tvQty.text.toString().toInt()
            count++
            holder.mBinder.tvQty.text = "" + count
            Log.d("count", count.toString())
            val dishName = model.name
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)
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
                    val databaseRef = mFirebaseDatabase.reference.child("cartTotal")
                    val query1 = databaseRef.child(userUid)
                    query1.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val amountFromFirebase = snapshot.child("totalAmount").value.toString()
                            val totalAmount = amountFromFirebase.toInt() + model.price!!.toInt()
                            snapshot.ref.child("totalAmount").setValue(totalAmount)
                            getCartCount()
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                }

                override fun onCancelled(error: DatabaseError) {}
            })


        }

        holder.mBinder.ivMinus.setOnClickListener {
            progressDialog.setMessage("Updating..., Please wait!")
            progressDialog.show()

            var count = holder.mBinder.tvQty.text.toString().toInt()

            count -= 1
            holder.mBinder.tvQty.text = "" + count

            if (count < 1) {
                holder.mBinder.btnAddToCart.visibility = View.VISIBLE
                holder.mBinder.llPlusMinus.visibility = View.GONE
            }
            val dishName = model.name
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)
            val query = databaseReference.orderByChild("name").equalTo(dishName)
            Log.d("query", query.toString())
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val quantity = postSnapshot.child("quantity").value as String?
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
                    val databaseRef = mFirebaseDatabase.reference.child("cartTotal")
                    val query1 = databaseRef.child(userUid)
                    query1.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val amountFromFirebase = snapshot.child("totalAmount").value.toString()
                            val totalAmount = amountFromFirebase.toInt() - model.price!!.toInt()
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
        val mBinding: SouthindiancardviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.southindiancardview, parent, false
        )


        return MyViewHolder(mBinding)
    }


    private fun addToCart(model: MenuModel, holder: MyViewHolder) {

        progressDialog.setMessage("Updating..., Please wait!")
        progressDialog.show()
        val dishName = model.name
        val dishPrice = model.price
        val dishImage = model.image
        val dishQuantity = holder.mBinder.tvQty.text.toString()

        val map: MutableMap<String, Any> = HashMap()
        map["name"] = dishName!!
        map["price"] = dishPrice!!
        map["quantityprice"] = dishPrice
        map["image"] = dishImage
        map["quantity"] = dishQuantity
        mFirebaseDatabase.reference.child("cart").child(userUid!!).push().setValue(map)
            .addOnSuccessListener {
                val databaseRef = mFirebaseDatabase.reference.child("cartTotal")
                val query1 = databaseRef.child(userUid)
                query1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val amountFromFirebase = snapshot.child("totalAmount").value.toString()
                        val totalAmount = amountFromFirebase.toInt() + model.price.toInt()
                        snapshot.ref.child("totalAmount").setValue(totalAmount)
                        getCartCount()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }.addOnFailureListener {

            }
    }

    fun getCartCount() {
        session = AppSettingsPref(mContext)

        val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartCount = snapshot.childrenCount.toInt()
                (mContext as BaseActivity).setCartCount(cartCount)

//                AppSettingsPref.saveInt(mContext, AppSettingsPref.CART_COUNT, cartCount)
                callback.getCartCount(cartCount)
                Log.e("setCartCount", "${CommonSingleton.cartCount}")
                progressDialog.hide()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDataChanged() {
        super.onDataChanged()
        SouthIndianFragment.disableThem()
        MainCourseFragment.disableThem()
        ChineseFragment.disableThem()
        IndianStarterFragment.disableThem()
        ChineseStarterFragment.disableThem()
        RiceFragment.disableThem()
        PavBhajiFragment.disableThem()
        DrinksFragment.disableThem()
        RotiFragment.disableThem()
        HotItemActivity.disableThem()
        OfferForYouActivity.disableThem()
    }

    interface CallBack {
        fun getCartCount(cartCount: Int)
    }

    class MyViewHolder(var mBinder: SouthindiancardviewBinding) :
        RecyclerView.ViewHolder(mBinder.root)

}