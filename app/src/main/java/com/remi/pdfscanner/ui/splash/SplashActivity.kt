package com.remi.pdfscanner.ui.splash

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.remi.pdfscanner.base.BaseActivity
import com.remi.pdfscanner.databinding.ActivitySplashBinding
import com.remi.pdfscanner.ui.main.MainActivity
import com.remi.pdfscanner.util.setSize
import com.remi.remiads.SplashController
import java.util.*

class SplashActivity:BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {


    override fun setSize() {
        binding.tvNameApp.setSize(33)
        binding.tvAction.setSize(14)
    }

    private var splashController: SplashController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @SuppressLint("Recycle") val animator = ValueAnimator.ofFloat(1f)
        animator.addUpdateListener { animation: ValueAnimator ->
            val pro = animation.animatedValue as Float
            var pos = (pro * 100).toInt()
            if (pos >= 100) pos = 99
            binding.seekbarSplash.progress = pos
            binding.seekbarSplash.invalidate()
        }
        animator.duration = 4300
        animator.interpolator = DecelerateInterpolator(1.8f)
        animator.startDelay = 300
        animator.start()

        FirebaseApp.initializeApp(this)
        FirebaseAnalytics.getInstance(this)

        splashController = SplashController(this) { this.onEnd() }
        splashController!!.loadAds()

    }

    private fun onEnd() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (splashController != null) splashController!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (splashController != null) splashController!!.onPause()
    }
}