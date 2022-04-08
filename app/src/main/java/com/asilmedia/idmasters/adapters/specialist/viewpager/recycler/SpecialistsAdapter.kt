package com.asilmedia.idmasters.adapters.specialist.viewpager.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.asilmedia.idmasters.models.Resume
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.ItemSpecialistBinding

class SpecialistsAdapter(
    var context: Context,
    var setOnItemClickListener: SetOnItemClickListener
) :
    ListAdapter<Resume, SpecialistsAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemSpecialists: ItemSpecialistBinding) :
        RecyclerView.ViewHolder(itemSpecialists.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(resume: Resume) {
            itemSpecialists.apply {
                Glide.with(itemSpecialists.root).load(resume.imgUrl)
                    .placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                    .into(ivSpecialist)
                tvSpecialistName.text = resume.full_name
                tvSpecialistSkills.text =
                    context.getString(R.string.skills, resume.skills)
                tvSpecialistExperience.text =
                    context.getString(R.string.experience, resume.experience)
                layout.setOnClickListener {
                    setOnItemClickListener.setOnItemClick(resume)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Resume>() {
        override fun areItemsTheSame(oldItem: Resume, newItem: Resume): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Resume, newItem: Resume): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemSpecialistBinding.inflate(
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
        fun setOnItemClick(resume: Resume)
    }

}