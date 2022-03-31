package com.masters.idmasters.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.masters.idmasters.databinding.ItemMenuBinding
import com.masters.idmasters.models.Menu

class MenuAdapter(var setOnItemClickListener: SetOnItemClickListener) :
    ListAdapter<Menu, MenuAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemMenuBinding: ItemMenuBinding) :
        RecyclerView.ViewHolder(itemMenuBinding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(menu: Menu) {
            itemMenuBinding.apply {
                tvTitle.text = menu.name
                tvBrief.text = menu.body
                layout.setOnClickListener {
                    setOnItemClickListener.setOnItemClick(menu)
                }
                tvTitle.isSelected = true
                ivMainImage.setImageResource(menu.pic)
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Menu>() {
        override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

    interface SetOnItemClickListener {
        fun setOnItemClick(menu: Menu)
    }

}