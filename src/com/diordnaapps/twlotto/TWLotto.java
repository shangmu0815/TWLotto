package com.diordnaapps.twlotto;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diordnaapps.twlotto.menuactivity.ThreeMenuItemActivity;
import com.diordnaapps.twlotto.provider.WinningInfoProvider;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
/////////////////////////////////////////////////////////////////////////////////

public class TWLotto extends ThreeMenuItemActivity implements OnClickListener, OnTouchListener {
    
    Context mContext;
    WinningInfoAdapter mWinningInfoAdapter;
    private Handler mHandler;
    private RelativeLayout mLoadingLayout;
    private Button mButtonSearch = null;
    private Button mButtonTypedin = null;
    private TextView mTextUpdating = null;
    //For Ad
    private static Handler mAdHandler = new Handler();
  
///////////////////////////////////////////////////////////////////////////////////////////////////    
    private AdRequest mAdRequest;
    private AdView mAdView;
    private static final int AD_REFRESH_MSEC = 30000;//30secs
    
    private static final int MENU_REFRESH = Menu.FIRST + 1;
    
    private static final int UPDATE_DONE = 1;
    
    private static final int DIALOG_WAIT_FOR_UPDATING = 1;

    // contains the layout id of each type of lotto
    public static int[] mLottoList = {
            R.layout.weililotto_list, 
            R.layout.biglotto_list, 
            R.layout.lotto39_list, 
            R.layout.ticktacktoelotto_list,
            R.layout.star3_list,
            R.layout.star4_list, 
            R.layout.lotto38_list,
            R.layout.lotto49_list,
            R.layout.lotto539_list
            };

    // contains each id of TextView
    public static int[] mLottoWeili = {R.id.weili_lotto_number_1, R.id.weili_lotto_number_2, R.id.weili_lotto_number_3, R.id.weili_lotto_number_4, R.id.weili_lotto_number_5, R.id.weili_lotto_number_6, R.id.weili_lotto_number_special};
    public static int[] mLottoBig =   {R.id.biglotto_lotto_number_1, R.id.biglotto_lotto_number_2, R.id.biglotto_lotto_number_3, R.id.biglotto_lotto_number_4, R.id.biglotto_lotto_number_5, R.id.biglotto_lotto_number_6, R.id.biglotto_lotto_number_special};
    public static int[] mLotto539 =   {R.id.lotto39_lotto_number_1, R.id.lotto39_lotto_number_2, R.id.lotto39_lotto_number_3, R.id.lotto39_lotto_number_4, R.id.lotto39_lotto_number_5};
    public static int[] mLottoTTT =   {R.id.ttt_lotto_number_11, R.id.ttt_lotto_number_12, R.id.ttt_lotto_number_13, R.id.ttt_lotto_number_21, R.id.ttt_lotto_number_22, R.id.ttt_lotto_number_23, R.id.ttt_lotto_number_31, R.id.ttt_lotto_number_32, R.id.ttt_lotto_number_33};
    public static int[] mLottoStar3 = {R.id.star3_lotto_number_1, R.id.star3_lotto_number_2, R.id.star3_lotto_number_3};
    public static int[] mLottoStar4 = {R.id.star4_lotto_number_1, R.id.star4_lotto_number_2, R.id.star4_lotto_number_3, R.id.star4_lotto_number_4};
    public static int[] mLotto38 =    {R.id.lotto38_lotto_number_1, R.id.lotto38_lotto_number_2, R.id.lotto38_lotto_number_3, R.id.lotto38_lotto_number_4, R.id.lotto38_lotto_number_5, R.id.lotto38_lotto_number_6};
    public static int[] mLotto49 =    {R.id.lotto49_lotto_number_1, R.id.lotto49_lotto_number_2, R.id.lotto49_lotto_number_3, R.id.lotto49_lotto_number_4, R.id.lotto49_lotto_number_5, R.id.lotto49_lotto_number_6};
    public static int[] mLotto39 =    {R.id.lotto539_lotto_number_1, R.id.lotto539_lotto_number_2, R.id.lotto539_lotto_number_3, R.id.lotto539_lotto_number_4, R.id.lotto539_lotto_number_5};

    // id defined in each xxx_list.xml
    public static int[] mLottoDateList = {R.id.weili_lotto_date, R.id.biglotto_lotto_date, R.id.lotto39_lotto_date, R.id.ttt_lotto_date, R.id.star3_lotto_date, R.id.star4_lotto_date, R.id.lotto38_lotto_date, R.id.lotto49_lotto_date, R.id.lotto539_lotto_date};
    public static int[] mLottoDrawList = {R.id.weili_lotto_draw_number, R.id.biglotto_lotto_draw_number, R.id.lotto39_lotto_draw_number, R.id.ttt_lotto_draw_number, R.id.star3_lotto_draw_number, R.id.star4_lotto_draw_number, R.id.lotto38_lotto_draw_number, R.id.lotto49_lotto_draw_number, R.id.lotto539_lotto_draw_number};
    
    public static int[][] mLottoTypeNOList = {mLottoWeili, mLottoBig, mLotto539, mLottoTTT, 
            mLottoStar3, mLottoStar4, mLotto38, mLotto49, mLotto39};
    
    private static final long VIBRATE_TIME = 25;    
    private Vibrator mVibrator;
    private boolean activityFirstEnter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        mContext = this;
        mWinningInfoAdapter = new WinningInfoAdapter(this);
        
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        mLoadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);
        
        // if can't get the result of lotto weili, show the loading info to user.
        if (mWinningInfoAdapter
                .getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_WEILI) == null) {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }
        /*
        if (mWinningInfoAdapter
                .getLatestLotteryDrawIDFromDB(WinningInfo.LOTTO_WEILI) == null) {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }
        */
///////////////////////////////////////////////////////////////////        
        // Create the adView
        mAdView = new AdView(this, AdSize.BANNER, "a14dc8f11366b57");
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
        // Add the adView to it
        layout.addView(mAdView);
        // Initiate a generic request to load it with an ad
        mAdRequest = new AdRequest();
        mAdView.loadAd(mAdRequest);
        
        mTextUpdating = (TextView) findViewById(R.id.update_prompt);
        mTextUpdating.setVisibility(View.GONE);

        // get latest draw number
        preLoadDrawResult();
        
        // R.id.spinnerLotto defined in main.xml
        Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.lottoTypes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        
        // default select the lotto weili
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                // R.id.lotto_info is a LinearLayout defined in edit_draw_id.xml
                
                if( !activityFirstEnter )
                {                    
                    if (TWLottoSetting.ifVibrate())
                        mVibrator.vibrate(VIBRATE_TIME);
                }
                else
                    activityFirstEnter = false;
                
                ViewGroup group = (ViewGroup) findViewById(R.id.lotto_info);
                group.removeAllViews();
                displayLottoresult(position);
                if (mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(position) == null)
                    refreshResult(position, true);
                else
                    refreshResult(position, false);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        
        // for history search button
        mButtonSearch = (Button) this.findViewById(R.id.main_search);
        mButtonSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (TWLottoSetting.ifVibrate())
                    mVibrator.vibrate(VIBRATE_TIME);
                
                Intent intent = new Intent(TWLotto.this, TWLottoSearch.class);
                Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
                intent.putExtra("lottoType", s.getSelectedItemPosition());
                startActivity(intent);
            }
        });

        // for prize checking button
        mButtonTypedin = (Button) this.findViewById(R.id.main_typed_in);
        mButtonTypedin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (TWLottoSetting.ifVibrate())
                    mVibrator.vibrate(VIBRATE_TIME);
                
                Intent intent = new Intent(TWLotto.this,
                        EditDrawIdActivity.class);
                Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
                intent.putExtra("lottoType", s.getSelectedItemPosition());
                startActivity(intent);
            }
        });

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case UPDATE_DONE:
                        Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
                        if (s.getSelectedItemPosition() == WinningInfoV2.LOTTO_WEILI
                                && mWinningInfoAdapter.getCount() == 0) {
                            Toast.makeText(mContext,
                                    getString(R.string.lotto_retrieve_error),
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else
                            mLoadingLayout.setVisibility(View.GONE);

                        removeDialog(DIALOG_WAIT_FOR_UPDATING);
                        if ((Integer) msg.obj == s.getSelectedItemPosition()) {
                            ViewGroup group = (ViewGroup) findViewById(R.id.lotto_info);
                            group.removeAllViews();
                            mTextUpdating.setVisibility(View.GONE);

                            displayLottoresult(s.getSelectedItemPosition());
                            // Toast.makeText(mContext,
                            // getString(R.string.latest_info),
                            // Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
/////////////////////////////////////////////////////////////////////////////////////        
        mAdHandler.removeCallbacks(AdRefreshTimer);
        mAdView.loadAd(mAdRequest);
        mAdHandler.postDelayed(AdRefreshTimer, AD_REFRESH_MSEC);
        
        activityFirstEnter = true;
    }    
    
    @Override
    protected void onStop() {
        super.onStop();
        
        mAdHandler.removeCallbacks(AdRefreshTimer);
        
////////////////////////////////////////////////////////////////////////        
        mAdView.stopLoading();
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
                
        menu.add(0, MENU_REFRESH, 0, R.string.invoice_menu_refresh).setIcon(
                R.drawable.ic_menu_refresh).setShortcut('0', 'r');
        
        return true;
    }
    */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.three_menu_refresh);
        if (mTextUpdating.getVisibility() == View.VISIBLE)
            item.setEnabled(false);
        else
            item.setEnabled(true);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        switch (item.getItemId()) {
        case R.id.three_menu_preferences:            
            TWLottoSetting.actionSettings(this);
            break;
        case R.id.three_menu_about:            
            TWLottoAbout.actionSettings(this);
            break;        
        case R.id.three_menu_refresh:
            Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
            int index = s.getSelectedItemPosition();
            displayLottoresult(index);
            refreshResult(index, true);
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        
        return true;
        /*
        switch (item.getItemId()) {
        case MENU_REFRESH:
            Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
            int index = s.getSelectedItemPosition();
            displayLottoresult(index);
            refreshResult(index, true);
            break;
        }
        return super.onOptionsItemSelected(item);
        */
    }
    
    private void updateLottoWinningResult(int index) {
        String site = mWinningInfoAdapter.getLottoSite(index);
        mWinningInfoAdapter.updateWinningResult(mContext, site, 
            WinningInfoProvider.getLottoTable(index), 
            WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10, 
            mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(index), 
            WinningInfoV2.PARSE_LOTTO);
        
//        mWinningInfoAdapter.updateWinningResult(mContext, site, 
//                WinningInfoProvider.getLottoTable(index), 
//                WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10, 
//                mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(index), 
//                WinningInfo.PARSE_LOTTO);
    }
    
    private void preLoadDrawResult() {
        // Pre-load draw result for 4 star and lotto big
        new Thread() {
            public void run() {
                if (mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_STAR4) == null) {
                    updateLottoWinningResult(WinningInfoV2.LOTTO_STAR4);
                }
                if (mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_BIG) == null) {
                    updateLottoWinningResult(WinningInfoV2.LOTTO_BIG);
                }
                
//                if (mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfo.LOTTO_STAR4) == null) {
//                    updateLottoWinningResult(WinningInfo.LOTTO_STAR4);
//                }
//                if (mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfo.LOTTO_BIG) == null) {
//                    updateLottoWinningResult(WinningInfo.LOTTO_BIG);
//                }
            }
        }.start();
    }
    
    // index to lotto type
    private void displayLottoresult(int index){
        
        // R.id.lotto_info is a LinearLayout defined in edit_draw_id.xml
        ViewGroup group = (ViewGroup) this.findViewById(R.id.lotto_info);
        group.setVisibility(View.VISIBLE);
        
        //Get Lotto Result
        mWinningInfoAdapter.getLottoWinningInfoFromDB(index);
        
        // add rows of each issue of lotto winning info to group( LinearLayout )
        addRow(group, index);
    }
    
    private void refreshResult(int index, boolean forceRefresh) {
        final int idx = index;
        boolean refresh = false;
        
        if (forceRefresh)
            refresh = true;
        else
            refresh = mWinningInfoAdapter.isAutoRefreshNeeded(index);
        
        mTextUpdating.setVisibility(View.GONE);
        if (refresh) {
            if (mWinningInfoAdapter.getCount() == 0) {
                if (mLoadingLayout.getVisibility() == View.GONE)
                    showDialog(DIALOG_WAIT_FOR_UPDATING);
            }
            mTextUpdating.setVisibility(View.VISIBLE);
            new Thread() {
                public void run() {
                    updateLottoWinningResult(idx);
                    mWinningInfoAdapter.getLottoWinningInfoFromDB(idx);
                    Message msg = new Message();
                    msg.what = UPDATE_DONE;
                    msg.obj = idx;
                    mHandler.sendMessage(msg);
                }
            }.start();
        }
    }
    
    
    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    
    // add rows of each issue of lotto winning info to ViewGroup group
    public void addRow(ViewGroup group, int lottoType) {
        
        WinningInfoV2 winningInfo;
        
        // the ball numbers depends on different lotto type
        int lottonumber = TWLotto.mLottoTypeNOList[lottoType].length;

        for (int j = 0; j < mWinningInfoAdapter.getCount(); j++) {        
                        
            winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(j);  
            
            // let each number to be a two-digit number
            zeroStringInsert(winningInfo, lottonumber, lottoType);

            // get a child view of root view group
            // mLottoList[lottoType] is a layout xml of lotto type
            View row = getLayoutInflater().inflate(mLottoList[lottoType], group, false);
            
            ((TextView) row.findViewById(mLottoDateList[lottoType]))
                    .setText(winningInfo.drawDate);
            
            ((TextView) row.findViewById(mLottoDrawList[lottoType]))
                    .setText(" " + getString(R.string.draw_period)
                            + winningInfo.drawID + getString(R.string.draw_id));

            for (int k = 0; k < lottonumber ; k++) {    
                ((TextView) row.findViewById(mLottoTypeNOList[lottoType][k]))
                        .setText(winningInfo.sec1WinningNo[k]);
            }
            
            // only these two type of lottos has a special number
            if (lottoType == WinningInfoV2.LOTTO_WEILI || lottoType == WinningInfoV2.LOTTO_BIG) {
                ((TextView) row.findViewById(mLottoTypeNOList[lottoType][lottonumber - 1]))
                        .setText(winningInfo.sec2WinningNo[0]);
            }
            row.setId(j);
            
            // click each draw of lotto info can get more detail info
            row.setOnClickListener(this);           
            row.setOnTouchListener(this);
            group.addView(row);
        }
    }
    
//    public void addRow(ViewGroup group, int lottoType) {
//        WinningInfo winningInfo;
//        int lottonumber = TWLotto.mLottoTypeNOList[lottoType].length;
//
//        for (int j = 0; j < mWinningInfoAdapter.getCount(); j++) {            
//            winningInfo = (WinningInfo) mWinningInfoAdapter.getItem(j);    
//            zeroStringInsert(winningInfo, lottonumber, lottoType);
//
//            View row = getLayoutInflater().inflate(mLottoList[lottoType], group, false);
//            ((TextView) row.findViewById(mLottoDateList[lottoType]))
//                    .setText(winningInfo.drawDate);
//            ((TextView) row.findViewById(mLottoDrawList[lottoType]))
//                    .setText(" " + getString(R.string.draw_period)
//                            + winningInfo.drawID + getString(R.string.draw_id));
//
//            for (int k = 0; k < lottonumber ; k++) {    
//                ((TextView) row.findViewById(mLottoTypeNOList[lottoType][k]))
//                        .setText(winningInfo.sec1WinningNo[k]);
//            }
//            if (lottoType == WinningInfo.LOTTO_WEILI || lottoType == WinningInfo.LOTTO_BIG) {
//                ((TextView) row.findViewById(mLottoTypeNOList[lottoType][lottonumber - 1]))
//                        .setText(winningInfo.sec2WinningNo[0]);
//            }
//            row.setId(j);
//            row.setOnClickListener(this);           
//            row.setOnTouchListener(this);
//            group.addView(row);
//        }
//    }

    
    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    
    // let each number to be a two-digit number
    public static void zeroStringInsert(WinningInfoV2 winningInfo, int lottounits, int lottoType) {

        // all of these two type of lottos are one-digit number, so skip them
        if (lottoType == WinningInfoV2.LOTTO_STAR3 || lottoType == WinningInfoV2.LOTTO_STAR4)
            return;
        
        // if find any number only have one digit
        // add a zero digit before the digit
        for (int j = 0; j < lottounits; j++) {
            if ((winningInfo.sec1WinningNo[j]) != null
                    && (winningInfo.sec1WinningNo[j].length()) == 1) {
                winningInfo.sec1WinningNo[j] = 
                    "0" + winningInfo.sec1WinningNo[j];
            }

        }
        if ((winningInfo.sec2WinningNo[0]) != null
                && (winningInfo.sec2WinningNo[0].length()) == 1) {
            winningInfo.sec2WinningNo[0] = "0" + winningInfo.sec2WinningNo[0];
        }
    }
    
//    public static void zeroStringInsert(WinningInfo winningInfo, int lottounits, int lottoType) {
//
//        if (lottoType == WinningInfo.LOTTO_STAR3 || lottoType == WinningInfo.LOTTO_STAR4)
//            return;
//        
//        for (int j = 0; j < lottounits; j++) {
//            if ((winningInfo.sec1WinningNo[j]) != null
//                    && (winningInfo.sec1WinningNo[j].length()) == 1) {
//                winningInfo.sec1WinningNo[j] = 
//                    "0" + winningInfo.sec1WinningNo[j];
//            }
//
//        }
//        if ((winningInfo.sec2WinningNo[0]) != null
//                && (winningInfo.sec2WinningNo[0].length()) == 1) {
//            winningInfo.sec2WinningNo[0] = "0" + winningInfo.sec2WinningNo[0];
//        }
//    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_WAIT_FOR_UPDATING:
            {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.info_updateing));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
        }
        return null;
    }
    
    private Runnable AdRefreshTimer = new Runnable() {
        public void run() {
        	
////////////////////////////////////////////////////////////////////////////////        	
            mAdView.loadAd(mAdRequest);
            mAdHandler.postDelayed(this, AD_REFRESH_MSEC);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        
        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        if (id >= 0 && id < 10) {
            /*
            Intent intent = new Intent();
            intent.putExtra("lottoId", id);
            intent.putExtra("lottoType", mPosition);
            intent.setClass(mContext, TWLottoDetail.class);
            startActivity(intent);
            */
            
            Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
            mWinningInfoAdapter.getLottoWinningInfoFromDB(s.getSelectedItemPosition());
            WinningInfoV2 winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(id);
//            WinningInfo winningInfo = (WinningInfo) mWinningInfoAdapter.getItem(id);
            
            Intent intent = new Intent();
            intent.putExtra("lottoDrawId", winningInfo.drawID);
            intent.putExtra("lottoType", s.getSelectedItemPosition());
            intent.setClass(mContext, EditDrawIdActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(Color.GRAY);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(Color.BLACK);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            v.setBackgroundColor(Color.BLACK);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(Color.BLACK);
        }
        return false;
    }
}
