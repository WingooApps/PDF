package com.remi.pdfscanner.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.remi.pdfscanner.ui.dialog.DialogRecognize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B) : AppCompatActivity(),CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job
    var isFullScreen = true

    val binding: B by lazy { bindingFactory(layoutInflater) }
    lateinit var mContext: Context
    lateinit var loadingDialog:DialogRecognize
    open fun binding() {
        val window = window
        window.navigationBarColor = Color.BLACK
        window.statusBarColor = Color.TRANSPARENT
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        getWindow().decorView.systemUiVisibility = flags
        setContentView(binding.root)
    }

    /**
     * to set size of view (TextView,..etc) by screen width
     */
    abstract fun setSize()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        mContext = this
        loadingDialog = DialogRecognize(this)
        binding()
        setSize()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}