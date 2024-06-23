package com.example.gatewayrestaurant.Session

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.gatewayrestaurant.Activity.HomePageActivity
import com.example.gatewayrestaurant.Utils.CommonSingleton
import com.example.gatewayrestaurant.model.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(private val context: Context) {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val PRIVATE_MODE = 0
    private var selectedAddressData = Address()
    private val KEY_SELECTED_ADDRESS_DATA = "KEY_SELECTED_ADDRESS_DATA"
    private val IS_ADDRESS_AVAILABLE = "IS_ADDRESS_AVAILABLE"
    private val KEY_CART_COUNT = "KEY_CART_COUNT"
    init {
        pref = context.getSharedPreferences(Companion.PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        editor.commit()
    }

    fun getAddressBottomSheet(): Boolean {
        return pref.getBoolean(
            IS_ADDRESS_AVAILABLE,
            false
        )
    }

    fun setAddressBottomSheet(isAddress: Boolean?) {
        editor.putBoolean(
            IS_ADDRESS_AVAILABLE,
            isAddress!!
        )
        editor.commit()
    }

    fun getSelectedAddressData(): Address {
        selectedAddressData = if (pref.getString(
                KEY_SELECTED_ADDRESS_DATA,
                null
            ) != null
        ) {
            Gson().fromJson(
                pref.getString(
                    KEY_SELECTED_ADDRESS_DATA,
                    null
                ), object : TypeToken<Address?>() {}.type
            )
        } else {
            Address()
        }

        Log.e(
            "getRmfTransactionData :",
            selectedAddressData.toString()
        )
        return selectedAddressData
    }

    fun setSelectedAddressData(selectedAddressData: Address?) {
        Log.e("selectedAddressObject:-", Gson().toJson(selectedAddressData))
        editor.putString(
            KEY_SELECTED_ADDRESS_DATA,
            Gson().toJson(selectedAddressData)
        )
        editor.commit()
    }

    fun getCartCount(): Int {
        return pref.getInt(KEY_CART_COUNT, 0)  // Default cart count is 0
    }

    fun setCartCount(cartCount: Int) {
        editor.putInt(KEY_CART_COUNT, cartCount)
        editor.commit()
    }
    companion object {
        private const val PREF_NAME = "GatewayRestaurant"
    }

    fun logoutUser() {
        editor.clear()
        editor.commit()

        FirebaseAuth.getInstance().signOut()

        val notif =
            context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notif.cancelAll()
        val i = Intent(context, HomePageActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(i)
    }
}