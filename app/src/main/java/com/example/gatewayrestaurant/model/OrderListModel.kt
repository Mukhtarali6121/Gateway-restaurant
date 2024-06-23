package com.example.gatewayrestaurant.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderListModel(
    val name: String? = "",
    val price: String? = "",
    val quantity: String? = "",
    val quantityprice: String? = "",
    val image: String? = "",
) : Parcelable