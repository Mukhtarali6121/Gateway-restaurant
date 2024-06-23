package com.example.gatewayrestaurant.Activity

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.Dialogs.NoInternetBottomSheet
import com.example.gatewayrestaurant.Dialogs.SelectAddressBottomSheetFragment
import com.example.gatewayrestaurant.Fragment.CartFragment
import com.example.gatewayrestaurant.Fragment.HomeFragment
import com.example.gatewayrestaurant.Fragment.OrderFragment
import com.example.gatewayrestaurant.Fragment.ProfileFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Session.SessionManager
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.Utils.NetworkConnection
import com.example.gatewayrestaurant.databinding.ActivityHomePageBinding
import com.example.gatewayrestaurant.model.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomePageActivity : BaseActivity() {

    private lateinit var mBinding: ActivityHomePageBinding
    val fragment = HomeFragment()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_page)

        openMainFragment()
        supportActionBar?.hide()


        if (intent.getStringExtra("toCart") == "fromCart") {
            mBinding.bottomMenu.setItemSelected(R.id.cart)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.clFragment, CartFragment())
            transaction.commit()
        } else if (intent.getStringExtra("toOrderPage") == "fromProfile") {
            mBinding.bottomMenu.setItemSelected(R.id.order)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.clFragment, OrderFragment())
            transaction.commit()

        } else {
            mBinding.bottomMenu.setItemSelected(R.id.home)
        }


        checkinternetBottomSheet()
        refreshBadge()

        mBinding.bottomMenu.setOnItemSelectedListener {
            when (it) {

                R.id.home -> {
                    openMainFragment()
                }

                R.id.cart -> {
                    val favoriteFragment = CartFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.clFragment, favoriteFragment).commit()
                }

                R.id.order -> {
                    val profileFragment = OrderFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.clFragment, profileFragment).commit()
                }

                R.id.profile -> {
                    val profileFragment = ProfileFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.clFragment, profileFragment).commit()
                }
            }
        }
    }

    private fun checkinternetBottomSheet() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this) { isConnected ->
            if (!isConnected) {
                NoInternetBottomSheet(
                    this
                ) { isRetryClicked: Boolean ->
                    if (isRetryClicked) {
                        checkinternetBottomSheet()
                    } else {
                        showShortToast("SomeThing Went Wrong.")
                    }
                }.show(
                    supportFragmentManager, "no_internet_connection"
                )
            }
        }
    }


    fun refreshBadge() {
//        val cartCount = AppSettingsPref.getIntValue(this, AppSettingsPref.CART_COUNT, 0)
        val cartCount = sessionManager!!.getCartCount()
        if (cartCount <= 0) {
            mBinding.bottomMenu.dismissBadge(R.id.cart)
        } else {
            mBinding.bottomMenu.showBadge(R.id.cart, cartCount)
        }
    }

    private fun openMainFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.clFragment, fragment)
        transaction.commit()
    }

    private fun getAddressList() {
        if (user != null) {
            val addressList = ArrayList<Address>()
            FirebaseDatabase.getInstance().reference.child("users").child(user.uid).child("Address")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        addressList.clear()
                        for (snapshot in dataSnapshot.children) {
                            val address = snapshot.getValue(Address::class.java)
                            address?.let { addressList.add(it) }
                        }
                        if (addressList.isNotEmpty()) {
                            SelectAddressBottomSheetFragment(
                                this@HomePageActivity, addressList
                            ).show(
                                supportFragmentManager, "show_delete_bottomsheet"
                            )
                            sessionManager?.setAddressBottomSheet(false)
                        }
                        Log.e("hello", addressList.toString())
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle possible errors.
                    }
                })
        }
    }


    override fun onResume() {
        refreshBadge()
        if (sessionManager!!.getAddressBottomSheet()){
            getAddressList()
        }

        super.onResume()
    }
}