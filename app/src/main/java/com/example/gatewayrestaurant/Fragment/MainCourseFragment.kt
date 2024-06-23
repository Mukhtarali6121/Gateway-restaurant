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
import com.example.gatewayrestaurant.databinding.FragmentMainCourseBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class MainCourseFragment : BaseFragment() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var mBinding: FragmentMainCourseBinding? = null
        fun disableThem() {
            if (mBinding != null) {
                mBinding!!.mProgressBar.visibility = View.INVISIBLE
            }
        }
    }

    var mainCourseAdapter: MenuAdapter? = null

    private var callBack: MenuAdapter.CallBack = object : MenuAdapter.CallBack {
        override fun getCartCount(cartCount: Int) {
            (mContext as TabLayoutActivity).refreshCartCount()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_course, container, false
        )
        enableThem()

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
        mBinding!!.rvDishList.isNestedScrollingEnabled = false

        mBinding!!.rvDishList.layoutManager = LinearLayoutManager(context)
        val options = FirebaseRecyclerOptions.Builder<MenuModel>().setQuery(
                FirebaseDatabase.getInstance().reference.child("maincourse"), MenuModel::class.java
            ).build()
        mainCourseAdapter = MenuAdapter(
            options, mActivity, callBack
        )
        mBinding!!.rvDishList.adapter = mainCourseAdapter
        return mBinding!!.root
    }

    override fun onStart() {
        super.onStart()
        mainCourseAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        mainCourseAdapter!!.stopListening()
    }

    fun enableThem() {
        mBinding!!.mProgressBar.visibility = View.VISIBLE
    }

    private fun searchDish(s: String) {
        val options1 = FirebaseRecyclerOptions.Builder<MenuModel>().setQuery(
                FirebaseDatabase.getInstance().reference.child("maincourse").orderByChild("name")
                    .startAt(s).endAt(s + "\uf8ff"), MenuModel::class.java
            ).build()

        mainCourseAdapter = MenuAdapter(
            options1, mActivity, callBack
        )
        mBinding!!.rvDishList.adapter = mainCourseAdapter

        mainCourseAdapter!!.startListening()
    }


}