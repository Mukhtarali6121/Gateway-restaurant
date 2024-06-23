package com.example.gatewayrestaurant.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.Fragment.*
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.Utils.AppSettingsPref
import com.example.gatewayrestaurant.databinding.ActivityTabLayoutBinding
import com.google.android.material.tabs.TabLayout

class TabLayoutActivity : BaseActivity() {

    private lateinit var mBinding: ActivityTabLayoutBinding
    private var categoryName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        categoryName = intent.getStringExtra("categoryName")

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tab_layout)
        setUpToolBar()

        setUpViewPager()
    }

    private fun setUpToolBar() {
        mBinding.toolbar.tvHeader.text = "Menu"
        mBinding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.toolbar.shoppingCart.setOnClickListener {
            startActivity(Intent(this,HomePageActivity::class.java)
                    .putExtra("toCart", "fromCart"))
        }
//        val cartCount = AppSettingsPref.getIntValue(this, AppSettingsPref.CART_COUNT, 0)
        val cartCount = getCartCount()

        if (cartCount > 0) {
            mBinding.toolbar.tvCartCount.visibility = View.VISIBLE
            mBinding.toolbar.tvCartCount.text = cartCount.toString()
        } else {
            mBinding.toolbar.tvCartCount.visibility = View.GONE
        }
    }


    private fun setUpViewPager() {
        val adapter = TabLayoutAdapter(
            supportFragmentManager
        )
        adapter.addFragment(
            SouthIndianFragment(), "South Indian"
        )

        adapter.addFragment(
            MainCourseFragment(), "Main Course"
        )

        adapter.addFragment(
            IndianStarterFragment(), "Indian Starter"
        )

        adapter.addFragment(
            ChineseFragment(), "Chinese Main Course"
        )

        adapter.addFragment(
            ChineseStarterFragment(), "Chinese Starter"
        )

        adapter.addFragment(
            PavBhajiFragment(), "Pav Bhaji"
        )

        adapter.addFragment(
            RiceFragment(), "Rice"
        )

        adapter.addFragment(
            RotiFragment(), "Roti"
        )
        adapter.addFragment(
            DrinksFragment(), "Drinks"
        )

        mBinding.viewPager.adapter = adapter
        mBinding.viewPager.offscreenPageLimit = 1

        Log.e("categoryName","$categoryName")

        when {
            !categoryName.isNullOrEmpty() -> {
                when {
                    categoryName!!.lowercase().trim() == "south indian" -> {
                        mBinding.viewPager.currentItem = 0
                    }
                    categoryName!!.lowercase().trim() == "indian" -> {
                        mBinding.viewPager.currentItem = 1
                    }
                    categoryName!!.lowercase().trim() == "chinese" -> {
                        mBinding.viewPager.currentItem = 3
                    }
                    categoryName!!.lowercase().trim() == "pav bhaji" -> {
                        mBinding.viewPager.currentItem = 5
                    }
                    categoryName!!.lowercase().trim() == "rice" -> {
                        mBinding.viewPager.currentItem = 6
                    }
                }
            }
            else -> mBinding.viewPager.currentItem = 0
        }

        mBinding.tabLayoutHeader.setupWithViewPager(mBinding.viewPager)
        mBinding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mBinding.tabLayoutHeader))
        mBinding.tabLayoutHeader.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        mBinding.viewPager.currentItem = 0
                    }
                    1 -> mBinding.viewPager.currentItem = 1


                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    internal class TabLayoutAdapter(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mTitleList = ArrayList<String>()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence = mTitleList[position]

        override fun getCount(): Int = mFragmentList.size

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mTitleList.add(title)
        }
    }

    public fun refreshCartCount(){
//        val cartCount = AppSettingsPref.getIntValue(this, AppSettingsPref.CART_COUNT, 0)
        val cartCount = getCartCount()

        if (cartCount > 0){
            mBinding.toolbar.tvCartCount.visibility = View.VISIBLE
            mBinding.toolbar.tvCartCount.text = cartCount.toString()
        }else{
            mBinding.toolbar.tvCartCount.visibility = View.GONE

        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}