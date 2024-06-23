package com.example.gatewayrestaurant.Activity

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityAboutUsBinding

class AboutUsActivity : BaseActivity() {

    private lateinit var mBinding :ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_about_us)
        supportActionBar!!.hide()

        mBinding.tvRestaurantName.paintFlags = mBinding.tvRestaurantName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mBinding.toolbar.tvHeader.text = "About Us"
        mBinding.toolbar.ivBack.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}