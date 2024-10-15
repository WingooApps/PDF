package com.remi.remiads.nativenew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.remi.remiads.R;
import com.remi.remiads.utils.RmSave;
import com.remi.remiads.utils.RmUtils;

import java.util.ArrayList;
import java.util.Random;

public class ViewNative extends RelativeLayout {

    private ItemNative itemNative;

    public ViewNative(Context context) {
        super(context);
        initView();
    }

    public ViewNative(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ViewNative(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        Handler handler = new Handler(msg -> {
            if (msg.what == 1) {
                if (itemNative != null)
                    init(itemNative);
                else
                    setVisibility(GONE);
            } else {
                setVisibility(GONE);
            }
            return true;
        });
        new Thread(() -> {
            AdsNativeItem adsNativeItem = RmSave.getItemNative(getContext());
            if (adsNativeItem == null) {
                handler.sendEmptyMessage(2);
                return;
            }
            if (adsNativeItem.gone != null && adsNativeItem.gone.size() > 0) {
                for (String s : adsNativeItem.gone) {
                    if (s.equals(getContext().getPackageName())) {
                        handler.sendEmptyMessage(2);
                        return;
                    }
                }
            }
            if (adsNativeItem.apps == null || adsNativeItem.apps.size() == 0) {
                handler.sendEmptyMessage(2);
                return;
            }
            ArrayList<String> arrAdsClick = RmSave.arrAppClick(getContext());
            ArrayList<ItemNative> arrNative = new ArrayList<>();
            ItemRemove itemRemove = null;
            if (adsNativeItem.remove != null && adsNativeItem.remove.size() > 0) {
                for (ItemRemove i : adsNativeItem.remove) {
                    if (i.pkg.equals(getContext().getPackageName())) {
                        itemRemove = i;
                        break;
                    }
                }
            }
            int max = 0;
            for (ItemNative app : adsNativeItem.apps) {
                if (app.pkg.equals(getContext().getPackageName()))
                    continue;
                boolean add = !RmUtils.isPackageInstalled(getContext(), app.pkg);
                if (add) {
                    for (String s : arrAdsClick) {
                        if (s.equals(app.pkg)) {
                            add = false;
                            break;
                        }
                    }
                    if (add && itemRemove != null && itemRemove.app != null && itemRemove.app.size() > 0) {
                        for (String s : itemRemove.app) {
                            if (s.equals(app.pkg)) {
                                add = false;
                                break;
                            }
                        }
                    }
                }
                if (add) {
                    arrNative.add(app);
                    max += app.num;
                }
            }
            if (arrNative.size() == 0) {
                handler.sendEmptyMessage(2);
                return;
            }

            int random = new Random().nextInt(max);
            int sum = 0;
            for (ItemNative aNative : arrNative) {
                sum += aNative.num;
                if (random <= sum) {
                    itemNative = aNative;
                    handler.sendEmptyMessage(1);
                    return;
                }
            }
            itemNative = arrNative.get(0);
            handler.sendEmptyMessage(1);
        }).start();
    }

    @SuppressLint({"ResourceType", "InflateParams"})
    public void init(ItemNative itemNative) {
        removeAllViews();
        int w = getContext().getResources().getDisplayMetrics().widthPixels;
        int pa = w / 50;
        CardView cv = new CardView(getContext());
        cv.setRadius(w / 40f);
        cv.setCardElevation(w / 80f);
        LayoutParams pCv = new LayoutParams(-1, -2);
        pCv.setMargins(pa, pa * 2, pa, pa);
        pCv.addRule(CENTER_HORIZONTAL);
        addView(cv, pCv);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_ads_other, null);
        cv.addView(v, -1, -1);
        ImageView imIcon = v.findViewById(R.id.ad_app_icon);
        TextView tvTitle = v.findViewById(R.id.ad_headline);
        TextView tvContent = v.findViewById(R.id.ad_body);
        ImageView imHeader = v.findViewById(R.id.ad_media);
        v.findViewById(R.id.ad_call_to_action).setOnClickListener(this::actionClick);
        Glide.with(getContext()).load(itemNative.icon).into(imIcon);
        Glide.with(getContext()).load(itemNative.cover).into(imHeader);
        tvTitle.setText(itemNative.title);
        tvContent.setText(itemNative.content);
    }

    private void actionClick(View v) {
        if (itemNative == null)
            return;
        RmUtils.ratePkg(getContext(), itemNative.pkg);
        RmSave.putAppClick(getContext(), itemNative.pkg);
    }

}
