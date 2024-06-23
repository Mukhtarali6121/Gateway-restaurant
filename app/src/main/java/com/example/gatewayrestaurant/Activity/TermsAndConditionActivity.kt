package com.example.gatewayrestaurant.Activity

import android.app.DownloadManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.Class.BaseActivity
import com.example.gatewayrestaurant.R
import com.example.gatewayrestaurant.databinding.ActivityTermsAndConditionBinding

class TermsAndConditionActivity : BaseActivity() {


    private lateinit var mBinding: ActivityTermsAndConditionBinding
    private var toolbarTitleName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_terms_and_condition)

        toolbarTitleName = intent.getStringExtra("titleName").toString()
        mBinding.toolBar.shoppingCart.visibility = View.GONE
        mBinding.toolBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        if (toolbarTitleName.contains("TermsAndCondition")) mBinding.toolBar.tvHeader.text =
            getString(R.string.terms_and_condition) else mBinding.toolBar.tvHeader.text =
            getString(R.string.privacy_policy)
        runOnUiThread { loadWebView(intent.extras!!.getString("url", "")) }

    }

    private fun loadWebView(data: String) {
        mBinding.webView.apply {
            this.settings.loadsImagesAutomatically = true
            this.settings.useWideViewPort = true
            this.settings.allowContentAccess = true
            this.settings.allowFileAccess = true
            this.settings.javaScriptEnabled = true
            this.settings.domStorageEnabled = true
            this.settings.javaScriptCanOpenWindowsAutomatically = true
            this.settings.pluginState = WebSettings.PluginState.ON
            this.settings.mediaPlaybackRequiresUserGesture = false
            this.webChromeClient = object : WebChromeClient() {
                override fun onJsAlert(
                    view: WebView?, url: String?, message: String?, result: JsResult?
                ): Boolean {
                    return super.onJsAlert(view, url, message, result)
                }

                override fun onJsBeforeUnload(
                    view: WebView?, url: String?, message: String?, result: JsResult?
                ): Boolean {
                    result!!.confirm()
                    return true
                }
            }
            this.loadUrl(data)
            this.setDownloadListener { url, _, contentDisposition, mimetype, _ ->
                val request = DownloadManager.Request(Uri.parse(url))
                request.setDescription("Download file...")
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    URLUtil.guessFileName(url, contentDisposition, mimetype)
                )
                val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()
            }
            this.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    Log.e("webViewClient: ", "UrlLoading")
                    view.loadUrl(url)
                    return false
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.e("webViewClient: ", "onPageStarted")
                    mBinding.progress.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    Log.e("webViewClient: ", "onPageFinished")
                    mBinding.progress.visibility = View.INVISIBLE
                    mBinding.webView.visibility = View.VISIBLE
                }

            }
        }
    }

}