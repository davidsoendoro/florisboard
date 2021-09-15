package com.kokatto.kobold.template

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kokatto.kobold.R
import com.kokatto.kobold.extension.showToast

class TemplateActivitySearch : AppCompatActivity() {

    private var buttonBack: ImageView? = null
    private var buttonClear: ImageView? = null
    private var searchResultFound: LinearLayout? = null
    private var searchResultNotFound: LinearLayout? = null
    private var searchEdittext: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.template_activity_search)

        buttonBack = findViewById(R.id.back_button)
        buttonClear = findViewById(R.id.clear_button)
        searchEdittext = findViewById(R.id.search_edittext)
        searchResultFound = findViewById(R.id.search_result_found_layout)
        searchResultNotFound = findViewById(R.id.search_result_not_found)

        buttonBack?.let { button -> button.setOnClickListener { onClicked(button) } }
        buttonClear?.let { button -> button.setOnClickListener { onClicked(button) } }
        searchEdittext?.let { editText -> editText.setOnKeyListener { v, keyCode, event -> onKeyEdit(v, keyCode, event) } }
    }

    private fun onKeyEdit(view: View, keyCode: Int?, event: KeyEvent?): Boolean {
        when (view.id) {
            R.id.search_edittext -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    performSearch()
                    return true
                }
            }
        }
        return false
    }

    private fun performSearch() {
        showToast("search test")
    }


    private fun onClicked(view: View) {
        when (view.id) {
            R.id.back_button -> {
                super.onBackPressed()
            }
            R.id.clear_button -> {
                searchEdittext?.setText("");
            }
        }
    }
}
