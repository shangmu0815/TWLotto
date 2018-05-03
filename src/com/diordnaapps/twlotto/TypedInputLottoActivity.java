package com.diordnaapps.twlotto;

import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diordnaapps.twlotto.menuactivity.TwoMenuItemActivity;

public class TypedInputLottoActivity extends TwoMenuItemActivity implements OnClickListener {

    private static final String TAG = "TypedInputLottoActivity";
    
    private static int RESULT_REQUEST_CODE = 0;
    public static int RESULT_BACK = 0;
    public static int RESULT_OK = 1;
    
    private static final int SELECTED_COLOR = Color.RED;
    private static final int UNSELECTED_COLOR = Color.WHITE;
    private static final int SEC2_SELECTED_COLOR = Color.YELLOW;
    
    public static final int PLAYTYPE_2_MATCH = 0;
    public static final int PLAYTYPE_3_MATCH = 1;
    public static final int PLAYTYPE_4_MATCH = 2;
    public static final int PLAYTYPE_5_MATCH = 3;
    
    public static final int PLAYTYPE_ORDER_MATCH = 0;
    public static final int PLAYTYPE_NO_ORDER_MATCH = 1;
    public static final int PLAYTYPE_FIRST_OR_LAST_MATCH = 2;
    
    private Button mButtonOk = null;
    private Button mButtonBack = null;
    private TextView mSec1SelPrompt = null;
    private TextView mSec2SelPrompt = null;
    
    int mLottoType;
    String mLottoDrawId = "";
    private String[] mInputLotto;
    
    private int mLottoPlayTypePosition = 0;
    Spinner s;
    ArrayAdapter<CharSequence> adapter;
    
    public class LayoutMapping {
        int layout[];
        TextView tv[];
        TextView textPrompt;
        int maxSelectable = 0;
        int selectedCnt = 0;
    }
    private Vector<LayoutMapping> mVector = new Vector<LayoutMapping>();
    
    private static final long VIBRATE_TIME = 25;    
    private Vibrator mVibrator;    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        // user start to check their number,
        // so we can get which lotto type and draw id he want
        mLottoType = intent.getIntExtra("lottoType", 0);
        mLottoDrawId = intent.getStringExtra("lottoDrawId");
        LottoLog.log("onCreate() mLottoType :" + mLottoType);
        LottoLog.log("onCreate() mLottoDrawId :" + mLottoDrawId);
        
        setContentView(TWLottoLayout.mLayout[mLottoType]);
        mVector = new Vector<LayoutMapping>();
        
        // prompt that how many ball user selected in section 1
        mSec1SelPrompt = (TextView) findViewById(R.id.sec1_ball_selected);
        
        // prompt that how many ball user selected in section 2 (if needed)
        mSec2SelPrompt = (TextView) findViewById(R.id.sec2_ball_selected);
        
        mButtonOk = (Button) findViewById(R.id.btnOk);
        mButtonOk.setOnClickListener(this);
        mButtonBack = (Button) findViewById(R.id.btnBack);
        mButtonBack.setOnClickListener(this);
        
        switch (mLottoType) {
            case WinningInfoV2.LOTTO_WEILI:
                layoutSetupX(TWLottoLayout.mWeiliA1NumId, mSec1SelPrompt, 6);
                layoutSetupX(TWLottoLayout.mWeiliA2NumId, mSec2SelPrompt, 1);
                break;
            case WinningInfoV2.LOTTO_BIG:
                layoutSetupX(TWLottoLayout.mBigLottoNumId, mSec1SelPrompt, 6);
                break;
            case WinningInfoV2.LOTTO_539:
                layoutSetupX(TWLottoLayout.mLotto539NumId, mSec1SelPrompt, 5);
                break;
            case WinningInfoV2.LOTTO_TICKTACKTOE:
                layoutSetupX(TWLottoLayout.mLottoTTTNum1Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum2Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum3Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum4Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum5Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum6Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum7Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum8Id, mSec1SelPrompt, 1);
                layoutSetupX(TWLottoLayout.mLottoTTTNum9Id, mSec1SelPrompt, 1);
                break;
                
            // some lotto type has many playing method
            // so add a spinner let user can choose
            case WinningInfoV2.LOTTO_STAR3:
                s = (Spinner) findViewById(R.id.spinnerLotto3star);
                adapter = ArrayAdapter.createFromResource(this, R.array.lotto3StarTypes,
                        R.layout.spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
                s.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    // @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        mLottoPlayTypePosition = position;
                        mVector.clear();
                        layoutSetupX(TWLottoLayout.mLotto3StarNum1Id, mSec1SelPrompt, 1);
                        layoutSetupX(TWLottoLayout.mLotto3StarNum2Id, mSec1SelPrompt, 1);
                        layoutSetupX(TWLottoLayout.mLotto3StarNum3Id, mSec1SelPrompt, 1);
                        LottoLog.log("3star mVector.size():" + mVector.size());
                        
                        // set ball text color to white
                        // maybe it's USELESS
                        // the text color is already defined in the xml file
                        unSelectedStatusLayout();
                    }
    
                    // @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
    
                    }
                });
                break;
            case WinningInfoV2.LOTTO_STAR4:
                s = (Spinner) findViewById(R.id.spinnerLotto4star);
                adapter = ArrayAdapter.createFromResource(this, R.array.lotto4StarTypes,
                        R.layout.spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
                s.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    // @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        mLottoPlayTypePosition = position;
                        mVector.clear();
                        
                        // the last parameter maxSel is 1
                        // let user can only select one lotto ball at each column
                        layoutSetupX(TWLottoLayout.mLotto4StarNum1Id, mSec1SelPrompt, 1);
                        layoutSetupX(TWLottoLayout.mLotto4StarNum2Id, mSec1SelPrompt, 1);
                        layoutSetupX(TWLottoLayout.mLotto4StarNum3Id, mSec1SelPrompt, 1);
                        layoutSetupX(TWLottoLayout.mLotto4StarNum4Id, mSec1SelPrompt, 1);
                        LottoLog.log("4star mVector.size():" + mVector.size());
                        
                        
                        unSelectedStatusLayout();
                    }
    
                    // @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
    
                    }
                });
                break;
            case WinningInfoV2.LOTTO_38:
                s = (Spinner) findViewById(R.id.spinnerLotto38);
                adapter = ArrayAdapter.createFromResource(this, R.array.lotto38Types,
                        R.layout.spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
                s.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    // @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        mLottoPlayTypePosition = position;
                        mVector.clear();
                        switch (mLottoPlayTypePosition) {
                        case PLAYTYPE_2_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto38NumId, mSec1SelPrompt, 2);
                            break;
                        case PLAYTYPE_3_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto38NumId, mSec1SelPrompt, 3);
                            break;
                        case PLAYTYPE_4_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto38NumId, mSec1SelPrompt, 4);
                            break;
                        case PLAYTYPE_5_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto38NumId, mSec1SelPrompt, 5);
                            break;
                        }
                        LottoLog.log("38lotto mVector.size():" + mVector.size());
                        unSelectedStatusLayout();
                    }
    
                    // @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
    
                    }
                });
                break;
            case WinningInfoV2.LOTTO_49:
                s = (Spinner) findViewById(R.id.spinnerLotto49);
                adapter = ArrayAdapter.createFromResource(this, R.array.lotto49Types,
                        R.layout.spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
                s.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    // @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        mLottoPlayTypePosition = position;
                        mVector.clear();
                        switch (mLottoPlayTypePosition) {
                        case PLAYTYPE_2_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto49NumId, mSec1SelPrompt, 2);
                            break;
                        case PLAYTYPE_3_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto49NumId, mSec1SelPrompt, 3);
                            break;
                        case PLAYTYPE_4_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto49NumId, mSec1SelPrompt, 4);
                            break;
                        }
                        LottoLog.log("49lotto mVector.size():" + mVector.size());
                        unSelectedStatusLayout();
                    }
    
                    // @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
    
                    }
                });
                break;
            case WinningInfoV2.LOTTO_39:
                s = (Spinner) findViewById(R.id.spinnerLotto39);
                adapter = ArrayAdapter.createFromResource(this, R.array.lotto39Types,
                        R.layout.spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
                s.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    // @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                        mLottoPlayTypePosition = position;
                        mVector.clear();
                        switch (mLottoPlayTypePosition) {
                        case PLAYTYPE_2_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto39NumId, mSec1SelPrompt, 2);
                            break;
                        case PLAYTYPE_3_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto39NumId, mSec1SelPrompt, 3);
                            break;
                        case PLAYTYPE_4_MATCH:
                            layoutSetupX(TWLottoLayout.mLotto39NumId, mSec1SelPrompt, 4);
                            break;
                        }
                        LottoLog.log("39lotto mVector.size():" + mVector.size());
                        unSelectedStatusLayout();
                    }
    
                    // @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
    
                    }
                });
                break;
        }
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        updatePrompt();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LottoLog.log("onActivityResult() resultCode: " + resultCode);
        if (resultCode == RESULT_OK) {
            finish();
        } else if (resultCode == RESULT_BACK) {
            unSelectedStatusLayout();
        }
    }
        
    private int getMaxNoSelectable() {
        int maxNoSelectedTotal = 0;
        for (int i = 0; i < mVector.size(); i++) {
            maxNoSelectedTotal += mVector.get(i).maxSelectable;
        }
        LottoLog.log("getMaxNoSelectable() maxNoSelectedTotal :" + maxNoSelectedTotal);
        return maxNoSelectedTotal;
    }
    
    // pick up the lotto balls user select
    // and send some info to another activity to check prize
    private void handleTypedInputLotto(int mLottoType, int mLottoPlayTypePosition) {
        LottoLog.log("handleTypedInputLotto() mLottoType :" + mLottoType);
        LottoLog.log("handleTypedInputLotto() mLottoPlayTypePosition :" + mLottoPlayTypePosition);
        
        // the maximum ball number user can select
        int maxNumSelectable = getMaxNoSelectable();
        mInputLotto = new String[maxNumSelectable];
        
        for (int i = 0; i < maxNumSelectable; i++) {
            mInputLotto[i] = "";
        }
        
        int idx = 0;
        for (int i = 0; i < mVector.size(); i++) {
            for (int j = 0; j < mVector.get(i).tv.length; j++) {
                mVector.get(i).tv[j] = (TextView) findViewById(mVector.get(i).layout[j]);
                if (mVector.get(i).tv[j].getCurrentTextColor() == SELECTED_COLOR
                        || mVector.get(i).tv[j].getCurrentTextColor() == SEC2_SELECTED_COLOR) {
                    mInputLotto[idx] = mVector.get(i).tv[j].getText().toString();
                    idx++;
                }
            }
        }
        LottoLog.log("handleTypedInputLotto idx:" + idx);
        if (idx < maxNumSelectable) {
            Toast.makeText(TypedInputLottoActivity.this, getString(R.string.select_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intentLotto = new Intent();
        intentLotto.putExtra("lottoType", mLottoType);
        intentLotto.putExtra("lottoDrawId", mLottoDrawId);
        intentLotto.putExtra("inputLotto", mInputLotto);
        intentLotto.putExtra("lottoPlayTypePosition", mLottoPlayTypePosition);
        intentLotto.setClass(TypedInputLottoActivity.this, TypedInputLottoResultActivity.class);
        startActivityForResult(intentLotto, RESULT_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        
                
        switch (v.getId()) {
        case R.id.btnOk:
            if (TWLottoSetting.ifVibrate())
                mVibrator.vibrate(VIBRATE_TIME);
            handleTypedInputLotto(mLottoType, mLottoPlayTypePosition);
            return;
        case R.id.btnBack:
            if (TWLottoSetting.ifVibrate())
                mVibrator.vibrate(VIBRATE_TIME);
            finish();
            return;
        }

        // not click any button but a lotto ball
        // set the ball's text color 
        // and update the prompt that how many balls user select
        for (int i = 0; i < mVector.size(); i++) {
            numberToggle(v.getId(), mVector.get(i));
            updateSelectionPrompt(mVector.get(i));
        }
    }

    // find which lotto ball is selected or de-selected
    // change its text color and update the how many balls user select
    private void numberToggle(int id, LayoutMapping layout) {
        for (int i = 0; i < layout.layout.length; i++) {
            if (layout.layout == TWLottoLayout.mWeiliA2NumId) {
                if (layout.layout[i] == id) {                    
                    if (layout.tv[i].getCurrentTextColor() == UNSELECTED_COLOR
                            && layout.selectedCnt < layout.maxSelectable) {
                        
                        if (TWLottoSetting.ifVibrate() &&
                                layout.selectedCnt < layout.maxSelectable)
                            mVibrator.vibrate(VIBRATE_TIME);
                        
                        layout.tv[i].setTextColor(SEC2_SELECTED_COLOR);
                        layout.selectedCnt++;
                    } else if (layout.tv[i].getCurrentTextColor() == SEC2_SELECTED_COLOR
                            && layout.selectedCnt <= layout.maxSelectable) {
                        layout.tv[i].setTextColor(UNSELECTED_COLOR);
                        layout.selectedCnt--;
                        
                        if (TWLottoSetting.ifVibrate())
                            mVibrator.vibrate(VIBRATE_TIME);
                    }
                    
                    break;
                }                

            } else {
                if (layout.layout[i] == id) {                    
                    if (layout.tv[i].getCurrentTextColor() == UNSELECTED_COLOR
                            && layout.selectedCnt < layout.maxSelectable) {
                        
                        if (TWLottoSetting.ifVibrate() &&
                                layout.selectedCnt < layout.maxSelectable)
                            mVibrator.vibrate(VIBRATE_TIME);
                        
                        layout.tv[i].setTextColor(SELECTED_COLOR);
                        layout.selectedCnt++;
                    } else if (layout.tv[i].getCurrentTextColor() == SELECTED_COLOR
                            && layout.selectedCnt <= layout.maxSelectable) {
                        layout.tv[i].setTextColor(UNSELECTED_COLOR);
                        layout.selectedCnt--;
                        
                        if (TWLottoSetting.ifVibrate())
                            mVibrator.vibrate(VIBRATE_TIME);
                    }
                    
                    break;
                }                
            }
        }
        LottoLog.log("numberToggle layout.maxSelectable:" + layout.maxSelectable);
        LottoLog.log("numberToggle layout.selectedCnt:" + layout.selectedCnt);
    }
    
    // layout contains all balls' id
    private void layoutSetupX(int[] layout, TextView prompt, int maxSelection) {
        LayoutMapping layoutMap = new LayoutMapping();
        layoutMap.layout = layout;
        
        // all balls represent by the TextView
        layoutMap.tv = new TextView[layout.length];
        layoutMap.textPrompt = prompt;
        layoutMap.maxSelectable = maxSelection;
        
        // we want all lotto balls can be selected
        // so set all of them a click listener
        for (int i = 0; i < layoutMap.tv.length; i++) {
            layoutMap.tv[i] = (TextView) findViewById(layoutMap.layout[i]);
            layoutMap.tv[i].setOnClickListener(this);
        }
        mVector.add(layoutMap);
    }

    // set ball text color to white
    private void unSelectedStatusLayout() {
        for (int i = 0; i < mVector.size(); i++) {
            mVector.get(i).selectedCnt = 0;
            for (int j = 0; j < mVector.get(i).tv.length; j++) {
                mVector.get(i).tv[j] = (TextView) findViewById(mVector.get(i).layout[j]);
                mVector.get(i).tv[j].setTextColor(UNSELECTED_COLOR);
            }
        }        
              
        updatePrompt();
    }       
    
    private void updatePrompt() {
        for (int i = 0; i < mVector.size(); i++)
            updateSelectionPrompt(mVector.get(i));
    }
    
    // only for lotto tick-tak-toe, star-3 and star-4
    private void updatePromptSpecial() {
        int cnt = mVector.size();
        int max = 0;
        int selectCnt = 0;
        for (int i = 0; i < cnt; i++) {
            max += mVector.get(i).maxSelectable;
            selectCnt += mVector.get(i).selectedCnt;
        }
        mVector.get(0).textPrompt.setText(selectCnt + "/" + max);
        updateOkButtonState();
    }
    
    // Display number of ball selected
    private void updateSelectionPrompt(LayoutMapping layout) {
        if (mLottoType == WinningInfoV2.LOTTO_TICKTACKTOE || 
                mLottoType == WinningInfoV2.LOTTO_STAR3 || 
                mLottoType == WinningInfoV2.LOTTO_STAR4) {
            updatePromptSpecial();
            return;
        }
        layout.textPrompt.setText(layout.selectedCnt + "/" + layout.maxSelectable);
        
        // check if user select enough lotto balls
        updateOkButtonState();
    }    
    
    // check if user select enough lotto balls
    private void updateOkButtonState() {
        int cnt = mVector.size();
        int max = 0;
        int selectCnt = 0;
        for (int i = 0; i < cnt; i++) {
            max += mVector.get(i).maxSelectable;
            selectCnt += mVector.get(i).selectedCnt;
        }
        if (selectCnt == max)
            mButtonOk.setEnabled(true);
        else
            mButtonOk.setEnabled(false);
    }
}
