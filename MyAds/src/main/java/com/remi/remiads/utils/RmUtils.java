package com.remi.remiads.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.widget.Toast;


import com.remi.remiads.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

public class RmUtils {

    public static final String LINK_FIST = "https://dl.dropboxusercontent.com/s/qcermu4e1d09u0r/thietlap_qc.txt?dl=0";
    public static final String ADS_OTHER = "https://dl.dropboxusercontent.com/s/6jr5xgaiihxovyp/native_ads_new.txt?dl=0";

    private static final String EMAIL = "@gmail.com";
    private static final String TITLE_EMAIL = ": Feedback";

    public static String getTextWithUrl(String link) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(link);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                result.append(str);
            }
            in.close();
        } catch (Exception ignored) {
        }
        if (result.length() > 0)
            return result.toString();
        return "";
    }

    public static boolean wasLoadTimeLessThanNHoursAgo(long loadTime, int numHours) {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public static boolean isInternetAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean isPackageInstalled(Context c, String pkg) {
        if (pkg == null || pkg.isEmpty())
            return false;
        try {
            c.getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void ratePkg(Context context, String pkg) {
        Uri uri = Uri.parse("market://details?id=" + pkg);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void feedback(Context c) {
        String[] email = {EMAIL};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, TITLE_EMAIL);
        try {
            c.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(c, c.getString(com.remi.remiads.R.string.no_email), Toast.LENGTH_SHORT).show();
        }
    }
}
