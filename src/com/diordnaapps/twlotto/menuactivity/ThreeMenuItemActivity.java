package com.diordnaapps.twlotto.menuactivity;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;

import com.diordnaapps.twlotto.R;

public class ThreeMenuItemActivity extends Activity {       
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.         
        
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.three_item_menu, menu);
        
        return true;
    }        

}
