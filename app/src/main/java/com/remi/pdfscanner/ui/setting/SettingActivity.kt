package com.remi.pdfscanner.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.remi.pdfscanner.base.BaseActivity
import com.remi.pdfscanner.databinding.ActivitySettingBinding
import com.remi.pdfscanner.ui.dialog.RateDialog
import com.remi.pdfscanner.ui.premium.PremiumActivity
import com.remi.pdfscanner.util.ActionUtils
import com.remi.pdfscanner.util.setSize
import com.remi.remiads.ads.FullManager
import com.remi.remiads.utils.RmSave

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {


    override fun setSize() {
        binding.tvTitle.setSize(20)
        binding.tvScan.setSize(14)
        binding.tvPdf.setSize(14)
        binding.tvMore.setSize(14)
        binding.tvShare.setSize(14)
        binding.tvRate.setSize(14)
        binding.tvFeedback.setSize(14)
        binding.tvPolicy.setSize(14)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onClick()

    }

    private fun onClick() {
        binding.btnScanSetting.setOnClickListener {
            FullManager.getInstance().showAds(this) {
                startActivity(Intent(this, ScanSettingActivity::class.java))
            }
        }
        binding.btnPdfSetting.setOnClickListener {
            FullManager.getInstance().showAds(this) {
                startActivity(Intent(this, PdfSettingActivity::class.java))
            }
        }
        binding.btnMore.setOnClickListener {
            ActionUtils.openOtherApps(this)
        }
        binding.btnRate.setOnClickListener {
            RateDialog(this).show()
        }
        binding.btnShare.setOnClickListener {
            ActionUtils.shareApp(this)
        }
        binding.btnFeedback.setOnClickListener {
            ActionUtils.sendFeedback(this)
        }
        binding.btnPolicy.setOnClickListener {
            ActionUtils.openPolicy(this)
        }
        binding.btnPremium.setOnClickListener {
            startActivity(Intent(this, PremiumActivity::class.java))
        }
        binding.imgBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        FullManager.getInstance().onResume(this)
        if (RmSave.getPay(this))
            binding.imgBanner.visibility = View.GONE
    }

    override fun onPause() {
        FullManager.getInstance().onPause(this)
        super.onPause()
    }
}