package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class LoginActivity : BaseActivity() {
    private lateinit var mBinding: ActivityLoginBinding


    private var fAuth: FirebaseAuth? = null
    private var fStore: FirebaseFirestore? = null
    private var user: FirebaseUser? = null
    private var fcmToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        supportActionBar!!.hide()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.login_text)
        user = FirebaseAuth.getInstance().currentUser
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()


        if (user!= null){
            val intent = Intent(applicationContext, HomePageActivity::class.java)
            startActivity(intent)
            finish()

        }

        setUpToolbar()

        mBinding.btnLogin.setOnClickListener {

            if (mBinding.etEmail.text.isNullOrEmpty()) {
                showShortToast("Please Enter Email Address.")
                return@setOnClickListener
            } else if (mBinding.etPassword.text.isNullOrEmpty()) {
                showShortToast("Please Enter Password.")
                return@setOnClickListener
            } else {
                showProgressDialog("","Logging In, Please wait...")
                fAuth!!.signInWithEmailAndPassword(
                    mBinding.etEmail.text.toString(),
                    mBinding.etPassword.text.toString()
                ).addOnSuccessListener {


                    FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
                        fcmToken = token
                    }.addOnFailureListener { e: Exception? -> }.addOnCanceledListener {}
                        .addOnCompleteListener { task: Task<String> ->
                            Log.v(
                                "asdb", "This is the token : " + task.result
                            )
                        }

                    val mAuth = FirebaseAuth.getInstance()
                    val mFirebaseUser = mAuth.currentUser

                    val databaseReference = FirebaseDatabase.getInstance().reference.child("users")
                        .child(mFirebaseUser!!.uid)
                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.child("fcmToken").exists()) {
                                snapshot.ref.child("fcmToken").setValue(fcmToken)
                                hideProgressDialog()
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        HomePageActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                }.addOnFailureListener { e ->
                    if (e.message!!.contains("The password is invalid")){
                        showShortToast("Password Entered is Incorrect. Please Try Again.")
                    }else if(e.message!!.contains("There is no user record corresponding to this identifier")){
                        showShortToast("User Doesn't Exists, Please Register Before Logging In")
                    }else{
                        showShortToast(e.message.toString())
                    }
                    Log.e("errorr",e.message.toString())
                    e.printStackTrace()
                    hideProgressDialog()
                }
            }
        }
        mBinding.tvRegister.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext, RegisterActivity::class.java
                )
            )
        }
        mBinding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = getString(R.string.login_text)
        mBinding.toolbar.shoppingCart.visibility = View.GONE
        mBinding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        val intent = Intent(this@LoginActivity, HomePageActivity::class.java)
        startActivity(intent)
        finish()
    }
}