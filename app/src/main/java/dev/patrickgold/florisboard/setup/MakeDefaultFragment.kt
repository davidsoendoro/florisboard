/*
 * Copyright (C) 2020 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.setup

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kokatto.kobold.R
import com.kokatto.kobold.databinding.SetupFragmentMakeDefaultBinding
import dev.patrickgold.florisboard.util.checkIfImeIsEnabled
import dev.patrickgold.florisboard.util.checkIfImeIsSelected

class MakeDefaultFragment : Fragment(), SetupActivity.EventListener {
    private lateinit var binding: SetupFragmentMakeDefaultBinding

    private var isChangeReceiverRegistered = false
    private val imeChangedReceiver = InputMethodChangedReceiver {
        unregisterImeChangeReceiver()
        updateState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SetupFragmentMakeDefaultBinding.inflate(inflater, container, false)
        binding.koboldGuideLanguageInput.setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_INPUT_METHOD_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivity(intent)
        }
        binding.koboldGuidePickKeyboard.setOnClickListener {
            registerImeChangeReceiver()
            (activity as SetupActivity).imm.showInputMethodPicker()
        }

        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun applyFinishButtonDesign(view: CardView) {
        val image = view.findViewById<ImageView>(R.id.kobold_guide_image)
        val text = view.findViewById<TextView>(R.id.kobold_guide_text)
        val drawable = resources.getDrawable(R.drawable.ic_kobold_guide_done, null)

        image.setImageDrawable(drawable)
        text.text = resources.getText(R.string.kobold_guide_finish)
        text.setTextColor(resources.getColor(R.color.textColorDark, null))

        view.background = resources.getDrawable(R.drawable.bg_button_done, null)
        view.setOnClickListener {  }
    }

    private fun updateState() {
        val isKoboldActive = checkIfImeIsEnabled(requireContext())
        if (isKoboldActive) {
            applyFinishButtonDesign(binding.koboldGuideLanguageInput)
        }

        val isImeSelected = checkIfImeIsSelected(requireContext())
        if (isImeSelected) {
            applyFinishButtonDesign(binding.koboldGuidePickKeyboard)
        }

//        (activity as SetupActivity).changePositiveButtonState(isImeSelected)
//        binding.textAfterEnabled.isVisible = isImeSelected

        val actionBar = (requireActivity() as? AppCompatActivity)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        updateState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterImeChangeReceiver()
    }

    private fun unregisterImeChangeReceiver() {
        if (isChangeReceiverRegistered) {
            isChangeReceiverRegistered = false
            activity?.unregisterReceiver(imeChangedReceiver)
        }
    }

    private fun registerImeChangeReceiver() {
        if (!isChangeReceiverRegistered) {
            isChangeReceiverRegistered = true
            activity?.registerReceiver(
                imeChangedReceiver,
                IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED)
            )
        }
    }
}
