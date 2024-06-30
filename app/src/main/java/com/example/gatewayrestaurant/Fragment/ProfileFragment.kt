package com.example.gatewayrestaurant.Fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Activity.*
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Session.SessionManager
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.Utils.CommonSingleton.cartCount
import com.example.gatewayrestaurant.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : BaseFragment() {

    private lateinit var mBinding: FragmentProfileBinding
    private val user = FirebaseAuth.getInstance().currentUser
    private val uid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        setUpToolbar()

        if (user != null) {
            mBinding.btnLogInOut.text = getString(R.string.log_out)
            /*val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (postSnapshot in snapshot.children) {

                        val userEmail = snapshot.child("userEmail").value.toString()
                        val userMobileNumber = snapshot.child("mobileNumber").value.toString()
                        val userName = snapshot.child("userName").value.toString()

                        mBinding.tvName.text = userName
                        mBinding.tvEmail.text = userEmail
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })*/

            val sessionManager = SessionManager(mActivity)
            val email = sessionManager.getUserEmail()
            val mobile = sessionManager.getUserMobile()
            val name = sessionManager.getUserName()
            mBinding.tvName.text = name
            mBinding.tvEmail.text = email

            mBinding.cvUserInfo.visibility =View.VISIBLE
        } else {
            mBinding.btnLogInOut.text = getString(R.string.log_in)
            mBinding.cvUserInfo.visibility =View.GONE

        }



        mBinding.btnLogInOut.setOnClickListener {
            if (user != null) {
                val builder = AlertDialog.Builder(mActivity)
                builder.setTitle(R.string.app_name)
                builder.setMessage(getString(R.string.warningLogout))
                builder.setPositiveButton("OK") { _, _ ->
                    checkUser()
                }
                builder.setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            } else {
                startActivity(Intent(mActivity, LoginActivity::class.java))
            }
        }

        mBinding.llAboutUs.setOnClickListener {
            startActivity(Intent(mActivity, AboutUsActivity::class.java))
        }
        mBinding.tvAddress.setOnClickListener {
            startActivity(
                Intent(mActivity, AddEditAddressActivity::class.java).putExtra(
                        "isFromProfile",
                        "Yes"
                    )
            )
        }
        mBinding.tvYourOrder.setOnClickListener {
            startActivity(
                Intent(mActivity, HomePageActivity::class.java).putExtra(
                        "toOrderPage",
                        "fromProfile"
                    )
            )
        }
        mBinding.tvContactUs.setOnClickListener {
//            startActivity(Intent(mActivity, ContactUsActivity::class.java))
            showContactUsDialog()
        }
        mBinding.LlTermsAndCondition.setOnClickListener {
            startActivity(
                Intent(mActivity, TermsAndConditionActivity::class.java).putExtra(
                        "url", "https://mukhtarali6121.github.io/Gateway-restaurant/"
                    ).putExtra("titleName","TermsAndCondition")
            )
        }

        mBinding.LlPrivacyPolicy.setOnClickListener {
            startActivity(
                Intent(mActivity, TermsAndConditionActivity::class.java).putExtra(
                        "url", "https://mukhtarali6121.github.io/Gateway-Privacy/"
                ).putExtra("titleName","PrivacyPolicy")

            )
        }
        return mBinding.root
    }

    private fun checkUser() {

//        AppSettingsPref.saveInt(mContext, AppSettingsPref.CART_COUNT, cartCount)
        val session = SessionManager(mActivity )
        session.setCartCount(0)

        session.logoutUser()

        Toast.makeText(mActivity, "You Have Successfully LogOut!", Toast.LENGTH_SHORT).show()
    }

    private fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = getString(R.string.my_profile)
        mBinding.toolbar.ivBack.visibility = View.GONE
        mBinding.toolbar.shoppingCart.visibility = View.GONE
    }

    private fun showContactUsDialog(){

        val dialogFeedback = AlertDialog.Builder(requireContext())
        val layoutInflater = layoutInflater
        val customView = layoutInflater.inflate(R.layout.contact_us_dialog, null)

        val send = customView.findViewById<Button>(R.id.send)
        val cancel = customView.findViewById<Button>(R.id.cancel)
        val tvContactDesc =
            customView.findViewById<TextView>(R.id.tvContactDesc)
        tvContactDesc.setTextColor(
            ContextCompat.getColor(
                mActivity,
                R.color.black
            )
        )
        dialogFeedback.setCancelable(false)
        dialogFeedback.setView(customView)
        val alertDialog = dialogFeedback.create()

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        send.setOnClickListener {
            try {
                val emailIntent = Intent(
                    Intent.ACTION_SENDTO,
                    Uri.fromParts("mailto", "gatewayrestaurant121@gmail.com", null)
                )
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Any Query :")
                startActivity(Intent.createChooser(emailIntent, "Send email..."))
            } catch (ex: ActivityNotFoundException) {
                showShortToast("Technical Issue While Sending A Mail")
            }
        }
        cancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }
}