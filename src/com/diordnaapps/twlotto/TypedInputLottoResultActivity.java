package com.diordnaapps.twlotto;

import java.util.ArrayList;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.diordnaapps.twlotto.menuactivity.TwoMenuItemActivity;
import com.diordnaapps.twlotto.provider.WinningInfoProvider;

public class TypedInputLottoResultActivity extends TwoMenuItemActivity {
    private static final String TAG = "TypedInputLottoResultActivity";
    private Button mResultButtonOk = null;
    private Button mResultButtonBack = null;
    int mLottoType;
    String mLottoDrawId = "";
    Intent intent;
    String mPrizeWon = "";
    String mPrizeBonus = "";
    
    // defined in typed_input_result.xml
    int[] mLottoResultNumId = { R.id.number_1, R.id.number_2, R.id.number_3, R.id.number_4,
            R.id.number_5, R.id.number_6, R.id.number_7, R.id.number_8, R.id.number_9 };
    
    // defined in typed_input_result.xml
    int[] mLottoResultNumIdTTT = { R.id.number_11, R.id.number_12, R.id.number_13, R.id.number_21,
            R.id.number_22, R.id.number_23, R.id.number_31, R.id.number_32, R.id.number_33 };
    String[] mInputLotto;

    int mSelectedColor = Color.RED;
    private static int mMaxSelectedNo = 0;
    private TWLottoMatch mTWLottoMatch;
    private String[] mWinningBallInfo;
    private WinningInfoAdapter mWinningInfoAdapter;
    TextView mTextSearching;
    WinningInfoV2 winningInfo;
    Handler mHandler;
    private static final int SEARCH_DONE = 1;
    private static final int SEARCH_FAIL = 2;
    Thread backgroundThread;
    
    private static final long VIBRATE_TIME = 25;    
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.typed_input_result);
        
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        intent = getIntent();
        mLottoType = intent.getIntExtra("lottoType", 0);
        mLottoDrawId = intent.getStringExtra("lottoDrawId");
        mInputLotto = intent.getStringArrayExtra("inputLotto");
        LottoLog.log("TypedInputLottoResultActivity mLottoType:" + mLottoType);

        mTWLottoMatch = new TWLottoMatch(this);
        mWinningInfoAdapter = new WinningInfoAdapter(this);

        TextView[] mResultTextView = new TextView[9];
        for(int i : mLottoResultNumId) {
            TextView tv = (TextView) findViewById(i);
            tv.setVisibility(View.GONE);
        }
        
        // the representation of lotto tick-tack-toe is different from other lotto
        // if user want to check tick-tack-toe
        // we have to let user only can see the balls of tick-tack-toe
        int[] lottoResultNumTable;
        if (mLottoType == WinningInfoV2.LOTTO_TICKTACKTOE) {
            lottoResultNumTable = mLottoResultNumIdTTT;
            for(int i : mLottoResultNumId) {
                TextView tv = (TextView) findViewById(i);
                tv.setVisibility(View.GONE);
            }
        } else {
            lottoResultNumTable = mLottoResultNumId;
            for(int i : mLottoResultNumIdTTT) {
                TextView tv = (TextView) findViewById(i);
                tv.setVisibility(View.GONE);
            }
        }
        
        for (int i = 0; i < mResultTextView.length; i++) {
            mResultTextView[i] = (TextView) findViewById(lottoResultNumTable[i]);
        }
        if (mLottoType == WinningInfoV2.LOTTO_WEILI) {
            TextView tv = (TextView) findViewById(R.id.number_7);
            tv.setBackgroundResource(R.drawable.lotto_ball_red);
        }

        mResultButtonOk = (Button) findViewById(R.id.resultBtnOk);
        mResultButtonOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (TWLottoSetting.ifVibrate())
                    mVibrator.vibrate(VIBRATE_TIME);
                
                TypedInputLottoResultActivity.this.setResult(TypedInputLottoActivity.RESULT_OK,
                        intent);
                TypedInputLottoResultActivity.this.finish();
            }
        });
        
        mResultButtonBack = (Button) findViewById(R.id.resultBtnBack);
        mResultButtonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (TWLottoSetting.ifVibrate())
                    mVibrator.vibrate(VIBRATE_TIME);
                
                TypedInputLottoResultActivity.this.setResult(TypedInputLottoActivity.RESULT_BACK,
                        intent);
                TypedInputLottoResultActivity.this.finish();
            }
        });
        
        mTextSearching = (TextView) findViewById(R.id.searching);
        mTextSearching.setVisibility(View.VISIBLE);
        boolean result = mWinningInfoAdapter.getSearchLottoWinningInfoFromDB(mLottoType,
                mLottoDrawId);
        LottoLog.log("result:" + result);
        
        if (result) {
            winningInfo = (WinningInfoV2) mWinningInfoAdapter.getItem(0);
            displayLottoMatchResult(mLottoType, mLottoDrawId, mInputLotto, intent.getIntExtra(
                    "lottoPlayTypePosition", 0), winningInfo);
        } else {
            //Check if EditDrawIdActivity have winning result
            if (EditDrawIdActivity.mWinningInfo != null && 
                    EditDrawIdActivity.mWinningInfo.drawID.equals(mLottoDrawId)) {
                winningInfo = EditDrawIdActivity.mWinningInfo;
                displayLottoMatchResult(mLottoType, mLottoDrawId, mInputLotto, intent.getIntExtra(
                        "lottoPlayTypePosition", 0), winningInfo);
            } else {
                mTextSearching.setVisibility(View.VISIBLE);
                mResultButtonOk.setVisibility(View.GONE);
                mResultButtonBack.setVisibility(View.GONE);
                
                backgroundThread = new Thread()
                {
                    public void run()
                    {
                        Vector<WinningInfoV2> mWinningInfo = mWinningInfoAdapter
                                .postHttpWinningResult(
                                        WinningInfoProvider
                                                .getLottoTable(mLottoType),
                                        WinningInfoProvider.LOTTO_QUERY_COLUMNS,
                                        10, mLottoDrawId, mLottoType, "", "");
                        if (mWinningInfo != null && mWinningInfo.size() > 0)
                            winningInfo = mWinningInfo.get(0);
                        if (winningInfo == null)
                        {
                            Message msg = new Message();
                            msg.what = SEARCH_FAIL;
                            mHandler.sendMessage(msg);
                        }
                        else
                        {
                            Message msg = new Message();
                            msg.what = SEARCH_DONE;
                            mHandler.sendMessage(msg);
                        }
                    }
                };
                LottoLog.log("backgroundThread.start()");
                backgroundThread.start();
            }
        }

        mHandler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                LottoLog.log("msg.what:" + msg.what);
                switch (msg.what)
                {
                    case SEARCH_DONE:
                        mTextSearching.setVisibility(View.GONE);
                        displayLottoMatchResult(mLottoType, mLottoDrawId,
                                mInputLotto,
                                intent.getIntExtra("lottoPlayTypePosition", 0),
                                winningInfo);
                        mResultButtonOk.setVisibility(View.VISIBLE);
                        mResultButtonBack.setVisibility(View.VISIBLE);
                        break;

                    case SEARCH_FAIL:
                        mTextSearching.setVisibility(View.GONE);
                        displayLottoNoResult();
                        mResultButtonOk.setVisibility(View.VISIBLE);
                        mResultButtonBack.setVisibility(View.VISIBLE);
                        break;
                }
            }
        };
    }

    private void displayLottoMatchResult(int lottoType, String lottoDrawId, String[] mInputLotto,
            int mlottoPlayTypePosition, WinningInfoV2 winningInfo) {
        
        // check the winning prize and prize type
        ArrayList mLottoMatchResultInfoAL = getLottoMatchResultInfo(mLottoType, mLottoDrawId,
                mInputLotto, intent.getIntExtra("lottoPlayTypePosition", 0), winningInfo);
        
        // get the info that which balls user selected are matched to the winning balls
        LattoNumInfo[] mLottoBallMatchResultInfo = getLattoBallMatchResultInfo(mLottoType,
                mLottoDrawId, mInputLotto, intent.getIntExtra("lottoPlayTypePosition", 0),
                winningInfo);
        
        // get the winning prize and prize type
        mPrizeWon = mLottoMatchResultInfoAL.get(0).toString();
        mPrizeBonus = mLottoMatchResultInfoAL.get(1).toString();
        LottoLog.log("TypedInputLottoResultActivity mPrizeWon:" + mPrizeWon);
        LottoLog.log("TypedInputLottoResultActivity mPrizeBonus:" + mPrizeBonus);
        
        mTextSearching.setVisibility(View.GONE);
        TextView mTv = (TextView) findViewById(R.id.type_input_result);
        
        mTv.setText(getString(R.string.lotto_type) + getLottoName(mLottoType)
                + getString(R.string.draw_period) + mLottoDrawId + getString(R.string.draw_id)
                + "\n" + getString(R.string.prize_condition) + mPrizeWon + "\n"
                + getString(R.string.prize_title) + mPrizeBonus + "\n");
        
        TextView[] mResultTextView = new TextView[WinningInfoV2.MAX_LOTTO_TYPE];
        
        // lotto tick-tack-toe is different from others
        int[] lottoResultNumTable;
        if (mLottoType == WinningInfoV2.LOTTO_TICKTACKTOE)
            lottoResultNumTable = mLottoResultNumIdTTT;
        else
            lottoResultNumTable = mLottoResultNumId;
        
        for (int i = 0; i < mResultTextView.length; i++) {
            mResultTextView[i] = (TextView) findViewById(lottoResultNumTable[i]);
        }
        
        // let user can see lotto balls
        // if user-selected balls are matched, change their text color
        for (int i = 0; i < WinningInfoV2.MAX_LOTTO_TYPE; i++)
        {
            if (i < mLottoBallMatchResultInfo.length)
            {
                mResultTextView[i].setText(mLottoBallMatchResultInfo[i].mLattoNum);
                mResultTextView[i].setVisibility(View.VISIBLE);
                
                if (mLottoBallMatchResultInfo[i].mBingo == true)
                {
                    mResultTextView[i].setTextColor(mSelectedColor);
                    if (mLottoType == WinningInfoV2.LOTTO_WEILI && i == 6)
                    {
                        mResultTextView[i].setTextColor(Color.YELLOW);
                    }
                    if (mLottoType == WinningInfoV2.LOTTO_BIG
                            && Integer
                                    .parseInt(mLottoBallMatchResultInfo[i].mLattoNum) == Integer
                                    .parseInt(winningInfo.sec2WinningNo[0]))
                    {
                        mResultTextView[i]
                                .setBackgroundResource(R.drawable.lotto_ball_red);
                        mResultTextView[i].setTextColor(Color.YELLOW);
                    }
                }
            }
        }
    }

    private void displayLottoNoResult() {
        TextView mTv = (TextView) findViewById(R.id.type_input_result);
        mTv.setText(getString(R.string.lotto_type) + getLottoName(mLottoType)
                + getString(R.string.draw_period) + mLottoDrawId + getString(R.string.draw_id)
                + "\n" + getString(R.string.data_error) + "\n");
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (backgroundThread != null) {
            if (backgroundThread.isAlive()) {
                backgroundThread.interrupt();
            }
        }
    }

    // Latto Ball Info
    class LattoNumInfo {
        public String mLattoNum = "";
        public boolean mBingo = false;
    }

    // Latto Ball Match Info
    public LattoNumInfo[] getLattoBallMatchResultInfo(int lottoType, String lottoDrawId,
            String[] mInputLotto, int mlottoPlayTypePosition, WinningInfoV2 winningInfo) {
        
        LattoNumInfo[] mLattoNumInfo = null;
        if (winningInfo != null) {
            
            switch (lottoType) {
                
            case WinningInfoV2.LOTTO_WEILI:                
                mWinningBallInfo = new String[mInputLotto.length];
                
                for (int i = 0; i < mInputLotto.length - 1; i++) {
                    mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
                }
                
                mWinningBallInfo[mInputLotto.length - 1] = winningInfo.sec2WinningNo[0];
                mLattoNumInfo = new LattoNumInfo[mInputLotto.length];
                
                for (int i = 0; i < mInputLotto.length - 1; i++) {
                    mLattoNumInfo[i] = new LattoNumInfo();
                    mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                    for (int j = 0; j < mInputLotto.length - 1; j++) {
                        if (Integer.parseInt(mWinningBallInfo[j]) == Integer
                                .parseInt(mInputLotto[i])) {
                            mLattoNumInfo[i].mBingo = true;
                            break;
                        }
                    }
                }
                
                for (int j = mInputLotto.length - 1; j < mInputLotto.length; j++) {
                    mLattoNumInfo[j] = new LattoNumInfo();
                    mLattoNumInfo[j].mLattoNum = mInputLotto[j];
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(mInputLotto[j])) {
                        mLattoNumInfo[j].mBingo = true;
                    }
                }
                
                break;
            case WinningInfoV2.LOTTO_BIG:
                mWinningBallInfo = new String[7];
                for (int i = 0; i < mInputLotto.length; i++) {
                    mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
                }
                mWinningBallInfo[mInputLotto.length] = winningInfo.sec2WinningNo[0];

                mLattoNumInfo = new LattoNumInfo[mInputLotto.length];
                for (int i = 0; i < mInputLotto.length; i++) {
                    mLattoNumInfo[i] = new LattoNumInfo();
                    mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                    for (int j = 0; j < mInputLotto.length; j++) {
                        if (Integer.parseInt(mWinningBallInfo[j]) == Integer
                                .parseInt(mInputLotto[i])) {
                            mLattoNumInfo[i].mBingo = true;
                            break;
                        }
                    }
                    for (int j = mInputLotto.length; j < 7; j++) {
                        if (Integer.parseInt(mWinningBallInfo[j]) == Integer
                                .parseInt(mInputLotto[i])) {
                            mLattoNumInfo[i].mBingo = true;
                        }
                    }
                }
                break;
            case WinningInfoV2.LOTTO_539:
                mWinningBallInfo = new String[mInputLotto.length];
                for (int i = 0; i < mInputLotto.length; i++) {
                    mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
                }
                mLattoNumInfo = new LattoNumInfo[mInputLotto.length];
                for (int i = 0; i < mInputLotto.length; i++) {
                    mLattoNumInfo[i] = new LattoNumInfo();
                    mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                    for (int j = 0; j < mInputLotto.length; j++) {
                        if (Integer.parseInt(mWinningBallInfo[j]) == Integer
                                .parseInt(mInputLotto[i])) {
                            mLattoNumInfo[i].mBingo = true;
                            break;
                        }
                    }
                }
                break;
            case WinningInfoV2.LOTTO_TICKTACKTOE:
                mWinningBallInfo = new String[mInputLotto.length];
                for (int i = 0; i < mInputLotto.length; i++) {
                    mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
                }
                mLattoNumInfo = new LattoNumInfo[mInputLotto.length];
                for (int i = 0; i < mInputLotto.length; i++) {
                    mLattoNumInfo[i] = new LattoNumInfo();
                    mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                    if (Integer.parseInt(mWinningBallInfo[i]) == Integer.parseInt(mInputLotto[i])) {
                        mLattoNumInfo[i].mBingo = true;
                    }
                }
                break;
            case WinningInfoV2.LOTTO_STAR3:
            case WinningInfoV2.LOTTO_STAR4:
                mWinningBallInfo = new String[mInputLotto.length];
                for (int i = 0; i < mInputLotto.length; i++) {
                    mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
                }
                mLattoNumInfo = new LattoNumInfo[mInputLotto.length];
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_ORDER_MATCH
                        || mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_FIRST_OR_LAST_MATCH) {
                    for (int i = 0; i < mInputLotto.length; i++) {
                        mLattoNumInfo[i] = new LattoNumInfo();
                        mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                        if (Integer.parseInt(mWinningBallInfo[i]) == Integer
                                .parseInt(mInputLotto[i])) {
                            mLattoNumInfo[i].mBingo = true;
                        }
                    }
                } else {
                    // 組彩
                    ArrayList<Object> mTargetAL = new ArrayList<Object>();
                    for (int i = 0; i < mInputLotto.length; i++) {
                        mLattoNumInfo[i] = new LattoNumInfo();
                        mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                        mTargetAL.add(mWinningBallInfo[i]);
                    }
                    for (int i = 0; i < mInputLotto.length; i++) {
                        int idx = mTargetAL.indexOf(mInputLotto[i]);
                        if (idx != -1) {
                            mLattoNumInfo[i].mBingo = true;
                            mTargetAL.remove(idx);
                        }
                    }
                }
                break;
            case WinningInfoV2.LOTTO_38:
            case WinningInfoV2.LOTTO_49:
                mWinningBallInfo = new String[6];
                for (int i = 0; i < 6; i++) {
                    mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
                }
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_2_MATCH) {
                    mMaxSelectedNo = 2;
                }
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_3_MATCH) {
                    mMaxSelectedNo = 3;
                }
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_4_MATCH) {
                    mMaxSelectedNo = 4;
                }
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_5_MATCH) {
                    mMaxSelectedNo = 5;
                }
                mLattoNumInfo = new LattoNumInfo[mMaxSelectedNo];
                for (int i = 0; i < mMaxSelectedNo; i++) {
                    mLattoNumInfo[i] = new LattoNumInfo();
                    mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                    for (int j = 0; j < 6; j++) {
                        if (Integer.parseInt(mWinningBallInfo[j]) == Integer
                                .parseInt(mInputLotto[i])) {
                            mLattoNumInfo[i].mBingo = true;
                            break;
                        }
                    }
                }
                break;
            case WinningInfoV2.LOTTO_39:
                mWinningBallInfo = new String[5];
                for (int i = 0; i < 5; i++) {
                    mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
                }
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_2_MATCH) {
                    mMaxSelectedNo = 2;
                }
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_3_MATCH) {
                    mMaxSelectedNo = 3;
                }
                if (mlottoPlayTypePosition == TypedInputLottoActivity.PLAYTYPE_4_MATCH) {
                    mMaxSelectedNo = 4;
                }
                mLattoNumInfo = new LattoNumInfo[mMaxSelectedNo];
                for (int i = 0; i < mMaxSelectedNo; i++) {
                    mLattoNumInfo[i] = new LattoNumInfo();
                    mLattoNumInfo[i].mLattoNum = mInputLotto[i];
                    for (int j = 0; j < 5; j++) {
                        if (Integer.parseInt(mWinningBallInfo[j]) == Integer
                                .parseInt(mInputLotto[i])) {
                            mLattoNumInfo[i].mBingo = true;
                            break;
                        }
                    }
                }
                break;
            }
        }
        return mLattoNumInfo;
    }


    public String getLottoName(int position) {
        switch (position) {
        case WinningInfoV2.LOTTO_WEILI:
            return getString(R.string.weililotto);
        case WinningInfoV2.LOTTO_BIG:
            return getString(R.string.biglotto);
        case WinningInfoV2.LOTTO_539:
            return getString(R.string.lotto539);
        case WinningInfoV2.LOTTO_TICKTACKTOE:
            return getString(R.string.ticktacktoelotto);
        case WinningInfoV2.LOTTO_STAR3:
            return getString(R.string.star3);
        case WinningInfoV2.LOTTO_STAR4:
            return getString(R.string.star4);
        case WinningInfoV2.LOTTO_38:
            return getString(R.string.lotto38);
        case WinningInfoV2.LOTTO_49:
            return getString(R.string.lotto49);
        case WinningInfoV2.LOTTO_39:
            return getString(R.string.lotto39);
        }
        return null;
    }

    public ArrayList getLottoMatchResultInfo(int lottoType, String lottoDrawId,
            String[] inputLotto, int lottoPlayTypePosition, WinningInfoV2 winningInfo) {
        
        ArrayList mLottoResultAL = new ArrayList();
                
        switch (lottoType) {
        case WinningInfoV2.LOTTO_WEILI:
            mLottoResultAL = mTWLottoMatch.getWeiLiLottoResult(inputLotto,
                    winningInfo);
            break;
        case WinningInfoV2.LOTTO_BIG:
            mLottoResultAL = mTWLottoMatch.getBigLottoResult(inputLotto,
                    winningInfo);
            break;
        case WinningInfoV2.LOTTO_539:
            mLottoResultAL = mTWLottoMatch.get539LottoResult(inputLotto,
                    winningInfo);
            break;
        case WinningInfoV2.LOTTO_TICKTACKTOE:
            mLottoResultAL = mTWLottoMatch.getTickTackToeLottoResult(inputLotto, winningInfo);
            break;
        case WinningInfoV2.LOTTO_STAR3:
            mLottoResultAL = mTWLottoMatch.get3StarLottoResult(inputLotto,
                    lottoPlayTypePosition, winningInfo);
            break;
        case WinningInfoV2.LOTTO_STAR4:
            mLottoResultAL = mTWLottoMatch.get4StarLottoResult(inputLotto,
                    lottoPlayTypePosition, winningInfo);
            break;
        case WinningInfoV2.LOTTO_38:
            mLottoResultAL = mTWLottoMatch.get38LottoResult(inputLotto,
                    lottoPlayTypePosition, winningInfo);
            break;
        case WinningInfoV2.LOTTO_49:
            mLottoResultAL = mTWLottoMatch.get49LottoResult(inputLotto,
                    lottoPlayTypePosition, winningInfo);
            break;
        case WinningInfoV2.LOTTO_39:
            mLottoResultAL = mTWLottoMatch.get39LottoResult(inputLotto,
                    lottoPlayTypePosition, winningInfo);
            break;
        }
        return mLottoResultAL;
    }
}
