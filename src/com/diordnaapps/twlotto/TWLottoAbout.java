package com.diordnaapps.twlotto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TWLottoAbout extends Activity {
    
    private Button mButtonOk;
    
    private static final long VIBRATE_TIME = 25;    
    private Vibrator mVibrator;
    
    public static void actionSettings(Activity fromActivity) {
        Intent i = new Intent(fromActivity, TWLottoAbout.class);
        fromActivity.startActivity(i);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
      super.onCreate(savedInstanceState);
      
      mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
      
      requestWindowFeature(Window.FEATURE_LEFT_ICON);
      setContentView(R.layout.about_dialog);      
      getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, 
              R.drawable.ic_launcher_twlotto);

      mButtonOk = (Button) this.findViewById(R.id.about_ok);
      mButtonOk.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              
              if (TWLottoSetting.ifVibrate())
                  mVibrator.vibrate(VIBRATE_TIME);
              
              finish();
          }
      });
      
      TextView dislogContent = (TextView) findViewById(R.id.about_dialog_content);
      dislogContent.setText("  " + getString(R.string.about_dialog_msg_lotto) +
              "\n\n" + "  " + getString(R.string.about_dialog_msg_invoice) +
              "\n\n" + "  " + getString(R.string.about_dialog_msg_res));
    }
}
