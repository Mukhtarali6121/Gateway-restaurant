package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityOrderPlacedBinding

class OrderPlacedActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityOrderPlacedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_order_placed)
        supportActionBar!!.hide()
        mBinding.ivCancel.setOnClickListener {
            startActivity(
                Intent(
                    this, HomePageActivity::class.java
                )
            )
        }
        mBinding.cvDeliveryStatus.setOnClickListener {
            startActivity(
                Intent(
                    this, HomePageActivity::class.java
                ).putExtra(
                    "toOrderPage", "fromProfile"
                )
            )


        }

    }
}