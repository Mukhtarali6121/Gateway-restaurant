package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging


class RegisterActivity : BaseActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private var fcmToken: String = ""

    var fAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        supportActionBar!!.hide()

        fAuth = FirebaseAuth.getInstance()
        var filter = InputFilter { source, start, end, dest, dstart, dend ->
            return@InputFilter if (source is SpannableStringBuilder) {
                for (i in end - 1 downTo start) {
                    val currentChar: Char = source[i]
                    if (currentChar == '/' || currentChar == '~' || Character.isDigit(currentChar)) {
                        source.delete(i, i + 1)
                    }
                }
                source
            } else {
                val filteredStringBuilder = java.lang.StringBuilder()
                for (i in start until end) {
                    val currentChar: Char = source[i]
                    if (Character.isLetter(currentChar) || Character.isWhitespace(currentChar) || currentChar == '\'') {
                        filteredStringBuilder.append(currentChar)
                    }
                }
                filteredStringBuilder.toString()
            }
        }


        mBinding.etUserName.filters = arrayOf(filter, InputFilter.LengthFilter(32))
        mBinding.etUserName.inputType =
            InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        mBinding.tvTermsConditions.setOnClickListener {
            startActivity(
                Intent(this, TermsAndConditionActivity::class.java).putExtra(
                    "url", "https://mukhtarali6121.github.io/Gateway-restaurant/"
                ).putExtra("titleName","TermsAndCondition")
            )

        }
        mBinding.tvPrivacyPolicy.setOnClickListener {
            startActivity(
                Intent(this, TermsAndConditionActivity::class.java).putExtra(
                    "url", "https://mukhtarali6121.github.io/Gateway-Privacy/"
                ).putExtra("titleName","PrivacyPolicy")

            )
        }


        mBinding.tvLogin.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext, LoginActivity::class.java
                )
            )
        })

        mBinding.btnCreateAccount.setOnClickListener {

            if (mBinding.etUserName.text.isNullOrEmpty()) {
                showShortToast("Please Enter Your Name.")
                return@setOnClickListener
            } else if (mBinding.etUserEmail.text.isNullOrEmpty()) {
                showShortToast("Please Enter Email Address.")
                return@setOnClickListener
            } else if (mBinding.etUserMobileNumber.text.isNullOrEmpty()) {
                showShortToast("Please Enter Mobile Number.")
                return@setOnClickListener
            }else if (mBinding.etUserMobileNumber.text!!.length < 10) {
                showShortToast("Please Enter Mobile Number of 10 Digits.")
                return@setOnClickListener
            } else if (mBinding.etPassword.text.isNullOrEmpty()) {
                showShortToast("Please Enter Password.")
                return@setOnClickListener
            } else if (mBinding.etPassword.text.isNullOrEmpty()) {
                showShortToast("Please Enter Confirm Password.")
                return@setOnClickListener
            } else if (mBinding.etPassword.text.toString() != mBinding.etPasswordConfirm.text.toString()) {
                mBinding.etPasswordConfirm.error = "Password Do not Match."
                return@setOnClickListener
            } else {
                mBinding.progressBar.visibility = View.VISIBLE

                fAuth!!.createUserWithEmailAndPassword(
                    mBinding.etUserEmail.text.toString(), mBinding.etPassword.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showShortToast("Registration Successfully!")
                        val usr = fAuth!!.currentUser
                        val request = UserProfileChangeRequest.Builder()
                            .setDisplayName(mBinding.etUserName.text.toString()).build()
                        usr!!.updateProfile(request)

                        val mAuth = FirebaseAuth.getInstance()
                        val mFirebaseUser = mAuth.currentUser
                        val map: MutableMap<String, Any> = HashMap()
                        map["userName"] = mBinding.etUserName.text.toString()
                        map["mobileNumber"] = mBinding.etUserMobileNumber.text.toString()
                        map["userEmail"] = mBinding.etUserEmail.text.toString()
                        map["fcmToken"] = ""

                        FirebaseDatabase.getInstance().reference.child("users").child(
                            mFirebaseUser!!.uid
                        ).setValue(map).addOnSuccessListener {


                            val map1: MutableMap<String, Any> = HashMap()
                            map1["totalAmount"] = 0

                            val ref1 = FirebaseDatabase.getInstance().reference.child("cartTotal")
                            ref1.child(mFirebaseUser.uid).setValue(map1).addOnSuccessListener {

                                    login(mBinding.etUserEmail.text.toString())

                                }.addOnFailureListener {

                                    it.printStackTrace()
                                }
                        }.addOnFailureListener { e ->
                                showShortToast(e.message)
                            }
                    } else {
                        showShortToast("Registration failed! Please try again later")
                        mBinding.progressBar.visibility = View.GONE
                    }
                }
            }
        }


        setUpToolbar()

    }

    fun login(email: String) {
        fAuth!!.signInWithEmailAndPassword(
            email, mBinding.etPassword.text.toString()
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

            val databaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(mFirebaseUser!!.uid)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("fcmToken").exists()) {
                        snapshot.ref.child("fcmToken").setValue(fcmToken)
                        hideProgressDialog()
                        startActivity(
                            Intent(
                                applicationContext, HomePageActivity::class.java
                            )
                        )
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        }.addOnFailureListener { e ->
            Log.e("errorr", e.message.toString())
            showShortToast(e.message.toString())
            e.printStackTrace()
            hideProgressDialog()
        }
    }

    private fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = "Register"
        mBinding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.toolbar.shoppingCart.visibility = View.GONE
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}