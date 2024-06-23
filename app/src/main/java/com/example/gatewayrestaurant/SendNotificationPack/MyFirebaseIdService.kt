package com.example.gatewayrestaurant.SendNotificationPack


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseIdService: FirebaseMessagingService(){
    override fun onNewToken(s:String){
        super.onNewToken(s)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val refreshToken:String = FirebaseInstanceId.getInstance().token.toString()
        if(firebaseUser!=null){
            updateToken(refreshToken)
        }
    }
    private fun updateToken(refreshToken:String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        var token:Token= Token(refreshToken)
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(token)
    }
}