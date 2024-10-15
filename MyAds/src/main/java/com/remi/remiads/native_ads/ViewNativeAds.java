package com.remi.remiads.native_ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.remi.remiads.R;

public class ViewNativeAds extends CardView {

    private RelativeLayout llLoad;
    private NativeAdView adView;
    private boolean small;
    private RelativeLayout llAll;
    private LoaderRect loaderRect;

    public ViewNativeAds(Context context) {
        super(context);
        init();
    }

    public ViewNativeAds(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewNativeAds(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int w = getResources().getDisplayMetrics().widthPixels;
        setCardBackgroundColor(Color.WHITE);
        setCardElevation(w / 80f);
        setRadius(w / 50f);
        llAll = new RelativeLayout(getContext());
        addView(llAll, -1, -2);

        loaderRect = new LoaderRect();
    }

    @SuppressLint({"InflateParams", "ResourceType"})
    public void addAds(Activity activity, boolean small, int id) {
        this.small = small;
        if (small)
            adView = (NativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.ad_unified_small, null);
        else
            adView = (NativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.ad_unified, null);
        adView.setId(456);
        llAll.addView(adView, -1, -2);

        llLoad = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams pL = new RelativeLayout.LayoutParams(-1, -1);
        pL.addRule(RelativeLayout.ALIGN_LEFT, adView.getId());
        pL.addRule(RelativeLayout.ALIGN_TOP, adView.getId());
        pL.addRule(RelativeLayout.ALIGN_RIGHT, adView.getId());
        pL.addRule(RelativeLayout.ALIGN_BOTTOM, adView.getId());
        llAll.addView(llLoad, pL);

        int w = getContext().getResources().getDisplayMetrics().widthPixels;
        ProgressBar pr = new ProgressBar(getContext());
        RelativeLayout.LayoutParams pPr = new RelativeLayout.LayoutParams(w / 10, w / 10);
        pPr.addRule(RelativeLayout.CENTER_IN_PARENT);
        llLoad.addView(pr, pPr);

        if (activity != null)
            loadAds(activity, small, id);
        else
            loadAds(getContext(), small, id);
    }

    private void loadAds(Activity activity, boolean small, int id) {
        NativeResult nativeResult = nativeAd -> {
            if (activity.isDestroyed() || activity.isFinishing())
                return;
            showAds(nativeAd);
        };
        if (small)
            loaderRect.loadAdsSmall(activity, id, nativeResult);
        else
            loaderRect.loadAds(activity, id, nativeResult);
    }

    private void loadAds(Context activity, boolean small, int id) {
        NativeResult nativeResult = this::showAds;
        if (small)
            loaderRect.loadAdsSmall(activity, id, nativeResult);
        else
            loaderRect.loadAds(activity, id, nativeResult);
    }

    private void showAds(NativeAd nativeAd) {
        if (nativeAd == null || adView == null) {
            setVisibility(GONE);
        } else {
            llLoad.setVisibility(GONE);
            if (small)
                populateNativeAdViewSmall(nativeAd, adView);
            else
                populateNativeAdView(nativeAd, adView);
        }
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));

        TextView tv1 = ((TextView) adView.getHeadlineView());
        if (tv1 != null)
            tv1.setText(nativeAd.getHeadline());
        if (adView.getMediaView() != null && nativeAd.getMediaContent() != null)
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getPriceView() != null) {
            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        }

        if (adView.getStoreView() != null) {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }

        if (adView.getStarRatingView() != null) {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getAdvertiserView() != null) {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        adView.setNativeAd(nativeAd);

        if (nativeAd.getMediaContent() != null) {
            VideoController vc = nativeAd.getMediaContent().getVideoController();
            if (vc.hasVideoContent()) {
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            }
        }
    }

    private void populateNativeAdViewSmall(NativeAd nativeAd, NativeAdView adView) {
        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));

        TextView tv1 = ((TextView) adView.getHeadlineView());
        if (tv1 != null)
            tv1.setText(nativeAd.getHeadline());
        if (adView.getMediaView() != null && nativeAd.getMediaContent() != null)
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getPriceView() != null) {
            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        }

        if (adView.getStoreView() != null) {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }

        if (adView.getStarRatingView() != null) {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getAdvertiserView() != null) {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        adView.setNativeAd(nativeAd);

        if (nativeAd.getMediaContent() != null) {
            VideoController vc = nativeAd.getMediaContent().getVideoController();
            if (vc.hasVideoContent()) {
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        super.onVideoEnd();
                    }
                });
            }
        }
    }

    public void onDestroy() {
        if (loaderRect != null)
            loaderRect.onDestroy();
    }
}
