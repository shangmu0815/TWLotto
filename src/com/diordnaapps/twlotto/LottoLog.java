package com.diordnaapps.twlotto;

import android.util.Log;

public class LottoLog {
	private static final String TAG = "TWLotto";
    private static final boolean LOG_ENABLED = true;
    
    public static void log(String message) {
        if (LOG_ENABLED) {
            Log.d(TAG, message);
        }
    }
}