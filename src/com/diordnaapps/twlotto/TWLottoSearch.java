package com.diordnaapps.twlotto;

import java.util.Date;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diordnaapps.twlotto.SearchDatePicker.OnDateChangedListener;
import com.diordnaapps.twlotto.menuactivity.TwoMenuItemActivity;
import com.diordnaapps.twlotto.provider.WinningInfoProvider;

public class TWLottoSearch extends TwoMenuItemActivity implements OnClickListener, 
                                   OnTouchListener, OnDateChangedListener {
    public static WinningInfoV2 mWinInfo;
    private Handler mHandler;
    
    WinningInfoAdapter mWinningInfoAdapter;
    private static final int DIALOG_INVLAID_DRAW_NUMBER = 1;
    private static final int DIALOG_LOTTO_INFO_UNAVAILABLE = 2;
    private static final int SEARCHING = 0;
    private static final int SEARCH_DONE = 1;
    private static final int SEARCH_FAIL = 2;
    private static final int UPDATE_DATE_TITLE = 3;
    private static final int UPDATE_DATE_SET = 4;
    private static final int DRAW_NUMBER_LENGTH = 9;

    private RadioButton mRadioButtonDraw = null;
    private RadioButton mRadioButtonDate = null;
    private boolean isButtonDrawEnable = true;
    private int mPosition = 0;
    private String mYear = "";
    private String mMonth = "";
    private Vector<WinningInfoV2> mWinningInfo;
    private boolean update_done = false;
    private Thread mSearchThread;
    private Dialog mDialog;
    private int monthPos;
    private int yearPos;
    SearchDatePicker mSearchDatePicker;
   
    Button mButtonSearch;
    Button mButtonAdd;
    Button mButtonMinus;
    Button mButtonDate;
    EditText mDrawNumber;
    TextView mTextSearching;
    
    private static final long VIBRATE_TIME = 25; 
    private boolean activityFirstEnter;
    private Vibrator mVibrator;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlotto);
        
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        Intent intent = getIntent();
        int lottoType = intent.getIntExtra("lottoType", 0);
        
        mWinningInfoAdapter = new WinningInfoAdapter(this);

        mButtonSearch = (Button) this.findViewById(R.id.search_ok);
        mButtonAdd = (Button) this.findViewById(R.id.search_draw_no_add);
        mButtonMinus = (Button) this.findViewById(R.id.search_draw_no_minus);
        mDrawNumber = (EditText) findViewById(R.id.search_draw_no);
        mButtonDate = (Button) this.findViewById(R.id.search_choose_date);
        mButtonSearch.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mButtonMinus.setOnClickListener(this);
        mButtonDate.setOnClickListener(this);
        
        mTextSearching = (TextView) findViewById(R.id.searching);
        mTextSearching.setVisibility(View.GONE);
        
        // R.id.spinnerLotto_ defined in searchlotto.xml
        Spinner s = (Spinner) findViewById(R.id.spinnerLotto_);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.lottoTypes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setSelection(lottoType); 
        s.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                mPosition = position;
                if (mSearchThread != null)
                    mSearchThread = null;
                setMode(SEARCH_DONE);
                
                if( !activityFirstEnter )
                {                    
                    if (TWLottoSetting.ifVibrate())
                        mVibrator.vibrate(VIBRATE_TIME);
                }
                else
                    activityFirstEnter = false;
                
                // displayNewestDrawNo will show the newest draw num to EditText "mDrawNumber"
                if (displayNewestDrawNo(mPosition, false) != 0)
                    setSearchDrawButtonEnable(false);

                displayNewestDrawNo(mPosition, true);
                
                if (!isButtonDrawEnable)
                    setSearchDrawButtonEnable(false);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                //System.out.println("Spinner1: unselected");
            }
        });
        
        mRadioButtonDraw = (RadioButton) findViewById(R.id.radiobutton_draw);
        mRadioButtonDate = (RadioButton) findViewById(R.id.radiobutton_date);
        mRadioButtonDraw.setChecked(true);
        setSearchDateButtonEnable(false);

        mRadioButtonDraw
                .setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        
                        if (TWLottoSetting.ifVibrate())
                            mVibrator.vibrate(VIBRATE_TIME);
                        
                        isButtonDrawEnable = isChecked;
                        mRadioButtonDate.setChecked(!isChecked);
                        setSearchDateButtonEnable(!isChecked);
                        setSearchDrawButtonEnable(isChecked);
                        mDrawNumber.setEnabled(isChecked);
                        if (mSearchThread != null)
                            mSearchThread = null;
                        setMode(SEARCH_DONE);
                    }
                });
        mRadioButtonDate
        .setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                mRadioButtonDraw.setChecked(!isChecked);
                /*
                setSearchDateButtonEnable(isChecked);
                setSearchDrawButtonEnable(!isChecked);
                mDrawNumber.setEnabled(!isChecked);
                */
            }
        });

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                String[] yeararray = getResources().getStringArray(R.array.date_picker_year);
                String[] montharray = getResources().getStringArray(R.array.date_picker_month);
                String title;
                switch (msg.what) {
                case SEARCH_DONE:
                    setMode(SEARCH_DONE);
                    displayLottoresult(mPosition, mWinningInfo);
                    setSearchButtonEnable(true);
                    if (!isButtonDrawEnable)
                        setSearchDrawButtonEnable(false);
                    else
                        setSearchDrawButtonEnable(true);
                    break;
                case SEARCH_FAIL:
                    setMode(SEARCH_DONE);
                    showDialog(DIALOG_LOTTO_INFO_UNAVAILABLE);
                    setSearchButtonEnable(true);
                    if (!isButtonDrawEnable)
                        setSearchDrawButtonEnable(false);
                    else
                        setSearchDrawButtonEnable(true);
                    break;
                case UPDATE_DATE_TITLE:
                    String year = yeararray[yearPos];
                    String month = montharray[monthPos];
                    title = getString(R.string.string_chinese_calendar) + 
                                    year + getString(R.string.string_chinese_year) +  
                                    month + getString(R.string.string_chinese_month);
                    mDialog.setTitle(title);
                    break;
                case UPDATE_DATE_SET:
                    mYear = yeararray[msg.arg1];
                    mMonth = montharray[msg.arg2];
                    title = getString(R.string.string_chinese_calendar) + 
                                    mYear + getString(R.string.string_chinese_year) + 
                                    mMonth + getString(R.string.string_chinese_month);
                    mButtonDate.setText(title);
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
        mWinInfo = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        String draw = mDrawNumber.getText().toString();
        update_done = false;

        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        int id = v.getId();
        
        // user click the search result
        if (id >= 0 && id < 10) {
            Spinner s = (Spinner) findViewById(R.id.spinnerLotto);
            mWinInfo = mWinningInfo.get(id);
            
            Intent intent = new Intent();
            intent.putExtra("lottoDrawId", mWinInfo.drawID);
            intent.putExtra("lottoType", mPosition);
            intent.setClass(this, EditDrawIdActivity.class);
            startActivity(intent);
        }
        
        switch (v.getId()) {
        case R.id.search_choose_date:
            mDialog = getDatePickerDialog();
            if (mDialog != null)
                mDialog.show();
            break;
            
        case R.id.search_ok:
            ViewGroup group = (ViewGroup) findViewById(R.id.lotto_info);
            group.removeAllViews();
            
            if (isButtonDrawEnable) {
                if (!checkDrawNumber()) {
                    showDialog(DIALOG_INVLAID_DRAW_NUMBER);
                    return;
                }
                
                setMode(SEARCHING);
                
                boolean result = mWinningInfoAdapter
                        .getSearchLottoWinningInfoFromDB(mPosition, draw);
                if (result) {
                    setMode(SEARCH_DONE);
                    
                    // fill search result we got from DB
                    mWinningInfo = new Vector<WinningInfoV2>();
                    mWinningInfo.add((WinningInfoV2) mWinningInfoAdapter.getItem(0));
                    
                    displayLottoresult(mPosition, null);
                } else {
                    if (update_done == false) {
                        setSearchDrawButtonEnable(false);
                        setSearchButtonEnable(false);
                        update_done = true;
                        if (mSearchThread != null)
                            mSearchThread = null;
                        mSearchThread = new Thread() {
                            public void run() {
                                //mMonth = "";
                                searchDrawWinningResult(true);
                                if (mSearchThread == null || !mSearchThread.equals(this))
                                    return;
                                if (mWinningInfo == null || mWinningInfo.size() == 0) {
                                    Message msg = new Message();
                                    msg.what = SEARCH_FAIL;
                                    mHandler.sendMessage(msg);

                                } else {
                                    Message msg = new Message();
                                    msg.what = SEARCH_DONE;
                                    mHandler.sendMessage(msg);
                                }
                            }
                        };
                        mSearchThread.start();
                    }
                }
            } else {
                if (mYear.equals("")) {
                    Toast.makeText(this, getString(R.string.string_date_empty_prompt),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                setMode(SEARCHING);
                
                if (update_done == false) {
                    setSearchDrawButtonEnable(false);
                    setSearchButtonEnable(false);
                    update_done = true;
                    if (mSearchThread != null)
                        mSearchThread = null;
                    mSearchThread = new Thread() {
                        public void run() {
                            searchDrawWinningResult(false);
                            if (mSearchThread == null || !mSearchThread.equals(this))
                                return;
                            if (mWinningInfo == null || mWinningInfo.size() == 0) {
                                Message msg = new Message();
                                msg.what = SEARCH_FAIL;
                                mHandler.sendMessage(msg);

                            } else {
                                Message msg = new Message();
                                msg.what = SEARCH_DONE;
                                mHandler.sendMessage(msg);
                            }
                        }
                    };
                    mSearchThread.start();
                }
            }

            break;

        case R.id.search_draw_no_add:
            if (draw.length() != 0) {
                long tmpDraw = Long.parseLong(draw) + 1;
                /*if (tmpDraw > displayNewestDrawNo(mPosition, false)) {
                    draw = Long.toString(tmpDraw - 1);
                    mDrawNumber.setText(draw);
                } else {*/
                    draw = Long.toString(tmpDraw);
                    mDrawNumber.setText(draw);
                //}
            }
            break;

        case R.id.search_draw_no_minus:
            if (draw.length() != 0) {
                long tmpDraw = Long.parseLong(draw);
                if (tmpDraw > 0) {
                    tmpDraw = tmpDraw - 1;
                    draw = Long.toString(tmpDraw);
                    mDrawNumber.setText(draw);
                }
            }
            break;
        }
    }
    
    private Dialog getDatePickerDialog() {
        LayoutInflater infoFactory = LayoutInflater.from(this);
        final View winningInfoView = infoFactory.inflate(
                R.layout.date_picker_dialog, null);
        mSearchDatePicker = (SearchDatePicker) winningInfoView
                .findViewById(R.id.periodPicker);

        Date dateSet;
        String setYear;
        String setMonth;
        if (!mYear.equals("")) {
            setYear = mYear;
            setMonth = mMonth;
        } else {
            dateSet = new Date();
            setYear = Integer.toString(dateSet.getYear() - 11);
            setMonth = Integer.toString(dateSet.getMonth() + 1);
        }
        
        String[] yeararray = getResources().getStringArray(R.array.date_picker_year);
        String[] montharray = getResources().getStringArray(R.array.date_picker_month);
        for (int i = 0; i < yeararray.length; i++) {
            if (yeararray[i].equals(setYear)) {
                yearPos = i;
                break;
            }
        }
        for (int i = 0; i < montharray.length; i++) {
            if (montharray[i].equals(setMonth))
                monthPos = i;
        }
        return new SearchDatePickerDialog(this, this, yearPos, monthPos);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        
        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        switch (id) {
        case DIALOG_INVLAID_DRAW_NUMBER:
            return new AlertDialog.Builder(TWLottoSearch.this)
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
            return new AlertDialog.Builder(TWLottoSearch.this)
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
        
    private void setSearchDrawButtonEnable(boolean isenable){
        String draw = mDrawNumber.getText().toString();
        if ((isenable && draw.length() > 0) || !isenable) {
            mButtonAdd.setEnabled(isenable);
            mButtonMinus.setEnabled(isenable);
        }
    }
        
    private void setSearchDateButtonEnable(boolean isEnable) {
        mButtonDate.setEnabled(isEnable);
    }
    
    private void setSearchButtonEnable(boolean isEnable) {
    	mButtonSearch.setEnabled(isEnable);
    }
    
    private void searchDrawWinningResult(boolean byDrawId) {
        if (byDrawId && mWinningInfoAdapter.isAutoRefreshNeeded(mPosition)) {
            String site = mWinningInfoAdapter.getLottoSite(mPosition);
            mWinningInfoAdapter.updateWinningResult(this, site, 
                WinningInfoProvider.getLottoTable(mPosition), 
                WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10, 
                mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(mPosition), 
                WinningInfoV2.PARSE_LOTTO);
            boolean result = mWinningInfoAdapter.getSearchLottoWinningInfoFromDB(
                    mPosition, mDrawNumber.getText().toString());
            if (result) {
                mWinningInfo = new Vector<WinningInfoV2>();
                mWinningInfo.add((WinningInfoV2) mWinningInfoAdapter.getItem(0));
                return;
            }
        }
        mWinningInfo = mWinningInfoAdapter.postHttpWinningResult(
                WinningInfoProvider.getLottoTable(mPosition),
                WinningInfoProvider.LOTTO_QUERY_COLUMNS, 10, mDrawNumber.getText().toString(),
                mPosition, byDrawId?"":mMonth, mYear);
    }
    
    private boolean checkDrawNumber() {
        String draw = mDrawNumber.getText().toString();
        if (draw.length() == 0) return false;
        
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

    private int displayNewestDrawNo(int index, boolean setdraw){
        String draw = null;

        draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(index);
        if (draw == null) {
            switch (index) {
            case WinningInfoV2.LOTTO_WEILI:
            case WinningInfoV2.LOTTO_38:
                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_WEILI);
                break;
            case WinningInfoV2.LOTTO_BIG:
            case WinningInfoV2.LOTTO_49:
                draw = mWinningInfoAdapter.getLatestLotteryDrawIDFromDB(WinningInfoV2.LOTTO_BIG);;
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
        if(setdraw){
            mDrawNumber=(EditText)findViewById(R.id.search_draw_no);
            mDrawNumber.setText(draw);
        }
        
        if (draw != null) {
            setSearchDrawButtonEnable(true);
            return Integer.parseInt(draw);
        } else {
            setSearchDrawButtonEnable(false);
            return 0;
        }
    }
    
    private void displayLottoresult(int index, Vector<WinningInfoV2> mWinningInfo){
        
        ViewGroup group = (ViewGroup) this.findViewById(R.id.lotto_info);
        group.removeAllViews();
        group.setVisibility(View.VISIBLE);
        
        addRow(group, mWinningInfo, index);
    }

    public void addRow(ViewGroup group, Vector<WinningInfoV2> winningInfoFile,
            int lottoType) {

        boolean getDateFromDB = false;
        WinningInfoV2 winningInfo = null;
        int lottonumber = TWLotto.mLottoTypeNOList[lottoType].length;
        int infosize = 0;

        if (winningInfoFile == null) {
            winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(0);
            getDateFromDB = true;
            infosize = 1;
        } else {
            infosize = winningInfoFile.size();
        }

        for (int j = 0; j < infosize; j++) {
            if (!getDateFromDB)
                winningInfo = winningInfoFile.get(j);
            TWLotto.zeroStringInsert(winningInfo, lottonumber, lottoType);

            View row = getLayoutInflater().inflate(
                    TWLotto.mLottoList[lottoType], group, false);
            ((TextView) row.findViewById(TWLotto.mLottoDateList[lottoType]))
                    .setText(winningInfo.drawDate);
            ((TextView) row.findViewById(TWLotto.mLottoDrawList[lottoType]))
                    .setText(" " + getString(R.string.draw_period)
                            + winningInfo.drawID + getString(R.string.draw_id));

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
            row.setId(j);
            row.setOnClickListener(this);
            row.setOnTouchListener(this);
            group.addView(row);
        }
    }

    @Override
    public void onPeriodChanged(SearchDatePicker view, int year, int month) {
        
        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        monthPos = month;
        yearPos = year;
        
        Message msg = new Message();
        msg.what = UPDATE_DATE_TITLE;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onPeriodSet(SearchDatePicker view, int year, int month) {
        
        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
        
        Message msg = new Message();
        msg.what = UPDATE_DATE_SET;
        msg.arg1 = year;
        msg.arg2 = month;
        mHandler.sendMessage(msg);
    }    
    
    
    @Override
    public void onPeriodCancel(SearchDatePicker view, int period, int type)
    {
        if (TWLottoSetting.ifVibrate())
            mVibrator.vibrate(VIBRATE_TIME);
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
