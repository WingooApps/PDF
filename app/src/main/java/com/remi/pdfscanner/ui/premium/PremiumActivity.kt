package com.remi.pdfscanner.ui.premium

import android.os.Bundle
import android.transition.TransitionManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.END
import androidx.constraintlayout.widget.ConstraintSet.START
import com.android.billingclient.api.SkuDetails
import com.remi.pdfscanner.base.BaseActivity
import com.remi.pdfscanner.databinding.ActivityPremiumBinding
import com.remi.pdfscanner.util.setSize
import com.remi.remiads.R
import com.remi.remiads.a_pro.MyBilling
import com.remi.remiads.a_pro.MyBillingSubsResult

class PremiumActivity : BaseActivity<ActivityPremiumBinding>(ActivityPremiumBinding::inflate) {

    var currentOption = R.string.id_sub_year

    private var myBilling: MyBilling? = null

    override fun setSize() {
        binding.tvPremium.setSize(18)
        binding.tvBottom.setSize(12)
        binding.tvMonth.setSize(12)
        binding.tvWeek.setSize(12)
        binding.tvYear.setSize(12)
        binding.tvMonthCost.setSize(20)
        binding.tvWeekCost.setSize(20)
        binding.tvYearCost.setSize(20)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.imgBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        val cts = ConstraintSet()

        binding.layoutMonth.setOnClickListener {
            currentOption = R.string.id_sub_month
            cts.apply {
                clone(binding.layoutOption)
                connect(binding.imgSelected.id, START, binding.layoutMonth.id, START)
                connect(binding.imgSelected.id, END, binding.layoutMonth.id, END)
                TransitionManager.beginDelayedTransition(binding.root)
                applyTo(binding.layoutOption)
            }
        }
        binding.layoutYear.setOnClickListener {
            currentOption = R.string.id_sub_year
            cts.apply {
                clone(binding.layoutOption)
                connect(binding.imgSelected.id, START, binding.layoutYear.id, START)
                connect(binding.imgSelected.id, END, binding.layoutYear.id, END)
                TransitionManager.beginDelayedTransition(binding.root)
                applyTo(binding.layoutOption)
            }
        }
        binding.layoutWeek.setOnClickListener {
            currentOption = R.string.id_sub_week
            cts.apply {
                clone(binding.layoutOption)
                connect(binding.imgSelected.id, START, binding.layoutWeek.id, START)
                connect(binding.imgSelected.id, END, binding.layoutWeek.id, END)
                TransitionManager.beginDelayedTransition(binding.root)
                applyTo(binding.layoutOption)
            }
        }

        cts.apply {
            clone(binding.layoutOption)
            connect(binding.imgSelected.id, START, binding.layoutYear.id, START)
            connect(binding.imgSelected.id, END, binding.layoutYear.id, END)
            TransitionManager.beginDelayedTransition(binding.root)
            applyTo(binding.layoutOption)
        }

        myBilling = MyBilling(this, false) { this.getPrice() }

        binding.tvPremium.setOnClickListener {
            myBilling!!.makePurchaseSubscription(
                getString(currentOption),
                object : MyBillingSubsResult {
                    override fun onPurchasesDone() {
                        setResult(RESULT_OK)
                        Toast.makeText(this@PremiumActivity, "Complete", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onPurchasesProcessing() {}
                    override fun onPurchasesCancel() {}
                    override fun onNotConnect() {
                        Toast.makeText(this@PremiumActivity, "Error", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPurchaseError() {
                        Toast.makeText(this@PremiumActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun getPrice() {
        if (isDestroyed || isFinishing) return
        myBilling!!.getSkuList(
            { arrPrice: List<SkuDetails> ->
                runOnUiThread {
                    for (s in arrPrice) {
                        when (s.sku) {
                            getString(R.string.id_sub_week) -> {
                                binding.tvWeekCost.text = s.price
                            }

                            getString(R.string.id_sub_year) -> {
                                binding.tvYearCost.text = s.price
                            }

                            else -> {
                                binding.tvMonthCost.text = s.price
                            }
                        }
                    }
                }
            },
            getString(R.string.id_sub_month),
            getString(R.string.id_sub_year),
            getString(R.string.id_sub_week)
        )
    }

}