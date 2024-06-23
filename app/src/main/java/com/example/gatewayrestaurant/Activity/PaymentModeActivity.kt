package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.SendNotificationPack.APIService
import com.example.gatewayrestaurant.SendNotificationPack.Client
import com.example.gatewayrestaurant.SendNotificationPack.MyResponse
import com.example.gatewayrestaurant.SendNotificationPack.Notification
import com.example.gatewayrestaurant.SendNotificationPack.NotificationObj
import com.example.gatewayrestaurant.databinding.ActivityPaymentModeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class PaymentModeActivity : BaseActivity(), PaymentResultListener {

    private lateinit var mBinding: ActivityPaymentModeBinding
    var userEmail: String = ""
    var userContact: String = ""
    var deliveryType: String = ""
    var instruction: String = ""
    var totalAmount: String = ""
    private lateinit var apiService: APIService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_mode)
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)

//        mBinding.rbCOD.isChecked = true
        deliveryType = intent.getStringExtra("deliveryType").toString()
        instruction = intent.getStringExtra("instruction").toString()

        if ((deliveryType == "takeaway")) {
            mBinding.rbCOD.text = "Cash Payment When Takeaway"
        } else {
            mBinding.rbCOD.text = "Cash Payment On Delivery"

        }
        setUpToolbar()


        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().reference.child("cartTotal")
        val query1 = databaseRef.child(uid)
        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalAmount = snapshot.child("totalAmount").value.toString()
                mBinding.tvAmount.text = "Amount Payable :- ₹$totalAmount"
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        mBinding.btnPay.setOnClickListener {
            try {
                if (mBinding.rbCOD.isChecked) {
                    if ((deliveryType == "takeaway")) {
                        placeOrder("takeaway", "cash")
                    } else {
                        placeOrder("homeDelivery", "cash")
                    }
                } else if (mBinding.rbOnlinePayment.isChecked) {
                    payViaRazorpay()
                }
            } catch (
                e: Exception, ) {
                Log.e("errsor", e.message.toString())
            }


        }
    }

    private fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = "Payment"
        mBinding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.toolbar.shoppingCart.visibility = View.GONE
    }

    private fun getUserForNotification() {

        //krgWOraAq1UMiC5R1h6v6ikpyu12 is of Gateway Business uid
        FirebaseDatabase.getInstance().reference.child("users")
            .child("krgWOraAq1UMiC5R1h6v6ikpyu12").child("fcmToken")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userToken: String = dataSnapshot.getValue(String::class.java).toString()

                    sendNotification(
                        userToken, "Please Accept Order.", "New Order!"
                    )
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    private fun sendNotification(usertoken: String, title: String, message: String) {
        val data = Notification(title, message)
        Log.e("usertoken", usertoken)
        val sender = NotificationObj(data, usertoken)

        Log.e("sender", sender.toString())

        apiService.sendNotifcation(sender)!!.enqueue(object : Callback<MyResponse?> {

            override fun onResponse(call: Call<MyResponse?>, response: Response<MyResponse?>) {
                if (response.body()!!.success == 1) {

                    Log.e("NotificationSuccess", "Success")
                } else {
                    Log.e("NotificationFailed", "Failed")
                }

            }

            override fun onFailure(call: Call<MyResponse?>, t: Throwable) {

            }
        })
    }

    private fun payViaRazorpay() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val ref = FirebaseDatabase.getInstance().getReference("users").child(
            uid
        )
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userEmail = snapshot.child("userEmail").value as String
                userContact = snapshot.child("mobileNumber").value as String

            }

            override fun onCancelled(error: DatabaseError) {
                showShortToast(error.toString())
            }
        })
        val databaseRef = FirebaseDatabase.getInstance().reference.child("cartTotal")
        val query1 = databaseRef.child(uid)
        query1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalAmount = snapshot.child("totalAmount").value.toString()
                Toast.makeText(this@PaymentModeActivity, totalAmount, Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(error: DatabaseError) {}
        })
        //initialize razorpay checkout
        val checkout = Checkout()

        //Set Key Id
        checkout.setKeyID("rzp_test_jNA9b4OTYrcmuo")

        //initialize json object
        val `object` = JSONObject()
        try {
            // Put Name
            `object`.put("name", "Gateway Restaurant")
            //Put Description
            `object`.put("description", "Test Payment")
            //Put theme color
            `object`.put("theme.color", "#EC6124")
            //Put currency unit
            `object`.put("currency", "INR")
            //Put Amount
            `object`.put("amount", (totalAmount.toInt() * 100).toString())
            //Put Mobile Number
            `object`.put("prefill.contact", userContact)
            //Put Email
            `object`.put("prefill.email", userEmail)
            //Open razorpay checkout activity
            checkout.open(this@PaymentModeActivity, `object`)
        } catch (_: Exception) {
        }
    }

    private fun placeOrder(deliveryType: String, paymentType: String) {
        try {
            val uniqueID = UUID.randomUUID().toString()
            val randomid = uniqueID.substring(0, 12)

            val mAuth = FirebaseAuth.getInstance()
            val mFirebaseUser = mAuth.currentUser
            if (mFirebaseUser == null) {
                showShortToast("User not logged in")
                return
            }

            val timestamp = System.currentTimeMillis().toString()
            val hashMap = HashMap<String, String>().apply {
                put("timestamp", timestamp)
                put("deliverytype", deliveryType)
                put("paymentType", paymentType)
                put("uid", mFirebaseUser.uid)
                put("randomUid", randomid)
                put("instruction", instruction)
                put("confirmation", "")
                put("cancellationReason", "")
                put("review", "")
                put("rating", "")
                put("totalAmount", totalAmount)
            }
            Log.e("addressPushed", deliveryType)

            val ref = FirebaseDatabase.getInstance().getReference("Order")
            ref.child(randomid).setValue(hashMap).addOnSuccessListener {
                Log.e("deliveryType", deliveryType)
                if (deliveryType == "homeDelivery") {
                    val (flatNumber, location, landmark, label) = setAddress()

                    val addressMap = HashMap<String, String?>().apply {
                        put("flatnumber", flatNumber?.toString().orEmpty())
                        put("landmark", landmark?.toString().orEmpty())
                        put("location", location?.toString().orEmpty())
                        put("label", label?.toString().orEmpty())
                    }

                    ref.child(randomid).child("Address").push().setValue(addressMap).addOnSuccessListener {
                        Log.e("success", "Address")
                        addCartItemsToOrder(randomid, mFirebaseUser.uid)
                    }.addOnFailureListener { e ->
                        showShortToast(e.message)
                        Log.e("error", e.message.toString())
                    }
                } else {
                    addCartItemsToOrder(randomid, mFirebaseUser.uid)
                }
            }.addOnFailureListener { e ->
                showShortToast(e.message)
                Log.e("error", e.message.toString())
            }
        } catch (e: Exception) {
            Log.e("catchError", e.message.toString())
        }
    }

    private fun addCartItemsToOrder(orderId: String, userId: String) {
        val reference = FirebaseDatabase.getInstance().getReference("cart").child(userId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val quantity = postSnapshot.child("quantity").value as? String
                    val image = postSnapshot.child("image").value as? String
                    val name = postSnapshot.child("name").value as? String
                    val price = postSnapshot.child("price").value as? String
                    val quantityPrice = postSnapshot.child("quantityprice").value as? String

                    if (quantity != null && image != null && name != null && price != null && quantityPrice != null) {
                        val itemMap = HashMap<String, String?>().apply {
                            put("quantity", quantity)
                            put("image", image)
                            put("name", name)
                            put("price", price)
                            put("quantityprice", quantityPrice)
                        }

                        val orderRef = FirebaseDatabase.getInstance().getReference("Order")
                        orderRef.child(orderId).child("Order").push().setValue(itemMap).addOnSuccessListener {
                            Log.e("cartOrder", "Cart item added to order")
                        }.addOnFailureListener { e ->
                            showShortToast(e.message)
                            Log.e("error", e.message.toString())
                        }
                    } else {
                        Log.e("cartOrder", "Missing cart item data")
                    }
                }
                getUserForNotification()
                deleteCart()
            }

            override fun onCancelled(error: DatabaseError) {
                showShortToast(error.message)
                Log.e("error", error.message)
            }
        })
    }


    private fun deleteCart() {
        val mFirebaseDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val databaseReference = mFirebaseDatabase.reference.child("cart").child(uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    postSnapshot.ref.removeValue()

                }

                val databaseRef = FirebaseDatabase.getInstance().reference.child("cartTotal")
                val query1 = databaseRef.child(uid)
                query1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        snapshot.ref.child("totalAmount").setValue(0)
                        setCartCount(0)
                        startActivity(
                            Intent(
                                this@PaymentModeActivity, OrderPlacedActivity::class.java
                            )
                        )
                        finish()

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })


            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }


    override fun onPaymentSuccess(s: String) {
        if ((deliveryType == "takeaway")) {
            placeOrder("takeaway", "online")
        } else {
            placeOrder("homeDelivery", "online")
        }
    }

    override fun onPaymentError(i: Int, s: String) {
        Log.d("onPaymentError", "$s")
        showShortToast(s)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun setAddress(): Array<String?> {
        val selectedAddress = sessionManager.getSelectedAddressData()
        val flatNumber = selectedAddress.flatNumber
        val location = selectedAddress.location
        val landmark = selectedAddress.landmark
        val label = selectedAddress.label

        return arrayOf(flatNumber, location, landmark, label)
    }
    /* private fun placeOrder(deliveryType: String, paymentType: String) {

               try {
                   val uniqueID = UUID.randomUUID().toString()
                   val randomid = uniqueID.substring(0, 12)

                   val mAuth = FirebaseAuth.getInstance()
                   val mFirebaseUser = mAuth.currentUser
                   val timestamp = "" + System.currentTimeMillis()
                   val hashMap = HashMap<String, String>()
                   hashMap["timestamp"] = timestamp
                   hashMap["deliverytype"] = deliveryType
                   hashMap["paymentType"] = paymentType
                   hashMap["uid"] = mFirebaseUser!!.uid
                   hashMap["randomUid"] = randomid
                   hashMap["instruction"] = instruction
                   hashMap["confirmation"] = ""
                   hashMap["cancellationReason"] = ""
                   hashMap["review"] = ""
                   hashMap["rating"] = ""
                   hashMap["totalAmount"] = totalAmount
                   val ref = FirebaseDatabase.getInstance().getReference("Order")
                   ref.child(randomid).setValue(hashMap).addOnSuccessListener {
                       Log.e("deliveryTpe", deliveryType)
                       if ((deliveryType == "homeDelivery")) {
                           val (flatNumber, location, landmark, label) = setAddress()
                           Log.e("location", "${flatNumber},${location},${landmark},${label}")

                           val hashMap1 = HashMap<String, String>()
                           hashMap1["flatnumber"] = flatNumber.toString()
                           hashMap1["landmark"] = landmark.toString()
                           hashMap1["location"] = location.toString()
                           hashMap1["label"] = label.toString()
                           val ref1 = FirebaseDatabase.getInstance().getReference("Order")
                           ref1.child(randomid).child("Address").push().setValue(hashMap1)
                               .addOnSuccessListener {
                                   Log.e("success", "Address")

                                   val reference =
                                       FirebaseDatabase.getInstance().getReference("cart").child(
                                           mFirebaseUser.uid
                                       )
                                   reference.addListenerForSingleValueEvent(object :
                                       ValueEventListener {
                                       override fun onDataChange(snapshot: DataSnapshot) {
                                           for (postSnapshot: DataSnapshot in snapshot.children) {
                                               Log.e("cartOrder", "cart fetched")
                                               val quantity =
                                                   postSnapshot.child("quantity").value as String?
                                               val image = postSnapshot.child("image").value as String?
                                               val name = postSnapshot.child("name").value as String?
                                               val price = postSnapshot.child("price").value as String?
                                               val quantityprice =
                                                   postSnapshot.child("quantityprice").value as String?
                                               val hashMap2 = HashMap<String, String?>()
                                               hashMap2["quantity"] = quantity
                                               hashMap2["image"] = image
                                               hashMap2["name"] = name
                                               hashMap2["price"] = price
                                               hashMap2["quantityprice"] = quantityprice
                                               val reference1 =
                                                   FirebaseDatabase.getInstance().getReference("Order")
                                               reference1.child(randomid).child("Order").push()
                                                   .setValue(hashMap2).addOnSuccessListener {
                                                       getUserForNotification()
                                                       deleteCart()
                                                   }.addOnFailureListener { e ->
                                                       showShortToast(e.message)
                                                       Log.e("errorr", e.message.toString())
                                                   }
                                           }
                                       }

                                       override fun onCancelled(error: DatabaseError) {
                                           showShortToast(error.message)
                                       }
                                   })
                               }.addOnFailureListener { e ->
                                   showShortToast(e.message)
                                   Log.e("errorr", e.message.toString())
                               }
                       } else {
                           val cartReference = FirebaseDatabase.getInstance().getReference("cart").child(
                               mFirebaseUser.uid
                           )
                           cartReference.addListenerForSingleValueEvent(object :
                               ValueEventListener {
                               override fun onDataChange(snapshot: DataSnapshot) {
                                   for (postSnapshot: DataSnapshot in snapshot.children) {
                                       val quantity = postSnapshot.child("quantity").value as String?
                                       val image = postSnapshot.child("image").value as String?
                                       val name = postSnapshot.child("name").value as String?
                                       val price = postSnapshot.child("price").value as String?
                                       val quantityprice =
                                           postSnapshot.child("quantityprice").value as String?
                                       val hashMap3 = HashMap<String, String?>()
                                       hashMap3["quantity"] = quantity
                                       hashMap3["image"] = image
                                       hashMap3["name"] = name
                                       hashMap3["price"] = price
                                       hashMap3["quantityprice"] = quantityprice
                                       val ref1 = FirebaseDatabase.getInstance().getReference("Order")
                                       ref1.child(randomid).child("Order").push().setValue(hashMap3)
                                           .addOnSuccessListener {
                                               getUserForNotification()
                                               deleteCart()
                                           }.addOnFailureListener { e ->
                                               showShortToast(e.message)
                                               Log.e("errorr", e.message.toString())
                                           }
                                   }
                               }

                               override fun onCancelled(error: DatabaseError) {}
                           })
                       }
                   }.addOnFailureListener { e ->
                       showShortToast(e.message)
                       Log.e("errorr", e.message.toString())
                   }
               } catch (e: Exception) {
                   Log.e("catchError", e.message.toString())
               }
   }*/
}