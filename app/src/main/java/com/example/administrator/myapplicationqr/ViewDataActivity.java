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
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
    public void onViewHistoryAssetTable (View v){

        Cursor c = MainActivity.mydb.getAllRowsFromTable("HISTORY_ASSET");
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
            buffer.append("HISTORY_ASSET_ID : " + c.getString(1) + "\n");
            buffer.append("HISTORY_ASSET_NAME : " + c.getString(2) + "\n");
            buffer.append("HISTORY_REFERID : " + c.getString(3) + "\n");
            buffer.append("HISTORY_REFERNAME : " + c.getString(4) + "\n");
            buffer.append("HISTORY_RECEIVEDATE : " + c.getString(5) + "\n");
            buffer.append("HISTORY_SPEC : " + c.getString(6) + "\n");
            buffer.append("HISTORY_UNITNAME : " + c.getString(7) + "\n");
            buffer.append("HISTORY_BUILDING_ID : " + c.getInt(8) + "\n");
            buffer.append("HISTORY_BUILDING_NAME : " + c.getString(9) + "\n");
            buffer.append("HISTORY_FLOOR_ID : " + c.getInt(10) + "\n");
            buffer.append("HISTORY_ROOM_ID : " + c.getString(11) + "\n");
            buffer.append("HISTORY_DAY : " + c.getInt(12) + "\n");
            buffer.append("HISTORY_MONTH : " + c.getInt(13) + "\n");
            buffer.append("HISTORY_YEAR : " + c.getInt(14) + "\n");
            buffer.append("HISTORY_HOUR : " + c.getInt(15) + "\n");
            buffer.append("HISTORY_MINUTE : " + c.getInt(16) + "\n");
            buffer.append("HISTORY_STATUS_NAME : " + c.getString(17) + "\n");
            buffer.append("HISTORY_USERNAME : " + c.getString(18) + "\n");
            i++;
        }

        // Show all data
        showMessage("HISTORY_ASSET", buffer.toString());

    } //
    public void onViewTempHistoryAssetTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_ASSET");
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
            buffer.append("HISTORY_ASSET_ID : " + c.getString(1) + "\n");
            buffer.append("HISTORY_ASSET_NAME : " + c.getString(2) + "\n");
            buffer.append("HISTORY_REFERID : " + c.getString(3) + "\n");
            buffer.append("HISTORY_REFERNAME : " + c.getString(4) + "\n");
            buffer.append("HISTORY_RECEIVEDATE : " + c.getString(5) + "\n");
            buffer.append("HISTORY_SPEC : " + c.getString(6) + "\n");
            buffer.append("HISTORY_UNITNAME : " + c.getString(7) + "\n");
            buffer.append("HISTORY_BUILDING_ID : " + c.getInt(8) + "\n");
            buffer.append("HISTORY_BUILDING_NAME : " + c.getString(9) + "\n");
            buffer.append("HISTORY_FLOOR_ID : " + c.getInt(10) + "\n");
            buffer.append("HISTORY_ROOM_ID : " + c.getString(11) + "\n");
            buffer.append("HISTORY_DAY : " + c.getInt(12) + "\n");
            buffer.append("HISTORY_MONTH : " + c.getInt(13) + "\n");
            buffer.append("HISTORY_YEAR : " + c.getInt(14) + "\n");
            buffer.append("HISTORY_HOUR : " + c.getInt(15) + "\n");
            buffer.append("HISTORY_MINUTE : " + c.getInt(16) + "\n");
            buffer.append("HISTORY_STATUS_NAME : " + c.getString(17) + "\n");
            buffer.append("HISTORY_USERNAME : " + c.getString(18) + "\n");
            i++;
        }

        // Show all data
        showMessage("TEMP_HISTORY_ASSET", buffer.toString());
    } //
    public void onViewAssetTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("ASSET");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("ASSETID : " + c.getString(0) + "\n");
            buffer.append("BARCODE : " + c.getString(1) + "\n");
            buffer.append("REFERIDITEM : " + c.getString(2) + "\n");
            buffer.append("ASSETNAME : " + c.getString(3) + "\n");
            buffer.append("RECEIVEDATE : " + c.getString(4) + "\n");
            buffer.append("SPEC : " + c.getString(5) + "\n");
            buffer.append("UNITNAME : " + c.getString(6) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("ASSET", buffer.toString());
    } //
    public void onViewReferTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("REFER");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("REFERID : " + c.getString(0) + "\n");
            buffer.append("REFERNAME : " + c.getString(1) + "\n");
            buffer.append("DEPARTMENTID : " + c.getInt(2) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("REFER", buffer.toString());
    } //
    public void onViewStatusTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("STATUS");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("STATUS_ID : " + c.getInt(0) + "\n");
            buffer.append("STATUS_NAME : " + c.getString(1) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("STATUS", buffer.toString());
    } //
    public void onViewDepartmentTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("DEPARTMENT");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("DEPARTMENTID : " + c.getInt(0) + "\n");
            buffer.append("DEPARTMENTNAME : " + c.getString(1) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("DEPARTMENT", buffer.toString());
    }
    public void onViewAssetVerifyTable (View v){
        /*
        Cursor c = MainActivity.mydb.getAllRowsFromTable("FLOOR");
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
            buffer.append("FLOOR_ID : " + c.getInt(1) + "\n");
            buffer.append("BUILDING_ID : " + c.getInt(2) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("FLOOR", buffer.toString());
        */
    }
    public void onViewUserTable (View v){
        /*
        Cursor c = MainActivity.mydb.getAllRowsFromTable("ROOM");
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
            buffer.append("ROOM_ID : " + c.getInt(1) + "\n");
            buffer.append("FLOOR_RID : " + c.getInt(2) + "\n");
            buffer.append("DEPARTMENTID : " + c.getInt(3) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("ROOM", buffer.toString());
        */
    }
    public void onViewLocationTable (View v){
        Cursor c = MainActivity.mydb.getAllRowsFromTable("LOCATION");
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }

        int i = 1;
        StringBuffer buffer = new StringBuffer();
        while (c.moveToNext()) {
            buffer.append(i + "=-------------------=" + "\n\n");
            buffer.append("LOCATION_ID : " + c.getInt(0) + "\n");
            buffer.append("LOCATION_BARCODE : " + c.getString(1) + "\n");
            buffer.append("LOCATION_BUILDING_ID : " + c.getInt(2) + "\n");
            buffer.append("LOCATION_BUILDING_NAME  : " + c.getString(3) + "\n");
            buffer.append("LOCATION_FLOOR_ID : " + c.getInt(4) + "\n");
            buffer.append("LOCATION_ROOM_ID  : " + c.getString(5) + "\n");
            buffer.append("DEPARTMENTID  : " + c.getInt(6) + "\n\n");
            i++;
        }

        // Show all data
        showMessage("LOCATION", buffer.toString());
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

    public void onClearData(View v){
        MainActivity.mydb.onDeleteTable("TEMP_HISTORY");
        MainActivity.mydb.onDeleteTable("HISTORY");

    }

}
