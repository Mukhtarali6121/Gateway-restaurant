package com.example.gatewayrestaurant.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.TabLayoutActivity
import com.example.gatewayrestaurant.Adapter.MenuAdapter
import com.example.gatewayrestaurant.Adapter.SouthIndianAdapter
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.FirebaseRepository
import com.example.gatewayrestaurant.Fragment.SouthIndianFragment.Companion
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.RoomInterface.MenuDao
import com.example.gatewayrestaurant.databinding.FragmentCartBinding
import com.example.gatewayrestaurant.databinding.FragmentSouthIndiannBinding
import com.example.gatewayrestaurant.room.AppDatabase
import kotlinx.coroutines.launch

class SouthIndiannFragment : BaseFragment() {

    private lateinit var mBinding: FragmentSouthIndiannBinding
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var menuDao: MenuDao
    var southIndianAdapter: SouthIndianAdapter? = null

    private var callBack: SouthIndianAdapter.CallBack = object : SouthIndianAdapter.CallBack {
        override fun getCartCount(cartCount: Int) {
            (mContext as TabLayoutActivity).refreshCartCount()

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_south_indiann,
            container,
            false
        )
        val db = AppDatabase.getDatabase(requireContext())
        menuDao = db.menuDao()

        firebaseRepository = FirebaseRepository(menuDao)
        mBinding.rvDishList.layoutManager = LinearLayoutManager(context)

        viewLifecycleOwner.lifecycleScope.launch {
            firebaseRepository.getMenuData { menuList ->
                mBinding.rvDishList.visibility = View.VISIBLE
                mBinding.shimmerMenu.visibility = View.GONE
                val arrayListMenuList = ArrayList(menuList)
                southIndianAdapter = SouthIndianAdapter(arrayListMenuList, mActivity,callBack)
                mBinding.rvDishList.adapter = southIndianAdapter
            }
        }
        return mBinding.root
    }

}