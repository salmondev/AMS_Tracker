package com.example.administrator.myapplicationqr;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckRoomAsset extends AppCompatActivity {

    private String BASE_URL;

    Cursor c;
    Cursor C_HISTORY_ASSET_RECENT;
    ArrayList<HashMap<String, String>> MyArrList;
    HashMap<String, String> hashMap;
    ListView listView;
    String[] split;
    Spinner sp1,sp2,sp3;
    ArrayAdapter<String> adapter1,adapter2,adapter3;

    String BUILDING_NAME;
    int BUILDING_RID,FLOOR_RID;
    String ROOM_RID;
    String t1;
    Cursor BUILDING_COLUMN,FLOOR_COLUMN,ROOM_COLUMN;
    Cursor C_BUILDING_RID,C_FLOOR_RID,C_ROOM_RID,C_LOCATION;

    Bitmap returnBitmap = null;

    Cursor STATUS,OWNER,INDOORMAP;

    String FROM_NFC_SCAN = null;

    NfcAdapter nfcAdapter;
    Boolean nfc_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_room_asset);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            //Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
            nfc_status = true;
        }else{
            //Toast.makeText(this, "NFC not available", Toast.LENGTH_LONG).show();
            nfc_status = false;
        }

        listView = (ListView)findViewById(R.id.listview_1);
        MainActivity.mydb.onDeleteTable("TEMP_HISTORY_ASSET");

        if(getIntent().getStringExtra("RESULT") != null){
            FROM_NFC_SCAN = getIntent().getStringExtra("RESULT");
        }
        else{
            FROM_NFC_SCAN = null;
        }

        if( FROM_NFC_SCAN != null  ){
            split = FROM_NFC_SCAN.split("-");

            //REFRESH_LISTVIEW =!=!=!=!=!=!=!
            refreshListView(null , null,null);
            //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

            // BUILDING_SPINNER ========================================================================
            List<String> splist1 = new ArrayList<String>();
            splist1.add("BUILDING");
            BUILDING_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_BUILDING_ID FROM LOCATION ORDER BY LOCATION_BUILDING_ID ASC",null);
            if(BUILDING_COLUMN.getCount() != 0) {
                while (BUILDING_COLUMN.moveToNext()) {
                    splist1.add( BUILDING_COLUMN.getString(0) );
                }
            }
            adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splist1);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1 = (Spinner) findViewById(R.id.spinner_building);
            sp1.setAdapter(adapter1);

            sp1.setSelection(getIndex(sp1,split[0]));

            // HIDDEN !!! PRESS + TO SEE MORE !!! BUILDING_LISTENER =======================================================================
            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    if(position > 0){
                        BUILDING_RID =  Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                        //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                        //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                        refreshListView(String.valueOf(BUILDING_RID) , null,null);
                        //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                        // FLOOR_SPINNER ========================================================================
                        List<String> splist2 = new ArrayList<String>();
                        splist2.add("FLOOR");
                        FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? ORDER BY LOCATION_FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                        if (FLOOR_COLUMN.getCount() != 0) {
                            while (FLOOR_COLUMN.moveToNext()) {
                                splist2.add(FLOOR_COLUMN.getString(0));
                            }
                        }
                        adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp2 = (Spinner) findViewById(R.id.spinner_floor);
                        sp2.setAdapter(adapter2);

                        sp2.setSelection(getIndex(sp2,split[1]));

                        // FLOOR_LISTENER ========================================================================
                        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                if (position > 0) {
                                    FLOOR_RID = Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                                    //Toast.makeText(CheckRoomAsset.this,"FLOOR_ID : " + FLOOR_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                    //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                    refreshListView(String.valueOf(BUILDING_RID) , String.valueOf(FLOOR_RID),null);
                                    //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? ORDER BY LOCATION_ROOM_ID ASC", new String[]{String.valueOf(BUILDING_RID),String.valueOf(FLOOR_RID)});
                                    if (ROOM_COLUMN.getCount() != 0) {
                                        while (ROOM_COLUMN.moveToNext()) {
                                            splist3.add(ROOM_COLUMN.getString(0));
                                        }
                                    }
                                    adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3.setAdapter(adapter3);

                                    sp3.setSelection(getIndex(sp3,split[2]));

                                    // ROOM_LISTENER ========================================================================
                                    sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                            if (position > 0) {
                                                ROOM_RID = adapterView.getItemAtPosition(position).toString();
                                                //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + "\nFLOOR_ID : " + FLOOR_RID + "\nROOM_ID : " + ROOM_RID,Toast.LENGTH_SHORT).show();

                                                //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                                refreshListView(String.valueOf(BUILDING_RID) , String.valueOf(FLOOR_RID),ROOM_RID);
                                                //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();
                                            } else {
                                                // IN CASE OF SELECTED 0 ROOM REFRESH NOTHING
                                                ROOM_RID = "-1";
                                                //Toast.makeText(CheckRoomAsset.this, "ROOM_RID : " + ROOM_RID +
                                                //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {
                                        }
                                    });
                                }else if(position == 0){
                                    //REFRESH_LISTVIEW333 =!=!=!=!=!=!=!
                                    refreshListView(String.valueOf(BUILDING_RID), null, null);

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3.setAdapter(adapter3);

                                } else {
                                    // IN CASE OF SELECTED 0 FLOOR REFRESH ROOM_SPINNER
                                    FLOOR_RID = -1;
                                    //Toast.makeText(CheckRoomAsset.this, "FLOOR_RID : " + FLOOR_RID +
                                    //" FLOOR_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                    ROOM_RID = "-1";

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3.setAdapter(adapter3);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }

                        });

                    }
                    else{
                        // IN CASE OF SELECTED 0 BUILDING REFRESH FLOOR_SPINNER AND ROOM_SPINNER
                        BUILDING_RID = -1;
                        //Toast.makeText(CheckRoomAsset.this,"BUILDING_RID : " + BUILDING_RID +
                        //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                        FLOOR_RID = -1;
                        // FLOOR_SPINNER ========================================================================
                        List<String> splist2 = new ArrayList<String>();
                        splist2.add("FLOOR");
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                        sp2.setAdapter(adapter2);

                        ROOM_RID = "-1";
                        // ROOM_SPINNER ========================================================================
                        List<String> splist3 = new ArrayList<String>();
                        splist3.add("ROOM");
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                        sp3.setAdapter(adapter3);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }else{
            //REFRESH_LISTVIEW =!=!=!=!=!=!=!
            refreshListView(null , null,null);

            // BUILDING_SPINNER ========================================================================
            List<String> splist1 = new ArrayList<String>();
            splist1.add("BUILDING");
            //*//BUILDING_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM BUILDING ORDER BY BUILDING_NAME ASC",null);
            BUILDING_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_BUILDING_ID FROM LOCATION ORDER BY LOCATION_BUILDING_ID ASC",null);
            if(BUILDING_COLUMN.getCount() != 0) {
                while (BUILDING_COLUMN.moveToNext()) {
                    splist1.add( BUILDING_COLUMN.getString(0) );
                }
            }
            //ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splist1);
            adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splist1);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Spinner sp1 = (Spinner) findViewById(R.id.spinner_building);
            sp1 = (Spinner) findViewById(R.id.spinner_building);
            sp1.setAdapter(adapter1);

            //sp1.setSelection(getIndex(sp1,"78"));

            // BUILDING_LISTENER =======================================================================
            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    if(position > 0){
                        BUILDING_RID =  Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                        //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                        //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                        refreshListView(String.valueOf(BUILDING_RID) , null,null);
                        //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                        // FLOOR_SPINNER ========================================================================
                        List<String> splist2 = new ArrayList<String>();
                        splist2.add("FLOOR");
                        FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? ORDER BY LOCATION_FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                        if (FLOOR_COLUMN.getCount() != 0) {
                            while (FLOOR_COLUMN.moveToNext()) {
                                splist2.add(FLOOR_COLUMN.getString(0));
                            }
                        }
                        //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
                        adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                        sp2 = (Spinner) findViewById(R.id.spinner_floor);
                        sp2.setAdapter(adapter2);

                        // FLOOR_LISTENER ========================================================================
                        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                if (position > 0) {
                                    FLOOR_RID = Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                                    //Toast.makeText(CheckRoomAsset.this,"FLOOR_ID : " + FLOOR_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                    //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                    refreshListView(String.valueOf(BUILDING_RID), String.valueOf(FLOOR_RID), null);
                                    //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? ORDER BY LOCATION_ROOM_ID ASC", new String[]{String.valueOf(BUILDING_RID), String.valueOf(FLOOR_RID)});
                                    if (ROOM_COLUMN.getCount() != 0) {
                                        while (ROOM_COLUMN.moveToNext()) {
                                            splist3.add(ROOM_COLUMN.getString(0));
                                        }
                                    }
                                    //ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3.setAdapter(adapter3);

                                    // ROOM_LISTENER ========================================================================
                                    sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                            if (position > 0) {
                                                ROOM_RID = adapterView.getItemAtPosition(position).toString();
                                                //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + "\nFLOOR_ID : " + FLOOR_RID + "\nROOM_ID : " + ROOM_RID,Toast.LENGTH_SHORT).show();
                                                //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                                refreshListView(String.valueOf(BUILDING_RID), String.valueOf(FLOOR_RID), ROOM_RID);
                                                //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                                            } else {
                                                // IN CASE OF SELECTED 0 ROOM REFRESH NOTHING
                                                ROOM_RID = "-1";
                                                //Toast.makeText(CheckRoomAsset.this, "ROOM_RID : " + ROOM_RID +
                                                //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }else if(position == 0){
                                    //REFRESH_LISTVIEW333 =!=!=!=!=!=!=!
                                    refreshListView(String.valueOf(BUILDING_RID), null, null);

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3.setAdapter(adapter3);

                                } else {
                                    // IN CASE OF SELECTED 0 FLOOR REFRESH ROOM_SPINNER
                                    FLOOR_RID = -1;
                                    //Toast.makeText(CheckRoomAsset.this, "FLOOR_RID : " + FLOOR_RID +
                                    //" FLOOR_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                    ROOM_RID = "-1";

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3.setAdapter(adapter3);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }

                        });

                    }
                    else{
                        // IN CASE OF SELECTED 0 BUILDING REFRESH FLOOR_SPINNER AND ROOM_SPINNER
                        BUILDING_RID = -1;
                        //Toast.makeText(CheckRoomAsset.this,"BUILDING_RID : " + BUILDING_RID +
                        //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                        FLOOR_RID = -1;
                        // FLOOR_SPINNER ========================================================================
                        List<String> splist2 = new ArrayList<String>();
                        splist2.add("FLOOR");
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                        sp2.setAdapter(adapter2);

                        ROOM_RID = "-1";
                        // ROOM_SPINNER ========================================================================
                        List<String> splist3 = new ArrayList<String>();
                        splist3.add("ROOM");
                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                        sp3.setAdapter(adapter3);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        BASE_URL = sharedPreferences.getString("BaseUrl",getResources().getString(R.string.BASE_URL));
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
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            disableForegroundDispatchSystem();
        }else{

        }
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
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            enableForegroundDispatchSystem();
        }else{

        }
    }

    private void enableForegroundDispatchSystem(){
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Intent intent = new Intent(this,CheckRoomAsset.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
            IntentFilter[] intentFilter = new IntentFilter[]{};
            nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilter,null);
            nfc_status = true;
        }else{

            nfc_status = false;
        }
    }
    private void disableForegroundDispatchSystem(){
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            nfcAdapter.disableForegroundDispatch(this);
            nfc_status = true;
        }else{

            nfc_status = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toast.makeText(this, "onNewIntent Method", Toast.LENGTH_SHORT).show(); //TOAST
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareUltralight mifareUltralight = MifareUltralight.get(tag);
        try {
            //======================================================================================
            mifareUltralight.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // =========================================================================================
        // =========================================================================================
        // =========================================================================================
        // IF_NFC_IS_AVAILABLE
        try{
            if(mifareUltralight.isConnected()) {
                Toast.makeText(this, "NFC Connection : " + "TRUE", Toast.LENGTH_SHORT).show(); //TOAST

                byte readP0[] = mifareUltralight.readPages(0);
                byte readP4[] = mifareUltralight.readPages(4);
                byte readP8[] = mifareUltralight.readPages(8);
                byte readP12[] = mifareUltralight.readPages(12);
                byte readP16[] = mifareUltralight.readPages(16);
                byte readP20[] = mifareUltralight.readPages(20);
                byte readP24[] = mifareUltralight.readPages(24);
                byte readP28[] = mifareUltralight.readPages(28);
                byte readP32[] = mifareUltralight.readPages(32);
                byte readP36[] = mifareUltralight.readPages(36);
                byte readP40[] = mifareUltralight.readPages(40);
                byte readP44[] = mifareUltralight.readPages(44);

                //Toast.makeText(this, "TAG PROTECTED PAGE START AT : " + new String(String.valueOf(readP40[7])), Toast.LENGTH_SHORT).show(); //TOAST

                showreadablePage(readP4,readP8,readP12,
                        readP16,readP20,readP24,readP28,
                        readP32,readP36);
            }
            else{
                Toast.makeText(this, "NFC Connection : " + "FALSE", Toast.LENGTH_SHORT).show(); //TOAST

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // =========================================================================================
        // =========================================================================================
        // =========================================================================================
        try {
            mifareUltralight.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showreadablePage(byte[] readP4,byte[] readP8,byte[] readP12,
                                 byte[] readP16,byte[] readP20,byte[] readP24,byte[] readP28,
                                 byte[] readP32,byte[] readP36){
        String result = "";

        result = result + read4Pages(readP4);
        result = result + read4Pages(readP8);
        result = result + read4Pages(readP12);
        result = result + read4Pages(readP16);
        result = result + read4Pages(readP20);
        result = result + read4Pages(readP24);
        result = result + read4Pages(readP28);
        result = result + read4Pages(readP32);
        result = result + read4Pages(readP36);
        result = result.replaceAll("@","").trim();

        Toast.makeText(this, "Your data : \n" + result , Toast.LENGTH_LONG).show(); //TOAST

        //result = "412000010005-30406-00003";

        if(result != null){
            Cursor C_LOCATION_BARCODE = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BARCODE = ?",new String[]{ result } );
            Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ result });
            if( (c != null) && (c.getCount() > 0) ){
                Toast.makeText(this,"ASSET QR CODE : " + result + "\nThis mode can get only Location QR Code.",Toast.LENGTH_SHORT).show();
            }
            else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                split = C_LOCATION_BARCODE.getString(1).split("-");

                //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                refreshListView(null , null,null);
                //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                // BUILDING_SPINNER ========================================================================
                List<String> splist1 = new ArrayList<String>();
                splist1.add("BUILDING");
                BUILDING_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_BUILDING_ID FROM LOCATION ORDER BY LOCATION_BUILDING_ID ASC",null);
                if(BUILDING_COLUMN.getCount() != 0) {
                    while (BUILDING_COLUMN.moveToNext()) {
                        splist1.add( BUILDING_COLUMN.getString(0) );
                    }
                }
                adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splist1);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp1 = (Spinner) findViewById(R.id.spinner_building);
                sp1.setAdapter(adapter1);

                sp1.setSelection(getIndex(sp1,split[0]));

                // HIDDEN !!! PRESS + TO SEE MORE !!! BUILDING_LISTENER =======================================================================
                sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        if(position > 0){
                            BUILDING_RID =  Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                            //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                            //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                            refreshListView(String.valueOf(BUILDING_RID) , null,null);
                            //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                            // FLOOR_SPINNER ========================================================================
                            List<String> splist2 = new ArrayList<String>();
                            splist2.add("FLOOR");
                            FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? ORDER BY LOCATION_FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                            if (FLOOR_COLUMN.getCount() != 0) {
                                while (FLOOR_COLUMN.moveToNext()) {
                                    splist2.add(FLOOR_COLUMN.getString(0));
                                }
                            }
                            adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sp2 = (Spinner) findViewById(R.id.spinner_floor);
                            sp2.setAdapter(adapter2);

                            sp2.setSelection(getIndex(sp2,split[1]));

                            // FLOOR_LISTENER ========================================================================
                            sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                    if (position > 0) {
                                        FLOOR_RID = Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                                        //Toast.makeText(CheckRoomAsset.this,"FLOOR_ID : " + FLOOR_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                        //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                        refreshListView(String.valueOf(BUILDING_RID) , String.valueOf(FLOOR_RID),null);
                                        //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                                        // ROOM_SPINNER ========================================================================
                                        List<String> splist3 = new ArrayList<String>();
                                        splist3.add("ROOM");
                                        ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? ORDER BY LOCATION_ROOM_ID ASC", new String[]{String.valueOf(BUILDING_RID),String.valueOf(FLOOR_RID)});
                                        if (ROOM_COLUMN.getCount() != 0) {
                                            while (ROOM_COLUMN.moveToNext()) {
                                                splist3.add(ROOM_COLUMN.getString(0));
                                            }
                                        }
                                        adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        sp3 = (Spinner) findViewById(R.id.spinner_room);
                                        sp3.setAdapter(adapter3);

                                        sp3.setSelection(getIndex(sp3,split[2]));

                                        // ROOM_LISTENER ========================================================================
                                        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                if (position > 0) {
                                                    ROOM_RID = adapterView.getItemAtPosition(position).toString();
                                                    //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + "\nFLOOR_ID : " + FLOOR_RID + "\nROOM_ID : " + ROOM_RID,Toast.LENGTH_SHORT).show();

                                                    //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                                    refreshListView(String.valueOf(BUILDING_RID) , String.valueOf(FLOOR_RID),ROOM_RID);
                                                    //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // IN CASE OF SELECTED 0 ROOM REFRESH NOTHING
                                                    ROOM_RID = "-1";
                                                    //Toast.makeText(CheckRoomAsset.this, "ROOM_RID : " + ROOM_RID +
                                                    //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {
                                            }
                                        });
                                    }else if(position == 0){
                                        //REFRESH_LISTVIEW333 =!=!=!=!=!=!=!
                                        refreshListView(String.valueOf(BUILDING_RID), null, null);

                                        // ROOM_SPINNER ========================================================================
                                        List<String> splist3 = new ArrayList<String>();
                                        splist3.add("ROOM");
                                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                        sp3.setAdapter(adapter3);

                                    } else {
                                        // IN CASE OF SELECTED 0 FLOOR REFRESH ROOM_SPINNER
                                        FLOOR_RID = -1;
                                        //Toast.makeText(CheckRoomAsset.this, "FLOOR_RID : " + FLOOR_RID +
                                        //" FLOOR_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                        ROOM_RID = "-1";

                                        // ROOM_SPINNER ========================================================================
                                        List<String> splist3 = new ArrayList<String>();
                                        splist3.add("ROOM");
                                        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                        sp3.setAdapter(adapter3);
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }

                            });

                        }
                        else{
                            // IN CASE OF SELECTED 0 BUILDING REFRESH FLOOR_SPINNER AND ROOM_SPINNER
                            BUILDING_RID = -1;
                            //Toast.makeText(CheckRoomAsset.this,"BUILDING_RID : " + BUILDING_RID +
                            //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                            FLOOR_RID = -1;
                            // FLOOR_SPINNER ========================================================================
                            List<String> splist2 = new ArrayList<String>();
                            splist2.add("FLOOR");
                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                            sp2.setAdapter(adapter2);

                            ROOM_RID = "-1";
                            // ROOM_SPINNER ========================================================================
                            List<String> splist3 = new ArrayList<String>();
                            splist3.add("ROOM");
                            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                            sp3.setAdapter(adapter3);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //Toast.makeText(this,C_LOCATION_BARCODE.getString(1) + "\n1 = " + split[0] + "\n2 = " + split[1] + "\n3 = " + split[2],Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,C_LOCATION_BARCODE.getString(1),Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(this, "UNKNOWN QR CODE : " + result, Toast.LENGTH_LONG).show();
            }

        }
        else{

        }
    }

    public String read4Pages(byte[] pages){
        String readtext = "";

        String[] a1 = new String(pages, Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
            }
            if(i == 3 || i == 7 || i == 11 || i == 15){
                for(int k = i - 3; k <= i; k++){
                    readtext = readtext + a1[k+1];
                }
            }
        }

        return readtext;
    }


    public void onScan(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Please Focus Your Camera on QR Code.");
        integrator.setOrientationLocked(true);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    public class TextAdapter extends BaseAdapter
    {
        private Context context;

        public TextAdapter(Context c)
        {
            // TODO Auto-generated method stub
            context = c;
            notifyDataSetChanged();
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArrList.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // TODO Auto-generated method stub
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_row2, null);
            }

            TextView textView_ASSET_ID = (TextView) convertView.findViewById(R.id.textView_ASSET_ID);
            textView_ASSET_ID.setText(getResources().getString(R.string.ASSET_ID) + MyArrList.get(position).get("ASSETID"));

            TextView textView_ASSET_NAME = (TextView) convertView.findViewById(R.id.textView_ASSET_NAME);
            textView_ASSET_NAME.setText(getResources().getString(R.string.ASSET_NAME) + MyArrList.get(position).get("ASSETNAME"));

            TextView textView_REFER_ID = (TextView) convertView.findViewById(R.id.textView_REFER_ID);
            textView_REFER_ID.setText(getResources().getString(R.string.REFER_ID) + MyArrList.get(position).get("REFERID"));

            TextView textView_REFER_NAME = (TextView) convertView.findViewById(R.id.textView_REFER_NAME);
            textView_REFER_NAME.setText(getResources().getString(R.string.REFER_NAME) + MyArrList.get(position).get("REFERNAME"));

            TextView textView_RECEIVEDATE = (TextView) convertView.findViewById(R.id.textView_RECEIVEDATE);
            textView_RECEIVEDATE.setText(getResources().getString(R.string.RECEIVEDATE) + MyArrList.get(position).get("RECEIVEDATE"));

            TextView textView_SPEC = (TextView) convertView.findViewById(R.id.textView_SPEC);
            textView_SPEC.setText(getResources().getString(R.string.SPEC) + MyArrList.get(position).get("SPEC"));

            TextView textView_UNITNAME = (TextView) convertView.findViewById(R.id.textView_UNITNAME);
            textView_UNITNAME.setText(getResources().getString(R.string.UNITNAME) + MyArrList.get(position).get("UNITNAME"));

            TextView textView_LOCATION = (TextView) convertView.findViewById(R.id.textView_LOCATION);
            textView_LOCATION.setText(getResources().getString(R.string.LOCATION) + MyArrList.get(position).get("LOCATION"));

            TextView textView_DATE = (TextView) convertView.findViewById(R.id.textView_DATE);
            textView_DATE.setText(getResources().getString(R.string.DATE) + MyArrList.get(position).get("DATE"));

            TextView textView_TIME = (TextView) convertView.findViewById(R.id.textView_TIME);
            textView_TIME.setText(getResources().getString(R.string.TIME) + MyArrList.get(position).get("TIME"));

            TextView textView_STATUS = (TextView) convertView.findViewById(R.id.textView_STATUS);
            textView_STATUS.setText(getResources().getString(R.string.STATUS) + MyArrList.get(position).get("STATUS"));

            TextView textView_SCANBY = (TextView) convertView.findViewById(R.id.textView_SCANBY);
            textView_SCANBY.setText(getResources().getString(R.string.SCAN_BY) + MyArrList.get(position).get("SCANBY"));

            TextView textView_Note = (TextView) convertView.findViewById(R.id.textView_NOTE);
            textView_Note.setText(getResources().getString(R.string.NOTE) + MyArrList.get(position).get("NOTE"));

            GridLayout gridLayout_PhotoAlbum = (GridLayout)convertView.findViewById(R.id.gridLayout_PhotoAlbum);
            gridLayout_PhotoAlbum.removeAllViews();

            Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM HISTORY_IMAGE INNER JOIN HISTORY_ASSET_RECENT ON " +
                    "HISTORY_ASSET_RECENT.HISTORY_RID = HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE HISTORY_ASSET_RECENT.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ MyArrList.get(position).get("ASSETRID") });
            c.moveToFirst();


            if( c.getCount() <= 0 ){

            }else{
                c.moveToFirst();
                do{
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300,300);
                    //layoutParams.setMargins(10,10,10,10);

                    ImageView imageViewAdd = new ImageView(CheckRoomAsset.this);
                    //imageViewAdd.setLayoutParams(layoutParams);
                    Glide
                            .with(context)
                            .load( BASE_URL + c.getString(2) )
                            .override(300,300)
                            .centerCrop()
                            .into(imageViewAdd);

                    final String s = BASE_URL + c.getString(2);

                    imageViewAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(CheckRoomAsset.this);
                            View mView = getLayoutInflater().inflate(R.layout.custom_photo_view, null);
                            PhotoView photoView = (PhotoView) mView.findViewById(R.id.imageView_PhotoZoom);
                            Glide
                                    .with(mView)
                                    .load( s )
                                    .centerCrop()
                                    .into(photoView);
                            //photoView.setImageResource(R.drawable.easyqr_background);
                            mBuilder.setView(mView);
                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();

                        }
                    });

                    gridLayout_PhotoAlbum.addView(imageViewAdd);

                }while(c.moveToNext());
            }

            Button button_History = (Button) convertView.findViewById(R.id.button_History);
            final AlertDialog.Builder adb = new AlertDialog.Builder(CheckRoomAsset.this);
            button_History.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Cursor C_SORT_HISTORY = MainActivity.mydb.rawQuery("SELECT * FROM HISTORY_ASSET WHERE HISTORY_ASSET_ID = ? ORDER BY HISTORY_YEAR DESC,HISTORY_MONTH DESC,HISTORY_DAY DESC,HISTORY_HOUR DESC,HISTORY_MINUTE DESC",
                            new String[]{ MyArrList.get(position).get("ASSETID") });
                    if( (C_SORT_HISTORY != null) && (C_SORT_HISTORY.getCount() > 0) ){
                        C_SORT_HISTORY.moveToFirst();
                        Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                        intent.putExtra("ASSETID", MyArrList.get(position).get("ASSETID"));
                        startActivity(intent);
                        /*
                        int i = 1;
                        StringBuffer buffer = new StringBuffer();
                        do{
                            buffer.append(i + " =-------------------=" + "\n\n");
                            buffer.append("SCAN_BY : " + C_SORT_HISTORY.getString(18) + "\n");
                            buffer.append("DATE : " + C_SORT_HISTORY.getInt(12) + "/" + C_SORT_HISTORY.getInt(13) + "/" + C_SORT_HISTORY.getInt(14) + " " + C_SORT_HISTORY.getInt(15) + ":" + C_SORT_HISTORY.getInt(16) + "\n");
                            buffer.append("LOCATION : " + C_SORT_HISTORY.getInt(8) + "-" + C_SORT_HISTORY.getInt(10) + "-" + C_SORT_HISTORY.getString(11) + " " + C_SORT_HISTORY.getString(9) + "\n");
                            buffer.append("REFER_ID : " + C_SORT_HISTORY.getString(3) + "\n");
                            buffer.append("REFER_NAME : " + C_SORT_HISTORY.getString(4) + "\n");
                            buffer.append("STATUS : " + C_SORT_HISTORY.getString(17) + "\n");
                            buffer.append("NOTE : " + C_SORT_HISTORY.getString(19) + "\n");
                            i++;
                        }while(C_SORT_HISTORY.moveToNext());


                        // Show all data
                        showMessage("ASSET_ID : " + MyArrList.get(position).get("ASSETID"), buffer.toString());
                        //showMessage("ASSET_ID : " + MyArrList.get(position).get("ASSETID"), "");
                        */
                    }
                    else{
                        showMessage("ASSET_ID : " + MyArrList.get(position).get("ASSETID"), "This asset doesn't have any histories.");
                    }
                }
            });

            return convertView;

        }
    }

    public Bitmap returnBitmap(String path){
        Glide.with(CheckRoomAsset.this)
                .asBitmap()
                .load(path)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        returnBitmap = resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        return returnBitmap;
    }

    public void refreshListView(String BUILDING_RID , String FLOOR_RID , String ROOM_RID){

        if(BUILDING_RID == null && FLOOR_RID == null && ROOM_RID == null){
            listView.setAdapter(null);
            listView.setSelection(0);
        }
        else if(BUILDING_RID != null && FLOOR_RID == null && ROOM_RID == null){
            C_HISTORY_ASSET_RECENT = MainActivity.mydb.rawQuery("SELECT * FROM HISTORY_ASSET_RECENT WHERE HISTORY_BUILDING_ID = ?",
                    new String[]{ BUILDING_RID } );
            refreshReadtoArray(C_HISTORY_ASSET_RECENT);
            listView.setAdapter(new CheckRoomAsset.TextAdapter(CheckRoomAsset.this));
            listView.setSelection(0);
        }
        else if(BUILDING_RID != null && FLOOR_RID != null && ROOM_RID == null){
            C_HISTORY_ASSET_RECENT = MainActivity.mydb.rawQuery("SELECT * FROM HISTORY_ASSET_RECENT WHERE HISTORY_BUILDING_ID = ? AND HISTORY_FLOOR_ID = ?",
                    new String[]{ BUILDING_RID , FLOOR_RID } );
            refreshReadtoArray(C_HISTORY_ASSET_RECENT);
            listView.setAdapter(new CheckRoomAsset.TextAdapter(CheckRoomAsset.this));
            listView.setSelection(0);
        }
        else if(BUILDING_RID != null && FLOOR_RID != null && ROOM_RID != null){
            C_HISTORY_ASSET_RECENT = MainActivity.mydb.rawQuery("SELECT * FROM HISTORY_ASSET_RECENT WHERE HISTORY_BUILDING_ID = ? AND HISTORY_FLOOR_ID = ? AND HISTORY_ROOM_ID = ?",
                    new String[]{ BUILDING_RID , FLOOR_RID , ROOM_RID } );
            refreshReadtoArray(C_HISTORY_ASSET_RECENT);
            listView.setAdapter(new CheckRoomAsset.TextAdapter(CheckRoomAsset.this));
            listView.setSelection(0);
        }

    }

    public void refreshReadtoArray(Cursor C_HISTORY_ASSET_RECENT){
        MyArrList = new ArrayList<HashMap<String, String>>();
        C_HISTORY_ASSET_RECENT.moveToFirst();
        if((C_HISTORY_ASSET_RECENT != null) && (C_HISTORY_ASSET_RECENT.getCount() > 0)){
            C_HISTORY_ASSET_RECENT.moveToFirst();
            do{
                hashMap = new HashMap<String, String>();
                hashMap.put( "ASSETID", String.valueOf(C_HISTORY_ASSET_RECENT.getString(0)) );
                hashMap.put( "ASSETNAME", String.valueOf(C_HISTORY_ASSET_RECENT.getString(1)) );
                hashMap.put( "REFERID", String.valueOf(C_HISTORY_ASSET_RECENT.getString(2)));
                hashMap.put( "REFERNAME", String.valueOf(C_HISTORY_ASSET_RECENT.getString(3)));
                hashMap.put( "RECEIVEDATE", String.valueOf(C_HISTORY_ASSET_RECENT.getString(4)));
                hashMap.put( "SPEC", String.valueOf(C_HISTORY_ASSET_RECENT.getString(5)) );
                hashMap.put( "UNITNAME", String.valueOf(C_HISTORY_ASSET_RECENT.getString(6)) );
                hashMap.put( "LOCATION", String.valueOf(C_HISTORY_ASSET_RECENT.getInt(7)) + "-" + String.valueOf(C_HISTORY_ASSET_RECENT.getInt(9)) + "-" + String.valueOf(C_HISTORY_ASSET_RECENT.getString(10)) + " : " + String.valueOf(C_HISTORY_ASSET_RECENT.getString(8)) );
                hashMap.put( "DATE", String.valueOf(C_HISTORY_ASSET_RECENT.getInt(13)) + "-" + String.valueOf(C_HISTORY_ASSET_RECENT.getInt(12)) + "-" + String.valueOf(C_HISTORY_ASSET_RECENT.getInt(11)) );
                hashMap.put( "TIME", String.valueOf(C_HISTORY_ASSET_RECENT.getInt(14)) + " : " + String.valueOf(C_HISTORY_ASSET_RECENT.getInt(15)) );
                hashMap.put( "STATUS", String.valueOf(C_HISTORY_ASSET_RECENT.getString(16)) );
                hashMap.put( "SCANBY", String.valueOf(C_HISTORY_ASSET_RECENT.getString(17)) );
                hashMap.put( "NOTE", String.valueOf(C_HISTORY_ASSET_RECENT.getString(18)) );
                hashMap.put( "ASSETRID", String.valueOf(C_HISTORY_ASSET_RECENT.getInt(19)) );
                MyArrList.add(hashMap);
            }while(C_HISTORY_ASSET_RECENT.moveToNext());
        }
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SCAN_BY_CAMERA ==========================================================================
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this,"Scanning Cancelled",Toast.LENGTH_SHORT).show();
            }
            else {
                Cursor C_LOCATION_BARCODE = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BARCODE = ?",new String[]{ result.getContents().toString() } );
                Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ result.getContents().toString() });
                if( (c != null) && (c.getCount() > 0) ){
                    Toast.makeText(this,"ASSET QR CODE : " + result.getContents().toString() + "\nThis mode can get only Location QR Code.",Toast.LENGTH_SHORT).show();
                }
                else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                    split = C_LOCATION_BARCODE.getString(1).split("-");

                    //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                    refreshListView(null , null,null);
                    //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                    // BUILDING_SPINNER ========================================================================
                    List<String> splist1 = new ArrayList<String>();
                    splist1.add("BUILDING");
                    BUILDING_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_BUILDING_ID FROM LOCATION ORDER BY LOCATION_BUILDING_ID ASC",null);
                    if(BUILDING_COLUMN.getCount() != 0) {
                        while (BUILDING_COLUMN.moveToNext()) {
                            splist1.add( BUILDING_COLUMN.getString(0) );
                        }
                    }
                    adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splist1);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp1 = (Spinner) findViewById(R.id.spinner_building);
                    sp1.setAdapter(adapter1);

                    sp1.setSelection(getIndex(sp1,split[0]));

                    // HIDDEN !!! PRESS + TO SEE MORE !!! BUILDING_LISTENER =======================================================================
                    sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            if(position > 0){
                                BUILDING_RID =  Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                                //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                refreshListView(String.valueOf(BUILDING_RID) , null,null);
                                //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                                // FLOOR_SPINNER ========================================================================
                                List<String> splist2 = new ArrayList<String>();
                                splist2.add("FLOOR");
                                FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? ORDER BY LOCATION_FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                                if (FLOOR_COLUMN.getCount() != 0) {
                                    while (FLOOR_COLUMN.moveToNext()) {
                                        splist2.add(FLOOR_COLUMN.getString(0));
                                    }
                                }
                                adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp2 = (Spinner) findViewById(R.id.spinner_floor);
                                sp2.setAdapter(adapter2);

                                sp2.setSelection(getIndex(sp2,split[1]));

                                // FLOOR_LISTENER ========================================================================
                                sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                        if (position > 0) {
                                            FLOOR_RID = Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                                            //Toast.makeText(CheckRoomAsset.this,"FLOOR_ID : " + FLOOR_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                            //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                            refreshListView(String.valueOf(BUILDING_RID) , String.valueOf(FLOOR_RID),null);
                                            //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();

                                            // ROOM_SPINNER ========================================================================
                                            List<String> splist3 = new ArrayList<String>();
                                            splist3.add("ROOM");
                                            ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? ORDER BY LOCATION_ROOM_ID ASC", new String[]{String.valueOf(BUILDING_RID),String.valueOf(FLOOR_RID)});
                                            if (ROOM_COLUMN.getCount() != 0) {
                                                while (ROOM_COLUMN.moveToNext()) {
                                                    splist3.add(ROOM_COLUMN.getString(0));
                                                }
                                            }
                                            adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            sp3 = (Spinner) findViewById(R.id.spinner_room);
                                            sp3.setAdapter(adapter3);

                                            sp3.setSelection(getIndex(sp3,split[2]));

                                            // ROOM_LISTENER ========================================================================
                                            sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                    if (position > 0) {
                                                        ROOM_RID = adapterView.getItemAtPosition(position).toString();
                                                        //Toast.makeText(CheckRoomAsset.this,"BUILDING_ID : " + BUILDING_RID + "\nFLOOR_ID : " + FLOOR_RID + "\nROOM_ID : " + ROOM_RID,Toast.LENGTH_SHORT).show();

                                                        //REFRESH_LISTVIEW =!=!=!=!=!=!=!
                                                        refreshListView(String.valueOf(BUILDING_RID) , String.valueOf(FLOOR_RID),ROOM_RID);
                                                        //Toast.makeText(CheckRoomAsset.this,"REFRESH LISTVIEW 1",Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // IN CASE OF SELECTED 0 ROOM REFRESH NOTHING
                                                        ROOM_RID = "-1";
                                                        //Toast.makeText(CheckRoomAsset.this, "ROOM_RID : " + ROOM_RID +
                                                                //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {
                                                }
                                            });
                                        }else if(position == 0){
                                            //REFRESH_LISTVIEW333 =!=!=!=!=!=!=!
                                            refreshListView(String.valueOf(BUILDING_RID), null, null);

                                            // ROOM_SPINNER ========================================================================
                                            List<String> splist3 = new ArrayList<String>();
                                            splist3.add("ROOM");
                                            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                            sp3.setAdapter(adapter3);

                                        } else {
                                            // IN CASE OF SELECTED 0 FLOOR REFRESH ROOM_SPINNER
                                            FLOOR_RID = -1;
                                            //Toast.makeText(CheckRoomAsset.this, "FLOOR_RID : " + FLOOR_RID +
                                                    //" FLOOR_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                            ROOM_RID = "-1";

                                            // ROOM_SPINNER ========================================================================
                                            List<String> splist3 = new ArrayList<String>();
                                            splist3.add("ROOM");
                                            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                            sp3.setAdapter(adapter3);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {
                                    }

                                });

                            }
                            else{
                                // IN CASE OF SELECTED 0 BUILDING REFRESH FLOOR_SPINNER AND ROOM_SPINNER
                                BUILDING_RID = -1;
                                //Toast.makeText(CheckRoomAsset.this,"BUILDING_RID : " + BUILDING_RID +
                                        //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                FLOOR_RID = -1;
                                // FLOOR_SPINNER ========================================================================
                                List<String> splist2 = new ArrayList<String>();
                                splist2.add("FLOOR");
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist2);
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                                sp2.setAdapter(adapter2);

                                ROOM_RID = "-1";
                                // ROOM_SPINNER ========================================================================
                                List<String> splist3 = new ArrayList<String>();
                                splist3.add("ROOM");
                                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(CheckRoomAsset.this, android.R.layout.simple_spinner_item, splist3);
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                sp3.setAdapter(adapter3);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    //Toast.makeText(this,C_LOCATION_BARCODE.getString(1) + "\n1 = " + split[0] + "\n2 = " + split[1] + "\n3 = " + split[2],Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this,C_LOCATION_BARCODE.getString(1),Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(this, "UNKNOWN QR CODE : " + result.getContents().toString(), Toast.LENGTH_LONG).show();
                }
            }
            //showMessage("YES", "DATA ADDED");
            //listView.setSelection(listView.getCount() - 1);
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
        // SCAN_FROM_IMAGE =========================================================================
    }
}
