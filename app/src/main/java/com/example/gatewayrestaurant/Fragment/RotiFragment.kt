package com.example.gatewayrestaurant.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.TabLayoutActivity
import com.example.gatewayrestaurant.Adapter.MenuAdapter
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.FragmentRotiBinding
import com.example.gatewayrestaurant.databinding.FragmentSouthIndianBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class RotiFragment : BaseFragment() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var mBinding: FragmentRotiBinding? = null
        fun disableThem() {
            if (mBinding != null) {
                mBinding!!.rvDishList.visibility = View.VISIBLE
                mBinding!!.shimmerMenu.visibility = View.GONE
            }
        }
    }
    var rotiAdapter: MenuAdapter? = null

    private var callBack: MenuAdapter.CallBack = object : MenuAdapter.CallBack {
        override fun getCartCount(cartCount: Int) {
            (mContext as TabLayoutActivity).refreshCartCount()

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_roti,
            container,
            false
        )
        enableThem()


        mBinding!!.rvDishList.layoutManager = LinearLayoutManager(context)


        mBinding!!.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do Nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    mBinding!!.ivSearchClear.visibility = View.VISIBLE
                } else if (s.toString().isEmpty()) {
                    mBinding!!.ivSearchClear.visibility = View.INVISIBLE
                }
                searchDish(mBinding!!.etSearch.text.toString())

            }

            override fun afterTextChanged(s: Editable?) {
                // Do Nothing
            }

        })
        mBinding!!.ivSearchClear.setOnClickListener {
            mBinding!!.ivSearchClear.visibility = View.INVISIBLE
            mBinding!!.etSearch.text!!.clear()
            searchDish(mBinding!!.etSearch.text.toString())

        }

        val options = FirebaseRecyclerOptions.Builder<MenuModel>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("roti").orderByChild("name"),
                MenuModel::class.java
            )
            .build()

        rotiAdapter =
            MenuAdapter(
                options,
                mActivity,
                callBack
            )
        mBinding!!.rvDishList.adapter = rotiAdapter
        return mBinding!!.root
    }

    override fun onStart() {
        super.onStart()
        rotiAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        rotiAdapter!!.stopListening()
    }

    fun enableThem() {
        mBinding!!.rvDishList.visibility = View.GONE
    }

    private fun searchDish(s: String) {
        val options1 = FirebaseRecyclerOptions.Builder<MenuModel>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("roti")
                    .orderByChild("nameLowerCase")
                    .startAt(s).endAt(s + "\uf8ff"),
                MenuModel::class.java
            )
            .build()

        rotiAdapter =
            MenuAdapter(
                options1,
                mActivity,
                callBack
            )
        mBinding!!.rvDishList.adapter = rotiAdapter

        rotiAdapter!!.startListening()
    }
}