package com.kokatto.kobold.dashboard

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.ActivityDashboardBinding
import com.kokatto.kobold.databinding.FragmentDashboardOnboardingBinding
import dev.patrickgold.florisboard.settings.SettingsMainActivity
import timber.log.Timber


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
        binding = FragmentDashboardOnboardingBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.koboldDashboardOnboardingLayout.setOnClickListener {
            Timber.d("[Onboarding Fragment] Ahli Cuan clicked")
            Toast.makeText(requireContext(), "Jadi ahli cuan!", Toast.LENGTH_LONG).show()
        }
    }
}
