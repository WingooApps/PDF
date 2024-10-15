package com.remi.remiads.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.remi.remiads.R;

public class DialogLoadAds extends Dialog {

    private final LoadDone loadDone;
    private final Activity activity;

    public DialogLoadAds(@NonNull Context context, LoadDone loadDone) {
        super(context);
        activity = (Activity) context;
        this.loadDone = loadDone;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int w = getContext().getResources().getDisplayMetrics().widthPixels;
        LinearLayout lAll = new LinearLayout(getContext());
        lAll.setGravity(Gravity.CENTER);
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setBackground(bgLayout(Color.parseColor("#f1ffffff"), w / 20));
        lAll.addView(ll, w / 2, -2);
        setContentView(lAll);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(R.string.ads_load);
        tvTitle.setTextColor(Color.parseColor("#454545"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            tvTitle.setTypeface(Typeface.create(Typeface.SANS_SERIF, 400, false));
        else
            tvTitle.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, w * 4f / 100);
        tvTitle.setPadding(0, w / 18, 0, w / 50);
        ll.addView(tvTitle, -2, -2);

        ProgressBar pr = new ProgressBar(getContext());
        LinearLayout.LayoutParams pP = new LinearLayout.LayoutParams(w / 20, w / 20);
        pP.setMargins(0, 0, 0, w / 18);
        ll.addView(pr, pP);

        new Handler().postDelayed(() -> {
            if (activity.isFinishing() || activity.isDestroyed())
                return;
            try {
                cancel();
            } catch (Exception ignored) {

            }
            loadDone.onDone();
        }, 1000);
    }

    private GradientDrawable bgLayout(int color, int size) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(size / 2f);
        drawable.setColor(color);
        return drawable;
    }

    public interface LoadDone {
        void onDone();
    }
}
