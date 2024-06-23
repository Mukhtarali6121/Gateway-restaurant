package com.example.gatewayrestaurant.Adapter

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
import com.example.gatewayrestaurant.Activity.HomePageActivity
import com.example.gatewayrestaurant.Activity.LoginActivity
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.databinding.LayoutMenuItemBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class OfferMenuAdapter(
    var mContext: Context,
    private val offerMenuList: ArrayList<MenuModel>,
) : RecyclerView.Adapter<OfferMenuAdapter.MyViewHolder>() {
    val progressDialog = ProgressDialog(mContext)

    val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private val mFirebaseDatabase = FirebaseDatabase.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var nextAvailableFrom: String = ""
    private var nextAvailableTo: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: LayoutMenuItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.layout_menu_item, parent, false
        )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        val offerMenuList = offerMenuList[position]


        if (user != null) {
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)
            val query = databaseReference.orderByChild("name").equalTo(offerMenuList.name)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val quantity = postSnapshot.child("quantity").value as String?
                            holder.mBinder.tvQty.text = quantity
                        }
                        holder.mBinder.btnAddToCart.visibility = View.GONE
                        holder.mBinder.llPlusMinus.visibility = View.VISIBLE

                    } else {
                        holder.mBinder.btnAddToCart.visibility = View.VISIBLE
                        holder.mBinder.llPlusMinus.visibility = View.GONE

                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        val sdf = SimpleDateFormat("hh:mm a")
        val currentTime = sdf.format(Date())

        val fromAvailable = offerMenuList.availableFrom!!.trimStart('0')
        val toAvailable = offerMenuList.availableTo!!.trimStart('0')

        if (!offerMenuList.nextAvailableFrom.isNullOrEmpty()) {
            nextAvailableFrom = offerMenuList.nextAvailableFrom.trimStart('0')
            nextAvailableTo = offerMenuList.nextAvailableTo!!.trimStart('0')

        }


        if (offerMenuList.nextAvailableFrom.isNullOrEmpty()) {
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

        if (offerMenuList.offerPrice.isNullOrEmpty()) {
            holder.mBinder.tvAmount.text = "\u20B9 ${offerMenuList.price}"
        } else {
            holder.mBinder.tvOfferAmount.visibility = View.VISIBLE
            holder.mBinder.tvOfferAmount.text = "\u20B9 ${offerMenuList.price}"
            holder.mBinder.tvAmount.text = "\u20B9 ${offerMenuList.offerPrice}"
            holder.mBinder.tvAmount.setBackgroundResource(R.drawable.line)
        }

        holder.mBinder.tvDishName.text = offerMenuList.name
        Glide.with(mContext).load(offerMenuList.image).placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_placeholder).into(holder.mBinder.ivDish)

        if (user != null) {
            val dishName = offerMenuList.name
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)

            val query = databaseReference.orderByChild("name").equalTo(dishName)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val quantity = postSnapshot.child("quantity").value as String?
                            val quantityInInt = Integer.valueOf(quantity)
                            if (quantityInInt <= 0) {
                                holder.mBinder.btnAddToCart.visibility = View.VISIBLE
                                holder.mBinder.llPlusMinus.visibility = View.GONE
                                postSnapshot.ref.removeValue()
                                notifyDataSetChanged()
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

                progressDialog.setMessage("Updating..., Please wait!")
                progressDialog.show()

                val dishName = offerMenuList.name
                val dishPrice = offerMenuList.price
                val dishImage = offerMenuList.image
                val dishQuantity = holder.mBinder.tvQty.text.toString()


                val map: MutableMap<String, Any> = HashMap()
                map["name"] = dishName.toString()
                map["price"] = dishPrice.toString()
                map["quantityprice"] = dishPrice.toString()
                map["image"] = dishImage
                map["quantity"] = dishQuantity
                mFirebaseDatabase.reference.child("cart").child(userUid!!).push().setValue(map)
                    .addOnSuccessListener {
                        val databaseRef = mFirebaseDatabase.reference.child("cartTotal")
                        val query1 = databaseRef.child(userUid)
                        query1.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val amountFromFirebase =
                                    snapshot.child("totalAmount").value.toString()
                                val totalAmount =
                                    amountFromFirebase.toInt() + offerMenuList.price!!.toInt()
                                snapshot.ref.child("totalAmount").setValue(totalAmount)
                                getCartCount()

                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }.addOnFailureListener {

                    }

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
            val dishName = offerMenuList.name
            val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)
            val query = databaseReference.orderByChild("name").equalTo(dishName)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val quantity = postSnapshot.child("quantity").value as String?
                        val quantityprice = postSnapshot.child("quantityprice").value as String?
                        val price = postSnapshot.child("price").value as String?

                        Log.e("quantityyyyy", quantity!!)
                        Log.e("priceeee", price!!)


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
                            val totalAmount = amountFromFirebase.toInt() + offerMenuList.price!!.toInt()
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

            Log.d("count", count.toString())
            if (count < 1) {
                holder.mBinder.btnAddToCart.visibility = View.VISIBLE
                holder.mBinder.llPlusMinus.visibility = View.GONE
            }
            val dishName = offerMenuList.name
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
                            val totalAmount = amountFromFirebase.toInt() - offerMenuList.price!!.toInt()
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

    fun getCartCount() {
        val databaseReference = mFirebaseDatabase.reference.child("cart").child(userUid!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartCount = snapshot.childrenCount.toInt()
                (mContext as BaseActivity).setCartCount(cartCount)

//                AppSettingsPref.saveInt(mContext, AppSettingsPref.CART_COUNT, cartCount)
                Log.e("setCartCount","$cartCount")
                (mContext as HomePageActivity).refreshBadge()
                progressDialog.hide()


            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    override fun getItemCount(): Int {
        return offerMenuList.size
    }


    class MyViewHolder(var mBinder: LayoutMenuItemBinding) : RecyclerView.ViewHolder(mBinder.root)
}