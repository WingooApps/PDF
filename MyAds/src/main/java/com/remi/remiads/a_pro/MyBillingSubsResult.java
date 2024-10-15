package com.remi.remiads.a_pro;

/**
 * Interface handling billing subscription result
 * */
public interface MyBillingSubsResult {
    void onPurchasesDone();
    void onPurchasesProcessing();
    void onPurchasesCancel();
    void onNotConnect();
    void onPurchaseError();
}
