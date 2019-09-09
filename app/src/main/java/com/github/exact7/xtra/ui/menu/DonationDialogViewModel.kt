package com.github.exact7.xtra.ui.menu

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.github.exact7.xtra.ui.common.BaseViewModel

class DonationDialogViewModel : BaseViewModel() {

    private var billingClient: BillingClient? = null
    private var skuDetailsList: List<SkuDetails>? = null

    private val _state = MutableLiveData<Boolean>()
    val state: LiveData<Boolean>
        get() = _state

    fun launchBillingFlow(activity: Activity, index: Int) {
        if (billingClient == null) {
            Log.i("DONATION", "billingClient null")
            billingClient = BillingClient.newBuilder(activity)
                    .setListener { billingResult, purchases ->
                        Log.i("DONATION", "Result ${billingResult.responseCode}, $purchases")
                        _state.value = billingResult.responseCode == BillingClient.BillingResponseCode.OK
                    }
                    .enablePendingPurchases()
                    .build().also {
                        it.startConnection(object : BillingClientStateListener {
                            override fun onBillingSetupFinished(billingResult: BillingResult) {
                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    val skuDetails = SkuDetailsParams.newBuilder()
                                            .setSkusList(listOf("donation_0.99", "donation_2.49", "donation_3.49", "donation_4.99", "donation_7.49", "donation_9.99"))
                                            .setType(BillingClient.SkuType.INAPP)
                                            .build()
                                    it.querySkuDetailsAsync(skuDetails) { skuDetailsBillingResult, skuDetailsList ->
                                        if (skuDetailsBillingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                            Log.i("DONATION", "Query sku details ${billingResult.responseCode}")
                                            if (skuDetailsList.isEmpty()) return@querySkuDetailsAsync
                                            this@DonationDialogViewModel.skuDetailsList = skuDetailsList
                                            launch(activity, index)
                                        }
                                    }
                                }
                            }

                            override fun onBillingServiceDisconnected() {
                                Log.i("DONATION", "onBillingServiceDisconnected")
                            }
                        })
                    }
        } else {
            Log.i("DONATION", "billingClient not null")
            launch(activity, index)
        }
    }

    private fun launch(activity: Activity, index: Int) {
        skuDetailsList?.let {
            val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it[index])
                    .build()
            billingClient!!.launchBillingFlow(activity, flowParams)
        }
    }

    override fun onCleared() {
        billingClient?.endConnection()
        super.onCleared()
    }
}