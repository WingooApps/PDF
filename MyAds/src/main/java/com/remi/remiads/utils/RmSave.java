package com.remi.remiads.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.remi.remiads.nativenew.AdsNativeItem;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RmSave {

    private static final long TIME_END_ADS_CLICK = 180000;
    private static final long TIME_LOAD_NATIVE = 600000;

    private static SharedPreferences getShare(Context c) {
        return c.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    //PREMIUM
    public static boolean getPay(Context c) {
        return getShare(c).getBoolean("pay_subs", false)
                || getShare(c).getBoolean("pay_lifetime", false)
                || getShare(c).getBoolean("pay", false);
    }

    //Subscription
    public static void putPaySubscription(Context c, boolean isPaid) {
        getShare(c).edit().putBoolean("pay_subs", isPaid).apply();
    }

    public static void putPayLifetime(Context c, boolean isPaid) {
        getShare(c).edit().putBoolean("pay_lifetime", isPaid).apply();
    }

    public static void putSubToken(Context c, String type) {
        getShare(c).edit().putString("pay_sub_Token", type).apply();
    }

    public static String getSubToken(Context c) {
        return getShare(c).getString("pay_sub_Token", "");
    }

    public static void putSubType(Context c, String key) {
        getShare(c).edit().putString("sub_type", key).apply();
    }

    public static String getSubType(Context c) {
        return getShare(c).getString("sub_type", null);
    }

    public static boolean isFistOpen(Context c) {
        return getShare(c).getBoolean("fist_open", true);
    }

    public static void putFistOpen(Context c) {
        getShare(c).edit().putBoolean("fist_open", false).apply();
    }

    public static boolean isRate(Context c) {
        return getShare(c).getBoolean("is_rate", false);
    }

    public static void rated(Context c) {
        getShare(c).edit().putBoolean("is_rate", true).apply();
    }

    public static void putFist(Context c, String str) {
        getShare(c).edit().putString("key_fist", str).apply();
    }

    public static boolean getGoogleFist(Context c) {
        String str = getShare(c).getString("key_fist", "");
        if (str.isEmpty())
            return true;
        try {
            Type type = new TypeToken<ItemFist>() {
            }.getType();
            ItemFist itemNative = new Gson().fromJson(str, type);
            if (itemNative != null)
                return itemNative.googleFist(c.getPackageName());
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    public static void putNativeAds(Context c) {
        boolean time = Math.abs(System.currentTimeMillis() - getShare(c).getLong("time_load_native", 0)) >= TIME_LOAD_NATIVE;
        if (!time)
            return;
        new Thread(() -> {
            String str = RmUtils.getTextWithUrl(RmUtils.LINK_FIST);
            RmSave.putFist(c, str);
            str = RmUtils.getTextWithUrl(RmUtils.ADS_OTHER);
            if (str.isEmpty())
                return;
            getShare(c).edit().putLong("time_load_native", System.currentTimeMillis()).apply();
            getShare(c).edit().putString("native_ads_other_n", str).apply();
        }).start();
    }

    public static AdsNativeItem getItemNative(Context c) {
        String str = getShare(c).getString("native_ads_other_n", "");
        if (str.isEmpty())
            return null;
        try {
            Type type = new TypeToken<AdsNativeItem>() {
            }.getType();
            return new Gson().fromJson(str, type);
        } catch (Exception e) {
            return null;
        }
    }

    public static void putAppClick(Context c, String pkg) {
        ArrayList<String> arr = arrAppClick(c);
        for (String s : arr) {
            if (s.equals(pkg))
                return;
        }
        arr.add(pkg);
        getShare(c).edit().putString("app_click", new Gson().toJson(arr)).apply();
    }

    public static ArrayList<String> arrAppClick(Context c) {
        String str = getShare(c).getString("app_click", "");
        ArrayList<String> arr = null;
        if (str != null && !str.isEmpty()) {
            try {
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                arr = new Gson().fromJson(str, type);
            } catch (Exception ignored) {

            }
        }
        if (arr == null)
            arr = new ArrayList<>();
        return arr;
    }

    //time ads click
    public static void putTimeAdsClick(Context c) {
        getShare(c).edit().putLong("ads_click", System.currentTimeMillis()).apply();
    }

    public static boolean isLoadAds(Context c) {
        return Math.abs(System.currentTimeMillis() - getShare(c).getLong("ads_click", 0)) > TIME_END_ADS_CLICK;
    }
}
