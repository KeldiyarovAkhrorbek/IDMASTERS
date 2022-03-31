package com.masters.idmasters.fragments.main.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.masters.idmasters.R
import com.masters.idmasters.activities.MainActivity
import com.masters.idmasters.adapters.NewsViewPagerAdapter
import com.masters.idmasters.databinding.FragmentNewsBinding
import com.masters.idmasters.databinding.ItemTabBinding


class NewsFragment : Fragment() {
    private lateinit var binding: FragmentNewsBinding
    private var mRootView: ViewGroup? = null
    private var mIsFirstLoad = false
    private lateinit var newsViewPagerAdapter: NewsViewPagerAdapter
    private var created = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        newsViewPagerAdapter = NewsViewPagerAdapter(this)
        binding.viewpager.adapter = newsViewPagerAdapter
        val list = ArrayList<String>()
        list.add(resources.getString(R.string.the_newest))
        list.add(resources.getString(R.string.statistics))
        TabLayoutMediator(
            binding.tabLayout,
            binding.viewpager
        ) { tab: TabLayout.Tab, position: Int ->
            val itemTabBinding: ItemTabBinding = ItemTabBinding.inflate(layoutInflater)
            tab.customView = itemTabBinding.root
            itemTabBinding.text.text = list[position]
            if (position == 0) {
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                    text.setTextColor(resources.getColor(R.color.white))
                }
            } else {
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.white))
                    text.setTextColor(resources.getColor(R.color.tab_color))
                }
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.tab_color))
                    text.setTextColor(resources.getColor(R.color.white))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val itemTabBinding = ItemTabBinding.bind(tab.customView!!)
                with(itemTabBinding) {
                    card.setCardBackgroundColor(resources.getColor(R.color.white))
                    text.setTextColor(resources.getColor(R.color.tab_color))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.viewpager.isUserInputEnabled = false
        return binding.root
    }

    override fun onResume() {
        (activity as MainActivity).hideBottom()
        super.onResume()
    }

    override fun onPause() {
        (activity as MainActivity).showBottom()
        super.onPause()
    }


}