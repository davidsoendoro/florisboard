package com.kokatto.kobold.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.FragmentDashboardOnboardingBinding


/**
 * A simple [Fragment] subclass.
 * Use the [DashboardOnboardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardOnboardingFragment : Fragment() {
    private lateinit var binding: FragmentDashboardOnboardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_onboarding, container, false)
    }

    override fun onResume() {
        super.onResume()
    }
}
