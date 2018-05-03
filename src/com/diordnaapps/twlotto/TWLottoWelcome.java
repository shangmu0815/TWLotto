package com.diordnaapps.twlotto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageButton;

import com.diordnaapps.twlotto.menuactivity.TwoMenuItemActivity;

public class TWLottoWelcome extends TwoMenuItemActivity implements OnTouchListener {
    private static final int MENU_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_ABOUT = Menu.FIRST + 2;
    
    private static final long VIBRATE_TIME = 25;    
    private Vibrator mVibrator;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.welcome);
        
        ImageButton mImgButtonLotto = (ImageButton) this.findViewById(R.id.img_buttonlotto); 
        ImageButton mImgButtonInvoice = (ImageButton) this.findViewById(R.id.img_buttonInvoice); 

        mImgButtonLotto.setOnClickListener(mClickListenerLotto);
        mImgButtonInvoice.setOnClickListener(mClickListenerInvoice);
    }

    private final OnClickListener mClickListenerLotto = new OnClickListener() {
        public void onClick(View v) {            
            
            if (TWLottoSetting.ifVibrate())
                mVibrator.vibrate(VIBRATE_TIME);            
            
            Intent intent = new Intent(TWLottoWelcome.this, TWLotto.class);
            startActivity(intent);
        }
    };
    
    private final OnClickListener mClickListenerInvoice = new OnClickListener() {
        public void onClick(View v) {
            
            mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (TWLottoSetting.ifVibrate())
                mVibrator.vibrate(VIBRATE_TIME);
            
            Intent intent = new Intent(TWLottoWelcome.this, TWLottoInvoiceDisplay.class);
            startActivity(intent);
        }
    };
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.clear();
        menu.add(0, MENU_SETTINGS, 0, R.string.welcomemenu_setting).setIcon(
                R.drawable.ic_menu_preferences).setShortcut('0', 'r');
                
        menu.add(0, MENU_ABOUT, 1, R.string.welcomemenu_about).setIcon(
                R.drawable.ic_menu_about).setShortcut('0', 'r');
        
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.two_item_menu, menu);        
        
        return true;
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
    */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /*
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //v.setBackgroundResource(R.drawable.icon_highlight_square);
            v.setBackgroundResource(R.drawable.digit_pressed);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundResource(R.drawable.btn_digit_highlight);
        }
        */
        
        return false;
    }
}
