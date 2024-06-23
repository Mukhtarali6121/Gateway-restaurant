package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {

    private lateinit var mBinding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        supportActionBar!!.hide()

        mBinding.btnResetPassword.setOnClickListener {
            if (mBinding.etEmail.text!!.isEmpty()) {
                showShortToast("Enter Email Address.")
                return@setOnClickListener
            } else {
                showProgressDialog("", "Checking, Please wait...")
                resetPassword()
            }
        }

        mBinding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        setUpToolbar()
    }

    private fun resetPassword() {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(mBinding.etEmail.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    hideProgressDialog()
                    showShortToast("Mail has been sent to your Mail Address for password reset.")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    hideProgressDialog()
                    showShortToast(task.exception!!.message)
                }
            }
    }

    private fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = getString(R.string.reset)
        mBinding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.toolbar.shoppingCart.visibility = View.GONE
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}