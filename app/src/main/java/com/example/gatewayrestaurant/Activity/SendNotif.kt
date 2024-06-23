package com.example.gatewayrestaurant.Activity


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.SendNotificationPack.Notification
import com.example.gatewayrestaurant.SendNotificationPack.NotificationObj
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.SendNotificationPack.*
import com.example.gatewayrestaurant.databinding.ActivitySendNotifBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendNotif : AppCompatActivity() {

    private lateinit var mBinding: ActivitySendNotifBinding
    private lateinit var apiService: APIService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_notif)
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)

        mBinding.button.setOnClickListener(View.OnClickListener {
            FirebaseDatabase.getInstance().reference.child("users")
                .child(mBinding.UserID.text.toString().trim()).child("fcmToken")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val usertoken: String = dataSnapshot.getValue(String::class.java).toString()

                        sendNotification(
                            usertoken,
                            mBinding.Title.text.toString().trim(),
                            mBinding.Message.text.toString().trim()
                        )
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
        })
//        UpdateToken()


    }

    private fun UpdateToken() {
        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        var refreshToken: String = FirebaseInstanceId.getInstance().token.toString()
        var token: Token = Token(refreshToken)
        FirebaseDatabase.getInstance().reference.child("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(token)
    }

    private fun sendNotification( usertoken: String, title: String, message: String) {
        var data = Notification(title, message)
        Log.e("ajsba","$usertoken")
        val usertoken2 = "e96_Kz84S7-z2F3pkuTjrA:APA91bFS2nYfL47V5s9zSyrWnHCIgF7270uwP0ytK2MUJyEFvQEE7hA4avvp7aUecn24NwMRDcNQxUgvn7bS0hEJRssk-hVZqcfyve0bK0VTfIjUi1d5J0Y_ItJBQ03OpjQ5OOpW1gG6"
        var sender = NotificationObj(data, usertoken)

        Log.e("sender","${sender.toString()}")

        apiService.sendNotifcation(sender)!!.enqueue(object : Callback<MyResponse?> {

            override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                if (response.body()!!.success == 1) {
                    val notif =
                    Toast.makeText(this@SendNotif, "Success ", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@SendNotif, "Failed ", Toast.LENGTH_LONG).show()
                }


//                if (response.code() === 200) {
//                    if (response.body()!!.success !== 1) {
//                        Toast.makeText(this@SendNotif, "Failed ", Toast.LENGTH_LONG).show()
//                    }
//                }
            }

            override fun onFailure(call: Call<MyResponse?>, t: Throwable?) {

            }
        })
    }


}