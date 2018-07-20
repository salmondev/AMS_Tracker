package com.example.administrator.myapplicationqr;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper mydb;

    Intent intent;
    CurrentTime time1;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mydb = new DatabaseHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onStop() {
        super.onStop();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    protected void onResume() {
        super.onResume();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Back Button is not allowed on this state.",Toast.LENGTH_SHORT).show();
    }

    public void onSyncData(View v){

    }
    public void onViewData(View v){
        intent = new Intent(this, ViewDataActivity.class);
        startActivity(intent);
    }
    public void onGenerateQRCode(View v){
        intent = new Intent(this, GenerateQRActivity.class);
        startActivity(intent);
    }
    public void onScanQRCode(View v){
        intent = new Intent(this, ScanQRActivity.class);
        startActivity(intent);
    }

    public void Backup() {
        /*
    // ON_VIEW_TABLES ==============================================================================
    public void onViewMapTable (View v){
        Cursor c = mydb.getAllRowsFromTable("MAP_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append("MAP_RID : " + c.getInt(0) + "\n");
            buffer.append("MAP_IMG : " + c.getBlob(1) + "\n");
            buffer.append("MAP_NAME : " + c.getString(2) + "\n");
            buffer.append("MAP_STATUS : " + c.getInt(3) + "\n\n");
        }

        // Show all data
        showMessage("MAP_TABLE", buffer.toString());
    }
    public void onViewOwnerTable (View v){
        Cursor c = mydb.getAllRowsFromTable("OWNER_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append("OWNER_RID : " + c.getInt(0) + "\n");
            buffer.append("OWNER_UID : " + c.getInt(1) + "\n");
            buffer.append("OWNER_FNAME : " + c.getString(2) + "\n");
            buffer.append("OWNER_LNAME : " + c.getString(3) + "\n\n");
        }

        // Show all data
        showMessage("OWNER_TABLE", buffer.toString());
    }
    public void onViewItemTable (View v){
        Cursor c = mydb.getAllRowsFromTable("ITEM_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append("ITEM_RID : " + c.getInt(0) + "\n");
            buffer.append("ITEM_UID : " + c.getLong(1) + "\n");
            buffer.append("ITEM_SERIAL : " + c.getString(2) + "\n");
            buffer.append("ITEM_NAME : " + c.getString(3) + "\n");
            buffer.append("OWNER_RID : " + c.getInt(4) + "\n\n");
        }

        // Show all data
        showMessage("ITEM_TABLE", buffer.toString());
    }
    public void onViewHistoryTable (View v){
        Cursor c = mydb.getAllRowsFromTable("HISTORY_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append("HISTORY_RID : " + c.getInt(0) + "\n");
            buffer.append("HISTORY_POS_X : " + c.getInt(1) + "\n");
            buffer.append("HISTORY_POS_Y : " + c.getInt(2) + "\n");
            buffer.append("HISTORY_DAY : " + c.getInt(3) + "\n");
            buffer.append("HISTORY_MONTH : " + c.getInt(4) + "\n");
            buffer.append("HISTORY_YEAR : " + c.getInt(5) + "\n");
            buffer.append("ITEM_RID : " + c.getInt(6) + "\n");
            buffer.append("OWNER_RID : " + c.getInt(7) + "\n");
            buffer.append("MAP_RID : " + c.getInt(8) + "\n\n");
        }

        // Show all data
        showMessage("HISTORY_TABLE", buffer.toString());
    }
    public void onJoinItemWithOwner (View v){
        Cursor c = mydb.rawQueryWithoutMoveToFirst("SELECT * FROM ITEM_TABLE INNER JOIN OWNER_TABLE ON ITEM_TABLE.OWNER_RID = OWNER_TABLE.OWNER_RID", null);
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append("ITEM_RID : " + c.getInt(0) + "\n");
            buffer.append("ITEM_UID : " + c.getLong(1) + "\n");
            buffer.append("ITEM_SERIAL : " + c.getString(2) + "\n");
            buffer.append("ITEM_NAME : " + c.getString(3) + "\n");
            buffer.append("ITEM.OWNER_RID : " + c.getInt(4) + "\n");
            buffer.append("OWNER.OWNER_RID : " + c.getInt(5) + "\n");
            buffer.append("OWNER_UID : " + c.getInt(6) + "\n");
            buffer.append("OWNER_FNAME : " + c.getString(7) + "\n");
            buffer.append("OWNER_LNAME : " + c.getString(8) + "\n\n");
        }

        // Show all data
        showMessage("ITEM + OWNER TABLE", buffer.toString());
    }
    public void onJoinHistoryWithItemWithOwnerWithMap (View v){
        Cursor c = mydb.rawQueryWithoutMoveToFirst("SELECT * FROM HISTORY_TABLE INNER JOIN ITEM_TABLE ON HISTORY_TABLE.ITEM_RID = ITEM_TABLE.ITEM_RID INNER JOIN OWNER_TABLE ON HISTORY_TABLE.OWNER_RID = OWNER_TABLE.OWNER_RID INNER JOIN MAP_TABLE ON HISTORY_TABLE.MAP_RID = MAP_TABLE.MAP_RID", null);

        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append("HISTORY_RID : " + c.getInt(0) + "\n");
            buffer.append("HISTORY_POS_X : " + c.getInt(1) + "\n");
            buffer.append("HISTORY_POS_Y : " + c.getInt(2) + "\n");
            buffer.append("HISTORY_DAY : " + c.getInt(3) + "\n");
            buffer.append("HISTORY_MONTH : " + c.getInt(4) + "\n");
            buffer.append("HISTORY_YEAR : " + c.getInt(5) + "\n");

            //buffer.append("HISTORY.ITEM_RID : "+ c.getInt(6)+"\n");
            buffer.append("ITEM.ITEM_RID : " + c.getInt(9) + "\n");
            buffer.append("ITEM_UID : " + c.getLong(10) + "\n");
            buffer.append("ITEM_SERIAL : " + c.getString(11) + "\n");
            buffer.append("ITEM_NAME : " + c.getString(12) + "\n");
            //buffer.append("ITEM.OWNER_RID : " + c.getInt(13)+"\n");

            //buffer.append("HISTORY.OWNER_RID : "+ c.getInt(7)+"\n");
            buffer.append("OWNER.OWNER_RID : " + c.getInt(14) + "\n");
            buffer.append("OWNER_UID : " + c.getInt(15) + "\n");
            buffer.append("OWNER_FNAME : " + c.getString(16) + "\n");
            buffer.append("OWNER_LNAME : " + c.getString(17) + "\n");

            //buffer.append("HISTORY.MAP_RID : "+ c.getInt(8)+"\n");
            buffer.append("MAP.MAP_RID : " + c.getInt(18) + "\n");
            buffer.append("MAP_IMG : " + c.getBlob(19) + "\n");
            buffer.append("MAP_NAME : " + c.getString(20) + "\n");
            buffer.append("MAP_STATUS : " + c.getInt(21) + "\n\n");

        }

        // Show all data
        showMessage("HISTORY + ITEM + OWNER + MAP TABLE", buffer.toString());
    }
    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void onScanQRCode (View v){
        itn = new Intent(this, ScanQRActivity.class);
        startActivity(itn);
    }
    public void onGenerateQRCode (View v){
        itn = new Intent(this, GenerateQRActivity.class);
        startActivity(itn);
    }

    public void onAddMap (View v){
        itn = new Intent(this, AddMapActivity.class);
        startActivity(itn);
    }
    public void onAddAllData (View v){
        mydb.onInsert_OWNER_TABLE(101234, "RITTINAN", "CHANPEN");
        mydb.onInsert_OWNER_TABLE(204560, "SUKRIT", "AMPONPONG");
        mydb.onInsert_OWNER_TABLE(751423, "KITTIKUN", "SIRIKARIN");
        mydb.onInsert_OWNER_TABLE(900345, "PANURUT", "CHINAKUL");
        mydb.onInsert_OWNER_TABLE(318201, "PRUET", "HOLASUT");

        mydb.onInsert_ITEM_TABLE(123456789123456789L, "FAS2018", "PC A", -1);
        mydb.onInsert_ITEM_TABLE(421052431542795835L, "KEU4132", "PC B", -1);
        mydb.onInsert_ITEM_TABLE(379568874123548963L, "KUX9463", "PC C", -1);
        mydb.onInsert_ITEM_TABLE(754123589454513769L, "LOA2345", "PC D", -1);
        mydb.onInsert_ITEM_TABLE(244755896332115242L, "MYU7321", "PC E", -1);
    }
    public void onGoogleMap (View v){
        itn = new Intent(this, GoogleMapActivity.class);
        startActivity(itn);
    }
    */
    }
}