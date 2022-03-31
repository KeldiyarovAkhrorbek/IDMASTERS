package com.masters.idmasters.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.ItemVideoBinding
import com.masters.idmasters.models.Video

class VideoAdapter(var setOnItemClickListener: SetOnItemClickListener) :
    ListAdapter<Video, VideoAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemCompetition: ItemVideoBinding) :
        RecyclerView.ViewHolder(itemCompetition.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(competition: Video) {
            itemCompetition.apply {
                Glide.with(itemCompetition.root).load(competition.imgUrl)
                    .placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                    .into(ivMainImage)
                layout.setOnClickListener {
                    setOnItemClickListener.setOnItemClick(competition)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemVideoBinding.inflate(
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
        fun setOnItemClick(video: Video)
    }

}