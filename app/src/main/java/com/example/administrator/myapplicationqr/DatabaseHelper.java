package com.example.administrator.myapplicationqr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "EasyQR.db";

    public static final String HISTORY_ASSET = "HISTORY_ASSET";
    public static final String HISTORY_IMAGE = "HISTORY_IMAGE";
    public static final String TEMP_HISTORY_ASSET = "TEMP_HISTORY_ASSET";
    public static final String TEMP_HISTORY_IMAGE = "TEMP_HISTORY_IMAGE";
    public static final String HISTORY_ASSET_RECENT = "HISTORY_ASSET_RECENT";
    public static final String STORE_HISTORY_ASSET = "STORE_HISTORY_ASSET";
    public static final String STORE_HISTORY_IMAGE = "STORE_HISTORY_IMAGE";
    public static final String ASSET = "ASSET";
    public static final String REFER = "REFER";
    public static final String STATUS = "STATUS";
    public static final String DEPARTMENT = "DEPARTMENT";
    public static final String LOCATION = "LOCATION";


    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + HISTORY_ASSET + "(" +
                "HISTORY_RID INTEGER PRIMARY KEY UNIQUE," +
                "HISTORY_ASSET_ID TEXT," +
                "HISTORY_ASSET_NAME TEXT," +
                "HISTORY_REFERID TEXT," +
                "HISTORY_REFERNAME TEXT," +
                "HISTORY_RECEIVEDATE TEXT," +
                "HISTORY_SPEC TEXT," +
                "HISTORY_UNITNAME TEXT," +
                "HISTORY_BUILDING_ID INTEGER," +
                "HISTORY_BUILDING_NAME TEXT," +
                "HISTORY_FLOOR_ID INTEGER," +
                "HISTORY_ROOM_ID TEXT," +
                "HISTORY_DAY INTEGER," +
                "HISTORY_MONTH INTEGER," +
                "HISTORY_YEAR INTEGER," +
                "HISTORY_HOUR INTEGER," +
                "HISTORY_MINUTE INTEGER," +
                "HISTORY_STATUS_NAME TEXT," +
                "HISTORY_USERNAME TEXT," +
                "HISTORY_NOTE TEXT" +
                ")"
        );

        db.execSQL("CREATE TABLE " + HISTORY_IMAGE + "(" +
                "HISTORY_IMAGE_RID INTEGER PRIMARY KEY UNIQUE," +
                "HISTORY_IMAGE_HISTORY_RID INTEGER," +
                "HISTORY_IMAGE_PATH TEXT" +
                ")"
        );

        db.execSQL("CREATE TABLE " + TEMP_HISTORY_ASSET + "(" +
                "HISTORY_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "HISTORY_ASSET_ID TEXT," +
                "HISTORY_ASSET_NAME TEXT," +
                "HISTORY_REFERID TEXT," +
                "HISTORY_REFERNAME TEXT," +
                "HISTORY_RECEIVEDATE TEXT," +
                "HISTORY_SPEC TEXT," +
                "HISTORY_UNITNAME TEXT," +
                "HISTORY_BUILDING_ID INTEGER," +
                "HISTORY_BUILDING_NAME TEXT," +
                "HISTORY_FLOOR_ID INTEGER," +
                "HISTORY_ROOM_ID TEXT," +
                "HISTORY_DAY INTEGER," +
                "HISTORY_MONTH INTEGER," +
                "HISTORY_YEAR INTEGER," +
                "HISTORY_HOUR INTEGER," +
                "HISTORY_MINUTE INTEGER," +
                "HISTORY_STATUS_NAME TEXT," +
                "HISTORY_USERNAME TEXT," +
                "HISTORY_NOTE TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + TEMP_HISTORY_IMAGE + "(" +
                "HISTORY_IMAGE_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "HISTORY_IMAGE_HISTORY_RID INTEGER," +
                "HISTORY_IMAGE BLOB" +
                ")"
        );


        db.execSQL("CREATE TABLE " + HISTORY_ASSET_RECENT + "(" +
                "HISTORY_ASSET_ID TEXT PRIMARY KEY UNIQUE," +
                "HISTORY_ASSET_NAME TEXT," +
                "HISTORY_REFERID TEXT," +
                "HISTORY_REFERNAME TEXT," +
                "HISTORY_RECEIVEDATE TEXT," +
                "HISTORY_SPEC TEXT," +
                "HISTORY_UNITNAME TEXT," +
                "HISTORY_BUILDING_ID INTEGER," +
                "HISTORY_BUILDING_NAME TEXT," +
                "HISTORY_FLOOR_ID INTEGER," +
                "HISTORY_ROOM_ID TEXT," +
                "HISTORY_DAY INTEGER," +
                "HISTORY_MONTH INTEGER," +
                "HISTORY_YEAR INTEGER," +
                "HISTORY_HOUR INTEGER," +
                "HISTORY_MINUTE INTEGER," +
                "HISTORY_STATUS_NAME TEXT," +
                "HISTORY_USERNAME TEXT," +
                "HISTORY_NOTE TEXT," +
                "HISTORY_RID INTEGER" +
                ")"
        );
        db.execSQL("CREATE TABLE " + STORE_HISTORY_ASSET + "(" +
                "HISTORY_RID INTEGER PRIMARY KEY UNIQUE," +
                "HISTORY_ASSET_ID TEXT," +
                "HISTORY_ASSET_NAME TEXT," +
                "HISTORY_REFERID TEXT," +
                "HISTORY_REFERNAME TEXT," +
                "HISTORY_RECEIVEDATE TEXT," +
                "HISTORY_SPEC TEXT," +
                "HISTORY_UNITNAME TEXT," +
                "HISTORY_BUILDING_ID INTEGER," +
                "HISTORY_BUILDING_NAME TEXT," +
                "HISTORY_FLOOR_ID INTEGER," +
                "HISTORY_ROOM_ID TEXT," +
                "HISTORY_DAY INTEGER," +
                "HISTORY_MONTH INTEGER," +
                "HISTORY_YEAR INTEGER," +
                "HISTORY_HOUR INTEGER," +
                "HISTORY_MINUTE INTEGER," +
                "HISTORY_STATUS_NAME TEXT," +
                "HISTORY_USERNAME TEXT," +
                "HISTORY_NOTE TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + STORE_HISTORY_IMAGE + "(" +
                "HISTORY_IMAGE_RID INTEGER PRIMARY KEY UNIQUE," +
                "HISTORY_IMAGE_HISTORY_RID INTEGER," +
                "HISTORY_IMAGE BLOB" +
                ")"
        );

        db.execSQL("CREATE TABLE " + ASSET + "(" +
                "ASSETID TEXT PRIMARY KEY UNIQUE," +
                "BARCODE TEXT," +
                "REFERIDITEM TEXT," +
                "ASSETNAME TEXT," +
                "RECEIVEDATE TEXT," +
                "SPEC TEXT," +
                "UNITNAME TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + REFER + "(" +
                "REFERID TEXT PRIMARY KEY UNIQUE," +
                "REFERNAME TEXT," +
                "DEPARTMENTID INTEGER," +
                "FOREIGN KEY(DEPARTMENTID) REFERENCES DEPARTMENT(DEPARTMENTID)" +
                ")"
        );
        db.execSQL("CREATE TABLE " + DEPARTMENT + "(" +
                "DEPARTMENTID INTEGER PRIMARY KEY UNIQUE," +
                "DEPARTMENTNAME TEXT UNIQUE" +
                ")"
        );

        db.execSQL("CREATE TABLE " + STATUS + "(" +
                "STATUS_ID INTEGER PRIMARY KEY UNIQUE," +
                "STATUS_NAME TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + LOCATION + "(" +
                "LOCATION_ID INTEGER PRIMARY KEY UNIQUE," +
                "LOCATION_BARCODE TEXT," +
                "LOCATION_BUILDING_ID INTEGER," +
                "LOCATION_BUILDING_NAME TEXT," +
                "LOCATION_FLOOR_ID INTEGER," +
                "LOCATION_ROOM_ID TEXT," +
                "DEPARTMENTID INTEGER," +
                "FOREIGN KEY(DEPARTMENTID) REFERENCES DEPARTMENT(DEPARTMENTID)" +
                ")"
        );



    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_ASSET);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TEMP_HISTORY_ASSET);
        db.execSQL("DROP TABLE IF EXISTS " + TEMP_HISTORY_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_ASSET_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + STORE_HISTORY_ASSET);
        db.execSQL("DROP TABLE IF EXISTS " + STORE_HISTORY_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + ASSET);
        db.execSQL("DROP TABLE IF EXISTS " + REFER);
        db.execSQL("DROP TABLE IF EXISTS " + DEPARTMENT);
        db.execSQL("DROP TABLE IF EXISTS " + STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + LOCATION);

        onCreate(db);
    }

    public Cursor getAllRowsFromTable(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return c;
    }
    public Cursor rawQuery(String QUERY,String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(QUERY,selectionArgs);
        c.moveToFirst();
        return c;
    }
    public Cursor rawQueryWithoutMoveToFirst(String QUERY,String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(QUERY,selectionArgs);
        return c;
    }

    public void onInsert_HISTORY_ASSET( int HISTORY_RID,String HISTORY_ASSET_ID,String HISTORY_ASSET_NAME,
                                        String HISTORY_REFERID,String HISTORY_REFERNAME,String HISTORY_RECEIVEDATE,
                                        String HISTORY_SPEC,String HISTORY_UNITNAME,
                                        int HISTORY_BUILDING_ID,String HISTORY_BUILDING_NAME,
                                        int HISTORY_FLOOR_ID,String HISTORY_ROOM_ID,
                                        int HISTORY_DAY,int HISTORY_MONTH, int HISTORY_YEAR,
                                        int HISTORY_HOUR,int HISTORY_MINUTE,String HISTORY_STATUS_NAME,
                                        String HISTORY_USERNAME, String HISTORY_NOTE){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(HISTORY_RID != -1){
            contentValues.put("HISTORY_RID",HISTORY_RID);
        }
        contentValues.put("HISTORY_ASSET_ID",HISTORY_ASSET_ID);
        if(HISTORY_ASSET_NAME != null){
            contentValues.put("HISTORY_ASSET_NAME",HISTORY_ASSET_NAME);
        }
        if(HISTORY_REFERID != null){
            contentValues.put("HISTORY_REFERID",HISTORY_REFERID);
        }
        if(HISTORY_REFERNAME != null){
            contentValues.put("HISTORY_REFERNAME",HISTORY_REFERNAME);
        }
        if(HISTORY_RECEIVEDATE != null){
            contentValues.put("HISTORY_RECEIVEDATE",HISTORY_RECEIVEDATE);
        }
        if(HISTORY_SPEC != null){
            contentValues.put("HISTORY_SPEC",HISTORY_SPEC);
        }
        if(HISTORY_UNITNAME != null){
            contentValues.put("HISTORY_UNITNAME",HISTORY_UNITNAME);
        }
        if(HISTORY_BUILDING_ID != -1){
            contentValues.put("HISTORY_BUILDING_ID",HISTORY_BUILDING_ID);
        }
        if(HISTORY_BUILDING_NAME != null){
            contentValues.put("HISTORY_BUILDING_NAME",HISTORY_BUILDING_NAME);
        }
        if(HISTORY_FLOOR_ID != -1){
            contentValues.put("HISTORY_FLOOR_ID",HISTORY_FLOOR_ID);
        }
        if(HISTORY_ROOM_ID != null){
            contentValues.put("HISTORY_ROOM_ID",HISTORY_ROOM_ID);
        }
        contentValues.put("HISTORY_DAY",HISTORY_DAY);
        contentValues.put("HISTORY_MONTH",HISTORY_MONTH);
        contentValues.put("HISTORY_YEAR",HISTORY_YEAR);
        contentValues.put("HISTORY_HOUR",HISTORY_HOUR);
        contentValues.put("HISTORY_MINUTE",HISTORY_MINUTE);
        if(HISTORY_STATUS_NAME != null){
            contentValues.put("HISTORY_STATUS_NAME",HISTORY_STATUS_NAME);
        }
        if(HISTORY_USERNAME != null){
            contentValues.put("HISTORY_USERNAME",HISTORY_USERNAME);
        }
        if(HISTORY_NOTE != null){
            contentValues.put("HISTORY_NOTE",HISTORY_NOTE);
        }

        db.insert(HISTORY_ASSET,null ,contentValues);
    }

    public void onInsert_HISTORY_IMAGE( int HISTORY_IMAGE_RID,int HISTORY_IMAGE_HISTORY_RID,String HISTORY_IMAGE_PATH){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HISTORY_IMAGE_RID",HISTORY_IMAGE_RID);
        contentValues.put("HISTORY_IMAGE_HISTORY_RID",HISTORY_IMAGE_HISTORY_RID);
        if(HISTORY_IMAGE_PATH != null){
            contentValues.put("HISTORY_IMAGE_PATH",HISTORY_IMAGE_PATH);
        }

        db.insert(HISTORY_IMAGE,null ,contentValues);
    }

    public void onInsert_TEMP_HISTORY_ASSET( int HISTORY_RID,String HISTORY_ASSET_ID,String HISTORY_ASSET_NAME,
                                             String HISTORY_REFERID,String HISTORY_REFERNAME,String HISTORY_RECEIVEDATE,
                                             String HISTORY_SPEC,String HISTORY_UNITNAME,
                                             int HISTORY_BUILDING_ID,String HISTORY_BUILDING_NAME,
                                             int HISTORY_FLOOR_ID,String HISTORY_ROOM_ID,
                                             int HISTORY_DAY,int HISTORY_MONTH, int HISTORY_YEAR,
                                             int HISTORY_HOUR,int HISTORY_MINUTE,String HISTORY_STATUS_NAME,
                                             String HISTORY_USERNAME,String HISTORY_NOTE ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(HISTORY_RID != -1){
            contentValues.put("HISTORY_RID",HISTORY_RID);
        }
        contentValues.put("HISTORY_ASSET_ID",HISTORY_ASSET_ID);
        if(HISTORY_ASSET_NAME != null){
            contentValues.put("HISTORY_ASSET_NAME",HISTORY_ASSET_NAME);
        }
        if(HISTORY_REFERID != null){
            contentValues.put("HISTORY_REFERID",HISTORY_REFERID);
        }
        if(HISTORY_REFERNAME != null){
            contentValues.put("HISTORY_REFERNAME",HISTORY_REFERNAME);
        }
        if(HISTORY_RECEIVEDATE != null){
            contentValues.put("HISTORY_RECEIVEDATE",HISTORY_RECEIVEDATE);
        }
        if(HISTORY_SPEC != null){
            contentValues.put("HISTORY_SPEC",HISTORY_SPEC);
        }
        if(HISTORY_UNITNAME != null){
            contentValues.put("HISTORY_UNITNAME",HISTORY_UNITNAME);
        }
        if(HISTORY_BUILDING_ID != -1){
            contentValues.put("HISTORY_BUILDING_ID",HISTORY_BUILDING_ID);
        }
        if(HISTORY_BUILDING_NAME != null){
            contentValues.put("HISTORY_BUILDING_NAME",HISTORY_BUILDING_NAME);
        }
        if(HISTORY_FLOOR_ID != -1){
            contentValues.put("HISTORY_FLOOR_ID",HISTORY_FLOOR_ID);
        }
        if(HISTORY_ROOM_ID != null){
            contentValues.put("HISTORY_ROOM_ID",HISTORY_ROOM_ID);
        }
        contentValues.put("HISTORY_DAY",HISTORY_DAY);
        contentValues.put("HISTORY_MONTH",HISTORY_MONTH);
        contentValues.put("HISTORY_YEAR",HISTORY_YEAR);
        contentValues.put("HISTORY_HOUR",HISTORY_HOUR);
        contentValues.put("HISTORY_MINUTE",HISTORY_MINUTE);
        if(HISTORY_STATUS_NAME != null){
            contentValues.put("HISTORY_STATUS_NAME",HISTORY_STATUS_NAME);
        }
        if(HISTORY_USERNAME != null){
            contentValues.put("HISTORY_USERNAME",HISTORY_USERNAME);
        }
        if(HISTORY_NOTE != null){
            contentValues.put("HISTORY_NOTE",HISTORY_NOTE);
        }

        db.insert(TEMP_HISTORY_ASSET,null ,contentValues);
    }

    public void onInsert_TEMP_HISTORY_IMAGE( int HISTORY_IMAGE_RID,int HISTORY_IMAGE_HISTORY_RID,byte[] HISTORY_IMAGE){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(HISTORY_IMAGE_RID != -1){
            contentValues.put("HISTORY_IMAGE_RID",HISTORY_IMAGE_RID);
        }
        contentValues.put("HISTORY_IMAGE_HISTORY_RID",HISTORY_IMAGE_HISTORY_RID);
        if(HISTORY_IMAGE != null){
            contentValues.put("HISTORY_IMAGE",HISTORY_IMAGE);
        }

        db.insert(TEMP_HISTORY_IMAGE,null ,contentValues);
    }

    public void onInsert_HISTORY_ASSET_RECENT( String HISTORY_ASSET_ID,String HISTORY_ASSET_NAME,
                                             String HISTORY_REFERID,String HISTORY_REFERNAME,String HISTORY_RECEIVEDATE,
                                             String HISTORY_SPEC,String HISTORY_UNITNAME,
                                             int HISTORY_BUILDING_ID,String HISTORY_BUILDING_NAME,
                                             int HISTORY_FLOOR_ID,String HISTORY_ROOM_ID,
                                             int HISTORY_DAY,int HISTORY_MONTH, int HISTORY_YEAR,
                                             int HISTORY_HOUR,int HISTORY_MINUTE,String HISTORY_STATUS_NAME,
                                             String HISTORY_USERNAME ,String HISTORY_NOTE, int HISTORY_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HISTORY_ASSET_ID",HISTORY_ASSET_ID);
        if(HISTORY_ASSET_NAME != null){
            contentValues.put("HISTORY_ASSET_NAME",HISTORY_ASSET_NAME);
        }
        if(HISTORY_REFERID != null){
            contentValues.put("HISTORY_REFERID",HISTORY_REFERID);
        }
        if(HISTORY_REFERNAME != null){
            contentValues.put("HISTORY_REFERNAME",HISTORY_REFERNAME);
        }
        if(HISTORY_RECEIVEDATE != null){
            contentValues.put("HISTORY_RECEIVEDATE",HISTORY_RECEIVEDATE);
        }
        if(HISTORY_SPEC != null){
            contentValues.put("HISTORY_SPEC",HISTORY_SPEC);
        }
        if(HISTORY_UNITNAME != null){
            contentValues.put("HISTORY_UNITNAME",HISTORY_UNITNAME);
        }
        if(HISTORY_BUILDING_ID != -1){
            contentValues.put("HISTORY_BUILDING_ID",HISTORY_BUILDING_ID);
        }
        if(HISTORY_BUILDING_NAME != null){
            contentValues.put("HISTORY_BUILDING_NAME",HISTORY_BUILDING_NAME);
        }
        if(HISTORY_FLOOR_ID != -1){
            contentValues.put("HISTORY_FLOOR_ID",HISTORY_FLOOR_ID);
        }
        if(HISTORY_ROOM_ID != null){
            contentValues.put("HISTORY_ROOM_ID",HISTORY_ROOM_ID);
        }
        contentValues.put("HISTORY_DAY",HISTORY_DAY);
        contentValues.put("HISTORY_MONTH",HISTORY_MONTH);
        contentValues.put("HISTORY_YEAR",HISTORY_YEAR);
        contentValues.put("HISTORY_HOUR",HISTORY_HOUR);
        contentValues.put("HISTORY_MINUTE",HISTORY_MINUTE);
        if(HISTORY_STATUS_NAME != null){
            contentValues.put("HISTORY_STATUS_NAME",HISTORY_STATUS_NAME);
        }
        if(HISTORY_USERNAME != null){
            contentValues.put("HISTORY_USERNAME",HISTORY_USERNAME);
        }
        if(HISTORY_NOTE != null){
            contentValues.put("HISTORY_NOTE",HISTORY_NOTE);
        }
        if(HISTORY_RID != -1){
            contentValues.put("HISTORY_RID",HISTORY_RID);
        }

        db.insert(HISTORY_ASSET_RECENT,null ,contentValues);
    }

    public void onInsert_STORE_HISTORY_ASSET( int HISTORY_RID,String HISTORY_ASSET_ID,String HISTORY_ASSET_NAME,
                                             String HISTORY_REFERID,String HISTORY_REFERNAME,String HISTORY_RECEIVEDATE,
                                             String HISTORY_SPEC,String HISTORY_UNITNAME,
                                             int HISTORY_BUILDING_ID,String HISTORY_BUILDING_NAME,
                                             int HISTORY_FLOOR_ID,String HISTORY_ROOM_ID,
                                             int HISTORY_DAY,int HISTORY_MONTH, int HISTORY_YEAR,
                                             int HISTORY_HOUR,int HISTORY_MINUTE,String HISTORY_STATUS_NAME,
                                             String HISTORY_USERNAME , String HISTORY_NOTE){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(HISTORY_RID != -1){
            contentValues.put("HISTORY_RID",HISTORY_RID);
        }
        contentValues.put("HISTORY_ASSET_ID",HISTORY_ASSET_ID);
        if(HISTORY_ASSET_NAME != null){
            contentValues.put("HISTORY_ASSET_NAME",HISTORY_ASSET_NAME);
        }
        if(HISTORY_REFERID != null){
            contentValues.put("HISTORY_REFERID",HISTORY_REFERID);
        }
        if(HISTORY_REFERNAME != null){
            contentValues.put("HISTORY_REFERNAME",HISTORY_REFERNAME);
        }
        if(HISTORY_RECEIVEDATE != null){
            contentValues.put("HISTORY_RECEIVEDATE",HISTORY_RECEIVEDATE);
        }
        if(HISTORY_SPEC != null){
            contentValues.put("HISTORY_SPEC",HISTORY_SPEC);
        }
        if(HISTORY_UNITNAME != null){
            contentValues.put("HISTORY_UNITNAME",HISTORY_UNITNAME);
        }
        if(HISTORY_BUILDING_ID != -1){
            contentValues.put("HISTORY_BUILDING_ID",HISTORY_BUILDING_ID);
        }
        if(HISTORY_BUILDING_NAME != null){
            contentValues.put("HISTORY_BUILDING_NAME",HISTORY_BUILDING_NAME);
        }
        if(HISTORY_FLOOR_ID != -1){
            contentValues.put("HISTORY_FLOOR_ID",HISTORY_FLOOR_ID);
        }
        if(HISTORY_ROOM_ID != null){
            contentValues.put("HISTORY_ROOM_ID",HISTORY_ROOM_ID);
        }
        contentValues.put("HISTORY_DAY",HISTORY_DAY);
        contentValues.put("HISTORY_MONTH",HISTORY_MONTH);
        contentValues.put("HISTORY_YEAR",HISTORY_YEAR);
        contentValues.put("HISTORY_HOUR",HISTORY_HOUR);
        contentValues.put("HISTORY_MINUTE",HISTORY_MINUTE);
        if(HISTORY_STATUS_NAME != null){
            contentValues.put("HISTORY_STATUS_NAME",HISTORY_STATUS_NAME);
        }
        if(HISTORY_USERNAME != null){
            contentValues.put("HISTORY_USERNAME",HISTORY_USERNAME);
        }
        if(HISTORY_NOTE != null){
            contentValues.put("HISTORY_NOTE",HISTORY_NOTE);
        }

        db.insert(STORE_HISTORY_ASSET,null ,contentValues);
    }

    public void onInsert_STORE_HISTORY_IMAGE( int HISTORY_IMAGE_RID,int HISTORY_IMAGE_HISTORY_RID,byte[] HISTORY_IMAGE){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(HISTORY_IMAGE_RID != -1){
            contentValues.put("HISTORY_IMAGE_RID",HISTORY_IMAGE_RID);
        }
        contentValues.put("HISTORY_IMAGE_HISTORY_RID",HISTORY_IMAGE_HISTORY_RID);
        if(HISTORY_IMAGE != null){
            contentValues.put("HISTORY_IMAGE",HISTORY_IMAGE);
        }

        db.insert(STORE_HISTORY_IMAGE,null ,contentValues);
    }

    public void onInsert_ASSET(String ASSETID,String BARCODE,String REFERIDITEM,String ASSETNAME,String RECEIVEDATE,String SPEC,String UNITNAME){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ASSETID",ASSETID);
        contentValues.put("BARCODE",BARCODE);
        contentValues.put("REFERIDITEM",REFERIDITEM);
        contentValues.put("ASSETNAME",ASSETNAME);
        contentValues.put("RECEIVEDATE",RECEIVEDATE);
        contentValues.put("SPEC",SPEC);
        contentValues.put("UNITNAME",UNITNAME);

        db.insert(ASSET,null ,contentValues);
    }

    public void onInsert_REFER(String REFERID,String REFERNAME,int DEPARTMENTID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("REFERID",REFERID);
        contentValues.put("REFERNAME",REFERNAME);
        contentValues.put("DEPARTMENTID",DEPARTMENTID);
        db.insert(REFER,null ,contentValues);
    }
    public void onInsert_STATUS(int STATUS_ID,String STATUS_NAME){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATUS_ID",STATUS_ID);
        contentValues.put("STATUS_NAME",STATUS_NAME);
        db.insert(STATUS,null ,contentValues);
    }
    public void onInsert_DEPARTMENT(int DEPARTMENTID,String DEPARTMENTNAME){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("DEPARTMENTID",DEPARTMENTID);
        contentValues.put("DEPARTMENTNAME",DEPARTMENTNAME);
        db.insert(DEPARTMENT,null ,contentValues);
    }

    public void onInsert_LOCATION(int LOCATION_ID,String LOCATION_BARCODE,int LOCATION_BUILDING_ID,String LOCATION_BUILDING_NAME,
                                  int LOCATION_FLOOR_ID,String LOCATION_ROOM_ID,int DEPARTMENTID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("LOCATION_ID",LOCATION_ID);
        contentValues.put("LOCATION_BARCODE",LOCATION_BARCODE);
        contentValues.put("LOCATION_BUILDING_ID",LOCATION_BUILDING_ID);
        contentValues.put("LOCATION_BUILDING_NAME",LOCATION_BUILDING_NAME);
        contentValues.put("LOCATION_FLOOR_ID",LOCATION_FLOOR_ID);
        contentValues.put("LOCATION_ROOM_ID",LOCATION_ROOM_ID);
        contentValues.put("DEPARTMENTID",DEPARTMENTID);
        db.insert(LOCATION,null ,contentValues);
    }

    public long getCountFromTable(String TABLE_NAME) {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }

    public void UpdaterawQueryWithoutMoveToFirst(String QUERY,String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(QUERY,selectionArgs);
        c.moveToFirst();
        c.close();
    }

    public void onUpdate_TEMP_TABLE_TABLE( int HISTORY_RID,String HISTORY_ASSET_ID,String HISTORY_ASSET_NAME,
                                           String HISTORY_REFERID,String HISTORY_REFERNAME,String HISTORY_RECEIVEDATE,
                                           String HISTORY_SPEC,String HISTORY_UNITNAME,
                                           int HISTORY_BUILDING_ID,String HISTORY_BUILDING_NAME,
                                           int HISTORY_FLOOR_ID,String HISTORY_ROOM_ID,
                                           int HISTORY_DAY,int HISTORY_MONTH, int HISTORY_YEAR,
                                           int HISTORY_HOUR,int HISTORY_MINUTE,String HISTORY_STATUS_NAME,
                                           String HISTORY_USERNAME ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(HISTORY_RID != -1){
            contentValues.put("HISTORY_RID",HISTORY_RID);
        }
        contentValues.put("HISTORY_ASSET_ID",HISTORY_ASSET_ID);
        if(HISTORY_ASSET_NAME != null){
            contentValues.put("HISTORY_ASSET_NAME",HISTORY_ASSET_NAME);
        }
        if(HISTORY_REFERID != null){
            contentValues.put("HISTORY_REFERID",HISTORY_REFERID);
        }
        if(HISTORY_REFERNAME != null){
            contentValues.put("HISTORY_REFERNAME",HISTORY_REFERNAME);
        }
        if(HISTORY_RECEIVEDATE != null){
            contentValues.put("HISTORY_RECEIVEDATE",HISTORY_RECEIVEDATE);
        }
        if(HISTORY_SPEC != null){
            contentValues.put("HISTORY_SPEC",HISTORY_SPEC);
        }
        if(HISTORY_UNITNAME != null){
            contentValues.put("HISTORY_UNITNAME",HISTORY_UNITNAME);
        }
        if(HISTORY_BUILDING_ID != -1){
            contentValues.put("HISTORY_BUILDING_ID",HISTORY_BUILDING_ID);
        }
        if(HISTORY_BUILDING_NAME != null){
            contentValues.put("HISTORY_BUILDING_NAME",HISTORY_BUILDING_NAME);
        }
        if(HISTORY_FLOOR_ID != -1){
            contentValues.put("HISTORY_FLOOR_ID",HISTORY_FLOOR_ID);
        }
        if(HISTORY_ROOM_ID != null){
            contentValues.put("HISTORY_ROOM_ID",HISTORY_ROOM_ID);
        }
        contentValues.put("HISTORY_DAY",HISTORY_DAY);
        contentValues.put("HISTORY_MONTH",HISTORY_MONTH);
        contentValues.put("HISTORY_YEAR",HISTORY_YEAR);
        contentValues.put("HISTORY_HOUR",HISTORY_HOUR);
        contentValues.put("HISTORY_MINUTE",HISTORY_MINUTE);
        if(HISTORY_STATUS_NAME != null){
            contentValues.put("HISTORY_STATUS_NAME",HISTORY_STATUS_NAME);
        }
        if(HISTORY_USERNAME != null){
            contentValues.put("HISTORY_USERNAME",HISTORY_USERNAME);
        }
        db.update(TEMP_HISTORY_ASSET, contentValues, "HISTORY_ASSET_ID = ?",new String[] {String.valueOf(HISTORY_ASSET_ID)});
    }
/*
    public void onUpdate_HISTORY_PHOTO (String TABLE_NAME,String HISTORY_ASSET_ID,byte[] HISTORY_PHOTO){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("HISTORY_PHOTO",HISTORY_PHOTO);
        db.update(TABLE_NAME, contentValues, "HISTORY_ASSET_ID = ?",new String[] {String.valueOf(HISTORY_ASSET_ID)});
    }
    */

    public void onDeleteALL_TEMP_HISTORY_TABLE(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEMP_HISTORY_ASSET,null,null);
    }
    public void onDelete_TEMP_HISTORY_ASSET(String HISTORY_ASSET_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TEMP_HISTORY_ASSET,"HISTORY_ASSET_ID = ?",new String[]{String.valueOf(HISTORY_ASSET_ID)});
    }

    public void onDelete_STORE_DATA(String HISTORY_ASSET_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STORE_HISTORY_ASSET,"HISTORY_ASSET_ID = ?",new String[]{String.valueOf(HISTORY_ASSET_ID)});
    }

    public void onDelete_STORE_DATA2(String HISTORY_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(STORE_HISTORY_ASSET,"HISTORY_RID = ?",new String[]{String.valueOf(HISTORY_RID)});
    }

    public void onDelete_ITEM_TABLE(String ITEM_UID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ASSET,"ITEM_UID = ?",new String[]{String.valueOf(ITEM_UID)});
    }

    public void onDeleteTable(String TABLE_NAME){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
    }
    public void onDeleteTable_Where_Clause(String TABLE_NAME,String WHERE_CLAUSE,String[] WHERE_ARGS){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,WHERE_CLAUSE,WHERE_ARGS);
    }


    public void Backup(){
    /*
    public void onInsert_MAP_TABLE(byte[] MAP_IMG,String MAP_NAME){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("MAP_STATUS",0);
        db.update(MAP_TABLE, contentValues, "MAP_NAME = ?",new String[] {MAP_NAME} );

        contentValues = new ContentValues();
        contentValues.put("MAP_IMG",MAP_IMG);
        contentValues.put("MAP_NAME",MAP_NAME);
        contentValues.put("MAP_STATUS",1);
        db.insert(MAP_TABLE,null ,contentValues);
    }
    public void onInsert_OWNER_TABLE(int OWNER_UID,String OWNER_FNAME,String OWNER_LNAME){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("OWNER_UID",OWNER_UID);
        contentValues.put("OWNER_FNAME",OWNER_FNAME);
        contentValues.put("OWNER_LNAME",OWNER_LNAME);
        db.insert(OWNER_TABLE,null ,contentValues);
    }
    public void onInsert_ITEM_TABLE(long ITEM_UID,String ITEM_SERIAL,String ITEM_NAME,int OWNER_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ITEM_UID",ITEM_UID);
        contentValues.put("ITEM_SERIAL",ITEM_SERIAL);
        contentValues.put("ITEM_NAME",ITEM_NAME);
        if(OWNER_RID != -1){
            contentValues.put("OWNER_RID",OWNER_RID);
        }
        db.insert(ITEM_TABLE,null ,contentValues);
    }
    public void onInsert_HISTORY_TABLE(int HISTORY_POS_X,int HISTORY_POS_Y,int HISTORY_DAY,int HISTORY_MONTH,int HISTORY_YEAR,int ITEM_RID,int OWNER_RID,int MAP_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HISTORY_POS_X",HISTORY_POS_X);
        contentValues.put("HISTORY_POS_Y",HISTORY_POS_Y);
        contentValues.put("HISTORY_DAY",HISTORY_DAY);
        contentValues.put("HISTORY_MONTH",HISTORY_MONTH);
        contentValues.put("HISTORY_YEAR",HISTORY_YEAR);
        contentValues.put("ITEM_RID",ITEM_RID);
        contentValues.put("OWNER_RID",OWNER_RID);
        contentValues.put("MAP_RID",MAP_RID);
        db.insert(HISTORY_TABLE,null ,contentValues);
    }

    public void onUpdate_MAP_TABLE(int MAP_RID,int MAP_STATUS){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("MAP_STATUS",MAP_STATUS);
        db.update(MAP_TABLE, contentValues, "MAP_RID = ?",new String[] {String.valueOf(MAP_RID)});
    }
    public void onUpdate_OWNER_TABLE(int OWNER_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("OWNER_RID",OWNER_RID);
        db.update(OWNER_TABLE, contentValues, "OWNER_RID = ?",new String[] {String.valueOf(OWNER_RID)});
    }
    public void onUpdate_ITEM_TABLE(int ITEM_RID,int OWNER_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("OWNER_RID",OWNER_RID);
        db.update(ITEM_TABLE, contentValues, "ITEM_RID = ?",new String[] {String.valueOf(ITEM_RID)});
    }
    public void onUpdate_HISTORY_TABLE(int HISTORY_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HISTORY_RID",HISTORY_RID);
        db.update(HISTORY_TABLE, contentValues, "HISTORY_RID = ?",new String[] {String.valueOf(HISTORY_RID)});
    }

    public Cursor getAllRowsFromTable(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return c;
    }
    public Cursor rawQuery(String QUERY,String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(QUERY,selectionArgs);
        c.moveToFirst();
        return c;
    }
    public Cursor rawQueryWithoutMoveToFirst(String QUERY,String[] selectionArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(QUERY,selectionArgs);
        return c;
    }
    */
    }

}
