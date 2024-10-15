package com.remi.remiads;

import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Handler;

import com.remi.remiads.a_pro.MyBilling;
import com.remi.remiads.ads.FullManager;
import com.remi.remiads.ads.OpenAdManager;
import com.remi.remiads.itf.SplashResult;
import com.remi.remiads.utils.RmSave;

public class SplashController {

    private boolean time, load;
    private boolean pause;
    private OpenAdManager openAdManager;
    private final Activity activity;
    private final SplashResult splashResult;

    public SplashController(Activity activity, SplashResult splashResult) {
        this.activity = activity;
        this.splashResult = splashResult;
        new MyBilling(activity, true, null);
        if (!RmSave.getPay(activity))
            new RmManager(activity);
    }

    public void loadAds() {
        if (RmSave.getPay(activity)) {
            splashResult.onEnd();
            return;
        }
        openAdManager = new OpenAdManager();
        openAdManager.loadAd(activity, () -> {
            load = true;
            check();
        });
        CountDownTimer countDownTimer = new CountDownTimer(2 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                time = true;
                check();
            }
        };
        countDownTimer.start();
    }

    private void check() {
        if (!load || !time)
            return;
        if (activity.isFinishing() || activity.isDestroyed())
            return;
        if (pause) {
            activity.finish();
            return;
        }
        openAdManager.showAdIfAvailable(activity, () -> new Handler().postDelayed(() -> {
            FullManager.getInstance().loadAds(activity);
            splashResult.onEnd();
        }, 1000));
    }

    public void onResume() {
        pause = false;
    }

    public void onPause() {
        pause = true;
    }
}
