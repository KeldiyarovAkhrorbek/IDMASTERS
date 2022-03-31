package com.masters.idmasters.adapters.vacancy

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.masters.idmasters.R
import com.masters.idmasters.databinding.ItemVacancyBinding
import com.masters.idmasters.models.Vacancy

class VacanciesAdapter(
    var context: Context,
    var setOnItemClickListener: SetOnItemClickListener
) :
    ListAdapter<Vacancy, VacanciesAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemVacancyBinding: ItemVacancyBinding) :
        RecyclerView.ViewHolder(itemVacancyBinding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(vacancy: Vacancy) {
            itemVacancyBinding.apply {
                Glide.with(itemVacancyBinding.root).load(vacancy.imgUrl)
                    .placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                    .into(ivSpecialist)
                tvCompanyName.text = vacancy.company_name
                tvWhoIsNeeded.text =
                    vacancy.speciality + " " + context.getString(R.string.programmer)
                tvSalary.text = vacancy.salary
                layout.setOnClickListener {
                    setOnItemClickListener.setOnItemClick(vacancy)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Vacancy>() {
        override fun areItemsTheSame(oldItem: Vacancy, newItem: Vacancy): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Vacancy, newItem: Vacancy): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemVacancyBinding.inflate(
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
        fun setOnItemClick(vacancy: Vacancy)
    }

}