package com.masters.idmasters.adapters.specialist.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.masters.idmasters.fragments.main.specialist.NullFragment

class SpecialistViewpagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return NullFragment()
    }
}