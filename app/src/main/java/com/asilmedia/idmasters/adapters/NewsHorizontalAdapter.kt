package com.asilmedia.idmasters.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asilmedia.idmasters.models.News
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.ItemNewsHorizontalBinding

class NewsHorizontalAdapter(var setOnItemClickListener: SetOnItemClickListener) :
    ListAdapter<News, NewsHorizontalAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemnewsHor: ItemNewsHorizontalBinding) :
        RecyclerView.ViewHolder(itemnewsHor.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(news: News) {
            itemnewsHor.apply {
                titleTv.text = news.title
                Glide.with(itemnewsHor.root).load(news.imgUrl).placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder).into(ivImage)
                layout.setOnClickListener {
                    setOnItemClickListener.setOnItemClick(news)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemNewsHorizontalBinding.inflate(
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
        fun setOnItemClick(news: News)
    }

}