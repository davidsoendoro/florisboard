package com.kokatto.kobold.setting


import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.kokatto.kobold.R
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity


class HelpActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_web_view)

        val mWebView = findViewById<View>(R.id.webview) as WebView
        mWebView.loadUrl("https://kobold-microsite.kokatto.net/help")

        val webSetting = mWebView.settings
        webSetting.javaScriptEnabled = true
        myWebclient()
        mWebView.webViewClient = myWebclient()

        mWebView.canGoBack()
        mWebView.setOnKeyListener(View.OnKeyListener {
            v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action == MotionEvent.ACTION_UP
                && mWebView.canGoBack()){
                mWebView.goBack()
                return@OnKeyListener true
            }
            false
        })
    }

    inner class myWebclient : WebViewClient() {
        override fun shouldOverrideUrlLoading(wv: WebView, url: String): Boolean {
            if (url.startsWith("whatsapp:")) {

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
                return true
            }
            return false
        }
    }
}
