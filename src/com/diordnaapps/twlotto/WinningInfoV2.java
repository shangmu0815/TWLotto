package com.diordnaapps.twlotto;

public class WinningInfoV2 {
    
    public static final int PARSE_LOTTO = 0;
    public static final int PARSE_INVOICE = 1;
    
    //For Lottery
    public String drawID;
    public String drawDate;
    public String cashDate;
    public String sec1WinningNo[] = {null, null, null, null, null, null, null, null, null};
    public String sec2WinningNo[] = {null};
    public String prize[] = {null, null, null, null, null, null, null, null, null, null};
    
    //For Invoice
    public String drawID2; // new added
    public String grandPrizeNo[] = {null, null, null};
    public String grandPrizeNo2[] = {null, null, null}; // new added
    public String grandPrize;
    public String topPrizeNo[] = {null, null, null};
    public String topPrizeNo2[] = {null, null, null}; // new added
    public String topPrize;
    public String firstPrizeNo[] = {null, null, null};
    public String firstPrizeNo2[] = {null, null, null}; // new added
    public String firstPrize;
    public String sixthPrizeNo[] = {null, null, null, null, null, null, null, null, null, null};
    
    // new added
    public String sixthPrizeNo2[] = {null, null, null, null, null, null, null, null, null, null};
    public String sixthPrize;

    //For Lottery type index
    public static final int LOTTO_WEILI = 0;
    public static final int LOTTO_BIG = 1;
    public static final int LOTTO_539 = 2;
    public static final int LOTTO_TICKTACKTOE = 3;
    public static final int LOTTO_STAR3 = 4;
    public static final int LOTTO_STAR4 = 5;
    public static final int LOTTO_38 = 6;
    public static final int LOTTO_49 = 7;
    public static final int LOTTO_39 = 8;

    public static final int MAX_LOTTO_TYPE = LOTTO_39 + 1;

    public static final int LOTTO_DRAW_HOUR = 20; //8pm
}
