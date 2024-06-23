package com.example.gatewayrestaurant.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.CategoryListItemBinding
import com.example.gatewayrestaurant.model.CategoryModel

class CategoryAdapter(
    var mContext: Context,
    private val categoryList: ArrayList<CategoryModel>, var Callback: CallBack
) :
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: CategoryListItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.category_list_item,
                parent,
                false
            )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val category = categoryList[position]
        Glide.with(mContext).load(category.itemImage).into(holder.mBinder.ivItemImage)
        holder.mBinder.tvCategoryName.text = category.itemName

        holder.mBinder.clParent.setOnClickListener{
            Callback.itemCount(category.itemName.toString())
        }
    }


    override fun getItemCount(): Int {
        return categoryList.size
    }
    interface CallBack {
        fun itemCount(categoryName :String )
    }
    class MyViewHolder(var mBinder: CategoryListItemBinding) :
        RecyclerView.ViewHolder(mBinder.root)
}