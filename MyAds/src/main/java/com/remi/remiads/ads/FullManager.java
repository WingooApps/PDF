package com.remi.remiads.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.remi.remiads.R;
import com.remi.remiads.dialog.DialogLoadAds;
import com.remi.remiads.itf.ShowAdsListen;
import com.remi.remiads.utils.RmSave;
import com.remi.remiads.utils.RmUtils;

import java.util.Date;

public class FullManager {

    private static FullManager fullAds;

    public static FullManager getInstance() {
        if (fullAds == null)
            fullAds = new FullManager();
        return fullAds;
    }

    private boolean isLoading = false;
    private long loadTime = 0;

    private InterstitialAd interstitialAd;
    private ShowAdsListen showAdsListen;
    private boolean isGoogleFist;

    private final Handler handler;

    public FullManager() {
        handler = new Handler(msg -> {
            if (FullManager.this.showAdsListen != null)
                FullManager.this.showAdsListen.onCloseAds();
            return true;
        });
    }

    /**
     * Load ads
     */
    public void loadAds(Context c) {
        if (RmSave.getPay(c) || !RmSave.isLoadAds(c)) {
            isLoading = false;
            return;
        }
        if (isAdAvailable() || isLoading)
            return;
        isLoading = true;
        isGoogleFist = RmSave.getGoogleFist(c);
        if (isGoogleFist) {
            InterstitialAd.load(c, c.getString(R.string.ads_full), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    isLoading = false;
                    if (isGoogleFist)
                        loadIrcAds();
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    FullManager.this.loadTime = (new Date()).getTime();
                    isLoading = false;
                    FullManager.this.interstitialAd = interstitialAd;
                }
            });
        } else {
            loadIrcAds();
        }
    }

    private void loadIrcAds() {
        if (IronSource.isInterstitialReady()) {
            isLoading = false;
            return;
        }
        IronSource.setInterstitialListener(new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {
                FullManager.this.loadTime = (new Date()).getTime();
                isLoading = false;
            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                isLoading = false;
            }

            @Override
            public void onInterstitialAdOpened() {

            }

            @Override
            public void onInterstitialAdClosed() {

            }

            @Override
            public void onInterstitialAdShowSucceeded() {

            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {

            }

            @Override
            public void onInterstitialAdClicked() {

            }
        });
        IronSource.loadInterstitial();
    }

    /**
     * Show ads
     */
    public void showAds(Activity activity, ShowAdsListen showAdsListen) {
        if (activity.isDestroyed() || activity.isFinishing() || RmSave.getPay(activity)) {
            if (showAdsListen != null)
                showAdsListen.onCloseAds();
            return;
        }
        this.showAdsListen = showAdsListen;
        if (isAdAvailable() || IronSource.isInterstitialReady()) {
            showAds(activity);
        } else {
            loadAds(activity);
            if (this.showAdsListen != null)
                this.showAdsListen.onCloseAds();
        }
    }

    private void showAds(Activity activity) {
        new DialogLoadAds(activity, () -> {
            if (interstitialAd != null) {
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        FullManager.this.interstitialAd = null;
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        FullManager.this.interstitialAd = null;
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        RmSave.putTimeAdsClick(activity);
                    }
                });
                interstitialAd.show(activity);
            } else if (IronSource.isInterstitialReady()) {
                IronSource.setInterstitialListener(new InterstitialListener() {
                    @Override
                    public void onInterstitialAdReady() {
                    }

                    @Override
                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                    }

                    @Override
                    public void onInterstitialAdOpened() {
                    }

                    @Override
                    public void onInterstitialAdClosed() {
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onInterstitialAdShowSucceeded() {

                    }

                    @Override
                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onInterstitialAdClicked() {

                    }
                });
                IronSource.showInterstitial();
            } else {
                if (FullManager.this.showAdsListen != null)
                    FullManager.this.showAdsListen.onCloseAds();
                loadAds(activity);
            }
        }).show();
    }

    public void showAdsNotDialog(Activity activity, ShowAdsListen showAdsListen) {
        if (activity.isDestroyed() || activity.isFinishing() || RmSave.getPay(activity)) {
            if (showAdsListen != null)
                showAdsListen.onCloseAds();
            return;
        }
        this.showAdsListen = showAdsListen;
        if (isAdAvailable() || IronSource.isInterstitialReady()) {
            if (interstitialAd != null) {
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        FullManager.this.interstitialAd = null;
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        FullManager.this.interstitialAd = null;
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        RmSave.putTimeAdsClick(activity);
                    }
                });
                interstitialAd.show(activity);
            } else if (IronSource.isInterstitialReady()) {
                IronSource.setInterstitialListener(new InterstitialListener() {
                    @Override
                    public void onInterstitialAdReady() {
                    }

                    @Override
                    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                    }

                    @Override
                    public void onInterstitialAdOpened() {
                    }

                    @Override
                    public void onInterstitialAdClosed() {
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onInterstitialAdShowSucceeded() {

                    }

                    @Override
                    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                        handler.sendEmptyMessage(1);
                        loadAds(activity);
                    }

                    @Override
                    public void onInterstitialAdClicked() {

                    }
                });
                IronSource.showInterstitial();
            } else {
                if (FullManager.this.showAdsListen != null)
                    FullManager.this.showAdsListen.onCloseAds();
                loadAds(activity);
            }
        } else {
            loadAds(activity);
            if (this.showAdsListen != null)
                this.showAdsListen.onCloseAds();
        }
    }

    public boolean isAdAvailable() {
        return interstitialAd != null && RmUtils.wasLoadTimeLessThanNHoursAgo(loadTime, 1);
    }

    public void onResume(Activity activity) {
        IronSource.onResume(activity);
    }

    public void onPause(Activity activity) {
        IronSource.onPause(activity);
    }
}

