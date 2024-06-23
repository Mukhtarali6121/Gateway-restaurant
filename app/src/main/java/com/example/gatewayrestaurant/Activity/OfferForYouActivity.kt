package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Adapter.MenuAdapter
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.Dialogs.NoInternetBottomSheet
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.Utils.NetworkConnection
import com.example.gatewayrestaurant.databinding.ActivityOfferForYouBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class OfferForYouActivity : BaseActivity() {

    companion object {
        private var mBinding: ActivityOfferForYouBinding? = null
        fun disableThem() {
            if (mBinding != null) {
                mBinding!!.rvDishList.visibility = View.VISIBLE
                mBinding!!.shimmerMenu.visibility = View.GONE
            }
        }
    }

    var offerForYouAdapter: MenuAdapter? = null

    private var callBack: MenuAdapter.CallBack = object : MenuAdapter.CallBack {
        override fun getCartCount(cartCount: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_for_you)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_offer_for_you)
        supportActionBar!!.hide()
        enableThem()
        mBinding!!.toolbar.tvHeader.text = getString(R.string.offer_for_you)
        mBinding!!.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
//        val cartCount = AppSettingsPref.getIntValue(this, AppSettingsPref.CART_COUNT, 0)
        val cartCount = getCartCount()

        mBinding!!.toolbar.shoppingCart.setOnClickListener {
            startActivity(
                Intent(this,HomePageActivity::class.java)
                .putExtra("toCart", "fromCart"))
        }

        if (cartCount > 0) {
            mBinding!!.toolbar.tvCartCount.visibility = View.VISIBLE
            mBinding!!.toolbar.tvCartCount.text = cartCount.toString()
        }

        mBinding!!.rvDishList.layoutManager = LinearLayoutManager(this)

        val options = FirebaseRecyclerOptions.Builder<MenuModel>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("offerDish").orderByChild("name"),
                MenuModel::class.java
            )
            .build()

        offerForYouAdapter =
            MenuAdapter(
                options,
                this,
                callBack
            )

        checkinternetBottomSheet()
        mBinding!!.rvDishList.adapter = offerForYouAdapter
    }

    private fun checkinternetBottomSheet() {

        val networkConnection = NetworkConnection(this)
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

    override fun onStart() {
        super.onStart()
        offerForYouAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        offerForYouAdapter!!.stopListening()
    }

    fun enableThem() {
        mBinding!!.rvDishList.visibility = View.GONE
    }
}