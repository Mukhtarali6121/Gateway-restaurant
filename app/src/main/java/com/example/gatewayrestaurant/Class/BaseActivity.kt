package com.example.gatewayrestaurant.Class

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gatewayrestaurant.Session.SessionManager

abstract class BaseActivity : AppCompatActivity() {
    protected var mProgressDialog: ProgressDialog? = null
    protected lateinit var sessionManager: SessionManager

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

    }

    protected fun showShortToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showProgressDialog(title: String?, message: String?) {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
        mProgressDialog = ProgressDialog.show(this, title, message)
    }

    protected fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
            mProgressDialog = null
        }
    }
    fun setCartCount(cartCount: Int) {
        sessionManager.setCartCount(cartCount)
    }

    fun getCartCount(): Int {
        return sessionManager.getCartCount()
    }
    override fun onStop() {
        super.onStop()
        // hide progress dialog to prevent leaks
        hideProgressDialog()
    }
}
