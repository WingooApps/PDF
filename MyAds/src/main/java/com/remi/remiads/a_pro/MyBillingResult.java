package com.remi.remiads.a_pro;

public interface MyBillingResult {

    void onPurchasesDone();
    void onPurchasesProcessing();
    void onPurchasesCancel();
    void onNotConnect();
}
