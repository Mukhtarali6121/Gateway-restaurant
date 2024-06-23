package com.example.gatewayrestaurant.SendNotificationPack

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call


public interface APIService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAKvf3ziU:APA91bEOAb_XmWqlgrVTFuKWMGtKz01Hkhg7ky2GlGyH_q1o1EthhQisWYuIljSr_vjHOzBI7-TCqFqvRQU9wb3VGRnWzLpcED6BuR7ZPj5PS7gkh35RZ2c_6Lf9neiZedXkKgI0MAT5" // Your server key refer to video for finding your server key
    )
    @POST("fcm/send")
    fun sendNotifcation(@Body body: NotificationObj?): Call<MyResponse?>?
}