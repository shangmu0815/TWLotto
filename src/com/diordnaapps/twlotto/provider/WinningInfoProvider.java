package com.diordnaapps.twlotto.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.diordnaapps.twlotto.LottoLog;
import com.diordnaapps.twlotto.WinningInfoV2;

public class WinningInfoProvider extends ContentProvider {
    
    private static final String TAG = "WinningInfoProvider";
    
    private SQLiteOpenHelper mOpenHelper;

    public static final String PROVIDER_NAME = 
        "com.diordnaapps.twlotto";
    
    public static final Uri CONTENT_URI = 
        Uri.parse("content://"+ PROVIDER_NAME);

    //TW Lottery
    public static final int MAX_LOTTO_TYPE = 9;
    public static final int MAX_LOTTO_WINNO = 9;
    public static final int LOTTO_WINNO_START_POS = 3;
    public static final int MAX_LOTTO_PRIZE = 10;
    public static final int LOTTO_PRIZE_START_POS = LOTTO_WINNO_START_POS + MAX_LOTTO_WINNO + 1;
    
    public static final String _ID = "_id";
    public static final String DRAW_DATE = "draw_date";
    public static final String CASH_DATE = "cash_date";
    public static final String WINNO1 = "win_no1";
    public static final String WINNO2 = "win_no2";
    public static final String WINNO3 = "win_no3";
    public static final String WINNO4 = "win_no4";
    public static final String WINNO5 = "win_no5";
    public static final String WINNO6 = "win_no6";
    public static final String WINNO7 = "win_no7";
    public static final String WINNO8 = "win_no8";
    public static final String WINNO9 = "win_no9";
    public static final String WINSEC2 = "win_sec2";
    public static final String PRIZE1 = "prize1";
    public static final String PRIZE2 = "prize2";
    public static final String PRIZE3 = "prize3";
    public static final String PRIZE4 = "prize4";
    public static final String PRIZE5 = "prize5";
    public static final String PRIZE6 = "prize6";
    public static final String PRIZE7 = "prize7";
    public static final String PRIZE8 = "prize8";
    public static final String PRIZE9 = "prize9";
    public static final String PRIZE10 = "prize10";
    
    public static final String[] LOTTO_QUERY_COLUMNS = {
        _ID, DRAW_DATE, CASH_DATE, WINNO1, WINNO2, WINNO3, WINNO4,
        WINNO5, WINNO6, WINNO7, WINNO8, WINNO9, WINSEC2, 
        PRIZE1, PRIZE2, PRIZE3, PRIZE4, PRIZE5, PRIZE6, 
        PRIZE7, PRIZE8, PRIZE9, PRIZE10};

    //TW Invoice
    public static final int MAX_INVOICE_PRIZE = 4;
    public static final int MAX_INVOICE_WINNO = 3;
    public static final int MAX_INVOICE_SIXTH_WINNO = 10;
    public static final int INVOICE_GPNO_START_POS = 1;
    public static final int INVOICE_TPNO_START_POS = 4;
    public static final int INVOICE_FPNO_START_POS = 7;
    public static final int INVOICE_SPNO_START_POS = 10;
    
    public static final String GPNO1 = "grandPrizeNo1";
    public static final String GPNO2 = "grandPrizeNo2";
    public static final String GPNO3 = "grandPrizeNo3";
    public static final String TPNO1 = "topPrizeNo1";
    public static final String TPNO2 = "topPrizeNo2";
    public static final String TPNO3 = "topPrizeNo3";
    public static final String FPNO1 = "firstPrizeNo1";
    public static final String FPNO2 = "firstPrizeNo2";
    public static final String FPNO3 = "firstPrizeNo3";
    public static final String SPNO1 = "sixthPrizeNo1";
    public static final String SPNO2 = "sixthPrizeNo2";
    public static final String SPNO3 = "sixthPrizeNo3";
    public static final String SPNO4 = "sixthPrizeNo4";
    public static final String SPNO5 = "sixthPrizeNo5";
    public static final String SPNO6 = "sixthPrizeNo6";
    public static final String SPNO7 = "sixthPrizeNo7";
    public static final String SPNO8 = "sixthPrizeNo8";
    public static final String SPNO9 = "sixthPrizeNo9";
    public static final String SPNO10 = "sixthPrizeNo10";
    public static final String GRANDPRIZE = "grandPrize";
    public static final String TOPPRIZE = "topPrize";
    public static final String FIRSTPRIZE = "firstPrize";
    public static final String SIXTHPRIZE = "sixthPrize";
    
    public static final String[] INVOICE_QUERY_COLUMNS = {
        _ID, GPNO1, GPNO2, GPNO3, TPNO1, TPNO2, TPNO3, 
        FPNO1, FPNO2, FPNO3, SPNO1, SPNO2, SPNO3, SPNO4, SPNO5,
        SPNO6, SPNO7, SPNO8, SPNO9, SPNO10, GRANDPRIZE, TOPPRIZE,
        FIRSTPRIZE, SIXTHPRIZE};
    
    public class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "winninginfo.db";
        private static final int DATABASE_VERSION = 1;
        
        public DatabaseHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (int i = 0; i < MAX_LOTTO_TYPE; i++) {
                db.execSQL("CREATE TABLE " + getLottoTable(i) + " (" +
                           "_id STRING PRIMARY KEY," +
                           DRAW_DATE + " DATE, " +
                           CASH_DATE + " DATE, " +
                           WINNO1 + " INTEGER, " + WINNO2 + " INTEGER, " +
                           WINNO3 + " INTEGER, " + WINNO4 + " INTEGER, " +
                           WINNO5 + " INTEGER, " + WINNO6 + " INTEGER, " +
                           WINNO7 + " INTEGER, " + WINNO8 + " INTEGER, " +
                           WINNO9 + " INTEGER, " + WINSEC2 + " INTEGER, " +
                           PRIZE1 + " INTEGER, " + PRIZE2 + " INTEGER, " +
                           PRIZE3 + " INTEGER, " + PRIZE4 + " INTEGER, " +
                           PRIZE5 + " INTEGER, " + PRIZE6 + " INTEGER, " +
                           PRIZE7 + " INTEGER, " + PRIZE8 + " INTEGER, " +
                           PRIZE9 + " INTEGER, " + PRIZE10 + " INTEGER);");
            }
            db.execSQL("CREATE TABLE Invoice (" +
                    "_id STRING PRIMARY KEY," +
                    GPNO1 + " INTEGER, " + GPNO2 + " INTEGER, " + GPNO3 + " INTEGER, " +
                    TPNO1 + " INTEGER, " + TPNO2 + " INTEGER, " + TPNO3 + " INTEGER, " +
                    FPNO1 + " INTEGER, " + FPNO2 + " INTEGER, " + FPNO3 + " INTEGER, " +
                    SPNO1 + " INTEGER, " + SPNO2 + " INTEGER, " + SPNO3 + " INTEGER, " +
                    SPNO4 + " INTEGER, " + SPNO5 + " INTEGER, " + SPNO6 + " INTEGER, " +
                    SPNO7 + " INTEGER, " + SPNO8 + " INTEGER, " + SPNO9 + " INTEGER, " +
                    SPNO10 + " INTEGER, " + GRANDPRIZE + " INTEGER, " + TOPPRIZE + " INTEGER, " + 
                    FIRSTPRIZE + " INTEGER, " + SIXTHPRIZE + " INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (int i = 0; i < MAX_LOTTO_TYPE; i++) {
                db.execSQL("DROP TABLE IF EXISTS " + getLottoTable(i));
            }
            db.execSQL("DROP TABLE IF EXISTS Invoice");
            onCreate(db);
        }
    }
     
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        count = db.delete(
                getTableNameByUrl(uri),
                selection, 
                selectionArgs);
        
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	LottoLog.log("getTableNameByUrl(uri):" + getTableNameByUrl(uri));
        
////////////////////////////////////////////////////////////////////////////////    	
//        ContentValues contentValues;
//        if (values != null)
//            contentValues = new ContentValues(values);
//        else
//            contentValues = new ContentValues();
        
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(getTableNameByUrl(uri), "", values);
        if (rowId < 0) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        Uri newUrl = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(newUrl, null);
        return newUrl;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mOpenHelper = new DatabaseHelper(context);
        SQLiteDatabase winninginfoDB = mOpenHelper.getWritableDatabase();
        return (winninginfoDB == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        qb.setTables(getTableNameByUrl(uri));

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor ret = qb.query(db, projection, selection, selectionArgs,
                              null, null, sortOrder);

        if (ret == null) {
        	LottoLog.log("WinningInfo.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        count = db.update(
                getTableNameByUrl(uri), 
                values,
                selection, 
                selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public static String getLottoTable(int lottoOrder) {
        switch (lottoOrder) {
            case WinningInfoV2.LOTTO_WEILI:
                return "SuperLotto638";
            case WinningInfoV2.LOTTO_BIG:
                return "Lotto649";
            case WinningInfoV2.LOTTO_539:
                return "Dailycash";
            case WinningInfoV2.LOTTO_TICKTACKTOE:
                return "TTT";
            case WinningInfoV2.LOTTO_STAR3:
                return "Lotto3D";
            case WinningInfoV2.LOTTO_STAR4:
                return "Lotto4D";
            case WinningInfoV2.LOTTO_38:
                return "Lotto38m6";
            case WinningInfoV2.LOTTO_49:
                return "Lotto49m6";
            case WinningInfoV2.LOTTO_39:
                return "Lotto39m5";
        }
        return null;
    }
    
    public static String getTableNameByUrl(Uri uri) {
        String contentUrl = uri.toString();
        int lastIdx = contentUrl.lastIndexOf(System.getProperty("file.separator", "/"));
        return contentUrl.substring(lastIdx + 1, contentUrl.length());
    }
}
