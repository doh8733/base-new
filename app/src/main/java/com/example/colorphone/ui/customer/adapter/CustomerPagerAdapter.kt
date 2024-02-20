package com.colorphone.callscreen.ui.customer.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.RequestManager
import com.example.colorphone.ui.customer.AllFragment
import com.example.colorphone.ui.customer.PhotoFragment
import com.example.colorphone.ui.customer.VideoFragment
import com.example.colorphone.util.PrefUtil

class CustomerPagerAdapter(val prefUtil: PrefUtil, val glide: RequestManager, fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllFragment(glide,prefUtil)
            1 ->  PhotoFragment(glide, prefUtil)
            2 ->  VideoFragment(glide,prefUtil)
            else -> AllFragment(glide,prefUtil)
        }
    }
}