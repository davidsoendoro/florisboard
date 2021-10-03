package com.kokatto.kobold.login.slider

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SliderAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val listFragment = listOf(Slider1Fragment(), Slider2Fragment(), Slider3Fragment())

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return listFragment.get(position)
    }

}
