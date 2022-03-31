package com.asilmedia.idmasters.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asilmedia.idmasters.models.Competition
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.ItemCompetitionBinding

class CompetitionAdapter(var setOnItemClickListener: SetOnItemClickListener) :
    ListAdapter<Competition, CompetitionAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemCompetition: ItemCompetitionBinding) :
        RecyclerView.ViewHolder(itemCompetition.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(competition: Competition) {
            itemCompetition.apply {
                tvText.text = competition.title
                Glide.with(itemCompetition.root).load(competition.imgUrl)
                    .placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                    .into(ivMainImage)
                layout.setOnClickListener {
                    setOnItemClickListener.setOnItemClick(competition)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Competition>() {
        override fun areItemsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Competition, newItem: Competition): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemCompetitionBinding.inflate(
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
        fun setOnItemClick(competition: Competition)
    }

}