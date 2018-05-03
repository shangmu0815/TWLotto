package com.diordnaapps.twlotto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class TWLottoDetail extends Activity {
    
    WinningInfoAdapter mWinningInfoAdapter;
    private Button mButtonOk;
    private int mLottoType = 0;
    private int mLottoId = 0;
    private int[] mLottoPrize = {R.id.lotto_detail_first, R.id.lotto_detail_second, R.id.lotto_detail_third,
        R.id.lotto_detail_fourth, R.id.lotto_detail_fifth,R.id.lotto_detail_sixth, R.id.lotto_detail_seventh,
        R.id.lotto_detail_eighth, R.id.lotto_detail_ninth, R.id.lotto_detail_general};

      
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
      super.onCreate(savedInstanceState);
      
      mWinningInfoAdapter = new WinningInfoAdapter(this);
      
      requestWindowFeature(Window.FEATURE_LEFT_ICON);
      setContentView(R.layout.lotto_detail_dialog);      
      getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, 
              R.drawable.ic_launcher_twlotto);

      Intent intent = getIntent();
      mLottoType = intent.getIntExtra("lottoType", 0);
      mLottoId = intent.getIntExtra("lottoId", 0);
      
      mButtonOk = (Button) this.findViewById(R.id.lotto_detail_ok);
      mButtonOk.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
          }
      });
      
      getDetailDataFromDB(mLottoType, mLottoId);
      String[] lottoTypes = getResources().getStringArray(R.array.lottoTypes);
      getWindow().setTitle(lottoTypes[mLottoType]);
    }

    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    private void getDetailDataFromDB(int type, int id) {
        WinningInfoV2 winningInfo;
        
        mWinningInfoAdapter.getLottoWinningInfoFromDB(type);
        winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(id); 

        Log.d("Tina", "winningInfo.prize.length = " + winningInfo.prize.length);
        for (int i = 0; i < winningInfo.prize.length; i++) {
            ((TextView) this.findViewById(mLottoPrize[i])).setVisibility(View.VISIBLE);
            if (winningInfo.prize[i].length() == 0){
                ((TextView) this.findViewById(mLottoPrize[i])).setVisibility(View.GONE);
            }
            else if (winningInfo.prize[i].length() < 2) {            
                ((TextView) this.findViewById(mLottoPrize[i]))
                .setText(" " + getString(R.string.lotto_detail_info_no_prize));
            } else {
                ((TextView) this.findViewById(mLottoPrize[i]))
                    .setText(" " + winningInfo.prize[i] + getString(R.string.string_dollar));
            }
        }
    }
    
//    private void getDetailDataFromDB(int type, int id) {
//        WinningInfo winningInfo;
//        
//        mWinningInfoAdapter.getLottoWinningInfoFromDB(type);
//        winningInfo = (WinningInfo) mWinningInfoAdapter.getItem(id); 
//
//        Log.d("Tina", "winningInfo.prize.length = " + winningInfo.prize.length);
//        for (int i = 0; i < winningInfo.prize.length; i++) {
//            ((TextView) this.findViewById(mLottoPrize[i])).setVisibility(View.VISIBLE);
//            if (winningInfo.prize[i].length() == 0){
//                ((TextView) this.findViewById(mLottoPrize[i])).setVisibility(View.GONE);
//            }
//            else if (winningInfo.prize[i].length() < 2) {            
//                ((TextView) this.findViewById(mLottoPrize[i]))
//                .setText(" " + getString(R.string.lotto_detail_info_no_prize));
//            } else {
//                ((TextView) this.findViewById(mLottoPrize[i]))
//                    .setText(" " + winningInfo.prize[i] + getString(R.string.string_dollar));
//            }
//        }
//    }
}