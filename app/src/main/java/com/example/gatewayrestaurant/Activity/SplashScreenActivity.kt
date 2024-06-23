package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Session.SessionManager
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.databinding.ActivitySplashScreenBinding
import org.checkerframework.common.returnsreceiver.qual.This

class SplashScreenActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        sessionManager.setAddressBottomSheet(true)
        init()
    }

    private fun init() {

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1000 // in milliseconds
        mBinding.imageview1.startAnimation(fadeIn)

        Handler().postDelayed({
            val hasOnBoard =
                AppSettingsPref.getBooleanValue(this, AppSettingsPref.HAS_ON_BOARDED, false)

            if (hasOnBoard) {
                val intent = Intent(applicationContext, HomePageActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(applicationContext, OnBoarding::class.java)
                startActivity(intent)
                finish()
            }

        }, SPLASH_SCREEN.toLong())
        changeStatusBarColor()
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.colorPrimary)
        }
    }

    companion object {
        private const val SPLASH_SCREEN = 3000
    }
}