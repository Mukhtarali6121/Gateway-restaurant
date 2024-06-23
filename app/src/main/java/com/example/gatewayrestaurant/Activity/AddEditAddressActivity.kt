package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Adapter.AddressManager
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityAddEditAddressBinding
import com.example.gatewayrestaurant.model.Address
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddEditAddressActivity : BaseActivity() {
    private lateinit var mBinding: ActivityAddEditAddressBinding
    private var isFromProfile: Boolean = false
    private var addressAdapter: AddressManager? = null
    private val user = FirebaseAuth.getInstance().currentUser


    private var itemCountCallBack: AddressManager.AddressSelectedCallBack =
        object : AddressManager.AddressSelectedCallBack {
            override fun addressSelected() {

            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_address)
        supportActionBar!!.hide()

        if (intent.hasExtra("isFromProfile")) {
            isFromProfile = true
        }
        mBinding.toolbar.tvHeader.text = getString(R.string.address_book)
        mBinding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.noLogIn.btnLogIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        mBinding.toolbar.shoppingCart.visibility = View.GONE
        mBinding.btnAddAddress.setOnClickListener {
            val intent = Intent(
                this@AddEditAddressActivity,
                AddressActivity::class.java
            )
            startActivity(intent)
        }
        mBinding.rvAddressList.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        if (user != null) {
            mBinding.shimmerAddress.startShimmer()
            mBinding.shimmerAddress.visibility = View.VISIBLE
            mBinding.noLogIn.root.visibility = View.GONE
            mBinding.rvAddressList.visibility = View.GONE
            getAddressList()
        } else {
            mBinding.noLogIn.root.visibility = View.VISIBLE
            mBinding.btnAddAddress.visibility = View.GONE
            mBinding.rvAddressList.visibility = View.GONE
            mBinding.tvNoAddress.visibility = View.GONE
            mBinding.ivNoAddress.visibility = View.GONE
            mBinding.shimmerAddress.visibility = View.GONE
            mBinding.shimmerAddress.stopShimmer()
        }
    }

    /*private fun getData() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val options1 =
            FirebaseRecyclerOptions.Builder<com.example.gatewayrestaurant.model.Address>()
                .setQuery(
                    FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        .child("Address"), com.example.gatewayrestaurant.model.Address::class.java
                )
                .build()
            addressAdapter = AddressManager(options1, this@AddEditAddressActivity,isFromProfile,false,itemCountCallBack)
        mBinding.rvAddressList.adapter = addressAdapter
    }*/

    private fun getAddressList() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val addressList = ArrayList<Address>()

        FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Address")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    addressList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val address = snapshot.getValue(Address::class.java)
                        address?.let { addressList.add(it) }
                    }

                    /*mBinding.noLogIn.root.visibility = View.VISIBLE
                    mBinding.btnAddAddress.visibility = View.GONE
                    mBinding.rvAddressList.visibility = View.GONE
                    mBinding.tvNoAddress.visibility = View.GONE
                    mBinding.ivNoAddress.visibility = View.GONE
                    mBinding.shimmerAddress.visibility = View.GONE
                    mBinding.shimmerAddress.stopShimmer()
*/
                    if (addressList.isNotEmpty()){

                        mBinding.rvAddressList.adapter = addressAdapter
                        mBinding.shimmerAddress.stopShimmer()
                        mBinding.shimmerAddress.visibility = View.GONE
                        mBinding.ivNoAddress.visibility = View.GONE
                        mBinding.tvNoAddress.visibility = View.GONE
                        mBinding.rvAddressList.visibility = View.VISIBLE

                    } else {
                        // Handle scenario where data doesn't exist
                        mBinding.ivNoAddress.visibility = View.VISIBLE
                        mBinding.tvNoAddress.visibility = View.VISIBLE
                        mBinding.rvAddressList.visibility = View.GONE
                        mBinding.shimmerAddress.stopShimmer()
                        mBinding.shimmerAddress.visibility = View.GONE

                    }


                    addressAdapter = AddressManager(addressList, this@AddEditAddressActivity,isFromProfile, false, itemCountCallBack)
                    mBinding.rvAddressList.adapter = addressAdapter
                    Log.e("hello",addressList.toString())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors.
                    mBinding.ivNoAddress.visibility = View.VISIBLE
                    mBinding.tvNoAddress.visibility = View.VISIBLE
                    mBinding.rvAddressList.visibility = View.GONE

                }
            })
    }

  /*  override fun onStart() {

        super.onStart()
    }*/

}