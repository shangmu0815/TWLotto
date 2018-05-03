package com.diordnaapps.twlotto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import android.content.Context;

public class TWLottoMatch {
    private static final String TAG = "TWLottoMatch";
    
    private static int mMaxSelectedNo = 0;

    private static Context mContext;

    public TWLottoMatch(Context context) {
        mContext = context;
    }

    // weili, return the winning prize and prize type
    public ArrayList getWeiLiLottoResult(String[] inputLotto, WinningInfoV2 winningInfo) {
        
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[7];
        
        if(winningInfo != null){
            
            // assign winning ball number
            for (int i = 0; i < 6; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            
            mWinningBallInfo[6] = winningInfo.sec2WinningNo[0];            
            mWinningPrizeInfo = new String[winningInfo.prize.length]; // default length 10
            
            // assign winning prize
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }
            
            int mArea1BingoNum = 0;
            boolean mArea2Bingo = false;
            
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[i])) {
                        mArea1BingoNum++;
                        break;
                    }
                }
            }
            
            for (int j = 6; j < mWinningBallInfo.length; j++) {
                if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[j])) {
                    mArea2Bingo = true;
                }
            }
            
            switch (mArea1BingoNum) {
                case 6:
                    if (mArea2Bingo) {
                        mPrizeWon = mContext.getString(R.string.first_prize);
                        mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                    } else {
                        mPrizeWon = mContext.getString(R.string.second_prize);
                        mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                    }
                    break;
                case 5:
                    if (mArea2Bingo) {
                        mPrizeWon = mContext.getString(R.string.third_prize);
                        mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                    } else {
                        mPrizeWon = mContext.getString(R.string.fourth_prize);
                        mPrizeBonus = mWinningPrizeInfo[3] + mContext.getString(R.string.dollar);
                    }
                    break;
                case 4:
                    if (mArea2Bingo) {
                        mPrizeWon = mContext.getString(R.string.fifth_prize);
                        mPrizeBonus = mWinningPrizeInfo[4] + mContext.getString(R.string.dollar);
                    } else {
                        mPrizeWon = mContext.getString(R.string.sixth_prize);
                        mPrizeBonus = mWinningPrizeInfo[5] + mContext.getString(R.string.dollar);
                    }
                    break;
                case 3:
                    if (mArea2Bingo) {
                        mPrizeWon = mContext.getString(R.string.seventh_prize);
                        mPrizeBonus = mWinningPrizeInfo[6] + mContext.getString(R.string.dollar);
                    } else {
                        mPrizeWon = mContext.getString(R.string.ninth_prize);
                        mPrizeBonus = mWinningPrizeInfo[8] + mContext.getString(R.string.dollar);
                    }
                    break;
                case 2:
                    if (mArea2Bingo) {
                        mPrizeWon = mContext.getString(R.string.eighth_prize);
                        mPrizeBonus = mWinningPrizeInfo[7] + mContext.getString(R.string.dollar);
                    } else {
                        mPrizeWon = mContext.getString(R.string.no_prize_won);
                        mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                    }
                    break;
                case 1:
                    if (mArea2Bingo) {
                        mPrizeWon = mContext.getString(R.string.general_prize);
                        mPrizeBonus = mWinningPrizeInfo[9] + mContext.getString(R.string.dollar);
                    } else {
                        mPrizeWon = mContext.getString(R.string.no_prize_won);
                        mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                    }
                    break;
                default:
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                    break;
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }

    // bigLotto
    public ArrayList getBigLottoResult(String[] inputLotto,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        int mBingoNum = 0;
        boolean mSpecial = false;
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;     
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[7];
        if(winningInfo!=null){
            for (int i = 0; i < 6; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            mWinningBallInfo[6] = winningInfo.sec2WinningNo[0];
            
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }
            
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[i])) {
                        mBingoNum++;
                        break;
                    }
                }
                for (int j = 6; j < mWinningBallInfo.length; j++) {
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[i])) {
                        mSpecial = true;
                    }
                }
            }
            switch (mBingoNum) {
            case 6:
                mPrizeWon = mContext.getString(R.string.first_prize);
                mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                break;
            case 5:
                if (mSpecial) {
                    mPrizeWon = mContext.getString(R.string.second_prize);
                    mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.third_prize);
                    mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                }
                break;
            case 4:
                if (mSpecial) {
                    mPrizeWon = mContext.getString(R.string.fourth_prize);
                    mPrizeBonus = mWinningPrizeInfo[3] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.fifth_prize);
                    mPrizeBonus = mWinningPrizeInfo[4] + mContext.getString(R.string.dollar);
                }
                break;
            case 3:
                if (mSpecial) {
                    mPrizeWon = mContext.getString(R.string.sixth_prize);
                    mPrizeBonus = mWinningPrizeInfo[5] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.general_prize);
                    mPrizeBonus = mWinningPrizeInfo[6] + mContext.getString(R.string.dollar);
                }
                break;
            case 2:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            case 1:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            case 0:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }

    // 539
    public ArrayList get539LottoResult(String[] inputLotto,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        int mBingoNum = 0;
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;       
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[5];
        if(winningInfo!=null){
            for (int i = 0; i < 5; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }

            for (int i = 0; i < inputLotto.length; i++) {
                for (int j = 0; j < mWinningBallInfo.length; j++) {
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[i])) {
                        mBingoNum++;
                        break;
                    }
                }
            }
            switch (mBingoNum) {
            case 5:
                mPrizeWon = mContext.getString(R.string.first_prize);
                mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                break;
            case 4:
                mPrizeWon = mContext.getString(R.string.second_prize);
                mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                break;
            case 3:
                mPrizeWon = mContext.getString(R.string.third_prize);
                mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                break;
            case 2:
                mPrizeWon = mContext.getString(R.string.fourth_prize);
                mPrizeBonus = mWinningPrizeInfo[3] + mContext.getString(R.string.dollar);
                break;
            default:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }

    // TTT
    public ArrayList getTickTackToeLottoResult(String[] inputLotto,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;       
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[9];
        if(winningInfo!=null){
            for (int i = 0; i < 9; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }

            int count = 0;
            // check horizontal match
            // {B[0],B[1],B[2]}{B[3],B[4],B[5]}{B[6],B[7],B[8]}
            for (int i = 0; i <= 6; i += 3) {
                if (Integer.parseInt(mWinningBallInfo[i]) == Integer.parseInt(inputLotto[i])
                        && Integer.parseInt(mWinningBallInfo[i + 1]) == Integer
                                .parseInt(inputLotto[i + 1])
                        && Integer.parseInt(mWinningBallInfo[i + 2]) == Integer
                                .parseInt(inputLotto[i + 2])) {
                    count++;
                }
            }
            // check vertical match
            // {B[0],B[3],B[6]}{B[1],B[4],B[7]}{B[2],B[5],B[8]}
            for (int i = 0; i < 3; i++) {
                if (Integer.parseInt(mWinningBallInfo[i]) == Integer.parseInt(inputLotto[i])
                        && Integer.parseInt(mWinningBallInfo[i + 3]) == Integer
                                .parseInt(inputLotto[i + 3])
                        && Integer.parseInt(mWinningBallInfo[i + 6]) == Integer
                                .parseInt(inputLotto[i + 6])) {
                    count++;
                }
            }
            // check oblique match {B[0],B[4],B[8]}{B[2],B[4],B[6]}
            if (Integer.parseInt(mWinningBallInfo[0]) == Integer.parseInt(inputLotto[0])
                    && Integer.parseInt(mWinningBallInfo[4]) == Integer.parseInt(inputLotto[4])
                    && Integer.parseInt(mWinningBallInfo[8]) == Integer.parseInt(inputLotto[8])) {
                count++;
            }
            if (Integer.parseInt(mWinningBallInfo[2]) == Integer.parseInt(inputLotto[2])
                    && Integer.parseInt(mWinningBallInfo[4]) == Integer.parseInt(inputLotto[4])
                    && Integer.parseInt(mWinningBallInfo[6]) == Integer.parseInt(inputLotto[6])) {
                count++;
            }
            switch (count) {
            case 8:
                mPrizeWon = mContext.getString(R.string.first_prize);
                mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                break;
            case 6:
                mPrizeWon = mContext.getString(R.string.second_prize);
                mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                break;
            case 5:
                mPrizeWon = mContext.getString(R.string.third_prize);
                mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                break;
            case 4:
                mPrizeWon = mContext.getString(R.string.fourth_prize);
                mPrizeBonus = mWinningPrizeInfo[3] + mContext.getString(R.string.dollar);
                break;
            case 3:
                mPrizeWon = mContext.getString(R.string.fifth_prize);
                mPrizeBonus = mWinningPrizeInfo[4] + mContext.getString(R.string.dollar);
                break;
            case 2:
                mPrizeWon = mContext.getString(R.string.sixth_prize);
                mPrizeBonus = mWinningPrizeInfo[5] + mContext.getString(R.string.dollar);
                break;
            case 1:
                mPrizeWon = mContext.getString(R.string.general_prize);
                mPrizeBonus = mWinningPrizeInfo[6] + mContext.getString(R.string.dollar);
                break;
            case 0:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }

    // 3star
    public ArrayList get3StarLottoResult(String[] inputLotto,
            int playTypePosition,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        int mBingoNum = 0;
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;        
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[3];
        if(winningInfo!=null){
            for (int i = 0; i < 3; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }

            ArrayList<Object> mTargetAL = new ArrayList<Object>();
            // PLAYTYPE_ORDER_MATCH
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_ORDER_MATCH) {
                for (int i = 0; i < 3; i++) {
                    if (!(mWinningBallInfo[i].equals(inputLotto[i]))) {
                        mPrizeWon = mContext.getString(R.string.no_prize_won);
                        mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                        break;
                    }
                    if (i == 2) {
                        mPrizeWon = mContext.getString(R.string.prize_won);
                        mPrizeBonus = mWinningPrizeInfo[0]+ mContext.getString(R.string.dollar);
                    }
                }
            }
            // PLAYTYPE_NO_ORDER_MATCH
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_NO_ORDER_MATCH) {
                if (mWinningBallInfo[0].equals(mWinningBallInfo[1])
                        && mWinningBallInfo[1].equals(mWinningBallInfo[2])) {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                } else {
                    mTargetAL.add(mWinningBallInfo[0]);
                    mTargetAL.add(mWinningBallInfo[1]);
                    mTargetAL.add(mWinningBallInfo[2]);
                    for (int i = 0; i < inputLotto.length; i++) {
                        int idx = mTargetAL.indexOf(inputLotto[i]);
                        if (idx == -1) {
                            mPrizeWon = mContext.getString(R.string.no_prize_won);
                            mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                            break;
                        }
                        if (idx != -1) {
                            mTargetAL.remove(idx);
                        }
                    }
                    if (mTargetAL.size() == 0) {
                        TreeSet<String> tS = new TreeSet<String>();
                        for (int i = 0; i < 3; i++) {
                            tS.add(mWinningBallInfo[i]);
                        }
                        if (tS.size() == 2) {
                            mPrizeWon = mContext.getString(R.string.prize_won);
                            mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                        }
                        if (tS.size() == 3) {
                            mPrizeWon = mContext.getString(R.string.prize_won);
                            mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                        }
                    }
                }
            }
            // PLAYTYPE_FIRST_OR_LAST_MATCH
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_FIRST_OR_LAST_MATCH) {
                for (int i = 0; i <= 1; i++) {
                    if (mWinningBallInfo[i].equals(inputLotto[i])) {
                        mBingoNum++;
                    }
                }
                if (mBingoNum != 2) {
                    mBingoNum = 0;
                    for (int i = 1; i <= 2; i++) {
                        if (mWinningBallInfo[i].equals(inputLotto[i])) {
                            mBingoNum++;
                        }
                    }
                }
                switch (mBingoNum) {
                case 2:
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[3] + mContext.getString(R.string.dollar);
                    break;
                default:
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                    break;
                }
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }

    // 4star
    public ArrayList get4StarLottoResult(String[] inputLotto,
            int playTypePosition,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        ArrayList<Object> mTargetAL = new ArrayList<Object>();
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;        
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[4];
        if(winningInfo!=null){
            for (int i = 0; i < 4; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }
            // PLAYTYPE_ORDER_MATCH
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_ORDER_MATCH) {
                for (int i = 0; i < mWinningBallInfo.length; i++) {
                    if (!(mWinningBallInfo[i].equals(inputLotto[i]))) {
                        mPrizeWon = mContext.getString(R.string.no_prize_won);
                        mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                        break;
                    }
                    if (i == 3)
                        mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                }
            }
            // PLAYTYPE_NO_ORDER_MATCH
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_NO_ORDER_MATCH) {
                if (mWinningBallInfo[0].equals(mWinningBallInfo[1])
                        && mWinningBallInfo[1].equals(mWinningBallInfo[2])
                        && mWinningBallInfo[2].equals(mWinningBallInfo[3])) {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                } else {
                    mTargetAL.add(mWinningBallInfo[0]);
                    mTargetAL.add(mWinningBallInfo[1]);
                    mTargetAL.add(mWinningBallInfo[2]);
                    mTargetAL.add(mWinningBallInfo[3]);
                    for (int i = 0; i < inputLotto.length; i++) {
                        int idx = mTargetAL.indexOf(inputLotto[i]);
                        if (idx == -1) {
                            mPrizeWon = mContext.getString(R.string.no_prize_won);
                            mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                            break;
                        }
                        if (idx != -1) {
                            mTargetAL.remove(idx);
                        }
                    }
                    if (mTargetAL.size() == 0) {
                        HashMap<String, String> hM = new HashMap<String, String>();
                        int keyNum = 1;
                        for (int i = 0; i < inputLotto.length; i++) {
                            if (hM.get(inputLotto[i]) == null) {
                                hM.put(inputLotto[i], String.valueOf(keyNum));
                            } else {
                                hM.put(inputLotto[i], String.valueOf(Integer.parseInt(hM
                                        .get(inputLotto[i])) + 1));
                            }
                        }
                        if (hM.size() == 4) {
                            mPrizeWon = mContext.getString(R.string.prize_won);
                            mPrizeBonus = mWinningPrizeInfo[4] + mContext.getString(R.string.dollar);
                        }
                        if (hM.size() == 3) {
                            mPrizeWon = mContext.getString(R.string.prize_won);
                            mPrizeBonus = mWinningPrizeInfo[3] + mContext.getString(R.string.dollar);
                        }
                        if (hM.size() == 2) {
                            if (hM.get(inputLotto[0]).equals("2")) {
                                mPrizeWon = mContext.getString(R.string.prize_won);
                                mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                            } else {
                                mPrizeWon = mContext.getString(R.string.prize_won);
                                mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                            }
                        }
                    }
                }
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }

    // 38Lotto
    public ArrayList get38LottoResult(String[] inputLotto,
            int playTypePosition,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        int mArea1BingoNum = 0;
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;       
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[6];
        if(winningInfo!=null){
            for (int i = 0; i < 6; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }

            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_2_MATCH) {
                mMaxSelectedNo = 2;
            }
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_3_MATCH) {
                mMaxSelectedNo = 3;
            }
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_4_MATCH) {
                mMaxSelectedNo = 4;
            }
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_5_MATCH) {
                mMaxSelectedNo = 5;
            }
            for (int i = 0; i < mMaxSelectedNo; i++) {
                for (int j = 0; j < mWinningBallInfo.length; j++) {
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[i])) {
                        mArea1BingoNum++;
                        break;
                    }
                }
            }
            switch (mArea1BingoNum) {
            case 5:
                if (mMaxSelectedNo == 5) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            case 4:
                if (mMaxSelectedNo == 4) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            case 3:
                if (mMaxSelectedNo == 3) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            case 2:
                if (mMaxSelectedNo == 2) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[3] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            default:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;

    }

    // 49Lotto
    public ArrayList get49LottoResult(String[] inputLotto,
            int playTypePosition,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        int mBingoNum = 0;
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;     
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[6];
        if(winningInfo!=null){
            for (int i = 0; i < 6; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];
            }

            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_2_MATCH) {
                mMaxSelectedNo = 2;
            }
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_3_MATCH) {
                mMaxSelectedNo = 3;
            }
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_4_MATCH) {
                mMaxSelectedNo = 4;
            }
            for (int i = 0; i < mMaxSelectedNo; i++) {
                for (int j = 0; j < mWinningBallInfo.length; j++) {
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[i])) {
                        mBingoNum++;
                        break;
                    }
                }
            }
            switch (mBingoNum) {
            case 4:
                if (mMaxSelectedNo == 4) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            case 3:
                if (mMaxSelectedNo == 3) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            case 2:
                if (mMaxSelectedNo == 2) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            default:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }

    // 39Lotto
    public ArrayList get39LottoResult(String[] inputLotto,
            int playTypePosition,WinningInfoV2 winningInfo) {
        ArrayList<Object> mResultMatchingAL = new ArrayList<Object>();
        int mBingoNum = 0;
        String mPrizeWon = "";
        String mPrizeBonus = "";
        String[] mWinningBallInfo;       
        String[] mWinningPrizeInfo;
        mWinningBallInfo = new String[5];
        if(winningInfo!=null){
            for (int i = 0; i < 5; i++) {
                mWinningBallInfo[i] = winningInfo.sec1WinningNo[i];
            }
            mWinningPrizeInfo = new String[winningInfo.prize.length];
            for (int i = 0; i < winningInfo.prize.length; i++) {
                mWinningPrizeInfo[i] = winningInfo.prize[i];                
            }

            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_2_MATCH) {
                mMaxSelectedNo = 2;
            }
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_3_MATCH) {
                mMaxSelectedNo = 3;
            }
            if (playTypePosition == TypedInputLottoActivity.PLAYTYPE_4_MATCH) {
                mMaxSelectedNo = 4;
            }
            for (int i = 0; i < mMaxSelectedNo; i++) {
                for (int j = 0; j < mWinningBallInfo.length; j++) {
                    if (Integer.parseInt(mWinningBallInfo[j]) == Integer.parseInt(inputLotto[i])) {
                        mBingoNum++;
                        break;
                    }
                }
            }
            switch (mBingoNum) {
            case 4:
                if (mMaxSelectedNo == 4) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[0] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            case 3:
                if (mMaxSelectedNo == 3) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[1] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            case 2:
                if (mMaxSelectedNo == 2) {
                    mPrizeWon = mContext.getString(R.string.prize_won);
                    mPrizeBonus = mWinningPrizeInfo[2] + mContext.getString(R.string.dollar);
                } else {
                    mPrizeWon = mContext.getString(R.string.no_prize_won);
                    mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                }
                break;
            default:
                mPrizeWon = mContext.getString(R.string.no_prize_won);
                mPrizeBonus = "0" + mContext.getString(R.string.dollar);
                break;
            }
            mResultMatchingAL.add(mPrizeWon);
            mResultMatchingAL.add(mPrizeBonus);
        }
        return mResultMatchingAL;
    }
}
