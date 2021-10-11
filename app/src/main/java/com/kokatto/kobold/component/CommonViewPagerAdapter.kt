package com.kokatto.kobold.component

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import timber.log.Timber

class CommonViewPagerAdapter(fa: FragmentActivity) :
    FragmentStateAdapter(fa) {
    private val fragments: MutableList<Fragment> = mutableListOf()
    private val codes: MutableList<Int> = mutableListOf()

    override fun getItemCount(): Int {
        return fragments.size
    }

    fun addFragment(fragment: Fragment, code: Int) {
        fragments.add(fragment)
        codes.add(code)
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getPageCode(position: Int): Int {
        return codes[position]
    }

    fun getFragmentByCode(code: Int): Fragment?{
        val pos = codes.indexOf(code)
        return if(pos > -1){
            fragments[pos]
        } else null
    }

    fun getPositionByCode(code: Int): Int{
        return codes.indexOf(code)
    }

    fun removeFragmentByCode(code: Int){
        val pos = codes.indexOf(code)
        Timber.d("removing fragment pos: $pos")
        Timber.d("codes: $codes")
        if(pos > -1){
            fragments.removeAt(pos)
            codes.removeAt(pos)
            notifyItemRemoved(pos)
            notifyItemRangeChanged(pos, fragments.size)
            notifyDataSetChanged()
        }
    }
}
