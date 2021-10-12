package com.kokatto.kobold.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.ActivityDashboardBinding
import com.kokatto.kobold.databinding.FragmentActivateKeyboardBinding
import dev.patrickgold.florisboard.settings.SettingsMainActivity
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * Use the [ActivateKeyboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActivateKeyboardFragment : Fragment() {
    private lateinit var binding: FragmentActivateKeyboardBinding
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
        // Inflate the layout for this fragment
        binding = FragmentActivateKeyboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.koboldActivateKeyboardButton.setOnClickListener {
            dashboardActivityListener?.onOKButtonClick(2)
        }

        binding.koboldActivateKeyboardLaterButton.setOnClickListener {
            dashboardActivityListener?.onLaterClick(2)
        }
    }
}