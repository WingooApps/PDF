package com.remi.remiads.a_pro;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.remi.remiads.R;
import com.remi.remiads.utils.RmSave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyBilling implements BillingClientStateListener {

    private final Activity activity;
    private BillingClient billingClient;
    private boolean isConnected;

    private MyBillingResult myBillingResult;
    private MyBillingSubsResult mBillingSubsResult;

    public enum PURCHASE_TYPE {
        PURCHASE_ONE_TIME,
        PURCHASE_SUBSCRIPTION,
        PURCHASE_SUBS_CACHE,
        PURCHASE_UNSPECIFIED
    }

    private PURCHASE_TYPE mPurchaseType = PURCHASE_TYPE.PURCHASE_UNSPECIFIED;

    private final BillingConnectListen billingConnectListen;
    private final boolean isCheck;

    public MyBilling(Activity a, boolean isCheck, BillingConnectListen billingConnectListen) {
        this.activity = a;
        this.isCheck = isCheck;
        this.billingConnectListen = billingConnectListen;
        isConnected = false;
        billingClient = BillingClient.newBuilder(a)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(this);

    }

    public final PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(@NonNull BillingResult billingResult, List<Purchase> purchases) {
            if ((billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    || billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)
                    && purchases != null) {
                if (mPurchaseType == PURCHASE_TYPE.PURCHASE_ONE_TIME) {
                    for (Purchase purchase : purchases) {
                        for (String arrId : purchase.getSkus()) {
                            if (arrId.equals(activity.getString(R.string.id_pay))) {
                                RmSave.putPayLifetime(activity, true);
                                handlePurchaseOneTime(purchase);
                                sendPurchasesResult();
                            } else {
                                if (myBillingResult != null)
                                    myBillingResult.onPurchasesProcessing();
                                handlePurchaseMoreTime(purchase);
                            }
                        }
                    }
                } else if (mPurchaseType == PURCHASE_TYPE.PURCHASE_SUBSCRIPTION) {
                    for (Purchase purchase : purchases) {
                        for (String purchaseId : purchase.getSkus()) {
                            if (purchaseId.equals(activity.getString(R.string.id_sub_month)) || purchaseId.equals(activity.getString(R.string.id_sub_year))
                                    || purchaseId.equals(activity.getString(R.string.id_sub_week))) {
                                saveCache(purchase);
                                RmSave.putPaySubscription(activity, true);
                                if (mBillingSubsResult != null)
                                    mBillingSubsResult.onPurchasesDone();
                                handlePurchaseSubscription(purchase);
                                return;
                            }
                        }
                    }
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR
                    || billingResult.getResponseCode() == BillingClient.BillingResponseCode.DEVELOPER_ERROR
                    || billingResult.getResponseCode() < 0) {
                if (mPurchaseType == PURCHASE_TYPE.PURCHASE_ONE_TIME) {
                    if (myBillingResult != null)
                        myBillingResult.onPurchasesCancel();
                } else if (mPurchaseType == PURCHASE_TYPE.PURCHASE_SUBSCRIPTION) {
                    if (mBillingSubsResult != null)
                        mBillingSubsResult.onPurchaseError();
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                if (mPurchaseType == PURCHASE_TYPE.PURCHASE_ONE_TIME) {
                    if (myBillingResult != null)
                        myBillingResult.onPurchasesCancel();
                } else if (mPurchaseType == PURCHASE_TYPE.PURCHASE_SUBSCRIPTION) {
                    if (mBillingSubsResult != null)
                        mBillingSubsResult.onPurchasesCancel();
                }
            }
        }
    };

    private void handlePurchaseOneTime(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken()).build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {

                });
            }
        }
    }

    private void sendPurchasesResult() {
        if (myBillingResult != null)
            myBillingResult.onPurchasesDone();
    }

    private void handlePurchaseMoreTime(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                sendPurchasesResult();
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    private void handlePurchaseSubscription(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            //todo: Verify Perchase using DEVELOPER private key (optional)
            if (purchase.isAcknowledged()) {
                RmSave.putPaySubscription(activity, true);
                if (mBillingSubsResult != null)
                    mBillingSubsResult.onPurchasesDone();
            } else {
                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            RmSave.putPaySubscription(activity, true);
                            if (mBillingSubsResult != null)
                                mBillingSubsResult.onPurchasesDone();
                        }
                    }
                });
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            if (mBillingSubsResult != null)
                mBillingSubsResult.onPurchasesProcessing();
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            if (mBillingSubsResult != null)
                mBillingSubsResult.onPurchaseError();
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        isConnected = false;
        reConnectToGooglePlay();
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        int responseCode = billingResult.getResponseCode();
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            isConnected = true;
            if (isCheck) {
                checkHistory();
                checkPurchaseSubscriptionCache();
            }
            if (billingConnectListen != null)
                billingConnectListen.onConnected();
        } else {
            reConnectToGooglePlay();
        }
    }

    private void reConnectToGooglePlay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (billingClient != null)
                billingClient.startConnection(MyBilling.this);
        }, 1000);
    }

    /**
     * Check if one-time Product is purchased in previous time or not
     */
    private void checkHistory() {
        if (!RmSave.getPay(activity))
            billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                    (billingResult, purchasesList) -> {
                        boolean flag = (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ||
                                billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK);
                        if (purchasesList != null) {
                            for (PurchaseHistoryRecord purchase : purchasesList) {
                                if (purchase != null) {
                                    for (String arrId : purchase.getSkus()) {
                                        if (arrId.equals(activity.getString(R.string.id_pay))) {
                                            RmSave.putPayLifetime(activity, flag);
                                        }
                                    }
                                }
                            }
                        }
                    });
    }

    /**
     * Get purchases details for currently owned items bought within our app. Only active subscriptions and non-consumed one-time purchases are returned
     * This method uses a cache of Google Play Store app without initiating a network request
     * We using this method to check subcription status change of user in case of they cancelling Subscription.
     */
    public void checkPurchaseSubscriptionCache() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                if (list.size() <= 0) {
                    // If no item in purchase list means subscription is not subscribed, Or subscription is cancelled and not renewed for next month
                    RmSave.putPaySubscription(activity, false);
                    RmSave.putSubToken(activity, "");
                    RmSave.putSubType(activity, "");
                } else {
                    RmSave.putPaySubscription(activity, true);
                    for (Purchase purchase : list) {
                        if (purchase != null) {
                            saveCache(purchase);
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Initial One-time purchase, show Purchase dialog to user
     */
    public void makePurchase(String key, MyBillingResult _myBillingResult) {
        mPurchaseType = PURCHASE_TYPE.PURCHASE_ONE_TIME;
        this.myBillingResult = _myBillingResult;
        if (!isConnected) {
            if (myBillingResult != null)
                myBillingResult.onNotConnect();
            return;
        }
        ArrayList<String> arrKey = new ArrayList<>();
        arrKey.add(key);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(arrKey).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(), ((billingResult, listSkuDetails) -> {
            int responseCode = billingResult.getResponseCode();
            if (responseCode == BillingClient.BillingResponseCode.OK && listSkuDetails != null && listSkuDetails.size() > 0) {
                for (SkuDetails skuDetails : listSkuDetails) {
                    if (skuDetails.getSku().equals(key)) {
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();
                        billingClient.launchBillingFlow(activity, flowParams);
                        return;
                    }
                }
            } else {
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    /**
     * Launcher purchase subscription dialog
     */
    public void makePurchaseSubscription(String productId, MyBillingSubsResult _billingSubsResult) {
        mPurchaseType = PURCHASE_TYPE.PURCHASE_SUBSCRIPTION;
        this.mBillingSubsResult = _billingSubsResult;
        if (!isConnected) {
            if (mBillingSubsResult != null)
                mBillingSubsResult.onNotConnect();
            return;
        }

        List<String> skuList = new ArrayList<>();
        skuList.add(productId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();

        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        /** Query our Product from Play Console */
        billingClient.querySkuDetailsAsync(params.build(), (billingResult, listSkuDetails) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && listSkuDetails != null && listSkuDetails.size() > 0) {
                for (SkuDetails skuDetail : listSkuDetails) {
                    if (skuDetail.getSku().equals(productId)) {
                        BillingFlowParams flowParams;
                        String tokenOld = RmSave.getSubToken(activity);
                        if (tokenOld == null || tokenOld.isEmpty())
                            flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(skuDetail)
                                    .build();
                        else
                            flowParams = BillingFlowParams.newBuilder()
                                    .setSubscriptionUpdateParams(BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                                            .setOldSkuPurchaseToken(tokenOld)
                                            .setReplaceSkusProrationMode(BillingFlowParams.ProrationMode.IMMEDIATE_WITHOUT_PRORATION).build())
                                    .setSkuDetails(skuDetail)
                                    .build();
                        billingClient.launchBillingFlow(activity, flowParams);
                        return;
                    }
                }
            }
        });
    }

    private void saveCache(Purchase purchase) {
        RmSave.putSubToken(activity, purchase.getPurchaseToken());
        for (String skus : purchase.getSkus())
            RmSave.putSubType(activity, skus);
    }

    public void getSkuList(PriceResult priceResult, String... list) {
        if (!isConnected) {
            priceResult.onListPrice(new ArrayList<>());
            return;
        }
        ArrayList<String> arrKey = new ArrayList<>();
        Collections.addAll(arrKey, list);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(arrKey).setType(BillingClient.SkuType.INAPP);
        try {
            billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
                int responseCode = billingResult.getResponseCode();
                if (responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null && skuDetailsList.size() > 0) {
                    priceResult.onListPrice(skuDetailsList);
                }
            });
            params.setType(BillingClient.SkuType.SUBS);
            billingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
                int responseCode = billingResult.getResponseCode();
                if (responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null && skuDetailsList.size() > 0) {
                    priceResult.onListPrice(skuDetailsList);
                }
            });
        } catch (NullPointerException e) {
            priceResult.onListPrice(new ArrayList<>());
        }
    }

    public void onDestroy() {
        try {
            if (this.billingClient != null) {
                this.billingClient.endConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.billingClient = null;
    }
}
