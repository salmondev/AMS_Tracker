package com.example.administrator.myapplicationqr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "EasyQR.db";

    public static final String HISTORY_TABLE = "HISTORY_TABLE";
    public static final String ITEM_TABLE = "ITEM_TABLE";
    public static final String OWNER_TABLE = "OWNER_TABLE";
    public static final String STATUS_TABLE = "STATUS_TABLE";
    public static final String MAP_TABLE = "MAP_TABLE";
    public static final String BUILDING_TABLE = "BUILDING_TABLE";
    public static final String FLOOR_TABLE = "FLOOR_TABLE";
    public static final String ROOM_TABLE = "ROOM_TABLE";

    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + HISTORY_TABLE + "(" +
                "HISTORY_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "HISTORY_ITEM_UID TEXT," +
                "HISTORY_OWNER_UID TEXT," +
                "HISTORY_STATUS_NAME TEXT," +
                "HISTORY_BUILDING_NAME TEXT," +
                "HISTORY_BUILDING_LAT FLOAT," +
                "HISTORY_BUILDING_LONG FLOAT," +
                "HISTORY_FLOOR INTEGER," +
                "HISTORY_ROOM INTEGER," +
                "HISTORY_HOUR INTEGER," +
                "HISTORY_MINUTE INTEGER," +
                "HISTORY_DAY INTEGER," +
                "HISTORY_MONTH INTEGER," +
                "HISTORY_YEAR INTEGER," +
                "HISTORY_POS_X INTEGER," +
                "HISTORY_POS_Y INTEGER," +
                "MAP_RID INTEGER," +
                "FOREIGN KEY(MAP_RID) REFERENCES MAP_TABLE(MAP_RID)" +
                ")"
        );
        db.execSQL("CREATE TABLE " + ITEM_TABLE + "(" +
                "ITEM_UID TEXT PRIMARY KEY UNIQUE," +
                "ITEM_SERIAL TEXT," +
                "ITEM_NAME TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + OWNER_TABLE + "(" +
                "OWNER_UID TEXT PRIMARY KEY UNIQUE," +
                "OWNER_FNAME TEXT," +
                "OWNER_LNAME TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + STATUS_TABLE + "(" +
                "STATUS_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "STATUS_NAME TEXT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + MAP_TABLE + "(" +
                "MAP_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "MAP_IMG BLOB," +
                "MAP_NAME TEXT," +
                "MAP_STATUS INTEGER" +
                ")"
        );
        db.execSQL("CREATE TABLE " + BUILDING_TABLE + "(" +
                "BUILDING_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "BUILDING_NAME TEXT," +
                "BUILDING_LAT FLOAT," +
                "BUILDING_LONG FLOAT" +
                ")"
        );
        db.execSQL("CREATE TABLE " + FLOOR_TABLE + "(" +
                "FLOOR_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FLOOR_NUMBER INTEGER," +
                "BUILDING_RID INTEGER," +
                "FOREIGN KEY(BUILDING_RID) REFERENCES BUILDING_TABLE(BUILDING_RID)" +
                ")"
        );
        db.execSQL("CREATE TABLE " + ROOM_TABLE + "(" +
                "ROOM_RID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ROOM_NUMBER INTEGER," +
                "FLOOR_RID INTEGER," +
                "FOREIGN KEY(FLOOR_RID) REFERENCES FLOOR_TABLE(FLOOR_RID)" +
                ")"
        );



    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OWNER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + STATUS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MAP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BUILDING_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FLOOR_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ROOM_TABLE);

        onCreate(db);
    }

    public Cursor getAllRowsFromTable(String TABLE_NAME) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME,null);
        return c;
    }

    public void onInsert_HISTORY_TABLE( long HISTORY_ITEM_UID,int HISTORY_OWNER_UID,String HISTORY_STATUS_NAME,String HISTORY_BUILDING_NAME,
                                        float HISTORY_BUILDING_LAT,float HISTORY_BUILDING_LONG, int HISTORY_FLOOR,int HISTORY_ROOM,
                                        int HISTORY_HOUR,int HISTORY_MINUTE,int HISTORY_DAY,int HISTORY_MONTH,
                                        int HISTORY_YEAR,int HISTORY_POS_X,int HISTORY_POS_Y,int MAP_RID ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("HISTORY_ITEM_UID",HISTORY_ITEM_UID);
        contentValues.put("HISTORY_OWNER_UID",HISTORY_OWNER_UID);
        contentValues.put("HISTORY_STATUS_NAME",HISTORY_STATUS_NAME);
        contentValues.put("HISTORY_BUILDING_NAME",HISTORY_BUILDING_NAME);
        if(HISTORY_BUILDING_LAT != -1){
            contentValues.put("HISTORY_BUILDING_LAT",HISTORY_BUILDING_LAT);
        }
        if(HISTORY_BUILDING_LONG != -1){
            contentValues.put("HISTORY_BUILDING_LONG",HISTORY_BUILDING_LONG);
        }
        contentValues.put("HISTORY_FLOOR",HISTORY_FLOOR);
        contentValues.put("HISTORY_ROOM",HISTORY_ROOM);
        contentValues.put("HISTORY_HOUR",HISTORY_HOUR);
        contentValues.put("HISTORY_MINUTE",HISTORY_MINUTE);
        contentValues.put("HISTORY_DAY",HISTORY_DAY);
        contentValues.put("HISTORY_MONTH",HISTORY_MONTH);
        contentValues.put("HISTORY_YEAR",HISTORY_YEAR);
        if(HISTORY_POS_X != -1){
            contentValues.put("HISTORY_POS_X",HISTORY_POS_X);
        }
        if(HISTORY_POS_Y != -1){
            contentValues.put("HISTORY_POS_Y",HISTORY_POS_Y);
        }
        if(MAP_RID != -1){
            contentValues.put("MAP_RID",MAP_RID);
        }

        db.insert(HISTORY_TABLE,null ,contentValues);
    }
    public void onInsert_ITEM_TABLE(String ITEM_UID,String ITEM_SERIAL,String ITEM_NAME){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ITEM_UID",ITEM_UID);
        contentValues.put("ITEM_SERIAL",ITEM_SERIAL);
        contentValues.put("ITEM_NAME",ITEM_NAME);
        db.insert(ITEM_TABLE,null ,contentValues);
    }
    public void onInsert_OWNER_TABLE(String OWNER_UID,String OWNER_FNAME,String OWNER_LNAME){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("OWNER_UID",OWNER_UID);
        contentValues.put("OWNER_FNAME",OWNER_FNAME);
        contentValues.put("OWNER_LNAME",OWNER_LNAME);
        db.insert(OWNER_TABLE,null ,contentValues);
    }
    public void onInsert_STATUS_TABLE(String STATUS_NAME){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("STATUS_NAME",STATUS_NAME);
        db.insert(STATUS_TABLE,null ,contentValues);
    }
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
    public void onInsert_BUILDING_TABLE(String BUILDING_NAME,float BUILDING_LAT,float BUILDING_LONG){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("BUILDING_NAME",BUILDING_NAME);
        if(BUILDING_LAT != -1){
            contentValues.put("BUILDING_LAT",BUILDING_LAT);
        }
        if(BUILDING_LONG != -1){
            contentValues.put("BUILDING_LONG",BUILDING_LONG);
        }
        db.insert(BUILDING_TABLE,null ,contentValues);
    }
    public void onInsert_FLOOR_TABLE(int FLOOR_NUMBER,int BUILDING_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("FLOOR_NUMBER",FLOOR_NUMBER);
        contentValues.put("BUILDING_RID",BUILDING_RID);
        db.insert(FLOOR_TABLE,null ,contentValues);
    }
    public void onInsert_ROOM_TABLE(int ROOM_NUMBER,int FLOOR_RID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ROOM_NUMBER",ROOM_NUMBER);
        contentValues.put("FLOOR_RID",FLOOR_RID);
        db.insert(ROOM_TABLE,null ,contentValues);
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
