package com.masters.idmasters.adapters.specialist.viewpager.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masters.idmasters.databinding.ItemTabSpecialityBinding
import com.masters.idmasters.models.SphereWithSelected


class SphereAdapter(var context: Context, var setOnItemClickListener: SetOnItemClickListener) :
    ListAdapter<SphereWithSelected, SphereAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemSpecialists: ItemTabSpecialityBinding) :
        RecyclerView.ViewHolder(itemSpecialists.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(sphere: SphereWithSelected, position: Int) {
            itemSpecialists.apply {
                val scaleUp: Animation =
                    AnimationUtils.loadAnimation(context, com.masters.idmasters.R.anim.scale_up)
                Glide.with(itemSpecialists.root).load(sphere.sphere?.imgUrl)
                    .placeholder(com.masters.idmasters.R.drawable.placeholder)
                    .error(com.masters.idmasters.R.drawable.placeholder)
                    .into(ivSpecialty)
                tvSpecialty.text = sphere.sphere?.title
                if (sphere.selected == true) {
                    ivSpecialty.layoutParams =
                        LinearLayout.LayoutParams(
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp),
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp)
                        )
                    ivSpecialty.startAnimation(scaleUp)
                } else {
                    ivSpecialty.layoutParams =
                        LinearLayout.LayoutParams(
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp),
                            context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp)
                        )
                }
                layout.setOnClickListener {
                    setOnItemClickListener.setOnItemClick(sphere, position)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<SphereWithSelected>() {
        override fun areItemsTheSame(
            oldItem: SphereWithSelected,
            newItem: SphereWithSelected
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SphereWithSelected,
            newItem: SphereWithSelected
        ): Boolean {
            return oldItem.sphere?.title == newItem.sphere?.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemTabSpecialityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position), position)
    }

    interface SetOnItemClickListener {
        fun setOnItemClick(sphereWithSelected: SphereWithSelected, position: Int)
    }

}