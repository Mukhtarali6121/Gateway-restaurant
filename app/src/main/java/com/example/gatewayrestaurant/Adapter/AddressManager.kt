package com.example.gatewayrestaurant.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gatewayrestaurant.Activity.AddEditAddressActivity
import com.example.gatewayrestaurant.Activity.AddressActivity
import com.example.gatewayrestaurant.Activity.HomePageActivity
import com.example.gatewayrestaurant.Dialogs.DeleteAddressBottomSheetFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Session.SessionManager
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.databinding.AddressViewBinding
import com.example.gatewayrestaurant.model.Address
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
class AddressManager(
    private val addressList: ArrayList<Address>,
    private val mContext: Context,
    private var isFromProfile: Boolean,
    private var isFromSelectAddressBottomSheet: Boolean,
    private var addressSelectedCallBack: AddressSelectedCallBack
) : RecyclerView.Adapter<AddressManager.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: AddressViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.address_view, parent, false
        )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = addressList[position]

        if (isFromSelectAddressBottomSheet){
            holder.mBinder.ivEdit.visibility = View.GONE
            holder.mBinder.ivDelete.visibility = View.GONE
            holder.mBinder.view.visibility = View.GONE
        }else{
            holder.mBinder.ivEdit.visibility = View.VISIBLE
            holder.mBinder.ivDelete.visibility = View.VISIBLE
            holder.mBinder.view.visibility = View.VISIBLE
        }

        when (model.label) {
            "Home" -> {
                holder.mBinder.ivLocation.setImageResource(R.drawable.nav_home)
                holder.mBinder.ivLocation.setColorFilter(ContextCompat.getColor(mContext, R.color.black))
            }
            "Office" -> holder.mBinder.ivLocation.setImageResource(R.drawable.ic_vector_office_bag)
            else -> {
                holder.mBinder.ivLocation.setImageResource(R.drawable.map_location)
            }
        }


        holder.mBinder.tvAddress.text = model.flatNumber + " " + model.location
        holder.mBinder.tvFullAddress.text = model.label ?: "Other"

        holder.mBinder.cvMain.setOnClickListener {
            if (!isFromProfile) {
                val sessionManager = SessionManager(mContext)
                sessionManager.setSelectedAddressData(Address(model.flatNumber, model.location, model.landmark,model.label))
                val intent = Intent(mContext, HomePageActivity::class.java)
                intent.putExtra("toCart", "fromCart")
                mContext.startActivity(intent)
            } else if (isFromSelectAddressBottomSheet) {
                val sessionManager = SessionManager(mContext)
                sessionManager.setSelectedAddressData(Address(model.flatNumber, model.location, model.landmark,model.label))
                addressSelectedCallBack.addressSelected()
            }
        }

        holder.mBinder.ivEdit.setOnClickListener { updateAddress(model) }
        holder.mBinder.ivDelete.setOnClickListener {
            DeleteAddressBottomSheetFragment(mContext, model).show(
                (mContext as AddEditAddressActivity).supportFragmentManager,
                "show_delete_bottomsheet"
            )
        }
    }

    private fun updateAddress(model: Address) {
        val intent = Intent(mContext, AddressActivity::class.java)
        intent.putExtra("flatNumber", model.flatNumber)
        intent.putExtra("location", model.location)
        intent.putExtra("Landmark", model.landmark)
        mContext.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    interface AddressSelectedCallBack {
        fun addressSelected()
    }

    class MyViewHolder(val mBinder: AddressViewBinding) : RecyclerView.ViewHolder(mBinder.root)
}
