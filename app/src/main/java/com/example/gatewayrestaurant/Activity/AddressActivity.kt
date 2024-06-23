package com.example.gatewayrestaurant.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Adapter.AddressTypeAdapter
import com.example.gatewayrestaurant.Adapter.MenuAdapter
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddressActivity : BaseActivity() {

    private lateinit var mBinding: ActivityAddressBinding
    private var flatNumber: String = ""
    private var location: String = ""
    private var landmark: String = ""
    private var updateAddress: Boolean = false
    private lateinit var adapter: AddressTypeAdapter
    private val addressTypes = listOf("Home", "Office", "Others")
    private var selectedLabel :String = ""

    private var callBack: AddressTypeAdapter.CallBack = object : AddressTypeAdapter.CallBack {
        override fun getSelectedLabel(selectedLabel: String) {
            this@AddressActivity.selectedLabel = selectedLabel
            if (selectedLabel == "Others"){
                mBinding.tvLabel.visibility = View.VISIBLE
                mBinding.clOthers.visibility = View.VISIBLE
            }else{
                mBinding.tvLabel.visibility = View.GONE
                mBinding.clOthers.visibility = View.GONE
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_address)


        if (intent.hasExtra("flatNumber")) {
            flatNumber = intent.getStringExtra("flatNumber").toString()
            location = intent.getStringExtra("location").toString()
            landmark = intent.getStringExtra("Landmark").toString()
            updateAddress = true
        }
        adapter = AddressTypeAdapter(addressTypes,callBack)
        mBinding.rvAddressLabel.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvAddressLabel.adapter = adapter

        setUpToolbar()
        if (updateAddress) {
            mBinding.etHouseFloor.setText(flatNumber)
            mBinding.etLocality.setText(location)
            mBinding.etLandmark.setText(landmark)
            mBinding.btnSaveAddress.text = getString(R.string.update_address)
            mBinding.toolbar.tvHeader.text = getString(R.string.update_address)

        }

        mBinding.btnSaveAddress.setOnClickListener {
            if (updateAddress) {
                updateAddress()
            } else {
                saveData()
            }
        }
    }

    private fun setUpToolbar() {
        mBinding.toolbar.tvHeader.text = getString(R.string.add_address_detail)
        mBinding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.toolbar.shoppingCart.visibility = View.GONE
    }

    private fun updateAddress() {
        showProgressDialog("", "Checking, Please wait...")

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Address")
        val query = ref.orderByChild("flatNumber").equalTo(flatNumber)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        postSnapshot.ref.child("flatNumber").setValue(mBinding.etHouseFloor.text.toString())
                        postSnapshot.ref.child("location").setValue(mBinding.etLocality.text.toString())
                        postSnapshot.ref.child("landmark").setValue(mBinding.etLandmark.text.toString())
                        onBackPressed()
                        hideProgressDialog()

                    }

                }
            }
                override fun onCancelled(error: DatabaseError) {}
            })
        }



    private fun saveData() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        showProgressDialog("", "Checking, Please wait...")

        val flatNumber = mBinding.etHouseFloor.text.toString()
        val landmark = mBinding.etLandmark.text.toString()
        val location = mBinding.etLocality.text.toString()
        if (flatNumber.isEmpty() || location.isEmpty()) {
            Toast.makeText(
                this, "Can Not Save Address With Empty Field.", Toast.LENGTH_SHORT
            ).show()
            hideProgressDialog()
            return
        }
        val map: MutableMap<String, Any> = HashMap()
        map["flatNumber"] = flatNumber
        map["location"] = location
        map["landmark"] = landmark
        if (selectedLabel != "Others"){
            map["label"] = selectedLabel
        }else if(mBinding.etothers.text.toString().isNotEmpty()){
            map["label"] = mBinding.etothers.text.toString()
        }else{
            map["label"] = "Others"
        }
        val ref = FirebaseDatabase.getInstance().reference.child("users")
        ref.child(uid).child("Address").push().setValue(map).addOnSuccessListener {
            Toast.makeText(
                this@AddressActivity, "Address Saved Successfully.", Toast.LENGTH_SHORT
            ).show()
            onBackPressed()
            hideProgressDialog()
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@AddressActivity, e.message, Toast.LENGTH_SHORT
            ).show()
            hideProgressDialog()
        }
    }
}