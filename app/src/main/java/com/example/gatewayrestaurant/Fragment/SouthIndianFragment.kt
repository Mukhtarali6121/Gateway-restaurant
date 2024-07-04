package com.example.gatewayrestaurant.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.HomePageActivity
import com.example.gatewayrestaurant.Activity.TabLayoutActivity
import com.example.gatewayrestaurant.Adapter.CategoryAdapter
import com.example.gatewayrestaurant.Adapter.MenuAdapter
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.FirebaseRepository
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.RoomInterface.MenuDao
import com.example.gatewayrestaurant.databinding.FragmentSouthIndianBinding
import com.example.gatewayrestaurant.model.MenuModel
import com.example.gatewayrestaurant.room.AppDatabase
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// TODO: This is a dummy fagment made for roomDB


class SouthIndianFragment : BaseFragment() {
    companion object {

        @SuppressLint("StaticFieldLeak")
        private var mBinding: FragmentSouthIndianBinding? = null
        fun disableThem() {
            if (mBinding != null) {
                mBinding!!.rvDishList.visibility = View.VISIBLE
                mBinding!!.shimmerMenu.visibility = View.GONE
            }
        }
    }

    var southIndianAdapter: MenuAdapter? = null
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var menuDao: MenuDao

    private var callBack: MenuAdapter.CallBack = object : MenuAdapter.CallBack {
        override fun getCartCount(cartCount: Int) {
            (mContext as TabLayoutActivity).refreshCartCount()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_south_indian,
            container,
            false
        )
        enableThem()

        val db = AppDatabase.getDatabase(requireContext())
        menuDao = db.menuDao()

        firebaseRepository = FirebaseRepository(menuDao)

        viewLifecycleOwner.lifecycleScope.launch {
            firebaseRepository.getMenuData { menuList ->
                // Update your UI with the menu data
                // This callback will be called with the initial data from Room (if it exists)
                // and then again with the latest data from Firebase.
                Log.e("hello",menuList.toString())
            }
        }
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
                FirebaseDatabase.getInstance().reference.child("southindian").orderByChild("name"),
                MenuModel::class.java
            )
            .build()

        southIndianAdapter =
            MenuAdapter(
                options,
                mActivity,
                callBack

            )
        mBinding!!.rvDishList.adapter = southIndianAdapter
        return mBinding!!.root
    }

    override fun onStart() {
        super.onStart()
        southIndianAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        southIndianAdapter!!.stopListening()
    }

    fun enableThem() {
        mBinding!!.rvDishList.visibility = View.GONE
    }

    private fun searchDish(s: String) {
        val lowerCaseQuery = s.toLowerCase()

        val options1 = FirebaseRecyclerOptions.Builder<MenuModel>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("southindian")
                    .orderByChild("nameLowerCase")
                    .startAt(lowerCaseQuery)
                    .endAt(lowerCaseQuery + "\uf8ff"),
                MenuModel::class.java
            )
            .build()

        southIndianAdapter =
            MenuAdapter(
                options1,
                mActivity,
                callBack
            )
        mBinding!!.rvDishList.adapter = southIndianAdapter

        southIndianAdapter!!.startListening()
    }


}