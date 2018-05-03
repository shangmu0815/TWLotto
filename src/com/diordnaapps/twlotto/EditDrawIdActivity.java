package com.diordnaapps.twlotto;

import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.diordnaapps.twlotto.menuactivity.TwoMenuItemActivity;
import com.diordnaapps.twlotto.provider.WinningInfoProvider;

// show the detail lotto winning information and 
// set a button that users can start check their numbers if click it
public class EditDrawIdActivity extends TwoMenuItemActivity implements OnClickListener,
        OnKeyListener {

    private Context mContext;
    private Handler mHandler;

    private static final int DIALOG_INVLAID_DRAW_NUMBER = 1;
    private static final int DIALOG_LOTTO_INFO_UNAVAILABLE = 2;
    private static final int SEARCHING = 0;
    private static final int SEARCH_DONE = 1;
    private static final int SEARCH_FAIL = 2;
    private static final int DRAW_NUMBER_LENGTH = 9;
    
    private static final int LOTTO_PRIZE_TYPE_COUNT = 10;

    private WinningInfoAdapter mWinningInfoAdapter;
    private int mPosition = 0;
    
    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    public static WinningInfoV2 mWinningInfo;
//    public static WinningInfo mWinningInfo;
    
    private String mLottoDrawId;
    private boolean update_done = false;
    private String mPreviousDraw = "";
    private Thread mSearchThread;

    Button mButtonMatching;
    Button mButtonAdd;
    Button mButtonMinus;
    EditText mDrawNumber;
    TextView mTextSearching;
    
    private static final long VIBRATE_TIME = 25;    
    private boolean activityFirstEnter;
    private Vibrator mVibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_draw_id);

        mContext = this;

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        Intent intent = getIntent();
        int lottoType = intent.getIntExtra("lottoType", 0);
        
        // if user click any draw of lotto info
        // then we will get the draw id
        mLottoDrawId = intent.getStringExtra("lottoDrawId");

        mWinningInfoAdapter = new WinningInfoAdapter(this);

        mButtonMatching = (Button) findViewById(R.id.lotto_start_matching);
        mButtonAdd = (Button) findViewById(R.id.search_draw_no_add);
        mButtonMinus = (Button) findViewById(R.id.search_draw_no_minus);
        mDrawNumber = (EditText) findViewById(R.id.search_draw_no);
        mButtonMatching.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mButtonMinus.setOnClickListener(this);
        mDrawNumber.setOnKeyListener(this);

        mTextSearching = (TextView) findViewById(R.id.searching);

        Spinner s = (Spinner) findViewById(R.id.spinnerLotto_);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.lottoTypes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setSelection(lottoType);
        
        // if user select a lotto type, default is lotto weili
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) { 
                
                // store lotto type which user select
                mPosition = position;
                
                if (mSearchThread != null)
                    mSearchThread = null;
                
                if( !activityFirstEnter )
                {                    
                    if (TWLottoSetting.ifVibrate())
                        mVibrator.vibrate(VIBRATE_TIME);
                }
                else
                    activityFirstEnter = false;
                
                if (displayNewestDrawNo(mPosition, false) != 0)
                {
                    // set add add minus button enable or disable 
                    setEditDrawButtonEnable(false);
                }
                
                update_done = false;
                displayNewestDrawNo(mPosition, true);
                
                // if user click any draw of lotto info in the very first lotto page
                // then we will get the draw id
                if (mLottoDrawId != null) {
                    
                    mDrawNumber.setText(mLottoDrawId);
                    mLottoDrawId = null;
                    
                    if (TWLottoSearch.mWinInfo != null) {
                        mWinningInfo = TWLottoSearch.mWinInfo;
                        setMode(SEARCH_DONE);
                        
                        // show winning info details to user
                        displayLottoresult(mPosition, mWinningInfo);
                    }
                    else {
                        onDrawnoChanged();
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // System.out.println("Spinner1: unselected");
            }
        });

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case SEARCH_DONE:
                    setMode(SEARCH_DONE);
                    displayLottoresult(mPosition, mWinningInfo);
                    
                    // set add add minus button enable or disable 
                    setEditDrawButtonEnable(true);
                    break;

                case SEARCH_FAIL:
                    setMode(SEARCH_DONE);
                    showDialog(DIALOG_LOTTO_INFO_UNAVAILABLE);
                    
                    // set add add minus button enable or disable 
                    setEditDrawButtonEnable(true);
                    break;
                }
            }
        };
    }
    
    
    
    @Override
    protected void onResume()
    {        
        super.onResume();
        activityFirstEnter = true;
    }



    @Override
    public void onDestroy() {
        if (mSearchThread != null)
            mSearchThread = null;
        mWinningInfo = null;
        super.onDestroy();
    }

    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    private void searchWinningResult() {
        
        // the variable mPosition is set in method onCreate
        if (mWinningInfoAdapter.isAutoRefreshNeeded(mPosition)) {
            
            // get the website we want to retrieve data
            String site = mWinningInfoAdapter.getLottoSite(mPosition);
            
            // get lotto winning info from web, and store it in DB
            mWinningInfoAdapter
                    .updateWinningResult(mContext, site, WinningInfoProvider
                            .getLottoTable(mPosition),
                            WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10,
                            mWinningInfoAdapter
                                    .getLatestLotteryDrawIDFromDB(mPosition),
                            WinningInfoV2.PARSE_LOTTO);
            
            // get the winning info user want to see
            boolean result = mWinningInfoAdapter
                    .getSearchLottoWinningInfoFromDB(mPosition, mDrawNumber
                            .getText().toString());
            
            if (result) {
                mWinningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(0);
                return;
            }
        }
        
        Vector<WinningInfoV2> winningInfo = mWinningInfoAdapter
                .postHttpWinningResult(
                        WinningInfoProvider.getLottoTable(mPosition),
                        WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10,
                        mDrawNumber.getText().toString(), mPosition, "", "");
        
        if (winningInfo != null && winningInfo.size() > 0)
            mWinningInfo = winningInfo.get(0);
    }
    
//    private void searchWinningResult() {
//        if (mWinningInfoAdapter.isAutoRefreshNeeded(mPosition)) {
//            String site = mWinningInfoAdapter.getLottoSite(mPosition);
//            mWinningInfoAdapter.updateWinningResult(mContext, site, 
//                WinningInfoProvider.getLottoTable(mPosition), 
//                WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10, 
//                mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(mPosition), 
//                WinningInfo.PARSE_LOTTO);
//            boolean result = mWinningInfoAdapter.getSearchLottoWinningInfoFromDB(
//                    mPosition, mDrawNumber.getText().toString());
//            if (result) {
//                mWinningInfo = (WinningInfo) mWinningInfoAdapter.getItem(0);
//                return;
//            }
//        }
//        Vector<WinningInfo> winningInfo = mWinningInfoAdapter.postHttpWinningResult(
//                WinningInfoProvider.getLottoTable(mPosition),
//                WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10, mDrawNumber.getText().toString(),
//                mPosition, "", "");
//        if (winningInfo != null && winningInfo.size() > 0)
//            mWinningInfo = winningInfo.get(0);
//    }

    private boolean checkDrawNumber() {
        String draw = mDrawNumber.getText().toString();
        if (draw.length() == 0)
            return false;

        //long tmpDraw = Long.parseLong(draw);
        if (draw.length() != DRAW_NUMBER_LENGTH) {
            return false;
        }
        long result = displayNewestDrawNo(mPosition, false);
        if (result == 0)
            return true;
        /*else if (tmpDraw > result)
            return false;*/
        else
            return true;
    }

    @Override
    public void onClick(View v) {

        String draw = mDrawNumber.getText().toString();
        update_done = false;
        
        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        if (v.getId() == R.id.lotto_start_matching) {
            if (!checkDrawNumber()) {
                showDialog(DIALOG_INVLAID_DRAW_NUMBER);
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("lottoType", mPosition);
            intent.putExtra("lottoDrawId", draw);
            intent.setClass(mContext, TypedInputLottoActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.search_draw_no_add) {

            if (draw.length() != 0) {
                long tmpDraw = Long.parseLong(draw) + 1;
                /*if (tmpDraw > displayNewestDrawNo(mPosition, false)) {
                    draw = Long.toString(tmpDraw - 1);
                    mDrawNumber.setText(draw);
                } else {*/
                draw = Long.toString(tmpDraw);
                mDrawNumber.setText(draw);
                //}
                // draw number change, update the corresponding winning info to user 
                onDrawnoChanged();
            }
            else {
                // set add add minus button enable or disable 
                setEditDrawButtonEnable(false);
            }

        } else if (v.getId() == R.id.search_draw_no_minus) {
            if (draw.length() != 0) {
                long tmpDraw = Long.parseLong(draw);
                if (tmpDraw > 0) {
                    tmpDraw = tmpDraw - 1;
                    draw = Long.toString(tmpDraw);
                    mDrawNumber.setText(draw);
                }
                
                // draw number change, update the corresponding winning info to user
                onDrawnoChanged();
            }
            else {
                // set add add minus button enable or disable 
                setEditDrawButtonEnable(false);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_INVLAID_DRAW_NUMBER:
            return new AlertDialog.Builder(EditDrawIdActivity.this)
                    .setIcon(R.drawable.ic_menu_about)
                    .setTitle(R.string.invalid_draw_number)
                    .setPositiveButton(
                            R.string.invoice_winning_alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    
                                    if (TWLottoSetting.ifVibrate())
                                        mVibrator.vibrate(VIBRATE_TIME);
                                    
                                    displayNewestDrawNo(mPosition, true);
                                }
                            }).create();
        case DIALOG_LOTTO_INFO_UNAVAILABLE:
            return new AlertDialog.Builder(EditDrawIdActivity.this)
                    .setIcon(R.drawable.ic_menu_about)
                    .setTitle(R.string.data_error)
                    .setPositiveButton(
                            R.string.invoice_winning_alert_dialog_ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    
                                    if (TWLottoSetting.ifVibrate())
                                        mVibrator.vibrate(VIBRATE_TIME);
                                    
                                    //displayNewestDrawNo(mPosition, true);
                                    setMode(SEARCH_FAIL);
                                }
                            }).create();
        }
        return null;
    }

    // set add add minus button enable or disable 
    private void setEditDrawButtonEnable(boolean isenable) {

        String draw = mDrawNumber.getText().toString();
        if ((isenable && draw.length() > 0) || !isenable) {
            mButtonAdd.setEnabled(isenable);
            mButtonMinus.setEnabled(isenable);
        }
    }

    // user choose a draw id about a specific lotto type
    // check the id is valid or not
    private void onDrawnoChanged() {

        // the draw number is invalid
        if ((displayNewestDrawNo(mPosition, false) != 0) && !checkDrawNumber()) {
            showDialog(DIALOG_INVLAID_DRAW_NUMBER);
            return;
        }

        mWinningInfo = null;
        setMode(SEARCHING);

        boolean result = mWinningInfoAdapter.getSearchLottoWinningInfoFromDB(
                mPosition, mDrawNumber.getText().toString());
        
        // store the current draw id
        mPreviousDraw = mDrawNumber.getText().toString();
        
        // if we can find any info from DB, show it
        if (result) {
            setMode(SEARCH_DONE);
            displayLottoresult(mPosition, null);
        }
        
        // scenario: user in the lotto page didn't select 
        // the lotto type he want to see from spinner,
        // and then he click the "match prize" button right away
        // now skip to matching prize page(this activity),
        // and user in this page choose the lotto type he want to see
        // so we have to update the winning info
        else {
            if (update_done == false) {

                // set add add minus button enable or disable
                setEditDrawButtonEnable(false);

                update_done = true;
                
                if (mSearchThread != null)
                    mSearchThread = null;
                
                mSearchThread = new Thread() {
                    public void run() {
                                                
                        searchWinningResult();
                        
                        if (mSearchThread == null || !mSearchThread.equals(this))
                            return;
                        
                        // if find any info from method searchWinningResult()
                        // the variable mWinningInfo will not be null
                        if (mWinningInfo == null) {
                            Message msg = new Message();
                            msg.what = SEARCH_FAIL;
                            mHandler.sendMessage(msg);

                        }
                        else {
                            Message msg = new Message();
                            msg.what = SEARCH_DONE;
                            mHandler.sendMessage(msg);
                        }
                    }
                };
                mSearchThread.start();
            }
        }
    }

    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    private int displayNewestDrawNo(int index, boolean setdraw) {
        String draw = null;

        draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(index);
        
        // if fail to get info from DB, do it again
        if (draw == null) {
            switch (index) {
            case WinningInfoV2.LOTTO_WEILI:
            case WinningInfoV2.LOTTO_38:
                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_WEILI);
                break;
            case WinningInfoV2.LOTTO_BIG:
            case WinningInfoV2.LOTTO_49:
                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_BIG);
                break;
            case WinningInfoV2.LOTTO_539:
            case WinningInfoV2.LOTTO_TICKTACKTOE:
            case WinningInfoV2.LOTTO_STAR3:
            case WinningInfoV2.LOTTO_STAR4:
            case WinningInfoV2.LOTTO_39:
                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_STAR4);
                break;
            }
        }
        
        if (setdraw && draw != null) {
            mDrawNumber = (EditText) findViewById(R.id.search_draw_no);
            mDrawNumber.setText(draw);
            onDrawnoChanged();
        }

        if (draw != null)
        {
            // set add add minus button enable or disable 
            setEditDrawButtonEnable(true);
            return Integer.parseInt(draw);
        }
        else
        {
            // set add add minus button enable or disable 
            setEditDrawButtonEnable(false);
            return 0;
        }
    }
    
//    private int displayNewestDrawNo(int index, boolean setdraw) {
//        String draw = null;
//
//        draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(index);
//        if (draw == null) {
//            switch (index) {
//            case WinningInfo.LOTTO_WEILI:
//            case WinningInfo.LOTTO_38:
//                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfo.LOTTO_WEILI);
//                break;
//            case WinningInfo.LOTTO_BIG:
//            case WinningInfo.LOTTO_49:
//                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfo.LOTTO_BIG);
//                break;
//            case WinningInfo.LOTTO_539:
//            case WinningInfo.LOTTO_TICKTACKTOE:
//            case WinningInfo.LOTTO_STAR3:
//            case WinningInfo.LOTTO_STAR4:
//            case WinningInfo.LOTTO_39:
//                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfo.LOTTO_STAR4);
//                break;
//            }
//        }
//        
//        if (setdraw && draw != null) {
//            mDrawNumber = (EditText) findViewById(R.id.search_draw_no);
//            mDrawNumber.setText(draw);
//            onDrawnoChanged();
//        }
//
//        if (draw != null) {
//            setEditDrawButtonEnable(true);
//            return Integer.parseInt(draw);
//        } else {
//            setEditDrawButtonEnable(false);
//            return 0;
//        }
//    }
    
    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    private void displayLottoresult(int index, WinningInfoV2 winningInfo) {
        
        ViewGroup group = (ViewGroup) this.findViewById(R.id.lotto_info);
        group.setVisibility(View.VISIBLE);

        addRow(group, winningInfo, index);
    }
    
//    private void displayLottoresult(int index, WinningInfo winningInfo) {
//        ViewGroup group = (ViewGroup) this.findViewById(R.id.lotto_info);
//        group.setVisibility(View.VISIBLE);
//
//        addRow(group, winningInfo, index);
//    }

    
    // 2012-12-19, Yu-An Chen
    // add some member variables in WinningInfoV2
    
    // show winning info details to user
    public void addRow(ViewGroup group, WinningInfoV2 winningInfo, int lottoType) {

        int lottonumber = TWLotto.mLottoTypeNOList[lottoType].length;
        
        View row = getLayoutInflater().inflate(TWLotto.mLottoList[lottoType],
                group, false);
        
        if (winningInfo == null) {
            winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(0);
        }

        TWLotto.zeroStringInsert(winningInfo, lottonumber, lottoType);
        
        ((TextView) row.findViewById(TWLotto.mLottoDateList[lottoType]))
                .setText(winningInfo.drawDate);
        
        ((TextView) row.findViewById(TWLotto.mLottoDrawList[lottoType]))
                .setText(" " + getString(R.string.draw_period)
                        + winningInfo.drawID + getString(R.string.draw_id));

        // show the lotto ball with number
        for (int k = 0; k < lottonumber; k++) {
            ((TextView) row
                    .findViewById(TWLotto.mLottoTypeNOList[lottoType][k]))
                    .setText(winningInfo.sec1WinningNo[k]);
        }
        
        if (lottoType == WinningInfoV2.LOTTO_WEILI
                || lottoType == WinningInfoV2.LOTTO_BIG) {
            ((TextView) row
                    .findViewById(TWLotto.mLottoTypeNOList[lottoType][lottonumber - 1]))
                    .setText(winningInfo.sec2WinningNo[0]);
        }
        group.addView(row);
        
        // add cash date and prize info
        row = getLayoutInflater().inflate(R.layout.lotto_details, group, false);
        TextView tv = (TextView) row.findViewById(R.id.single_draw_prize_title);
        tv.setVisibility(View.VISIBLE);
        tv = (TextView) row.findViewById(R.id.cash_date);
        tv.setText(getString(R.string.string_cash_date_title) + " " + 
                   winningInfo.cashDate);
        tv.setVisibility(View.VISIBLE);
        for (int i = 0; i < LOTTO_PRIZE_TYPE_COUNT; i++) {
            if (winningInfo.prize[i] != null) {
                tv = (TextView) row.findViewById(TWLottoLayout.prizeList[i]);
                if (winningInfo.prize[i].equals("0"))
                    tv.setText(getString(TWLottoLayout.prizeTitle[i]) + ": " + 
                        getString(R.string.lotto_detail_info_no_prize));
                else
                    tv.setText(getString(TWLottoLayout.prizeTitle[i]) + ": " + 
                       winningInfo.prize[i] + " " + getString(R.string.string_dollar));
                tv.setVisibility(View.VISIBLE);
            }
        }
        group.addView(row);
    }
    
//    public void addRow(ViewGroup group, WinningInfo winningInfo, int lottoType) {
//
//        int lottonumber = TWLotto.mLottoTypeNOList[lottoType].length;
//        View row = getLayoutInflater().inflate(TWLotto.mLottoList[lottoType],
//                group, false);
//        if (winningInfo == null) {
//            winningInfo = (WinningInfo) mWinningInfoAdapter.getItem(0);
//        }
//
//        TWLotto.zeroStringInsert(winningInfo, lottonumber, lottoType);
//
//        ((TextView) row.findViewById(TWLotto.mLottoDateList[lottoType]))
//                .setText(winningInfo.drawDate);
//        ((TextView) row.findViewById(TWLotto.mLottoDrawList[lottoType]))
//                .setText(" " + getString(R.string.draw_period)
//                        + winningInfo.drawID + getString(R.string.draw_id));
//
//        for (int k = 0; k < lottonumber; k++) {
//            ((TextView) row
//                    .findViewById(TWLotto.mLottoTypeNOList[lottoType][k]))
//                    .setText(winningInfo.sec1WinningNo[k]);
//        }
//        if (lottoType == WinningInfo.LOTTO_WEILI
//                || lottoType == WinningInfo.LOTTO_BIG) {
//            ((TextView) row
//                    .findViewById(TWLotto.mLottoTypeNOList[lottoType][lottonumber - 1]))
//                    .setText(winningInfo.sec2WinningNo[0]);
//        }
//        group.addView(row);
//        
//        // add cash date and prize info
//        row = getLayoutInflater().inflate(R.layout.lotto_details, group, false);
//        TextView tv = (TextView) row.findViewById(R.id.single_draw_prize_title);
//        tv.setVisibility(View.VISIBLE);
//        tv = (TextView) row.findViewById(R.id.cash_date);
//        tv.setText(getString(R.string.string_cash_date_title) + " " + 
//                   winningInfo.cashDate);
//        tv.setVisibility(View.VISIBLE);
//        for (int i = 0; i < 10; i++) {
//            if (winningInfo.prize[i] != null) {
//                tv = (TextView) row.findViewById(TWLottoLayout.prizeList[i]);
//                if (winningInfo.prize[i].equals("0"))
//                    tv.setText(getString(TWLottoLayout.prizeTitle[i]) + ": " + 
//                        getString(R.string.lotto_detail_info_no_prize));
//                else
//                    tv.setText(getString(TWLottoLayout.prizeTitle[i]) + ": " + 
//                       winningInfo.prize[i] + " " + getString(R.string.string_dollar));
//                tv.setVisibility(View.VISIBLE);
//            }
//        }
//        group.addView(row);
//    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        String draw = mDrawNumber.getText().toString();
        if (draw.length() == DRAW_NUMBER_LENGTH
                && !mPreviousDraw.equalsIgnoreCase(draw)) {
            update_done = false;
            onDrawnoChanged();
        }
        
        // new added condition
        if( keyCode == KeyEvent.KEYCODE_ENTER )
        {
            onDrawnoChanged();
        }
        
        return false;
    }
    
    private void setMode(int mode) {
        ViewGroup group = (ViewGroup) findViewById(R.id.lotto_info);
        group.removeAllViews();
        if (mode == SEARCHING) {
            mTextSearching.setVisibility(View.VISIBLE);
        } else {
            mTextSearching.setVisibility(View.GONE);
        }
    }
}
