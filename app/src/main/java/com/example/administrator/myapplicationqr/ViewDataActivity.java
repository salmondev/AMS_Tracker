package com.example.administrator.myapplicationqr;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ViewDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
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

    // VIEW_BUTTON =================================================================================
    public void onViewHistoryTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("HISTORY_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("HISTORY_RID : " + c.getInt(0) + "\n");
            buffer.append("HISTORY_ITEM_UID : " + c.getString(1) + "\n");
            buffer.append("HISTORY_OWNER_UID : " + c.getString(2) + "\n");
            buffer.append("HISTORY_STATUS_NAME : " + c.getString(3) + "\n");
            buffer.append("HISTORY_BUILDING_NAME : " + c.getString(4) + "\n");
            buffer.append("HISTORY_BUILDING_LAT : " + c.getFloat(5) + "\n");
            buffer.append("HISTORY_BUILDING_LONG : " + c.getFloat(6) + "\n");
            buffer.append("HISTORY_FLOOR : " + c.getInt(7) + "\n");
            buffer.append("HISTORY_ROOM : " + c.getInt(8) + "\n");
            buffer.append("HISTORY_HOUR : " + c.getInt(9) + "\n");
            buffer.append("HISTORY_MINUTE : " + c.getInt(10) + "\n");
            buffer.append("HISTORY_DAY : " + c.getInt(11) + "\n");
            buffer.append("HISTORY_MONTH : " + c.getInt(12) + "\n");
            buffer.append("HISTORY_YEAR : " + c.getInt(13) + "\n");
            buffer.append("HISTORY_POS_X : " + c.getInt(14) + "\n");
            buffer.append("HISTORY_POS_Y : " + c.getInt(15) + "\n");
            buffer.append("MAP_RID : " + c.getInt(16) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("HISTORY_TABLE", buffer.toString());
    } //
    public void onViewItemTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("ITEM_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("ITEM_UID : " + c.getString(0) + "\n");
            buffer.append("ITEM_SERIAL : " + c.getString(1) + "\n");
            buffer.append("ITEM_NAME : " + c.getString(2) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("ITEM_TABLE", buffer.toString());
    } //
    public void onViewOwnerTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("OWNER_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("OWNER_UID : " + c.getString(0) + "\n");
            buffer.append("OWNER_FNAME : " + c.getString(1) + "\n");
            buffer.append("OWNER_LNAME : " + c.getString(2) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("OWNER_TABLE", buffer.toString());
    } //
    public void onViewStatusTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("STATUS_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("STATUS_RID : " + c.getInt(0) + "\n");
            buffer.append("STATUS_NAME : " + c.getString(1) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("STATUS_TABLE", buffer.toString());
    } //
    public void onViewMapTable (View v)  {
        Cursor c = MainActivity.mydb.getAllRowsFromTable("MAP_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("MAP_RID : " + c.getInt(0) + "\n");
            buffer.append("MAP_IMG : " + c.getBlob(1) + "\n");
            buffer.append("MAP_NAME : " + c.getString(2) + "\n");
            buffer.append("MAP_STATUS : " + c.getInt(3) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("MAP_TABLE", buffer.toString());
    } //
    public void onViewBuildingTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("BUILDING_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("BUILDING_RID : " + c.getInt(0) + "\n");
            buffer.append("BUILDING_NAME : " + c.getString(1) + "\n");
            buffer.append("BUILDING_LAT : " + c.getFloat(2) + "\n");
            buffer.append("BUILDING_LONG : " + c.getFloat(3) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("BUILDING_TABLE", buffer.toString());
    }
    public void onViewFloorTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("FLOOR_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("FLOOR_RID : " + c.getInt(0) + "\n");
            buffer.append("FLOOR_NUMBER : " + c.getInt(1) + "\n");
            buffer.append("BUILDING_RID : " + c.getInt(2) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("FLOOR_TABLE", buffer.toString());
    }
    public void onViewRoomTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("ROOM_TABLE");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("ROOM_RID : " + c.getInt(0) + "\n");
            buffer.append("ROOM_NUMBER : " + c.getInt(1) + "\n");
            buffer.append("FLOOR_RID : " + c.getInt(2) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("ROOM_TABLE", buffer.toString());
    }
    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    // EDIT_BUTTON =================================================================================
    public void onEditHistory (View v){

    }
    public void onEditItem (View v){

    }
    public void onEditOwner (View v){

    }
    public void onEditStatus (View v){

    }
    public void onEditMap (View v){

    }
    public void onEditBuilding (View v){

    }
    public void onEditFloor (View v){

    }
    public void onEditRoom (View v){

    }

    public void onAddData(View v){
        MainActivity.mydb.onInsert_OWNER_TABLE("00101245", "RITTINAN", "CHANPEN");
        MainActivity.mydb.onInsert_OWNER_TABLE("00204560", "SUKRIT", "AMPONPONG");
        MainActivity.mydb.onInsert_OWNER_TABLE("00751423", "KITTIKUN", "SIRIKARIN");
        MainActivity.mydb.onInsert_OWNER_TABLE("00900345", "PANURUT", "CHINAKUL");
        MainActivity.mydb.onInsert_OWNER_TABLE("00318201", "PRUET", "HOLASUT");

        MainActivity.mydb.onInsert_ITEM_TABLE("1234567812345678-0000", "FAS2018", "PC A");
        MainActivity.mydb.onInsert_ITEM_TABLE("4122548563125484-0000", "KEU4132", "PC B");
        MainActivity.mydb.onInsert_ITEM_TABLE("2216546541648649-0000", "KUX9463", "PC C");
        MainActivity.mydb.onInsert_ITEM_TABLE("7418896358874123-0000", "LOA2345", "PC D");
        MainActivity.mydb.onInsert_ITEM_TABLE("9336579494651648-0000", "MYU7321", "PC E");
    }

}
