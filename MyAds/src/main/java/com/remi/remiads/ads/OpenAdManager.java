package com.remi.remiads.ads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.remi.remiads.itf.LoadAdsListen;
import com.remi.remiads.itf.ShowAdsListen;
import com.remi.remiads.utils.RmSave;
import com.remi.remiads.utils.RmUtils;

import java.util.Date;

public class OpenAdManager {

    private boolean isShowingAd = false;
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private long loadTime = 0;

    public void loadAd(Context context, @NonNull LoadAdsListen loadAdsListen) {
        if (isLoadingAd || isAdAvailable() || RmSave.getPay(context) || !RmSave.isLoadAds(context)) {
            loadAdsListen.onLoaded();
            return;
        }
        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(context, context.getString(com.remi.remiads.R.string.ads_open), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                        loadAdsListen.onLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAd = false;
                        loadAdsListen.onLoaded();
                    }
                });
    }

    private boolean isAdAvailable() {
        return appOpenAd != null && RmUtils.wasLoadTimeLessThanNHoursAgo(loadTime, 4);
    }

    public void showAdIfAvailable(@NonNull final Activity activity, @NonNull ShowAdsListen showAdsListen) {
        if (isShowingAd)
            return;
        if (RmSave.getPay(activity)) {
            showAdsListen.onCloseAds();
            return;
        }
        if (!isAdAvailable()) {
            showAdsListen.onCloseAds();
            return;
        }
        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                isShowingAd = false;
                showAdsListen.onCloseAds();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                appOpenAd = null;
                isShowingAd = false;
                showAdsListen.onCloseAds();
            }

            @Override
            public void onAdShowedFullScreenContent() {
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                RmSave.putTimeAdsClick(activity);
            }
        });

        isShowingAd = true;
        appOpenAd.show(activity);
    }

}
