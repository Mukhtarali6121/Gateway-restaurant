package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Adapter.MenuAdapter
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.Fragment.CartFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.Utils.CommonSingleton.cartCount
import com.example.gatewayrestaurant.databinding.ActivityHotItemBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class HotItemActivity : BaseActivity() {

    companion object {
        private var mBinding: ActivityHotItemBinding? = null
        fun disableThem() {
            if (mBinding != null) {
                mBinding!!.rvDishList.visibility = View.VISIBLE
                mBinding!!.shimmerMenu.visibility = View.GONE
            }
        }
    }

    var hotItemAdapter: MenuAdapter? = null

    private var callBack: MenuAdapter.CallBack = object : MenuAdapter.CallBack {
        override fun getCartCount(cartCount: Int) {

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hot_item)
        supportActionBar!!.hide()
        enableThem()
        mBinding!!.toolbar.tvHeader.text = "Hot Items"
        mBinding!!.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding!!.toolbar.shoppingCart.setOnClickListener {
            startActivity(Intent(this,HomePageActivity::class.java)
                .putExtra("toCart", "fromCart"))
        }

//        val cartCount = AppSettingsPref.getIntValue(this, AppSettingsPref.CART_COUNT, 0)
        val cartCount = getCartCount()

        if (cartCount > 0){
            mBinding!!.toolbar.tvCartCount.visibility = View.VISIBLE
            mBinding!!.toolbar.tvCartCount.text = cartCount.toString()
        }


        mBinding!!.rvDishList.layoutManager = LinearLayoutManager(this)

        val options = FirebaseRecyclerOptions.Builder<MenuModel>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("popularDish").orderByChild("name"),
                MenuModel::class.java
            )
            .build()

        hotItemAdapter =
            MenuAdapter(
                options,
                this,callBack

            )
        mBinding!!.rvDishList.adapter = hotItemAdapter
    }

    override fun onStart() {
        super.onStart()
        hotItemAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        hotItemAdapter!!.stopListening()
    }

    fun enableThem() {
        mBinding!!.rvDishList.visibility = View.GONE
    }

}