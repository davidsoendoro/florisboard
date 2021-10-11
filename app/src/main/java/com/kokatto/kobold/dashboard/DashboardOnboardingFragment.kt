package com.kokatto.kobold.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kokatto.kobold.databinding.FragmentDashboardOnboardingBinding
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * Use the [DashboardOnboardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardOnboardingFragment : Fragment() {
    private lateinit var binding: FragmentDashboardOnboardingBinding
    private var dashboardActivityListener: DashboardActivityListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            dashboardActivityListener = context as DashboardActivityListener
        } catch (castException: ClassCastException) {
            // Listener cannot be attached
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardOnboardingBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.koboldDashboardOnboardingBecomeProfitMasterButton.setOnClickListener {
            dashboardActivityListener?.onOKButtonClick(1)
        }
        binding.koboldDashboardOnboardingLaterButton.setOnClickListener {
            dashboardActivityListener?.onLaterClick(1)
        }
    }

}

