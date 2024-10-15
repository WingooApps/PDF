package com.remi.remiads.nativenew;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdsNativeItem {

    @SerializedName("gone")
    public ArrayList<String> gone;
    @SerializedName("apps")
    public ArrayList<ItemNative> apps;
    @SerializedName("remove")
    public ArrayList<ItemRemove> remove;

}