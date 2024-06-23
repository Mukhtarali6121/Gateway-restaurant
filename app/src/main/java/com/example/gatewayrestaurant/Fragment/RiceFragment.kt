package com.example.gatewayrestaurant.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.TabLayoutActivity
import com.example.gatewayrestaurant.Adapter.MenuAdapter
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.FragmentRiceBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class RiceFragment : BaseFragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var mBinding: FragmentRiceBinding? = null
        fun disableThem() {
            if (mBinding != null) {
                mBinding!!.rvDishList.visibility = View.VISIBLE
                mBinding!!.shimmerMenu.visibility = View.GONE
            }
        }
    }

    private var riceAdapter: MenuAdapter? = null

    private var callBack: MenuAdapter.CallBack = object : MenuAdapter.CallBack {
        override fun getCartCount(cartCount: Int) {
            (mContext as TabLayoutActivity).refreshCartCount()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_rice,
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
                FirebaseDatabase.getInstance().reference.child("rice").orderByChild("name"),
                MenuModel::class.java
            )
            .build()

        riceAdapter =
            MenuAdapter(
                options,
                mActivity,
                callBack
            )
        mBinding!!.rvDishList.adapter = riceAdapter
        return mBinding!!.root

    }

    override fun onStart() {
        super.onStart()
        riceAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        riceAdapter!!.stopListening()
    }

    fun enableThem() {
        mBinding!!.rvDishList.visibility = View.GONE
    }

    private fun searchDish(s: String) {

        val options1 = FirebaseRecyclerOptions.Builder<MenuModel>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("rice")
                    .orderByChild("nameLowerCase")
                    .startAt(s).endAt(s + "\uf8ff"),
                MenuModel::class.java
            )
            .build()

        riceAdapter =
            MenuAdapter(
                options1,
                mActivity,
                callBack
            )
        mBinding!!.rvDishList.adapter = riceAdapter

        riceAdapter!!.startListening()
    }
}