package com.diordnaapps.twlotto.menuactivity;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.diordnaapps.twlotto.R;
import com.diordnaapps.twlotto.TWLottoAbout;
import com.diordnaapps.twlotto.TWLottoSetting;

public class TwoMenuItemActivity extends Activity {       
    
    protected static final long VIBRATE_TIME = 25;    
    protected Vibrator mVibrator;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);        
               
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.two_item_menu, menu);              
        
        return super.onCreateOptionsMenu(menu);
    }        
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        switch (item.getItemId()) {
        case R.id.two_menu_preferences:            
            TWLottoSetting.actionSettings(this);
            break;
        case R.id.two_menu_about:            
            TWLottoAbout.actionSettings(this);
            break;        
        default:
            return super.onOptionsItemSelected(item);
        }
        
        return true;
    }

}
