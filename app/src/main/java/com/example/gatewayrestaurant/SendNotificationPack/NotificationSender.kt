package com.example.gatewayrestaurant.SendNotificationPack

class NotificationSender(val data: Data?, val to:String){
    constructor():this(null,""){}
}