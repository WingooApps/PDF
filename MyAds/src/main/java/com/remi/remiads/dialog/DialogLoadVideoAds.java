package com.remi.remiads.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.remi.remiads.R;
import com.remi.remiads.itf.DialogAdsResult;

public class DialogLoadVideoAds extends Dialog {

    private final DialogAdsResult dialogResult;

    public DialogLoadVideoAds(@NonNull Context context, DialogAdsResult dialogResult) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(flags);
            }
        });
        this.dialogResult = dialogResult;
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
        lAll.addView(ll, w * 72 / 100, -2);
        setContentView(lAll);
        int pa = w / 25;
        ll.setBackground(bgLayout(getContext(), Color.parseColor("#eaffffff")));

        TextView tvTitle = new TextView(getContext());
        tvTitle.setTextColor(Color.BLACK);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, w * 4.3f / 100);
        tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        tvTitle.setText(com.remi.remiads.R.string.title_ads_video);
        tvTitle.setSingleLine();
        LinearLayout.LayoutParams pTitle = new LinearLayout.LayoutParams(-2, -2);
        pTitle.setMargins(pa, pa, pa, pa / 6);
        ll.addView(tvTitle, pTitle);

        TextView tvContent = new TextView(getContext());
        tvContent.setTextColor(Color.BLACK);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, w * 3.3f / 100);
        tvContent.setGravity(Gravity.CENTER_HORIZONTAL);
        tvContent.setText(R.string.content_ads_video);
        LinearLayout.LayoutParams pContent = new LinearLayout.LayoutParams(-2, -2);
        pContent.setMargins(pa, pa * 2 / 3, pa, pa * 3 / 2);
        ll.addView(tvContent, pContent);

        int size = w * 13 / 100;

        TextView tvPro = new TextView(getContext());
        tvPro.setBackgroundResource(R.drawable.bg_tv_pro);
        tvPro.setTextColor(Color.WHITE);
        tvPro.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        tvPro.setTextSize(TypedValue.COMPLEX_UNIT_PX, w * 4.3f / 100);
        tvPro.setText(R.string.premium);
        tvPro.setGravity(Gravity.CENTER);
        tvPro.setOnClickListener(v -> {
            cancel();
            dialogResult.onPremium();
        });
        LinearLayout.LayoutParams pPro = new LinearLayout.LayoutParams(-1, size);
        pPro.setMargins(pa, pa / 2, pa, pa);
        ll.addView(tvPro, pPro);


        int sVideo = w * 6 / 100;
        LinearLayout llVideoAds = new LinearLayout(getContext());
        llVideoAds.setOrientation(LinearLayout.HORIZONTAL);
        llVideoAds.setGravity(Gravity.CENTER);
        llVideoAds.setPadding(pa, 0, pa, 0);
        llVideoAds.setBackgroundResource(R.drawable.bg_tv_watch);
        LinearLayout.LayoutParams pVideo = new LinearLayout.LayoutParams(-1, size);
        pVideo.setMargins(pa, 0, pa, pa);
        ll.addView(llVideoAds, pVideo);
        ImageView imVideo = new ImageView(getContext());
        imVideo.setImageResource(R.drawable.ic_video_ads);
        llVideoAds.addView(imVideo, sVideo, sVideo);
        TextView tvVideo = new TextView(getContext());
        tvVideo.setText(R.string.watch_video);
        tvVideo.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        tvVideo.setTextColor(Color.WHITE);
        tvVideo.setTextSize(TypedValue.COMPLEX_UNIT_PX, w * 4.4f / 100);
        tvVideo.setPadding(pa / 2, 0, 0, 0);
        llVideoAds.addView(tvVideo, -2, -2);
        llVideoAds.setOnClickListener(v -> {
            cancel();
            dialogResult.onPlayVideo();
        });
    }

    private GradientDrawable bgLayout(Context c, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(c.getResources().getDisplayMetrics().widthPixels * 4f / 80);
        drawable.setColor(color);
        return drawable;
    }
}
