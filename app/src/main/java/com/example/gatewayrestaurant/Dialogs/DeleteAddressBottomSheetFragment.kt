package com.example.gatewayrestaurant.Dialogs

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.DeleteAddressBottomsheetBinding
import com.example.gatewayrestaurant.model.Address
import com.example.gatewayrestaurant.model.model
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DeleteAddressBottomSheetFragment(var mContext: Context, private var model: Address) :

    BottomSheetDialogFragment() {

    private lateinit var mBinding: DeleteAddressBottomsheetBinding

    val progressDialog = ProgressDialog(mContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        setStyle(STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.delete_address_bottomsheet,
            container,
            false
        )


        mBinding.tvDeleteAddress.setOnClickListener {
            deleteAddress()
        }

        mBinding.tvCancel.setOnClickListener {
            dialog?.dismiss()
        }
        return mBinding.root
    }

    private fun deleteAddress() {
        progressDialog.setMessage("Deleting..., Please wait!")
        progressDialog.show()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Address")
        val query = ref.orderByChild("flatNumber").equalTo(model.flatNumber)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        postSnapshot.ref.removeValue()
                        progressDialog.hide()
                        dialog?.dismiss()
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }



}