package com.remi.remiads.utils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ItemFist {

    @SerializedName("adm")
    public boolean adm;
    @SerializedName("g")
    public ArrayList<String> g;
    @SerializedName("i")
    public ArrayList<String> i;

    public boolean googleFist(String pkg) {
        if (pkg == null)
            return adm;
        if (g != null && g.size() > 0) {
            for (String s : g) {
                if (s != null && s.equals(pkg))
                    return true;
            }
        }
        if (i != null && i.size() > 0) {
            for (String s : i) {
                if (s!= null && s.equals(pkg))
                    return false;
            }
        }
        return adm;
    }
}
