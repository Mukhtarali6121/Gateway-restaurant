package com.example.gatewayrestaurant.Utils

import com.example.gatewayrestaurant.Dialogs.NoInternetBottomSheet

object CommonSingleton {

    init {
        println("Singleton class invoked.")
    }

    var cartCount = 0

    var isInternetBottomSheetActive = false
    var internetBottomSheet : NoInternetBottomSheet? = null
}
