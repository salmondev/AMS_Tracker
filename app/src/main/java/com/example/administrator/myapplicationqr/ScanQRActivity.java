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
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
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
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanQRActivity extends AppCompatActivity {

    private String BASE_URL;

    Cursor c;
    ArrayList<HashMap<String, String>> MyArrList;
    HashMap<String, String> hashMap;
    ListView listView;
    ScrollView nestedScrollView;

    //EditText editText_Note;

    Spinner sp1,sp2,sp3;
    ArrayAdapter<String> adapter1,adapter2,adapter3;
    String[] split;
    String BUILDING_NAME;
    int BUILDING_RID,FLOOR_RID;
    String ROOM_RID;

    Cursor BUILDING_COLUMN,FLOOR_COLUMN,ROOM_COLUMN;
    Cursor C_BUILDING_RID,C_FLOOR_RID,C_ROOM_RID,C_LOCATION;

    Cursor STATUS,OWNER,INDOORMAP;

    String HISTORY_ASSET_ID_PHOTO;
    int HISTORY_ASSET_RID_PHOTO;

    NfcAdapter nfcAdapter;
    Boolean nfc_status = false;

    private File output=null;

    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Toast.makeText(ScanQRActivity.this,"Login Success !" + "\nUsername : " + LoginActivity.globalUsername + "\nRefer ID : " + LoginActivity.globalReferID + "\nRefer Name : " + LoginActivity.globalRefername +  "\nPassword : " + LoginActivity.globalPassword
                //,Toast.LENGTH_LONG).show();
        //editText_Note = (EditText) findViewById(R.id.editText_Note);

        listView = (ListView)findViewById(R.id.listview_1);

        MainActivity.mydb.onDeleteTable("TEMP_HISTORY_ASSET");
        MainActivity.mydb.onDeleteTable("TEMP_HISTORY_IMAGE");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
            nfc_status = true;
        }else{
            Toast.makeText(this, "NFC not available :( ", Toast.LENGTH_LONG).show();
            nfc_status = false;
        }

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
                    //C_BUILDING_RID = MainActivity.mydb.rawQuery("SELECT * FROM BUILDING WHERE BUILDING_NAME = ?",
                            //new String[] {adapterView.getItemAtPosition(position).toString()});
                    //BUILDING_RID =  C_BUILDING_RID.getInt(0);
                    BUILDING_RID =  Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                    //Toast.makeText(ScanQRActivity.this,"BUILDING_ID : " + BUILDING_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ScanQRActivity.this,"BUILDING_ID : " + C_BUILDING_RID.getInt(0) +
                            //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                        // FLOOR_SPINNER ========================================================================
                        List<String> splist2 = new ArrayList<String>();
                        splist2.add("FLOOR");
                        //FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM FLOOR WHERE BUILDING_ID = ? ORDER BY FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                        //SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ?
                        FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? ORDER BY LOCATION_FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                        if (FLOOR_COLUMN.getCount() != 0) {
                            while (FLOOR_COLUMN.moveToNext()) {
                                splist2.add(FLOOR_COLUMN.getString(0));
                            }
                        }
                        //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
                        adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                        sp2 = (Spinner) findViewById(R.id.spinner_floor);
                        sp2.setAdapter(adapter2);

                        //sp2.setSelection(getIndex(sp2,"6"));

                        // FLOOR_LISTENER ========================================================================
                        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                if (position > 0) {
                                    //C_FLOOR_RID = MainActivity.mydb.rawQuery("SELECT * FROM FLOOR WHERE FLOOR_ID = ? AND BUILDING_ID = ?",
                                            //new String[]{adapterView.getItemAtPosition(position).toString(), String.valueOf(BUILDING_RID)});
                                    //FLOOR_RID = C_FLOOR_RID.getInt(0);
                                    FLOOR_RID = Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                                    //Toast.makeText(ScanQRActivity.this,"FLOOR_ID : " + FLOOR_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(ScanQRActivity.this, "FLOOR_RID : " + C_FLOOR_RID.getInt(0) +
                                            //" FLOOR_ID : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    //ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM ROOM WHERE FLOOR_RID = ? ORDER BY ROOM_ID ASC", new String[]{String.valueOf(FLOOR_RID)});
                                    //SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOC_FLOOR_ID = ?
                                    ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? ORDER BY LOCATION_ROOM_ID ASC", new String[]{String.valueOf(BUILDING_RID),String.valueOf(FLOOR_RID)});
                                    if (ROOM_COLUMN.getCount() != 0) {
                                        while (ROOM_COLUMN.moveToNext()) {
                                            splist3.add(ROOM_COLUMN.getString(0));
                                        }
                                    }
                                    //ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
                                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3 = (Spinner) findViewById(R.id.spinner_room);
                                    sp3.setAdapter(adapter3);

                                    // ROOM_LISTENER ========================================================================
                                    sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                            if (position > 0) {
                                                //C_ROOM_RID = MainActivity.mydb.rawQuery("SELECT * FROM ROOM WHERE ROOM_ID = ? AND FLOOR_RID = ?",
                                                        //new String[]{adapterView.getItemAtPosition(position).toString(), String.valueOf(FLOOR_RID)});
                                                //ROOM_RID = C_ROOM_RID.getInt(0);
                                                //ROOM_RID = Integer.parseInt(adapterView.getItemAtPosition(position).toString());
                                                ROOM_RID = adapterView.getItemAtPosition(position).toString();
                                                //Toast.makeText(ScanQRActivity.this,"ROOM_ID : " + ROOM_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                                //Toast.makeText(ScanQRActivity.this,"BUILDING_ID : " + BUILDING_RID + "\nFLOOR_ID : " + FLOOR_RID + "\nROOM_ID : " + ROOM_RID,Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(ScanQRActivity.this, "ROOM_RID : " + C_ROOM_RID.getInt(0) +
                                                        //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                            } else {
                                                // IN CASE OF SELECTED 0 ROOM REFRESH NOTHING
                                                ROOM_RID = "-1";
                                                //Toast.makeText(ScanQRActivity.this, "ROOM_RID : " + ROOM_RID +
                                                        //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                } else {
                                    // IN CASE OF SELECTED 0 FLOOR REFRESH ROOM_SPINNER
                                    FLOOR_RID = -1;
                                    //Toast.makeText(ScanQRActivity.this, "FLOOR_RID : " + FLOOR_RID +
                                            //" FLOOR_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                    ROOM_RID = "-1";

                                    // ROOM_SPINNER ========================================================================
                                    List<String> splist3 = new ArrayList<String>();
                                    splist3.add("ROOM");
                                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
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
                    //Toast.makeText(ScanQRActivity.this,"BUILDING_RID : " + BUILDING_RID +
                            //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                    FLOOR_RID = -1;
                    // FLOOR_SPINNER ========================================================================
                    List<String> splist2 = new ArrayList<String>();
                    splist2.add("FLOOR");
                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                    sp2.setAdapter(adapter2);

                    ROOM_RID = "-1";
                    // ROOM_SPINNER ========================================================================
                    List<String> splist3 = new ArrayList<String>();
                    splist3.add("ROOM");
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                    sp3.setAdapter(adapter3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
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

    @Override
    public void onBackPressed() {
        if( MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_ASSET").getCount() != 0){
            final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(ScanQRActivity.this);
            adb.setTitle("Confirm Back");
            adb.setMessage("Your scanned data will be deleted, do you want to go back to main menu ?");
            adb.setNegativeButton("No", new android.support.v7.app.AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }});
            adb.setPositiveButton("Yes", new android.support.v7.app.AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ScanQRActivity.super.onBackPressed();
                }});
            adb.show();
        }else{
            ScanQRActivity.super.onBackPressed();
        }
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
    public void onSubmit(View v){
        if(BUILDING_RID != -1 && FLOOR_RID != -1 && ROOM_RID != "-1" && MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_ASSET").getCount() != 0){
            final AlertDialog.Builder adb = new AlertDialog.Builder(ScanQRActivity.this);
            adb.setTitle("Confirm Submit");
            adb.setMessage("Are you sure to submit ?");
            adb.setNegativeButton("No", null);
            adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    C_LOCATION = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? AND LOCATION_ROOM_ID = ?",
                            new String[] {String.valueOf(BUILDING_RID),String.valueOf(FLOOR_RID),String.valueOf(ROOM_RID)} );

                    //Toast.makeText(ScanQRActivity.this, "BUILDING_ID = " + BUILDING_RID + "\nFLOOR_ID = " + FLOOR_RID + "\nROOM_ID = " + ROOM_RID + "\nC_LOCATION_NAME = " + C_LOCATION.getString(3), Toast.LENGTH_SHORT).show();

                    MainActivity.mydb.UpdaterawQueryWithoutMoveToFirst("UPDATE TEMP_HISTORY_ASSET SET" +
                            " HISTORY_BUILDING_ID = " + BUILDING_RID + "," +
                            " HISTORY_BUILDING_NAME = '" + C_LOCATION.getString(3) + "'," +
                            " HISTORY_FLOOR_ID = " + FLOOR_RID + "," +
                            " HISTORY_ROOM_ID = '" + ROOM_RID + "'",null);

                    boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                        new Send().execute();
                        //sendData();
                        //finish();
                        //Intent intent = new Intent(ScanQRActivity.this,MainActivity.class);
                        //startActivity(intent);
                    }
                    else{
                        connected = false;
                        final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(ScanQRActivity.this);
                        adb.setTitle("Your internet is not available");
                        adb.setMessage("Do you want store it and send data manaully via Sync Data Menu ? or Cancel all data ?");
                        adb.setNegativeButton("Cancel All Data", new android.support.v7.app.AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.mydb.onDeleteTable("TEMP_HISTORY_ASSET");
                                Toast.makeText(ScanQRActivity.this, "SCANNED DATA has been deleted !", Toast.LENGTH_SHORT).show();
                                finish();
                                //Intent intent = new Intent(ScanQRActivity.this,MainActivity.class);
                                //startActivity(intent);
                            }});
                        adb.setPositiveButton("Store Data", new android.support.v7.app.AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Cursor C_TEMP_HISTORY_ASSET = MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_ASSET");
                                if (C_TEMP_HISTORY_ASSET.getCount() != 0) {
                                    while (C_TEMP_HISTORY_ASSET.moveToNext()) {
                                        // Add your data
                                        MainActivity.mydb.onInsert_STORE_HISTORY_ASSET(
                                                C_TEMP_HISTORY_ASSET.getInt(0),
                                                C_TEMP_HISTORY_ASSET.getString(1),
                                                C_TEMP_HISTORY_ASSET.getString(2),
                                                C_TEMP_HISTORY_ASSET.getString(3),
                                                C_TEMP_HISTORY_ASSET.getString(4),
                                                C_TEMP_HISTORY_ASSET.getString(5),
                                                C_TEMP_HISTORY_ASSET.getString(6),
                                                C_TEMP_HISTORY_ASSET.getString(7),
                                                C_TEMP_HISTORY_ASSET.getInt(8),
                                                C_TEMP_HISTORY_ASSET.getString(9),
                                                C_TEMP_HISTORY_ASSET.getInt(10),
                                                C_TEMP_HISTORY_ASSET.getString(11),
                                                C_TEMP_HISTORY_ASSET.getInt(12),
                                                C_TEMP_HISTORY_ASSET.getInt(13),
                                                C_TEMP_HISTORY_ASSET.getInt(14),
                                                C_TEMP_HISTORY_ASSET.getInt(15),
                                                C_TEMP_HISTORY_ASSET.getInt(16),
                                                C_TEMP_HISTORY_ASSET.getString(17),
                                                C_TEMP_HISTORY_ASSET.getString(18),
                                                C_TEMP_HISTORY_ASSET.getString(19)
                                        );

                                    }
                                }

                                Cursor C_TEMP_HISTORY_IMAGE = MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_IMAGE");
                                if (C_TEMP_HISTORY_IMAGE.getCount() != 0) {
                                    while (C_TEMP_HISTORY_IMAGE.moveToNext()) {
                                        // Add your data
                                        MainActivity.mydb.onInsert_STORE_HISTORY_IMAGE(
                                                C_TEMP_HISTORY_IMAGE.getInt(0),
                                                C_TEMP_HISTORY_IMAGE.getInt(1),
                                                C_TEMP_HISTORY_IMAGE.getBlob(2)
                                        );

                                    }
                                }
                                Toast.makeText(ScanQRActivity.this, "SCANNED DATA has been stored !", Toast.LENGTH_SHORT).show();
                                //Intent intent = new Intent(ScanQRActivity.this,MainActivity.class);
                                //startActivity(intent);
                                finish();
                            }});
                        adb.show();


                    }
                }});
            adb.show();
        }
        else if(BUILDING_RID == -1){
            Toast.makeText(this, "PLEASE SELECT BUILDING , FLOOR , ROOM.", Toast.LENGTH_SHORT).show();
        }
        else if(BUILDING_RID != -1 && FLOOR_RID == -1){
            Toast.makeText(this, "PLEASE SELECT FLOOR , ROOM.", Toast.LENGTH_SHORT).show();
        }
        else if(BUILDING_RID != -1 && FLOOR_RID != -1 && ROOM_RID == "-1"){
            Toast.makeText(this, "PLEASE SELECT ROOM.", Toast.LENGTH_SHORT).show();
        }
        else if(MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_ASSET").getCount() == 0){
            Toast.makeText(this, "PLEASE SCAN AT LEAST ONE ITEM.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "PLEASE SELECT BUILDING , FLOOR , ROOM.", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,ScanQRActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilter,null);

    }
    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "onNewIntent Method", Toast.LENGTH_SHORT).show(); //TOAST
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

        Toast.makeText(this, "Your data : \n" + result, Toast.LENGTH_LONG).show(); //TOAST

        //result = "412000010005-30406-00003";

        if(result != null){

                Cursor C_LOCATION_BARCODE = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BARCODE = ?",new String[]{ result } );
                Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ result });
                if( (c != null) && (c.getCount() > 0) ){
                    //IF Get the QR CODE and IT IS ASSET===============================================================
                    Cursor check = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM TEMP_HISTORY_ASSET WHERE HISTORY_ASSET_ID = ?",new String[]{ result });
                    CurrentTime currentTime = new CurrentTime();
                    // IF NOT DUPLICATE IN LIST ROW -> ADD LIST ROW
                    if(check.getCount() == 0){
                        Cursor C_STATUS = MainActivity.mydb.rawQuery("SELECT * FROM STATUS WHERE STATUS_ID = ?",new String[]{ String.valueOf(1) } );
                        Cursor C_ASSET = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ result } );
                        Cursor C_REFER = MainActivity.mydb.rawQuery("SELECT * FROM REFER WHERE REFERID = ?",new String[]{ C_ASSET.getString(2) } );
                        MainActivity.mydb.onInsert_TEMP_HISTORY_ASSET(-1,C_ASSET.getString(1),C_ASSET.getString(3),
                                C_REFER.getString(0),C_REFER.getString(1),C_ASSET.getString(4),
                                C_ASSET.getString(5),C_ASSET.getString(6),-1,
                                null,-1,null,
                                currentTime.getDay(),currentTime.getMonth(),currentTime.getYear(),
                                currentTime.getHour(),currentTime.getMin(),C_STATUS.getString(1),
                                LoginActivity.globalRefername,"");

                        refreshListView();
                        listView.setSelection(listView.getCount() - 1);
                        Toast.makeText(this,"Data not exist -> Added Data",Toast.LENGTH_SHORT).show();
                    }
                    // IF DUPLICATE IN LIST ROW -> UPDATE THAT LIST ROW
                    else{
                        MainActivity.mydb.onUpdate_TEMP_TABLE_TABLE(-1,result,null,
                                null,null,null,
                                null,null,
                                -1,null,-1,null,
                                currentTime.getDay(),currentTime.getMonth(),currentTime.getYear(),currentTime.getHour(),currentTime.getMin(),
                                null,null);
                        refreshListView();
                        listView.setSelection(0);
                        Toast.makeText(this,"Data exist -> Updated Data",Toast.LENGTH_SHORT).show();
                    }
                    //REFRESH_ADAPTER ==================================================================
                }
                else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                    split = C_LOCATION_BARCODE.getString(1).split("-");

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
                                //Toast.makeText(ScanQRActivity.this,"BUILDING_ID : " + BUILDING_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                // FLOOR_SPINNER ========================================================================
                                List<String> splist2 = new ArrayList<String>();
                                splist2.add("FLOOR");
                                FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? ORDER BY LOCATION_FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                                if (FLOOR_COLUMN.getCount() != 0) {
                                    while (FLOOR_COLUMN.moveToNext()) {
                                        splist2.add(FLOOR_COLUMN.getString(0));
                                    }
                                }
                                adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
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
                                            //Toast.makeText(ScanQRActivity.this,"FLOOR_ID : " + FLOOR_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                            // ROOM_SPINNER ========================================================================
                                            List<String> splist3 = new ArrayList<String>();
                                            splist3.add("ROOM");
                                            ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? ORDER BY LOCATION_ROOM_ID ASC", new String[]{String.valueOf(BUILDING_RID),String.valueOf(FLOOR_RID)});
                                            if (ROOM_COLUMN.getCount() != 0) {
                                                while (ROOM_COLUMN.moveToNext()) {
                                                    splist3.add(ROOM_COLUMN.getString(0));
                                                }
                                            }
                                            adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
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
                                                        //Toast.makeText(ScanQRActivity.this,"BUILDING_ID : " + BUILDING_RID + "\nFLOOR_ID : " + FLOOR_RID + "\nROOM_ID : " + ROOM_RID,Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // IN CASE OF SELECTED 0 ROOM REFRESH NOTHING
                                                        ROOM_RID = "-1";
                                                        //Toast.makeText(ScanQRActivity.this, "ROOM_RID : " + ROOM_RID +
                                                        //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });

                                        } else {
                                            // IN CASE OF SELECTED 0 FLOOR REFRESH ROOM_SPINNER
                                            FLOOR_RID = -1;
                                            //Toast.makeText(ScanQRActivity.this, "FLOOR_RID : " + FLOOR_RID +
                                            //" FLOOR_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                            ROOM_RID = "-1";

                                            // ROOM_SPINNER ========================================================================
                                            List<String> splist3 = new ArrayList<String>();
                                            splist3.add("ROOM");
                                            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
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
                                //Toast.makeText(ScanQRActivity.this,"BUILDING_RID : " + BUILDING_RID +
                                //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                FLOOR_RID = -1;
                                // FLOOR_SPINNER ========================================================================
                                List<String> splist2 = new ArrayList<String>();
                                splist2.add("FLOOR");
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                                sp2.setAdapter(adapter2);

                                ROOM_RID = "-1";
                                // ROOM_SPINNER ========================================================================
                                List<String> splist3 = new ArrayList<String>();
                                splist3.add("ROOM");
                                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                sp3.setAdapter(adapter3);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) { }
                    });

                    Toast.makeText(this,C_LOCATION_BARCODE.getString(1) + "\n1 = " + split[0] + "\n2 = " + split[1] + "\n3 = " + split[2],Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SCAN_BY_CAMERA ==========================================================================
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this,"Scanning Cancelled",Toast.LENGTH_SHORT).show();
            }
            else {
                //String[] split = result.getContents().toString().split("-");
                //Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE ASSETID = ?",new String[]{split[0]});
                Cursor C_LOCATION_BARCODE = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BARCODE = ?",new String[]{ result.getContents().toString() } );
                Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ result.getContents().toString() });
                if( (c != null) && (c.getCount() > 0) ){
                    //IF Get the QR CODE and IT IS ASSET===============================================================
                    Cursor check = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM TEMP_HISTORY_ASSET WHERE HISTORY_ASSET_ID = ?",new String[]{ result.getContents().toString() });
                    CurrentTime currentTime = new CurrentTime();
                    // IF NOT DUPLICATE IN LIST ROW -> ADD LIST ROW
                    if(check.getCount() == 0){
                        /* //BUG
                        MainActivity.mydb.onInsert_TEMP_HISTORY_ASSET(split[0],null,"ปกติ",
                                null,-1,-1,
                                -1,-1,currentTime.getHour(),currentTime.getMin(),currentTime.getDay(),currentTime.getMonth(),currentTime.getYear(),
                                -1,-1,-1,
                                c.getString(1),c.getString(2),null,null,
                                null );
                                */
                        //NEED RAW QUERY AND MOVE CURSOR TO FIRST BECAUSE IT HAS ONLY ONE RECORD
                        Cursor C_STATUS = MainActivity.mydb.rawQuery("SELECT * FROM STATUS WHERE STATUS_ID = ?",new String[]{ String.valueOf(1) } );
                        //Toast.makeText(this,"STATUS : " + C_STATUS.getString(1),Toast.LENGTH_SHORT).show();
                        /*
                        if(C_STATUS.getCount() == 0){
                            Toast.makeText(this,"0",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(this,"!= 0",Toast.LENGTH_SHORT).show();
                            Toast.makeText(this,"STATUS : " + C_STATUS.getString(1),Toast.LENGTH_SHORT).show();
                        }
                        */
                        //Toast.makeText(this,"STATUS : " + C_STATUS.getInt(0),Toast.LENGTH_SHORT).show();


                        Cursor C_ASSET = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ result.getContents().toString() } );
                        /*
                        Toast.makeText(this,"ASSETID : " + C_ASSET.getString(0) +
                                "\nBARCODE : " + C_ASSET.getString(1) +
                                "\nASSETNAME : " + C_ASSET.getString(2) +
                                "\nSPEC : " + C_ASSET.getString(3) +
                                "\nRECEIVEDATE : " + C_ASSET.getString(4) +
                                "\nUNITNAME : " + C_ASSET.getString(5) +
                                "\nREFERIDITEM : " + C_ASSET.getString(6),Toast.LENGTH_SHORT).show();
                                */
                        Cursor C_REFER = MainActivity.mydb.rawQuery("SELECT * FROM REFER WHERE REFERID = ?",new String[]{ C_ASSET.getString(2) } );
                        /*
                        Toast.makeText(this,"REFERID : " + C_REFER.getString(0) +
                                "\nREFERNAME : " + C_REFER.getString(1) +
                                "\nDEPARTMENTID : " + C_REFER.getInt(2),Toast.LENGTH_SHORT).show();
                                */
                        //EDIT 14-4-2562
                        MainActivity.mydb.onInsert_TEMP_HISTORY_ASSET(-1,C_ASSET.getString(1),C_ASSET.getString(3),
                                C_REFER.getString(0),C_REFER.getString(1),C_ASSET.getString(4),
                                C_ASSET.getString(5),C_ASSET.getString(6),-1,
                                null,-1,null,
                                currentTime.getDay(),currentTime.getMonth(),currentTime.getYear(),
                                currentTime.getHour(),currentTime.getMin(),C_STATUS.getString(1),
                                LoginActivity.globalRefername,"");

                        refreshListView();
                        listView.setSelection(listView.getCount() - 1);
                        Toast.makeText(this,"Data not exist -> Added Data",Toast.LENGTH_SHORT).show();
                    }
                    // IF DUPLICATE IN LIST ROW -> UPDATE THAT LIST ROW
                    else{
                        /*
                        MainActivity.mydb.onUpdate_TEMP_TABLE_TABLE(split[0],null,null,
                                null,-1,-1,
                                -1,-1,currentTime.getHour(),currentTime.getMin(),currentTime.getDay(),currentTime.getMonth(),currentTime.getYear(),
                                -1,-1,-1);
                                */

                        MainActivity.mydb.onUpdate_TEMP_TABLE_TABLE(-1,result.getContents().toString(),null,
                                null,null,null,
                                null,null,
                                -1,null,-1,null,
                                currentTime.getDay(),currentTime.getMonth(),currentTime.getYear(),currentTime.getHour(),currentTime.getMin(),
                                null,null);
                        refreshListView();
                        listView.setSelection(0);
                        Toast.makeText(this,"Data exist -> Updated Data",Toast.LENGTH_SHORT).show();
                    }
                    //REFRESH_ADAPTER ==================================================================
                }
                else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                    split = C_LOCATION_BARCODE.getString(1).split("-");

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
                                //Toast.makeText(ScanQRActivity.this,"BUILDING_ID : " + BUILDING_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                // FLOOR_SPINNER ========================================================================
                                List<String> splist2 = new ArrayList<String>();
                                splist2.add("FLOOR");
                                FLOOR_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_FLOOR_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? ORDER BY LOCATION_FLOOR_ID ASC", new String[]{String.valueOf(BUILDING_RID)});
                                if (FLOOR_COLUMN.getCount() != 0) {
                                    while (FLOOR_COLUMN.moveToNext()) {
                                        splist2.add(FLOOR_COLUMN.getString(0));
                                    }
                                }
                                adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
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
                                            //Toast.makeText(ScanQRActivity.this,"FLOOR_ID : " + FLOOR_RID + " POSITION_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                            // ROOM_SPINNER ========================================================================
                                            List<String> splist3 = new ArrayList<String>();
                                            splist3.add("ROOM");
                                            ROOM_COLUMN = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT DISTINCT LOCATION_ROOM_ID FROM LOCATION WHERE LOCATION_BUILDING_ID = ? AND LOCATION_FLOOR_ID = ? ORDER BY LOCATION_ROOM_ID ASC", new String[]{String.valueOf(BUILDING_RID),String.valueOf(FLOOR_RID)});
                                            if (ROOM_COLUMN.getCount() != 0) {
                                                while (ROOM_COLUMN.moveToNext()) {
                                                    splist3.add(ROOM_COLUMN.getString(0));
                                                }
                                            }
                                            adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
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
                                                        //Toast.makeText(ScanQRActivity.this,"BUILDING_ID : " + BUILDING_RID + "\nFLOOR_ID : " + FLOOR_RID + "\nROOM_ID : " + ROOM_RID,Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // IN CASE OF SELECTED 0 ROOM REFRESH NOTHING
                                                        ROOM_RID = "-1";
                                                        //Toast.makeText(ScanQRActivity.this, "ROOM_RID : " + ROOM_RID +
                                                                //" ROOM_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });

                                        } else {
                                            // IN CASE OF SELECTED 0 FLOOR REFRESH ROOM_SPINNER
                                            FLOOR_RID = -1;
                                            //Toast.makeText(ScanQRActivity.this, "FLOOR_RID : " + FLOOR_RID +
                                                    //" FLOOR_NUMBER : " + adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                                            ROOM_RID = "-1";

                                            // ROOM_SPINNER ========================================================================
                                            List<String> splist3 = new ArrayList<String>();
                                            splist3.add("ROOM");
                                            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
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
                                //Toast.makeText(ScanQRActivity.this,"BUILDING_RID : " + BUILDING_RID +
                                        //" BUILDING_NAME : " + adapterView.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                                FLOOR_RID = -1;
                                // FLOOR_SPINNER ========================================================================
                                List<String> splist2 = new ArrayList<String>();
                                splist2.add("FLOOR");
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist2);
                                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Spinner sp2 = (Spinner) findViewById(R.id.spinner_floor);
                                sp2.setAdapter(adapter2);

                                ROOM_RID = "-1";
                                // ROOM_SPINNER ========================================================================
                                List<String> splist3 = new ArrayList<String>();
                                splist3.add("ROOM");
                                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist3);
                                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                Spinner sp3 = (Spinner) findViewById(R.id.spinner_room);
                                sp3.setAdapter(adapter3);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) { }
                    });

                    Toast.makeText(this,C_LOCATION_BARCODE.getString(1) + "\n1 = " + split[0] + "\n2 = " + split[1] + "\n3 = " + split[2],Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this,C_LOCATION_BARCODE.getString(1),Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(this, "UNKNOWN QR CODE : " + result.getContents().toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
        // SCAN_FROM_IMAGE =========================================================================

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {


            try {
                //FileInputStream fileInputStream = new FileInputStream(currentPhotoPath);
                //Bitmap fullpicture_bitmap = (Bitmap) BitmapFactory.decodeFile(currentPhotoPath);

                Matrix matrix = new Matrix();

                matrix.postRotate(90);

                Bitmap scaledBitmap = (Bitmap) Bitmap.createScaledBitmap(BitmapFactory.decodeFile(currentPhotoPath), 624, 612, true);
                //Bitmap scaledBitmap = (Bitmap) Bitmap.createScaledBitmap(BitmapFactory.decodeFile(currentPhotoPath), 408, 306, true);
                //Bitmap scaledBitmap = (Bitmap) Bitmap.createScaledBitmap(BitmapFactory.decodeFile(currentPhotoPath), 204, 153, true);

                Bitmap rotatedBitmap = (Bitmap) Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);


                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                //rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

                byte[] image = bos.toByteArray();

                //String encoded_string = android.util.Base64.encodeToString(image,0);
                //Toast.makeText(this, "HISTORY_RID : " + HISTORY_ASSET_RID_PHOTO + "\nHISTORY_ASSET_ID : " + HISTORY_ASSET_ID_PHOTO, Toast.LENGTH_LONG).show();
                /*
                FileInputStream fileInputStream = new FileInputStream(currentPhotoPath);
                byte[] image = new byte[fileInputStream.available()];
                */
                MainActivity.mydb.onInsert_TEMP_HISTORY_IMAGE(-1,HISTORY_ASSET_RID_PHOTO,image);
                //fileInputStream.read(image);
                //MainActivity.mydb.onUpdate_HISTORY_PHOTO("TEMP_HISTORY_ASSET",HISTORY_ASSET_ID_PHOTO, image );
                //fileInputStream.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }





            Toast.makeText(this, "IMAGE CAPTURED !", Toast.LENGTH_LONG).show();
            refreshListView();
        }
    }

    public class TextAdapter extends BaseAdapter
    {

        String mSelected;
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
                convertView = inflater.inflate(R.layout.list_row, null);

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

            Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT HISTORY_IMAGE_RID,HISTORY_IMAGE FROM TEMP_HISTORY_IMAGE INNER JOIN TEMP_HISTORY_ASSET ON " +
                    "TEMP_HISTORY_ASSET.HISTORY_RID = TEMP_HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE TEMP_HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ MyArrList.get(position).get("ASSETRID") });

            c.moveToFirst();
            if( ( c.getCount() <= 0) ){
                //Toast.makeText(ScanQRActivity.this, "Image NULL", Toast.LENGTH_LONG).show();
            }else{
                //Toast.makeText(ScanQRActivity.this, "Image NOT NULL", Toast.LENGTH_LONG).show();
                c.moveToFirst();
                do{

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300,300);
                    layoutParams.setMargins(10,10,10,10);

                    ImageView imageViewAdd = new ImageView(ScanQRActivity.this);
                    imageViewAdd.setLayoutParams(layoutParams);

                    final byte[] byteArray = c.getBlob(1);
                    Bitmap resizedBitmap = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                    //Bitmap resizedBitmap = (Bitmap) Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length), 30, 40, true);
                    imageViewAdd.setImageBitmap(resizedBitmap);
                    imageViewAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                    Bitmap bitmaptest2 = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScanQRActivity.this);
                                    View mView = getLayoutInflater().inflate(R.layout.custom_photo_view, null);
                                    PhotoView photoView = (PhotoView) mView.findViewById(R.id.imageView_PhotoZoom);
                                    photoView.setImageBitmap(bitmaptest2);
                                    //photoView.setImageResource(R.drawable.easyqr_background);
                                    mBuilder.setView(mView);
                                    AlertDialog mDialog = mBuilder.create();
                                    mDialog.show();


                            }
                        });

/*
                        final String s = c.getString(2);

                        imageViewAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScanQRActivity.this);
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
*/
                    gridLayout_PhotoAlbum.addView(imageViewAdd);

                }while(c.moveToNext());
            }
            //ImageView imageView_Photo = (ImageView) convertView.findViewById(R.id.imageView_Photo);
            //imageView_Photo.setImageResource(0);
/*
            Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM HISTORY_IMAGE INNER JOIN HISTORY_ASSET ON " +
            "HISTORY_ASSET.HISTORY_RID = HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE TEMP_HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ MyArrList.get(position).get("ASSETID") });
            */
/*
            c.moveToFirst();
            //(c != null) && (c.getCount() > 0)
            if( (c.getBlob(20) != null) &&  (c.getCount() > 0) ){
                byte[] byteArray = c.getBlob(20);
                //Bitmap bitmaptest = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                Bitmap resizedBitmap = (Bitmap) Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length), 30, 40, true);
                imageView_Photo.setImageBitmap(resizedBitmap);
                imageView_Photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM TEMP_HISTORY_ASSET WHERE HISTORY_ASSET_ID = ?", new String[]{ MyArrList.get(position).get("ASSETID") } );
                        c.moveToFirst();
                        if( (c.getBlob(20) != null) &&  (c.getCount() > 0) ){
                            c.moveToFirst();
                            byte[] byteArray = c.getBlob(20);
                            Bitmap bitmaptest2 = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ScanQRActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.custom_photo_view, null);
                            PhotoView photoView = (PhotoView) mView.findViewById(R.id.imageView_PhotoZoom);
                            photoView.setImageBitmap(bitmaptest2);
                            //photoView.setImageResource(R.drawable.easyqr_background);
                            mBuilder.setView(mView);
                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();
                        }

                    }
                });

            }
*/

            // STATUS_SPINNER ========================================================================
            List<String> splist1 = new ArrayList<String>();
            splist1.add("STATUS");
            STATUS = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM STATUS ORDER BY STATUS_ID ASC",null);
            if(STATUS.getCount() != 0) {
                while (STATUS.moveToNext()) {
                    splist1.add( STATUS.getString(1) );
                }
            }
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(ScanQRActivity.this, android.R.layout.simple_spinner_item, splist1);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner sp1 = (Spinner) convertView.findViewById(R.id.spinner_Status);
            sp1.setAdapter(adapter1);
            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int positionSpinner, long id) {
                    if(positionSpinner > 0){
                        MainActivity.mydb.UpdaterawQueryWithoutMoveToFirst("UPDATE TEMP_HISTORY_ASSET SET" +
                                " HISTORY_STATUS_NAME = '" + adapterView.getItemAtPosition(positionSpinner).toString() + "'" +
                                " WHERE HISTORY_ASSET_ID = ?"
                                ,new String[]{ MyArrList.get(position).get("ASSETID") });

                        refreshListView();
                        listView.setSelection(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            Button button_Note = (Button) convertView.findViewById(R.id.button_Note);
            button_Note.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRActivity.this);
                    LayoutInflater layoutInflater = getLayoutInflater();
                    View view = layoutInflater.inflate(R.layout.dialog_custom, null);
                    builder.setView(view);
                    final EditText editText_Note = (EditText) view.findViewById(R.id.editText_Note);
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getApplicationContext(), editText_Note.getText().toString().trim(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(ScanQRActivity.this,"ASSETID : " + MyArrList.get(position).get("ASSETID") + "\nNote : " + editText_Note.getText().toString().trim(), Toast.LENGTH_SHORT).show();

                            MainActivity.mydb.UpdaterawQueryWithoutMoveToFirst("UPDATE TEMP_HISTORY_ASSET SET" +
                                            " HISTORY_NOTE = '" + editText_Note.getText().toString().trim() + "'" +
                                            " WHERE HISTORY_ASSET_ID = ?"
                                    ,new String[]{ MyArrList.get(position).get("ASSETID") });

                            refreshListView();
                            //listView.setSelection(position);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            });

            Button button_Photo = (Button) convertView.findViewById(R.id.button_Photo);
            button_Photo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    HISTORY_ASSET_ID_PHOTO = MyArrList.get(position).get("ASSETID");
                    HISTORY_ASSET_RID_PHOTO = Integer.parseInt(MyArrList.get(position).get("ASSETRID"));
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(ScanQRActivity.this, "com.example.administrator.myapplicationqr", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }

/*
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        HISTORY_ASSET_ID_PHOTO = MyArrList.get(position).get("ASSETID");
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
*/
                    //Toast.makeText(getApplicationContext(), "PHOTO", Toast.LENGTH_SHORT).show();
                }
            });

            Button button_Delete = (Button) convertView.findViewById(R.id.button_Delete);
            final AlertDialog.Builder adb = new AlertDialog.Builder(ScanQRActivity.this);
            button_Delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    adb.setTitle("Confirm Delete");
                    adb.setMessage("Are you sure to delete [" + MyArrList.get(position).get("ASSETID") +"]");
                    adb.setNegativeButton("No", null);
                    adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.mydb.onDelete_TEMP_HISTORY_ASSET(MyArrList.get(position).get("ASSETID"));

                            refreshListView();
                            listView.setSelection(position - 1);
                        }});
                    adb.show();
                }
            });

            return convertView;

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void refreshListView(){

        // FETCHING DATA ==================================================================
        MyArrList = new ArrayList<HashMap<String, String>>();
        //c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM TEMP_HISTORY_ASSET ORDER BY HISTORY_ASSET_ID ASC",null);
        c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM TEMP_HISTORY_ASSET ORDER BY HISTORY_YEAR ASC,HISTORY_MONTH ASC,HISTORY_DAY ASC,HISTORY_HOUR ASC,HISTORY_MINUTE ASC",null);
        while (c.moveToNext()) {
/*
            hashMap = new HashMap<String, String>();
            hashMap.put( "ITEM_UID", String.valueOf(c.getString(1)) );
            hashMap.put("ITEM_SERIAL", String.valueOf(c.getString(17)));
            hashMap.put("ITEM_NAME", String.valueOf(c.getString(18)));
            hashMap.put( "OWNER_UID", String.valueOf(c.getString(2)) );
            hashMap.put("OWNER_NAME", String.valueOf(c.getString(19)) + " " + String.valueOf(c.getString(20)));
            hashMap.put( "DATE", String.valueOf(c.getInt(11)) + "/" + String.valueOf(c.getInt(12)) + "/" + String.valueOf(c.getInt(13)) );
            hashMap.put( "TIME", String.valueOf(c.getInt(9)) + " : " + String.valueOf(c.getInt(10)) );
            hashMap.put( "STATUS", String.valueOf(c.getString(3)) );
            hashMap.put( "INDOOR_MAP", String.valueOf(c.getInt(16)) );
            MyArrList.add(hashMap);
            */
            hashMap = new HashMap<String, String>();
            hashMap.put( "ASSETRID", String.valueOf(c.getInt(0)) );
            hashMap.put( "ASSETID", String.valueOf(c.getString(1)) );
            hashMap.put( "ASSETNAME", String.valueOf(c.getString(2)) );
            hashMap.put( "REFERID", String.valueOf(c.getString(3)));
            hashMap.put( "REFERNAME", String.valueOf(c.getString(4)));
            hashMap.put( "RECEIVEDATE", String.valueOf(c.getString(5)));
            hashMap.put( "SPEC", String.valueOf(c.getString(6)) );
            hashMap.put( "UNITNAME", String.valueOf(c.getString(7)) );
            hashMap.put( "DATE", String.valueOf(c.getInt(14)) + "-" + String.valueOf(c.getInt(13)) + "-" + String.valueOf(c.getInt(12)) );
            hashMap.put( "TIME", String.valueOf(c.getInt(15)) + " : " + String.valueOf(c.getInt(16)) );
            hashMap.put( "STATUS", String.valueOf(c.getString(17)) );
            hashMap.put( "SCANBY", String.valueOf(c.getString(18)) );
            hashMap.put( "NOTE", String.valueOf(c.getString(19)) );
            /*
            StringBuffer buffer = new StringBuffer();
            buffer.append(c.getBlob(20));
            //Toast.makeText(this, String.valueOf(new String(c.getBlob(20))), Toast.LENGTH_LONG).show();
            hashMap.put( "PHOTO", buffer.toString() );
            */
            MyArrList.add(hashMap);
        }
        listView.setAdapter(new TextAdapter(ScanQRActivity.this));

    }

    class Send extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(BASE_URL + "save.php");
            //HttpPost httppost = new HttpPost("http://amsapp.net/save.php");

            Cursor C_TEMP_HISTORY_ASSET = MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_ASSET");

            try {
                if (C_TEMP_HISTORY_ASSET.getCount() != 0) {
                    while (C_TEMP_HISTORY_ASSET.moveToNext()){
                        JSONArray jArry=new JSONArray();
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ASSET_ID", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(1)), "UTF-8").replace("+", "%20")) );

                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ASSET_NAME", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(2)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_REFERID", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(3)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_REFERNAME", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(4)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_RECEIVEDATE", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(5)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_SPEC", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(6)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_UNITNAME", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(7)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_BUILDING_ID", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getInt(8)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_BUILDING_NAME", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(9)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_FLOOR_ID", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getInt(10)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ROOM_ID", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(11)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_DAY", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getInt(12)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_MONTH", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getInt(13)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_YEAR", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getInt(14)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_HOUR", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getInt(15)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_MINUTE", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getInt(16)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_STATUS_NAME", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(17)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_USERNAME", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(18)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_NOTE", URLEncoder.encode(String.valueOf(C_TEMP_HISTORY_ASSET.getString(19)), "UTF-8").replace("+", "%20")) );


                        Cursor C_TEMP_HISTORY_IMAGE = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT HISTORY_IMAGE_RID,HISTORY_IMAGE FROM TEMP_HISTORY_IMAGE INNER JOIN TEMP_HISTORY_ASSET ON " +
                                "TEMP_HISTORY_ASSET.HISTORY_RID = TEMP_HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE TEMP_HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ String.valueOf(C_TEMP_HISTORY_ASSET.getInt(0)) });
                        C_TEMP_HISTORY_IMAGE.moveToFirst();

                        if( C_TEMP_HISTORY_IMAGE.getCount() != 0 ){

                            C_TEMP_HISTORY_IMAGE.moveToFirst();
                            do{
                                if(C_TEMP_HISTORY_IMAGE.getBlob(1) != null){
                                    byte[] byteArray = C_TEMP_HISTORY_IMAGE.getBlob(1);
                                    String encoded_string = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                                    jArry.put(encoded_string);
                                    //String encoded_string = "ABC";
                                    //jArry.put(encoded_string);
                                }
                                else{
                                    String encoded_string = null;
                                    jArry.put(encoded_string);
                                    //String encoded_string = null;
                                    //jArry.put(encoded_string);
                                }

                            }while(C_TEMP_HISTORY_IMAGE.moveToNext());

                        }

                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_PHOTO", jArry.toString() ) );
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        //httppost.setHeader(HTTP.CONTENT_TYPE,HTTP.DEFAULT_CONTENT_TYPE);
                        //httppost.setHeader("","");
                        //httppost.addHeader("Content-Type", "text/plain");
                        //httppost.addHeader("Accept", "application/json");
                        //httppost.addHeader("Content-type", "application/json");
                        //httppost.addHeader("Content-Length","" + new UrlEncodedFormEntity(nameValuePairs).getContentLength());
                        //httppost.setHeader("Content-Length","" + new UrlEncodedFormEntity(nameValuePairs).getContentLength());
                        HttpResponse response = httpclient.execute(httppost);



                    }

                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }

            return null;

        }
        protected void onProgressUpdate(Integer... progress) {

        }
        protected void onPostExecute(Long result) {
            finish();
        }

    }
/*
    public void sendData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "save2.php",

        //StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://amsapp.net/save2.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                        }
                        catch (JSONException e){
                            Toast.makeText(ScanQRActivity.this,"DATA SENT" + e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                Cursor C_TEMP_HISTORY_ASSET = MainActivity.mydb.getAllRowsFromTable("TEMP_HISTORY_ASSET");

                if (C_TEMP_HISTORY_ASSET.getCount() != 0) {
                    while (C_TEMP_HISTORY_ASSET.moveToNext()){
                        params.put("HISTORY_ASSET_ID", String.valueOf(C_TEMP_HISTORY_ASSET.getString(1)) );
                        params.put("HISTORY_ASSET_NAME", String.valueOf(C_TEMP_HISTORY_ASSET.getString(2)) );
                        params.put("HISTORY_REFERID", String.valueOf(C_TEMP_HISTORY_ASSET.getString(3)) );
                        params.put("HISTORY_REFERNAME", String.valueOf(C_TEMP_HISTORY_ASSET.getString(4)) );
                        params.put("HISTORY_RECEIVEDATE", String.valueOf(C_TEMP_HISTORY_ASSET.getString(5)) );
                        params.put("HISTORY_SPEC", String.valueOf(C_TEMP_HISTORY_ASSET.getString(6)) );
                        params.put("HISTORY_UNITNAME", String.valueOf(C_TEMP_HISTORY_ASSET.getString(7)) );
                        params.put("HISTORY_BUILDING_ID", String.valueOf(C_TEMP_HISTORY_ASSET.getInt(8)) );
                        params.put("HISTORY_BUILDING_NAME", String.valueOf(C_TEMP_HISTORY_ASSET.getString(9)) );
                        params.put("HISTORY_FLOOR_ID", String.valueOf(C_TEMP_HISTORY_ASSET.getInt(10)) );
                        params.put("HISTORY_ROOM_ID", String.valueOf(C_TEMP_HISTORY_ASSET.getString(11)) );
                        params.put("HISTORY_DAY", String.valueOf(C_TEMP_HISTORY_ASSET.getInt(12)) );
                        params.put("HISTORY_MONTH", String.valueOf(C_TEMP_HISTORY_ASSET.getInt(13)) );
                        params.put("HISTORY_YEAR", String.valueOf(C_TEMP_HISTORY_ASSET.getInt(14)) );
                        params.put("HISTORY_HOUR", String.valueOf(C_TEMP_HISTORY_ASSET.getInt(15)) );
                        params.put("HISTORY_MINUTE", String.valueOf(C_TEMP_HISTORY_ASSET.getInt(16)) );
                        params.put("HISTORY_STATUS_NAME", String.valueOf(C_TEMP_HISTORY_ASSET.getString(17)) );
                        params.put("HISTORY_USERNAME", String.valueOf(C_TEMP_HISTORY_ASSET.getString(18)) );
                        params.put("HISTORY_NOTE", String.valueOf(C_TEMP_HISTORY_ASSET.getString(19)) );

                        JSONArray jArry=new JSONArray();
                        Cursor C_TEMP_HISTORY_IMAGE = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT HISTORY_IMAGE_RID,HISTORY_IMAGE FROM TEMP_HISTORY_IMAGE INNER JOIN TEMP_HISTORY_ASSET ON " +
                                "TEMP_HISTORY_ASSET.HISTORY_RID = TEMP_HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE TEMP_HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ String.valueOf(C_TEMP_HISTORY_ASSET.getInt(0)) });
                        C_TEMP_HISTORY_IMAGE.moveToFirst();

                        if( C_TEMP_HISTORY_IMAGE.getCount() != 0 ){

                            C_TEMP_HISTORY_IMAGE.moveToFirst();
                            do{
                                if(C_TEMP_HISTORY_IMAGE.getBlob(1) != null){
                                    byte[] byteArray = C_TEMP_HISTORY_IMAGE.getBlob(1);
                                    String encoded_string = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                                    jArry.put(encoded_string);
                                    //String encoded_string = "ABC";
                                    //jArry.put(encoded_string);
                                }
                                else{
                                    String encoded_string = null;
                                    jArry.put(encoded_string);
                                    //String encoded_string = null;
                                    //jArry.put(encoded_string);
                                }

                            }while(C_TEMP_HISTORY_IMAGE.moveToNext());

                        }

                        params.put("HISTORY_PHOTO", jArry.toString() );
                    }

                }
                return params;

            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
*/
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}