package com.diordnaapps.twlotto;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.FrameLayout;

public class SearchDatePicker extends FrameLayout {

    private final NumberPicker mYearPicker;
    private final NumberPicker mMonthPicker;
    private static final int DEFAULT_SPEED = 200;

    private OnDateChangedListener mOnPeriodChangedListener;
    private int mYear;
    private int mMonth;

    public interface OnDateChangedListener {
        void onPeriodChanged(SearchDatePicker view, int period, int type);
        void onPeriodSet(SearchDatePicker view, int period, int type);
        void onPeriodCancel(SearchDatePicker view, int period, int type);
    }

    public SearchDatePicker(Context context) {
        this(context, null);
    }

    public SearchDatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchDatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.date_picker, this, true);

        String[] yearArray = getResources().getStringArray(R.array.date_picker_year);
        mYearPicker = (NumberPicker) findViewById(R.id.period_year);
        mYearPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        mYearPicker.setSpeed(DEFAULT_SPEED);
        mYearPicker.setRange(0, yearArray.length - 1, getResources().getStringArray(
                R.array.date_picker_year));
        mYearPicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                mYear = newVal;

                if (mOnPeriodChangedListener != null) {
                    mOnPeriodChangedListener.onPeriodChanged(SearchDatePicker.this,
                            mYear, mMonth);
                    
                }
            }
        });

        String[] monthArray = getResources().getStringArray(R.array.date_picker_month);
        mMonthPicker = (NumberPicker) findViewById(R.id.period_month);
        mMonthPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
        mMonthPicker.setSpeed(DEFAULT_SPEED);
        mMonthPicker.setRange(0, monthArray.length - 1, getResources().getStringArray(
                R.array.date_picker_month));
        mMonthPicker.setOnChangeListener(new NumberPicker.OnChangedListener() {
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                mMonth = newVal;

                if (mOnPeriodChangedListener != null) {
                    mOnPeriodChangedListener.onPeriodChanged(SearchDatePicker.this,
                            mYear, mMonth);
                    
                }
            }
        });

        if (!isEnabled()) {
            setEnabled(false);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mYearPicker.setEnabled(enabled);
        mMonthPicker.setEnabled(enabled);
    }

    public void updatePeriod(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public void init(int year, int month,
            OnDateChangedListener OnDateChangedListener) {
        mYear = year;
        mMonth = month;
        mOnPeriodChangedListener = OnDateChangedListener;
        updateSpinners();
    }

    private void updateSpinners() {
        mYearPicker.setCurrent(mYear);
        mMonthPicker.setCurrent(mMonth);
    }

    @Override
    protected void dispatchRestoreInstanceState(
            SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        return new SavedState(superState, mYear, mMonth);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mYear = ss.getPeriod();
        mMonth = ss.getPeriodType();
    }

    private static class SavedState extends BaseSavedState {

        private final int mYear;
        private final int mMonth;

        /**
         * Constructor called from {@link DatePicker#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, int period, int type) {
            super(superState);
            mYear = period;
            mMonth = type;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mYear = in.readInt();
            mMonth = in.readInt();
        }

        public int getPeriod() {
            return mYear;
        }

        public int getPeriodType() {
            return mMonth;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mYear);
            dest.writeInt(mMonth);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
