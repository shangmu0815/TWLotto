package com.diordnaapps.twlotto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.ImageButton;
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
////////////////////////////////////////////////////////////////////////////////

public class TWLottoInvoiceDisplay extends ThreeMenuItemActivity implements OnClickListener, OnTouchListener {

    private static final String TAG = "TWLottoInvoiceDisplay";
    
    //For Ad
    private static Handler mAdHandler = new Handler();
    
////////////////////////////////////////////////////////////////////////////////    
    private AdRequest mAdRequest;
    private AdView mAdView;
    private static final int AD_REFRESH_MSEC = 30000;//30secs
    
    private Context mContext;
    private Handler mHandler;
    private WinningInfoAdapter mWinningInfoAdapter;
    private Button mButtonInvoiceMatching = null;
    private TextView mTextInvoice;
    private RelativeLayout mLoadingLayout;
    private int mIndex = 0;
    private int mPrize = 0;
    private int[] mPrizeIndex = {0,0,0,0}; // numbers for each winning info type
    private String array_spinner[];
    private String mAwardMoney;
    private ToneGenerator mToneGenerator;
    private LayoutMappingIme mlayoutMap; // LayoutMappintIme defined below at line 123
    
    private static final int MENU_REFRESH = Menu.FIRST + 1;
    
    private static final int MODE_VIEW = 0;
    private static final int MODE_MATCHING = 1;
    
    // store all invoice winning number
    private String[] mInvoiceMatching = { null, null, null, null, null, null,
            null, null, null, null, null, null };
    private String[][] winninginfo_invoice = {null, null, null, null};
    private String[] winninginfo_invoice_prize = {null, null, null, null};
    private String mUserInputInvoice = "";

    private static int SIXTH_PRIZE_LENGTH = 5;
    private static int INVOICE_PRIZE_LENGTH = 3;
    private static int INVOICE_PRIZE_ALL_LENGTH = 8;
    private boolean mMatchingAll = false;
    private boolean orderCorrect = true; 
    
    // defined in invoice_number_list.xml in layout directory
    // all of them are TextView
    private static int[] INVOICE_RESULT_LAYOUT_NO = {
            R.id.invoice_grand_number_1, R.id.invoice_grand_number_2,
            R.id.invoice_grand_number_3, R.id.invoice_topprize_number_1,
            R.id.invoice_topprize_number_2, R.id.invoice_topprize_number_3,
            R.id.invoice_firstprize_number_1, R.id.invoice_firstprize_number_2,
            R.id.invoice_firstprize_number_3, R.id.invoice_sixthprize_number_1,
            R.id.invoice_sixthprize_number_2, R.id.invoice_sixthprize_number_3,
            R.id.invoice_sixthprize_number_4, R.id.invoice_sixthprize_number_5 };

    // defined in invoice_number_list.xml in layout directory
    // all of them are TextView with description to corresponding winning type
    private static int[] INVOICE_WINNING_PRIZE_NOTE = {
        R.id.invoice_grand_notes, R.id.invoice_topprize_notes, 
        R.id.invoice_firstprize_notes, R.id.invoice_sixth_prize_notes };

    private static final int DIALOG_INVOICE_WINNING_PRIZE = 1;
    private static final int DIALOG_INVOICE_NO_WINNING = 2;
    private static final int DIALOG_WAIT_FOR_UPDATING = 3;

    private static final int MATCHING_INVOICE_GRAND = 8;
    private static final int MATCHING_INVOICE_TOP = 7;
    private static final int MATCHING_INVOICE_FIRST = 1;
    private static final int MATCHING_INVOICE_SECOND = 2;
    private static final int MATCHING_INVOICE_THIRD = 3;
    private static final int MATCHING_INVOICE_FORTH = 4;
    private static final int MATCHING_INVOICE_FIFTH = 5;
    private static final int MATCHING_INVOICE_SIXTH = 6;

    private static final int UPDATE_TEXT = 0;
    private static final int UPDATE_TEXT_NO_WIN = 1;
    private static final int UPDATE_TEXT_EMPTY = 2;
    private static final int UPDATE_DONE = 3;
    
    private static final long VIBRATE_TIME = 25;    
    private Vibrator mVibrator;
    
//    private static final int INVOICE_MONTH_OLDER = 0;
//    private static final int INVOICE_MONTH_LATEST = 1;
    
    public class LayoutMappingIme {
        int layout[];
        ImageButton ib[];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.display_invoice_number);
        
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
////////////////////////////////////////////////////////////////////////////////////////////////        
        // Create the adView
        mAdView = new AdView(this, AdSize.BANNER, "a14dc8f11366b57");
        LinearLayout layout = (LinearLayout)findViewById(R.id.mainLayout);
        // Add the adView to it
        layout.addView(mAdView);
        // Initiate a generic request to load it with an ad
        mAdRequest = new AdRequest();
        mAdView.loadAd(mAdRequest);
        
        mWinningInfoAdapter = new WinningInfoAdapter(this);
        mWinningInfoAdapter.getInvoiceWinningInfoFromDB();

        mLoadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        if (mWinningInfoAdapter.getCount() == 0) {
            dataRefresh(false);
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        setMode(MODE_VIEW);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case UPDATE_TEXT:
                    mTextInvoice = (TextView) findViewById(R.id.invoice_custom_ime_no);
                    mTextInvoice.setText(mUserInputInvoice);
                    break;
                case UPDATE_TEXT_NO_WIN:
                    mTextInvoice = (TextView) findViewById(R.id.invoice_custom_ime_no);
                    mUserInputInvoice = mUserInputInvoice + " "
                            + getString(R.string.no_prize_won)
                            + getString(R.string.invoice_continue);
                    mTextInvoice.setText(mUserInputInvoice);
                    break;
                case UPDATE_TEXT_EMPTY:
                    mTextInvoice = (TextView) findViewById(R.id.invoice_custom_ime_no);
                    mUserInputInvoice = "";
                    mTextInvoice.setText(mUserInputInvoice);
                    break;
                case UPDATE_DONE:
                    mWinningInfoAdapter.getInvoiceWinningInfoFromDB();
                    updateSelf();
                    removeDialog(DIALOG_WAIT_FOR_UPDATING);
                    if (mWinningInfoAdapter.getCount() == 0) {
                        Toast.makeText(mContext,
                                getString(R.string.invoice_retrieve_error),
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else
                        mLoadingLayout.setVisibility(View.GONE);
                    break;
                }
            }
        };
        mButtonInvoiceMatching = (Button) findViewById(R.id.invoice_matching);
        mButtonInvoiceMatching.setOnClickListener(this);

        updateSelf();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mToneGenerator = new ToneGenerator(AudioManager.STREAM_RING, 
                ToneGenerator.MAX_VOLUME);
        
        mAdHandler.removeCallbacks(AdRefreshTimer);
        
////////////////////////////////////////////////////////////////////////////////
        mAdView.loadAd(mAdRequest);
        mAdHandler.postDelayed(AdRefreshTimer, AD_REFRESH_MSEC);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mToneGenerator != null) {
            mToneGenerator.release();
            mToneGenerator = null;
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        mAdHandler.removeCallbacks(AdRefreshTimer);
        
////////////////////////////////////////////////////////////////////////////////        
        mAdView.stopLoading();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        if (mWinningInfoAdapter.getCount() == 0)
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
            dataRefresh(true);            
            break;
        default:
            return super.onOptionsItemSelected(item);
        }
        
        return true;
        
        /*switch (item.getItemId()) {
        case MENU_REFRESH:
            dataRefresh(true);
            break;
        }
        return super.onOptionsItemSelected(item);
        */
    }
    
    private void dataRefresh(Boolean loadingDialog) {
        if (loadingDialog)
            showDialog(DIALOG_WAIT_FOR_UPDATING);
        new Thread() {
            public void run() {
                updateInvoiceWinningResult();
                Message msg = new Message();
                msg.what = UPDATE_DONE;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {

        LottoLog.log("1. v.getId() = " + v.getId());
        String digit = "";

        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        switch (v.getId()) {
        // first time to show the custom keyboard
        case R.id.invoice_matching:    
            mMatchingAll = false;
            onSetInvoiceIME();
            break;
        case R.id.custom_ime_digit0:
            digit = getString(R.string.digit0);
            break;
        case R.id.custom_ime_digit1:
            digit = getString(R.string.digit1);
            break;
        case R.id.custom_ime_digit2:
            digit = getString(R.string.digit2);
            break;
        case R.id.custom_ime_digit3:
            digit = getString(R.string.digit3);
            break;
        case R.id.custom_ime_digit4:
            digit = getString(R.string.digit4);
            break;
        case R.id.custom_ime_digit5:
            digit = getString(R.string.digit5);
            break;
        case R.id.custom_ime_digit6:
            digit = getString(R.string.digit6);
            break;
        case R.id.custom_ime_digit7:
            digit = getString(R.string.digit7);
            break;
        case R.id.custom_ime_digit8:
            digit = getString(R.string.digit8);
            break;
        case R.id.custom_ime_digit9:
            digit = getString(R.string.digit9);
            break;
        case R.id.custom_ime_back:
            digit = getString(R.string.del);
            break;
        }
        // once user enter a number, check whether he get a prize or not
        if (v.getId() != R.id.invoice_matching){
            onDisplayUserInput(digit);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mMatchingAll) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundResource(R.drawable.digit_pressed);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundResource(R.drawable.btn_digit_highlight);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setBackgroundResource(R.drawable.btn_digit_highlight);
            }
            
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundResource(R.drawable.digit_pressed);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundResource(R.drawable.digit_normal);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.setBackgroundResource(R.drawable.digit_normal);
            }
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        Button button = (Button) findViewById(R.id.invoice_matching);
        // if user press the back button
        if (keyCode == KeyEvent.KEYCODE_BACK && !button.isShown()) {
            setMode(MODE_VIEW);
            
            // R.id.invoice_number_info is a LinearLayout defined in display_invoice_number_menu.xml
            ViewGroup group = (ViewGroup) findViewById(R.id.invoice_number_info);
            group.removeAllViews();
            mIndex = 0;
            Spinner s = (Spinner) findViewById(R.id.spinnerInvoice);
            addSpecialPrizeRowInvoice(s.getSelectedItemPosition());
            return true;
        } else {
            return super.onKeyDown(keyCode, keyEvent);
        }
    }
    
    public void onDisplayUserInput(String digit) {

    	// if user choose the back button, remove the last digit
        if (digit.equalsIgnoreCase(getString(R.string.del))){
            if (mUserInputInvoice.length() > 0
                    && mUserInputInvoice.length() < INVOICE_PRIZE_ALL_LENGTH) {
                mUserInputInvoice = mUserInputInvoice.substring(0,
                        mUserInputInvoice.length() - 1);
                mHandler.sendEmptyMessage(UPDATE_TEXT);
              }
        } else {
            LottoLog.log("mMatchingAll = " + mMatchingAll);
            // if we already know the user input got none of any prize
            // reset the user input to empty string
            if (mUserInputInvoice.contains(getString(R.string.no_prize_won)))
                mUserInputInvoice = "";
            mUserInputInvoice = mUserInputInvoice + digit;
            
            // match three digits, and now user enter eight digits
            if (mMatchingAll
                    && (mUserInputInvoice.length() == INVOICE_PRIZE_ALL_LENGTH)) {
            	  // show the user input to screen
                mHandler.sendEmptyMessage(UPDATE_TEXT); 
                
                // check if match exactly eight digits
                boolean result = matchingSpecialPrizeInvoiceAll();
                if (result) {
                    removeDialog(DIALOG_INVOICE_WINNING_PRIZE);
                    showDialog(DIALOG_INVOICE_WINNING_PRIZE);
                } else {
                    showDialog(DIALOG_INVOICE_NO_WINNING);
                }
              // user enter three digits and hasn't check yet
            } else if (!mMatchingAll
                    && mUserInputInvoice.length() == INVOICE_PRIZE_LENGTH) {
            	  // show user input to screen 
                mHandler.sendEmptyMessage(UPDATE_TEXT); 
                  
                // after checking and not match
                if (!matchingSpecialPrizeInvoice())
                    mHandler.sendEmptyMessage(UPDATE_TEXT_NO_WIN);
            } else {
            	  
            	  // user input digits number is less than 3, or
            	  // 4 <= user input < 8
                mHandler.sendEmptyMessage(UPDATE_TEXT);
            }
        }
    }
    
    // set custom number keyboard image button
    public void onSetInvoiceIME() {

        LottoLog.log("onSetInvoiceIME mMatchingAll = " + mMatchingAll);

        setMode(MODE_MATCHING);

        mlayoutMap = new LayoutMappingIme();
        mlayoutMap.layout = TWLottoLayout.mCustomIme;
        
        // assign array size mCustomIme to mlayoutMap.ib
        mlayoutMap.ib = new ImageButton[TWLottoLayout.mCustomIme.length];

        for (int i = 0; i < TWLottoLayout.mCustomIme.length; i++) {
            mlayoutMap.ib[i] = (ImageButton) findViewById(mlayoutMap.layout[i]);
            mlayoutMap.ib[i].setOnClickListener(this);
            mlayoutMap.ib[i].setOnTouchListener(this);
            onSetInvoiceIMEColor();
        }
    }
    
    // if user enter 3-digits number that match some winning info,
    // whole keyboard will change color.
    public void onSetInvoiceIMEColor() {

        for (int i = 0; i < TWLottoLayout.mCustomIme.length; i++) {   
            mlayoutMap.ib[i] = (ImageButton) findViewById(mlayoutMap.layout[i]);
            if(mMatchingAll) {
                mlayoutMap.ib[i].setBackgroundResource(R.drawable.btn_digit_highlight);
                ImageButton imgButton = (ImageButton) findViewById(R.id.custom_ime_none);
                imgButton.setBackgroundResource(R.drawable.btn_digit_highlight);
            } else {
                mlayoutMap.ib[i].setBackgroundResource(R.drawable.digit_normal);
                ImageButton imgButton = (ImageButton) findViewById(R.id.custom_ime_none);
                imgButton.setBackgroundResource(R.drawable.digit_normal);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_INVOICE_WINNING_PRIZE:
            LayoutInflater infoFactory = LayoutInflater.from(this);
            final View winningInfoView = infoFactory.inflate(
                    R.layout.invoice_alert_winning_info, null);
            return new AlertDialog.Builder(TWLottoInvoiceDisplay.this)
                    .setIcon(R.drawable.ic_star)
                    .setView(winningInfoView)
                    .setTitle(R.string.invoice_congratulations_you_win)
                    .setMessage(
                            getString(R.string.invoice_you_enter)
                                         + " "
                                    + mUserInputInvoice
                                    + "\n"
                                    + getString(R.string.invoice_congrads_you_win)
                                    + " " + mappingPrize(mPrize)
                                    + "\n"
                                    + getString(R.string.invoice_prize)
                                    + mAwardMoney)
                    .setPositiveButton(
                            R.string.invoice_winning_alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    
                                    if (TWLottoSetting.ifVibrate())
                                        mVibrator.vibrate(VIBRATE_TIME);
                                    
                                    mHandler.sendEmptyMessage(UPDATE_TEXT_EMPTY);
                                    mMatchingAll = false;
                                    onSetInvoiceIMEColor();
                                    TextView tv = (TextView) findViewById(R.id.invoice_title);
                                    tv.setText(R.string.invoice_enter_last_3no);
                                    tv.setTextColor(Color.WHITE);
                                }
                            }).create();
        case DIALOG_INVOICE_NO_WINNING:
            return new AlertDialog.Builder(TWLottoInvoiceDisplay.this)
                    .setIcon(R.drawable.ic_x)
                    .setTitle(R.string.invoice_try_again)
                    .setPositiveButton(
                            R.string.invoice_winning_alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    
                                    if (TWLottoSetting.ifVibrate())
                                        mVibrator.vibrate(VIBRATE_TIME);
                                    
                                    mHandler.sendEmptyMessage(UPDATE_TEXT_EMPTY);
                                    mMatchingAll = false;
                                    onSetInvoiceIMEColor();
                                    TextView tv = (TextView) findViewById(R.id.invoice_title);
                                    tv.setText(R.string.invoice_enter_last_3no);
                                    tv.setTextColor(Color.WHITE);
                                }
                            }).create();
        case DIALOG_WAIT_FOR_UPDATING: {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.info_updateing));
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            return dialog;
        }

        }
        return null;
    }

    private boolean matchingSpecialPrizeInvoice() {
        for (int i = 0; i < mInvoiceMatching.length; i++) {
            if (mInvoiceMatching[i] != null && mInvoiceMatching[i].length() > 0) {
                if (mInvoiceMatching[i].endsWith(mUserInputInvoice)) {
                    mMatchingAll = true;
                    onSetInvoiceIME();
                    mHandler.sendEmptyMessage(UPDATE_TEXT_EMPTY);
                    return true;
                } else {
                    LottoLog.log("matchingSpecialPrizeInvoice NOO!!");
                }
            }
        }
        return false;
    }

    private boolean matchingSpecialPrizeInvoiceAll() {

        int index = 0;
        // For grand(special) prize
        for (int i = 0; i < mPrizeIndex[0]; i++) {
            if (mInvoiceMatching[i].equalsIgnoreCase(mUserInputInvoice)) {
                mPrize = MATCHING_INVOICE_GRAND;
                return true;
            }
        }

        index = mPrizeIndex[0] + mPrizeIndex[1];
        // for top prize
        for (int i = mPrizeIndex[0]; i < index; i++) {
            if (mInvoiceMatching[i].equalsIgnoreCase(mUserInputInvoice)) {
                mPrize = MATCHING_INVOICE_TOP;
                return true;
            }
        }

        // for first prize and second prize..etc
        for (int i = index; i < index + mPrizeIndex[2]; i++) {
            if (mInvoiceMatching[i] != null && mInvoiceMatching[i].length() > 3) {
                for (int j = 0; j < 6; j++) {
                    if (mInvoiceMatching[i].endsWith(mUserInputInvoice
                            .substring(j, 8))) {
                        mPrize = j + 1;
                        return true;
                    }
                }
            }
        }
        
        // for extra sixth prize
        if (mPrizeIndex[2] != 0) {
            index = index + mPrizeIndex[2];
            for (int i = index; i < index + mPrizeIndex[3]; i++) {
                if (mUserInputInvoice.endsWith(mInvoiceMatching[i])){
                    mPrize = MATCHING_INVOICE_SIXTH;
                    return true;
                }
            }
        }

        return false;
    }

    private String mappingPrize(int index) {
        String prize = null;
        LottoLog.log("index = " + index);

        switch (index) {
        case MATCHING_INVOICE_GRAND:
            prize = getString(R.string.grand_prize);
            mAwardMoney = winninginfo_invoice_prize[0];
            break;
        case MATCHING_INVOICE_TOP:
            prize = getString(R.string.top_prize);
            mAwardMoney = winninginfo_invoice_prize[1];
            break;
        case MATCHING_INVOICE_FIFTH:
            prize = getString(R.string.fifth_prize);
            mAwardMoney = getString(R.string.invoice_winning_1000);
            break;
        case MATCHING_INVOICE_FORTH:
            prize = getString(R.string.fourth_prize);
            mAwardMoney = getString(R.string.invoice_winning_4000);
            break;
        case MATCHING_INVOICE_THIRD:
            prize = getString(R.string.third_prize);
            mAwardMoney = getString(R.string.invoice_winning_10000);
            break;
        case MATCHING_INVOICE_SECOND:
            prize = getString(R.string.second_prize);
            mAwardMoney = getString(R.string.invoice_winning_40000);
            break;
        case MATCHING_INVOICE_FIRST:
            prize = getString(R.string.first_prize);
            mAwardMoney = winninginfo_invoice_prize[2];
            break;
        case MATCHING_INVOICE_SIXTH:
            prize = getString(R.string.sixth_prize);
            mAwardMoney = winninginfo_invoice_prize[3];
            break;
        }
        return prize;
    }

    // show the invoice winning number info to user
    private void addSpecialPrizeRowInvoice(int position) {
        WinningInfoV2 winningInfo;
        int invoiceLength = 0;
        int invoiceId = 0; // use to control int array: INVOICE_RESULT_LAYOUT_NO
        // R.id.invoice_number_info is a LinearLayout defined in
        // display_invoice_number_menu.xml
        ViewGroup group = (ViewGroup) this
                .findViewById(R.id.invoice_number_info);
        group.setVisibility(View.VISIBLE);

        // mPrizeIndex.length is 4
        for (int i = 0; i < mPrizeIndex.length; i++)
            mPrizeIndex[i] = 0;

        // to get the correct issue of invoice winning info
        if (!orderCorrect) {
            if (position == 0)
                winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(1);
            else
                winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(0);
        }
        else {
            winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(position);
        }

        // set each type of winning number and corresponding prize number
        winninginfo_invoice[0] = winningInfo.grandPrizeNo;
        winninginfo_invoice[1] = winningInfo.topPrizeNo;
        winninginfo_invoice[2] = winningInfo.firstPrizeNo;
        winninginfo_invoice[3] = winningInfo.sixthPrizeNo;

        winninginfo_invoice_prize[0] = winningInfo.grandPrize;
        winninginfo_invoice_prize[1] = winningInfo.topPrize;
        winninginfo_invoice_prize[2] = winningInfo.firstPrize;
        winninginfo_invoice_prize[3] = winningInfo.sixthPrize;

        // LinearLayout
        View row = getLayoutInflater().inflate(R.layout.invoice_number_list,
                group, false);

        // set the title of invoice winning info
        // here we use the ISSUE number as the title
        ((TextView) row.findViewById(R.id.invoice_draw))
                .setText(winningInfo.drawID);

        // winninginfo_invoice.length is 4
        for (int i = 0; i < winninginfo_invoice.length; i++) {

            // if i == 3
            if (i == winninginfo_invoice.length - 1) {
                invoiceLength = SIXTH_PRIZE_LENGTH; // assign 5
            }
            else {
                invoiceLength = INVOICE_PRIZE_LENGTH; // assign 3
            }

            for (int j = 0; j < invoiceLength; j++) {
                if (winninginfo_invoice[i][j] != null) {

                    if (winninginfo_invoice[i].equals(winningInfo.sixthPrizeNo)) {
                        winninginfo_invoice[i][j] = checkIfNeedInsertZero(
                                INVOICE_PRIZE_LENGTH, winninginfo_invoice[i][j]);
                    }
                    else {
                        winninginfo_invoice[i][j] = checkIfNeedInsertZero(
                                INVOICE_PRIZE_ALL_LENGTH,
                                winninginfo_invoice[i][j]);
                    }

                    // set the winning number to specific TextView
                    ((TextView) row
                            .findViewById(INVOICE_RESULT_LAYOUT_NO[invoiceId]))
                            .setText(winninginfo_invoice[i][j]);

                    // store this winning number to mInvoiceMatching
                    mInvoiceMatching[mIndex++] = winninginfo_invoice[i][j];
                    mPrizeIndex[i]++;

                    // can't find any winning number, let user can't see the
                    // TextView
                }
                else {
                    row.findViewById(INVOICE_RESULT_LAYOUT_NO[invoiceId])
                            .setVisibility(View.GONE);
                }
                invoiceId++;
            }
        }

        // show the winning description to user
        // INVOICE_WINNING_PRIZE_NOTE.length is 4
        for (int j = 0; j < INVOICE_WINNING_PRIZE_NOTE.length; j++) {
            if (winninginfo_invoice_prize[j] != null) {
                if (j == INVOICE_WINNING_PRIZE_NOTE.length - 1) {
                    ((TextView) row.findViewById(INVOICE_WINNING_PRIZE_NOTE[j]))
                            .setText(getString(R.string.invoice_winning_mone_info_3)
                                    + winninginfo_invoice_prize[j]);
                }
                else {
                    ((TextView) row.findViewById(INVOICE_WINNING_PRIZE_NOTE[j]))
                            .setText(getString(R.string.invoice_winning_mone_info_8)
                                    + winninginfo_invoice_prize[j]);
                }
            }
        }
        group.addView(row);
    }

    private String checkIfNeedInsertZero(int numMaxLen, String number) {
        String winningNumber = number;
        if (number.length() < numMaxLen) {
            int insertCnt = numMaxLen - number.length();
            for (int i = 0; i < insertCnt; i++) {
                winningNumber = "0" + winningNumber;
            }
        }
        return winningNumber;
    }

    // 2012-12-19, Yu-An Chen
    // parse two issue of invoice info at the same time
    private void updateInvoiceWinningResult() {

        String site = "http://invoice.etax.nat.gov.tw";

        mWinningInfoAdapter.updateWinningResult(mContext, site, "Invoice",
                WinningInfoProvider.INVOICE_QUERY_COLUMNS, /* 2 */4,
                mWinningInfoAdapter.getLatestInvoiceDrawIDFromDB(0),
                WinningInfoV2.PARSE_INVOICE);

        mWinningInfoAdapter.deleteOldInvoiceRecords(2);
    }
    
//    private void updateInvoiceWinningResult() {
//        for (int i = 0; i < 2; i++) {
//            String site = "";
//            switch (i) {
//            case INVOICE_MONTH_OLDER:
//                site = "http://invoice.etax.nat.gov.tw/etaxinfo_1.htm";
//                break;
//            case INVOICE_MONTH_LATEST:
//                site = "http://invoice.etax.nat.gov.tw/etaxinfo_2.htm";
//                break;
//            }
//
//            mWinningInfoAdapter.updateWinningResult(site, "Invoice",
//                    WinningInfoProvider.INVOICE_QUERY_COLUMNS, /*2*/4,  
//                    mWinningInfoAdapter.getLatestInvoiceDrawIDFromDB(0), 
//                    WinningInfo.PARSE_INVOICE);
//        }
//        mWinningInfoAdapter.deleteOldInvoiceRecords(2);
//    }
    
    private void updateSelf() {
        //get correct spinner position, latest in position 0
        int[] spinnerYearMonth = {0, 0, 0, 0};
        String tmp = null;

        array_spinner = new String[mWinningInfoAdapter.getCount()];
        for (int i = 0; i < mWinningInfoAdapter.getCount(); i++) {
            WinningInfoV2 winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(i);
            array_spinner[i] = winningInfo.drawID;
        }

        if (mWinningInfoAdapter.getCount() > 1 && array_spinner[0].length() > 0 && array_spinner[1].length() > 0) {
            int yearPos = array_spinner[0].indexOf(getString(R.string.convert_year));
            spinnerYearMonth[0] = Integer.parseInt(array_spinner[0].substring(0, yearPos));
            spinnerYearMonth[1] = Integer.parseInt(array_spinner[0].substring(yearPos + 1, array_spinner[0].indexOf("-")));
            
            yearPos = array_spinner[1].indexOf(getString(R.string.convert_year));
            spinnerYearMonth[2] = Integer.parseInt(array_spinner[1].substring(0, yearPos));
            spinnerYearMonth[3] = Integer.parseInt(array_spinner[1].substring(yearPos + 1, array_spinner[1].indexOf("-")));
            
            if (spinnerYearMonth[0] < spinnerYearMonth[2]) {
                tmp = array_spinner[1];
                array_spinner[1] = array_spinner[0];
                array_spinner[0] = tmp;
                orderCorrect = false;
            } else if (spinnerYearMonth[0] == spinnerYearMonth[2]) {
                if (spinnerYearMonth[1] < spinnerYearMonth[3]) {
                    tmp = array_spinner[1];
                    array_spinner[1] = array_spinner[0];
                    array_spinner[0] = tmp;
                    orderCorrect = false;
                }
            }    
        }
        
        Spinner s = (Spinner) findViewById(R.id.spinnerInvoice);
        ArrayAdapter adapter = new ArrayAdapter(this, 
        //        android.R.layout.simple_spinner_item, array_spinner);
                R.layout.spinner_item, array_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {                
                
                setMode(MODE_VIEW);
                // R.id.invoice_number_info is a LinearLayout defined in display_invoice_number.xml
                ViewGroup group = (ViewGroup) findViewById(R.id.invoice_number_info);
                group.removeAllViews();
                mIndex = 0;
                addSpecialPrizeRowInvoice(position);
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    
    private void setMode(int mode) {
        if (mode == MODE_VIEW) {
            mUserInputInvoice = "";
            
            // R.id.invoice_title defined in display_invoice_number.xml
            TextView tv = (TextView) findViewById(R.id.invoice_title);
            tv.setVisibility(View.GONE);
            tv = (TextView) findViewById(R.id.invoice_custom_ime_no);
            tv.setVisibility(View.GONE);
            tv.setText(mUserInputInvoice);
            Button button = (Button) findViewById(R.id.invoice_matching);
            button.setVisibility(View.VISIBLE);
            LinearLayout AdLayout = (LinearLayout)findViewById(R.id.mainLayout);
            AdLayout.setVisibility(View.GONE);
        } else if (mode == MODE_MATCHING) {
            mUserInputInvoice = "";
            TextView tv = (TextView) findViewById(R.id.invoice_title);
            tv.setVisibility(View.VISIBLE);
            if (mMatchingAll) {
                tv.setText(getString(R.string.invoice_win_last_3));
                tv.setTextColor(Color.RED);
            } else {
                tv.setText(R.string.invoice_enter_last_3no);
                tv.setTextColor(Color.WHITE);
            }
            tv = (TextView) findViewById(R.id.invoice_custom_ime_no);
            tv.setVisibility(View.VISIBLE);
            tv.setText(mUserInputInvoice);
            Button button = (Button) findViewById(R.id.invoice_matching);
            button.setVisibility(View.GONE);
            LinearLayout AdLayout = (LinearLayout)findViewById(R.id.mainLayout);
            AdLayout.setVisibility(View.VISIBLE);
            // R.id.invoice_number_info is a LinearLayout defined in display_invoice_number_menu.xml
            ViewGroup group = (ViewGroup) findViewById(R.id.invoice_number_info);
            View row = getLayoutInflater().inflate(R.layout.custom_digits_ime,
                    group, false);
            group.removeAllViews();
            group.addView(row);
            ImageButton imgButton = (ImageButton) findViewById(R.id.custom_ime_digit7);
            imgButton.setMaxHeight(50);
            imgButton = (ImageButton) findViewById(R.id.custom_ime_digit8);
            imgButton.setMaxHeight(50);
            imgButton = (ImageButton) findViewById(R.id.custom_ime_digit9);
            imgButton.setMaxHeight(50);
        }
    }
    
    private void playTone(int toneType) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int ringerMode = audioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
            || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }
        
        if (mToneGenerator != null) {
            mToneGenerator.startTone(toneType, 300);
        }
    }
    
    private Runnable AdRefreshTimer = new Runnable() {
        public void run() {
        	
////////////////////////////////////////////////////////////////////////////////        	
            mAdView.loadAd(mAdRequest);
            mAdHandler.postDelayed(this, AD_REFRESH_MSEC);
        }
    };
}
