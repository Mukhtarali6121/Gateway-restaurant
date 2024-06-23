package com.example.gatewayrestaurant.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData

class NetworkConnection(private val context: Context) : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkConnectionCallback: ConnectivityManager.NetworkCallback? = null

    override fun onActive() {
        super.onActive()

        updateNetworkConnection()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                networkConnectionCallback = connectivityManagerCallback()
                connectivityManager.registerDefaultNetworkCallback(networkConnectionCallback!!)
            }
            else -> {
                val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                val receiverResult = context.registerReceiver(networkReceiver, filter)
                if (receiverResult == null) {
                    Log.w("NetworkConnection", "Failed to register networkReceiver")
                }
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        try {
            networkConnectionCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
                networkConnectionCallback = null
            }
            context.unregisterReceiver(networkReceiver)

        }catch (e: java.lang.Exception){

        }
    }

    private fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback {

        return object : ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }
        }
    }

    private fun updateNetworkConnection() {
        val activeNetworkConnection: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetworkConnection?.isConnected == true)
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateNetworkConnection()
        }
    }
}

