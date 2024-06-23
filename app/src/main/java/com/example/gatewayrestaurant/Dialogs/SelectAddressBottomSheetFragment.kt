package com.example.gatewayrestaurant.Dialogs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.AddEditAddressActivity
import com.example.gatewayrestaurant.Activity.AddressActivity
import com.example.gatewayrestaurant.Adapter.AddressManager
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Session.SessionManager
import com.example.gatewayrestaurant.databinding.FragmentSelectAddressBottomSheetBinding
import com.example.gatewayrestaurant.model.Address
import com.example.gatewayrestaurant.model.model
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class SelectAddressBottomSheetFragment(var mContext: Context, var addressList: ArrayList<Address>) :
    BottomSheetDialogFragment() {

    private lateinit var mBinding: FragmentSelectAddressBottomSheetBinding
    private var addressAdapter: AddressManager? = null
    private var itemCountCallBack: AddressManager.AddressSelectedCallBack =
        object : AddressManager.AddressSelectedCallBack {
            override fun addressSelected() {
                val sessionManager = SessionManager(mContext)

                Log.e("jabsd",sessionManager.getSelectedAddressData().toString())
                dialog?.dismiss()
            }

        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bottom sheet round corners can be obtained but the while background appears to remove that we need to add this.
        setStyle(STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_address_bottom_sheet, container, false)

        mBinding.rvAddressList.layoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        addressAdapter = AddressManager(addressList, mContext,true, true, itemCountCallBack)
        mBinding.rvAddressList.adapter = addressAdapter

        mBinding.tvViewAll.setOnClickListener {
            startActivity(Intent(mContext, AddEditAddressActivity::class.java))
            dialog?.dismiss()
        }

        mBinding.btnAddAddress.setOnClickListener {
            startActivity(Intent(mContext, AddressActivity::class.java))
            dialog?.dismiss()
        }
        return mBinding.root
    }

}