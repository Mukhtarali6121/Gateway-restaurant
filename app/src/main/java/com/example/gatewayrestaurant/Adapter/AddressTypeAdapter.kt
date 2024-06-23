package com.example.gatewayrestaurant.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.XItemAddressLableLayoutBinding

class AddressTypeAdapter(private val addressTypes: List<String>,var callback: CallBack) : RecyclerView.Adapter<AddressTypeAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(val binding: XItemAddressLableLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
                callback.getSelectedLabel(addressTypes[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = XItemAddressLableLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressType = addressTypes[position]
        holder.binding.tvAddressType.text = addressType

        if (selectedPosition == position) {
            holder.binding.clParent.setBackgroundResource(R.drawable.rounded_button_accent)
            holder.binding.tvAddressType.setTextColor(Color.WHITE)
        } else {
            holder.binding.clParent.setBackgroundResource(R.drawable.button_border)
            holder.binding.tvAddressType.setTextColor(Color.BLACK)
        }

    }

    override fun getItemCount(): Int = addressTypes.size
    interface CallBack {
        fun getSelectedLabel(selectedLabel: String)
    }
}
