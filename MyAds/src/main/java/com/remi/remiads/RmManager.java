package com.remi.remiads;

import android.app.Activity;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.ironsource.mediationsdk.IronSource;
import com.remi.remiads.utils.RmSave;

public class RmManager {

    public RmManager(Activity a) {
        MobileAds.initialize(a);
        IronSource.init(a, a.getString(R.string.ads_irc));
        AudienceNetworkAds.initialize(a);
        RmSave.putNativeAds(a);
    }
}