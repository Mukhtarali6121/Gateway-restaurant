package com.example.gatewayrestaurant.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class MenuModel(
    val name: String? = "",
    val nameLowerCase: String? = "",
    val nextAvailableFrom: String? = "",
    val nextAvailableTo: String? = "",
    val availableFrom: String? = "",
    val availableTo: String? = "",
    val price: String? = "",
    val image: String = "",
    val isPopular: String? = "",
    val offerPrice: String? = ""
):Parcelable{
    override fun toString(): String {
        return "MenuModel(name=$name, nameLowerCase=$nameLowerCase, nextAvailableFrom=$nextAvailableFrom, nextAvailableTo=$nextAvailableTo, availableFrom=$availableFrom, availableTo=$availableTo, price=$price, image='$image', isPopular=$isPopular, offerPrice=$offerPrice)"
    }
}
