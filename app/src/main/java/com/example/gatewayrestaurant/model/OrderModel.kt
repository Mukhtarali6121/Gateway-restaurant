package com.example.gatewayrestaurant.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderModel(
    val randomUid: String? = "",
    val deliverytype: String? = "",
    val instruction: String? = "",
    val timestamp: String? = "",
    val uid: String? = "",
    val location: String? = "",
    val flatnumber: String? = "",
    val cancellationReason: String? = "",
    val landmark: String = "",
    val confirmation: String? = "",
    val totalAmount: String? = "",
    val rating: String? = "",
    val review: String? = "",
    var Order : List<OrderListModel>? = listOf()
): Parcelable{
    override fun toString(): String {
        return "OrderModel(randomUid=$randomUid, deliverytype=$deliverytype, instruction=$instruction, timestamp=$timestamp, uid=$uid, location=$location, flatnumber=$flatnumber, landmark='$landmark', confirmation=$confirmation, totalAmount=$totalAmount, Order=$Order)"
    }

}