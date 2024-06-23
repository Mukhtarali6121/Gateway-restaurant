package com.example.gatewayrestaurant.Utils

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.gatewayrestaurant.R
import com.google.android.material.snackbar.Snackbar
import java.util.*

/**
 * Created by Chirag Desai on 25-06-2021.
 */
object Utilities {
    lateinit var mProgressDialog: ProgressDialog
    private val tz = TimeZone.getDefault()
    /**
     * Shows a progress dialog with a spinning animation in it. This method must preferably called
     * from a UI thread.
     *
     * @param ctx           Activity context
     * @param title         Title of the progress dialog
     * @param body          Body/Message to be shown in the progress dialog
     * @param icon          Icon to show in the progress dialog. It can be null.
     * @param isCancellable True if the dialog can be cancelled on back button press, false otherwise

     */
    //String Values to be Used in App
    /**
     * Shows a progress dialog with a spinning animation in it. This method must preferably called
     * from a UI thread.
     *
     * @param ctx   Activity context
     * @param title Title of the progress dialog
     * @param body  Body/Message to be shown in the progress dialog
     */


    /**
     * Checks if the Internet connection is available.
     *
     * @return Returns true if the Internet connection is available. False otherwise.

     */
    fun isInternetAvailable(ctx: Context?): Boolean {
        if (ctx != null) {
            val cm = (ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            val networkInfo = cm.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        return false
    }


    //Success Snackbar
    fun showSuccessSnackBar(view: View?, msg: String?, LENGTH: Int): Snackbar? {
        if (view == null) return null
        val snackbar = Snackbar.make(view, msg!!, LENGTH)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorPrimary))
        val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.textSize = 16.0.toFloat()
            textView.setTextColor(view.context.getColor(R.color.white))
            textView.gravity = Gravity.CENTER_HORIZONTAL
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        snackbar.show()
        return snackbar
    }

    fun showSuccessSnackBar(
        view: View?,
        msg: String?,
        LENGTH: Int,
        listener: SnackBarDismissListener
    ) {
        val snackbar = showSuccessSnackBar(view, msg, LENGTH)
        snackbar?.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                listener.onDismiss()
            }
        })
    }

    //Error Snackbar
    fun showErrorSnackBar(view: View?, msg: String?, LENGTH: Int): Snackbar? {
        if (view == null) return null
        val snackbar = Snackbar.make(view, msg!!, LENGTH)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorGrey))
        snackbar.setAction("Okay") { // Call your action method here
            snackbar.dismiss()
        }
        val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.textSize = 16.0.toFloat()
            textView.setTextColor(view.context.getColor(R.color.white))
            textView.gravity = Gravity.CENTER_HORIZONTAL
            //            textView.setTextAlignment(View.START);
        }
        snackbar.show()
        return snackbar
    }

    interface SnackBarDismissListener {
        fun onDismiss()
    }


}