package com.diordnaapps.twlotto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
//import android.preference.PreferenceFragment;


public class TWLottoSetting extends PreferenceActivity{
    
    private CheckBoxPreference mRefreshType;
    
    private CheckBoxPreference mVibrateType;
    private static boolean ifVibrate = true;
    
    public static void actionSettings(Activity fromActivity) {
        Intent i = new Intent(fromActivity, TWLottoSetting.class);
        fromActivity.startActivity(i);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        
        mRefreshType = (CheckBoxPreference) findPreference("twlotto_setting_refresh");
        mRefreshType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                if (summary.equalsIgnoreCase("true"))
                    mRefreshType.setChecked(true);
                else
                    mRefreshType.setChecked(false);
                return false;
            }
        });
        
        mVibrateType = (CheckBoxPreference) findPreference("twlotto_setting_vibrate");
        mVibrateType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                if (summary.equalsIgnoreCase("true"))
                {
                    mVibrateType.setChecked(true);
                    ifVibrate = true;
                }                
                else
                {
                    mVibrateType.setChecked(false);
                    ifVibrate = false;
                }
                return false;
            }
        });
    }
    
    public static boolean ifVibrate()
    {
        return ifVibrate;
    }
}



//public class TWLottoSetting extends PreferenceActivity{
//	
//	public static void actionSettings(Activity fromActivity) {
//        Intent intent = new Intent(fromActivity, TWLottoSetting.class);
//        fromActivity.startActivity(intent);
//    }
//	
//	protected void onCreate(final Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new TWLottoSettingFragment()).commit();
//    }
//	
//	public static class TWLottoSettingFragment extends PreferenceFragment{
//	    
//	    private CheckBoxPreference mRefreshType;	    
//	    
//	    @Override
//	    public void onCreate(final Bundle savedInstanceState) {
//	        super.onCreate(savedInstanceState);
//	        addPreferencesFromResource(R.xml.setting);
//	        
//	        mRefreshType = (CheckBoxPreference) findPreference("twlotto_setting_refresh");
//	        mRefreshType.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//	            @Override
//	            public boolean onPreferenceChange(Preference preference, Object newValue) {
//	                final String summary = newValue.toString();
//	                if (summary.equalsIgnoreCase("true"))
//	                    mRefreshType.setChecked(true);
//	                else
//	                    mRefreshType.setChecked(false);
//	                return false;
//	            }
//	        });
//	    }
//	}
//	
//}


