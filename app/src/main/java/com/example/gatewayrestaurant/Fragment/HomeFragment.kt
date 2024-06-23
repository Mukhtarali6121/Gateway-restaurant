package com.example.gatewayrestaurant.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gatewayrestaurant.Activity.HotItemActivity
import com.example.gatewayrestaurant.Activity.OfferForYouActivity
import com.example.gatewayrestaurant.Activity.TabLayoutActivity
import com.example.gatewayrestaurant.Adapter.CategoryAdapter
import com.example.gatewayrestaurant.Adapter.OfferMenuAdapter
import com.example.gatewayrestaurant.Adapter.PopularMenuAdapter
import com.example.gatewayrestaurant.Class.BaseFragment
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.FragmentHomeBinding
import com.example.gatewayrestaurant.model.CategoryModel
import com.example.gatewayrestaurant.model.MenuModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class HomeFragment : BaseFragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private var categoryList: ArrayList<CategoryModel> = ArrayList()
    private var categoryAdapter: CategoryAdapter? = null
    private var popularMenuAdapter: PopularMenuAdapter? = null
    private var offerMenuAdapter: OfferMenuAdapter? = null
    var menulist: ArrayList<MenuModel> = ArrayList()
    var offerlist: ArrayList<MenuModel> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        initViews()

        mBinding.shimmerHome.startShimmer()
        mBinding.tvViewAll.setOnClickListener {
            startActivity(Intent(mActivity, TabLayoutActivity::class.java))
        }

        mBinding.tvPopularViewAll.setOnClickListener {
//            startActivity(Intent(mActivity, SendNotif::class.java))
            startActivity(Intent(mActivity, OfferForYouActivity::class.java))
        }

        mBinding.tvHotItemViewAll.setOnClickListener {
            startActivity(Intent(mActivity, HotItemActivity::class.java))
        }
//        val networkConnection= NetworkConnection(mActivity)
//        networkConnection.observe(viewLifecycleOwner) { isConnected ->
//            when {
//                isConnected -> {
//                    Toast.makeText(mActivity, "Connected", Toast.LENGTH_SHORT).show()
//                }
//                else -> {
//                    Toast.makeText(mActivity, "No Internet", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }


        return mBinding.root
    }

    private fun setUpPopularDishes() {

        val query = FirebaseDatabase.getInstance().reference.child("popularDish")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menulist.clear()
                for (postSnapshot in snapshot.children) {
                    menulist.add(
                        MenuModel(
                            name = postSnapshot.child("name").value.toString(),
                            nameLowerCase = postSnapshot.child("nameLowerCase").value.toString(),
                            nextAvailableFrom = postSnapshot.child("nextAvailableFrom").value.toString(),
                            nextAvailableTo = postSnapshot.child("nextAvailableTo").value.toString(),
                            availableFrom = postSnapshot.child("availableFrom").value.toString(),
                            availableTo = postSnapshot.child("availableTo").value.toString(),
                            price = postSnapshot.child("price").value.toString(),
                            image = postSnapshot.child("image").value.toString(),
                            offerPrice = postSnapshot.child("offerPrice").value.toString(),
                            isPopular = postSnapshot.child("isPopular").value.toString(),


                            )
                    )
                }
                mBinding.rvPopularItemList.layoutManager = LinearLayoutManager(
                    mActivity, LinearLayoutManager.HORIZONTAL, false
                )

                popularMenuAdapter = PopularMenuAdapter(mActivity, menulist)
                mBinding.rvPopularItemList.adapter = popularMenuAdapter
                setUpOfferDishes()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun setUpOfferDishes() {

        val query = FirebaseDatabase.getInstance().reference.child("offerDish")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                offerlist.clear()
                for (postSnapshot in snapshot.children) {
                    offerlist.add(
                        MenuModel(
                            name = postSnapshot.child("name").value.toString(),
                            nameLowerCase = postSnapshot.child("nameLowerCase").value.toString(),
                            nextAvailableFrom = postSnapshot.child("nextAvailableFrom").value.toString(),
                            nextAvailableTo = postSnapshot.child("nextAvailableTo").value.toString(),
                            availableFrom = postSnapshot.child("availableFrom").value.toString(),
                            availableTo = postSnapshot.child("availableTo").value.toString(),
                            price = postSnapshot.child("price").value.toString(),
                            image = postSnapshot.child("image").value.toString(),
                            isPopular = postSnapshot.child("isPopular").value.toString(),
                            offerPrice = postSnapshot.child("offerPrice").value.toString()
                        )
                    )
                }
                mBinding.rvOfferItemList.layoutManager = LinearLayoutManager(
                    mActivity, LinearLayoutManager.VERTICAL, false
                )


                offerMenuAdapter = OfferMenuAdapter(mActivity, offerlist)
                mBinding.rvOfferItemList.adapter = offerMenuAdapter

                mBinding.clparent.visibility = View.VISIBLE
                mBinding.shimmerHome.stopShimmer()
                mBinding.shimmerHome.visibility = View.GONE

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }


    private fun initViews() {

        mBinding.toolbar.tvHeader.text = getString(R.string.app_name)
        mBinding.toolbar.ivBack.visibility = View.GONE
        mBinding.toolbar.shoppingCart.visibility = View.GONE

        categoryList.clear()
        categoryList.add(
            CategoryModel(
                "South Indian", R.drawable.ic_south_indian
            )
        )
        categoryList.add(
            CategoryModel(
                "Indian", R.drawable.ic_indian_item
            )
        )
        categoryList.add(
            CategoryModel(
                "Chinese", R.drawable.ic_chinese
            )
        )
        categoryList.add(
            CategoryModel(
                "Pav Bhaji", R.drawable.ic_pav_bhaji
            )
        )
        categoryList.add(
            CategoryModel(
                "Rice", R.drawable.ic_rice
            )
        )

        categoryList.add(
            CategoryModel(
                "Roti", R.drawable.ic_roti
            )
        )
        categoryList.add(
            CategoryModel(
                "Desert", R.drawable.ic_desert
            )
        )
        categoryList.add(
            CategoryModel(
                "Drinks", R.drawable.ic_drinks
            )
        )

        categoryList.add(
            CategoryModel(
                "Fast Food", R.drawable.ic_fast_food
            )
        )

        categoryList.add(
            CategoryModel(
                "Snacks", R.drawable.ic_snacks
            )
        )

        mBinding.rvCategoryList.layoutManager = LinearLayoutManager(
            mActivity, LinearLayoutManager.HORIZONTAL, false
        )
        categoryAdapter = CategoryAdapter(mActivity, categoryList, callBack)
        mBinding.rvCategoryList.adapter = categoryAdapter

        setUpPopularDishes()

    }

    private var callBack: CategoryAdapter.CallBack = object : CategoryAdapter.CallBack {
        override fun itemCount(categoryName: String) {
            startActivity(
                Intent(mContext, TabLayoutActivity::class.java).putExtra(
                    "categoryName",
                    categoryName
                )
            )
        }
    }

    interface CallBack {
        fun cartCount(cartCount: String)
    }

}