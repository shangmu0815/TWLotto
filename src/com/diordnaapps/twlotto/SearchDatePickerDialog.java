package com.diordnaapps.twlotto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.diordnaapps.twlotto.SearchDatePicker.OnDateChangedListener;

public class SearchDatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

    private static final String PERIOD = "period";
    private static final String PERIOD_TYPE = "period_type";
    private final String[] yearTitle;
    private final String[] monthTitle;
    private final String chineseCalendar;
    private final String chineseCalendarYear;
    private final String chineseCalendarMonth;
    private final SearchDatePicker.OnDateChangedListener mCallBack;
    private final SearchDatePicker mSearchDatePicker;
    private int mYear;
    private int mMonth;

    public interface OnPeriodSetListener {
        void onPeriodSet(SearchDatePicker view, int period, int type);
    }

    public SearchDatePickerDialog(Context context,
            SearchDatePicker.OnDateChangedListener callback, int year, int month) {
        super(context);

        mCallBack = callback;
        mYear = year;
        mMonth = month;
        LottoLog.log("mCallBack" + mCallBack);
        yearTitle = context.getResources().getStringArray(R.array.date_picker_year);        
        monthTitle = context.getResources().getStringArray(R.array.date_picker_month);
        chineseCalendar = context.getString(R.string.string_chinese_calendar);
        chineseCalendarYear = context.getString(R.string.string_chinese_year);
        chineseCalendarMonth = context.getString(R.string.string_chinese_month);
        
        setTitle(chineseCalendar + yearTitle[year] + chineseCalendarYear +  
                monthTitle[month] + chineseCalendarMonth);
        setButton(BUTTON_POSITIVE, context.getResources().getString(R.string.invoice_winning_alert_dialog_ok),(OnClickListener) this);
        setButton(BUTTON_NEGATIVE, context.getResources().getString(R.string.invoice_winning_alert_dialog_cancel), (OnClickListener) this);
        setIcon(R.drawable.ic_dialog_time);

        
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.date_picker_dialog, null);
        setView(view);
        mSearchDatePicker = (SearchDatePicker) view.findViewById(R.id.periodPicker);
        mSearchDatePicker.init(mYear, mMonth, mCallBack);
    }

    @Override
    public void show() {
        super.show();
    }

    public void onClick(DialogInterface dialog, int which) {
        LottoLog.log("invoice_winning_alert_dialog_ok");
        if (which == BUTTON_POSITIVE) {
            //TODO: Set button title
            LottoLog.log("POSITIVE");

        }
        if (which == BUTTON_NEGATIVE && mCallBack != null) {
            LottoLog.log("NEGATIVE");
            mCallBack.onPeriodCancel(mSearchDatePicker, mSearchDatePicker.getYear(),
                    mSearchDatePicker.getMonth());
        }
        if (which == BUTTON_POSITIVE && mCallBack != null) {
            mSearchDatePicker.clearFocus();
            mCallBack.onPeriodSet(mSearchDatePicker, mSearchDatePicker.getYear(),
                    mSearchDatePicker.getMonth());
        }
    }

    public void updatePeriod(int period, int type) {
        mYear = period;
        mMonth = type;
        mSearchDatePicker.updatePeriod(mYear, mMonth);
    }

    public void updateTitle(int yearPos, int monthPos) {
        
        /*int year = mSearchDatePicker.getYear();
        int month =  mSearchDatePicker.getMonth();
        setTitle(chineseCalendar + yearTitle[year] + chineseCalendarYear +  
                monthTitle[month] + chineseCalendarMonth);
                */
    	setTitle(chineseCalendar + yearTitle[yearPos] + chineseCalendarYear +  
    			monthTitle[monthPos] + chineseCalendarMonth);
    }

    public void onPeriodChanged(SearchDatePicker view, int period, int type) {
        LottoLog.log("SearchDatePickerDialog onPeriodChanged");
        
        updateTitle(period, type);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(PERIOD, mSearchDatePicker.getYear());
        state.putInt(PERIOD_TYPE, mSearchDatePicker.getMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int mPeriod = savedInstanceState.getInt(PERIOD);
        int mType = savedInstanceState.getInt(PERIOD_TYPE);
        mSearchDatePicker.init(mPeriod, mType, this);
        updateTitle(mPeriod, mType);
    }

    @Override
    public void onPeriodSet(SearchDatePicker view, int period, int type) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPeriodCancel(SearchDatePicker view, int period, int type)
    {
        // TODO Auto-generated method stub
        
    }


}
