package com.masters.idmasters.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.masters.idmasters.fragments.viewpagernews.NewestFragment
import com.masters.idmasters.fragments.viewpagernews.StatisticsFragment

class NewsViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return NewestFragment()
        }
        return StatisticsFragment()
    }
}