package com.example.administrator.myapplicationqr;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String BASE_URL;

    public static DatabaseHelper mydb;

    CurrentTime currentTime = new CurrentTime();

    ImageView imageView_Person;
    TextView textView_globalUsername,textView_globalRefername,textView_globalReferid;
    Boolean syncStatus = false;

    ProgressBar progressBar_Update;
    ScrollView scrollView_MainMenu;

    CardView SetupTagCardView;
    CardView WriteTagCardView;

    Intent intent;

    NfcAdapter nfcAdapter;
    boolean nfc_status;

    ToggleButton tglReadWrite;
    EditText txtTagContent;

    MifareUltralight mifareUltralight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Toast.makeText(MainActivity.this,"Login Success !" + "\nUsername : " + LoginActivity.globalUsername +
                        //"\nRefer ID : " + LoginActivity.globalReferID +
                        //"\nRefer Name : " + LoginActivity.globalRefername +
                        //"\nPassword : " + LoginActivity.globalPassword +
                        //"\nAuth : " + LoginActivity.globalAuth
                //,Toast.LENGTH_LONG).show();

        mydb = new DatabaseHelper(this);

        if(LoginActivity.SYNC_FROM_URL == true){
            MainActivity.mydb.onDeleteTable("ASSET");
            MainActivity.mydb.onDeleteTable("DEPARTMENT");
            MainActivity.mydb.onDeleteTable("STATUS");
            MainActivity.mydb.onDeleteTable("LOCATION");
            MainActivity.mydb.onDeleteTable("HISTORY_ASSET");
            MainActivity.mydb.onDeleteTable("HISTORY_ASSET_RECENT");
            MainActivity.mydb.onDeleteTable("HISTORY_IMAGE");
            MainActivity.mydb.onDeleteTable("REFER");
            LoginActivity.SYNC_FROM_URL = false;
        }

        ConstraintLayout constraintLayout;

        imageView_Person = (ImageView) findViewById(R.id.imageView_DefaultPerson);
        textView_globalUsername = (TextView) findViewById(R.id.textView_globalUsername);
        textView_globalRefername = (TextView) findViewById(R.id.textView_globalRefername);
        textView_globalReferid = (TextView) findViewById(R.id.textView_globalReferid);
        progressBar_Update = (ProgressBar) findViewById(R.id.progressBar_Update);
        scrollView_MainMenu = (ScrollView) findViewById(R.id.scrollView_MainMenu);

        SetupTagCardView = (CardView) findViewById(R.id.SetupTagCardView);
        WriteTagCardView = (CardView) findViewById(R.id.WriteTagCardView);

        progressBar_Update.setVisibility(View.GONE);
        syncStatus = false;

        if(LoginActivity.globalUsername != null){
            textView_globalUsername.setText(getResources().getString(R.string.USER_USERNAME) + " " + LoginActivity.globalUsername);
        }
        if(LoginActivity.globalRefername != null){
            textView_globalRefername.setText(getResources().getString(R.string.USER_REFER_NAME) + " " + LoginActivity.globalRefername);
        }
        if(LoginActivity.globalReferID != null){
            textView_globalReferid.setText(getResources().getString(R.string.USER_REFER_ID) + " " + LoginActivity.globalReferID);
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(LoginActivity.globalAuth.equals("ADMIN")){
            imageView_Person.setImageResource(R.drawable.easyqr_defaultadmin);
            if(nfcAdapter!=null){
                SetupTagCardView.setVisibility(View.VISIBLE);
                WriteTagCardView.setVisibility(View.VISIBLE);
            }else{
                SetupTagCardView.setVisibility(View.GONE);
                WriteTagCardView.setVisibility(View.GONE);
            }
        }
        else{
            imageView_Person.setImageResource(R.drawable.easyqr_defaultuser);
            SetupTagCardView.setVisibility(View.GONE);
            WriteTagCardView.setVisibility(View.GONE);
        }

        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            //Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
            nfc_status = true;
        }else{
            //Toast.makeText(this, "NFC not available :( ", Toast.LENGTH_LONG).show();
            nfc_status = false;
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        BASE_URL = sharedPreferences.getString("BaseUrl",getResources().getString(R.string.BASE_URL));

        syncStatus = false;
        countForSync();

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

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
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

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            enableForegroundDispatchSystem();
        }else{

        }
    }
    @Override
    public void onBackPressed() {
        final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        adb.setTitle("Confirm Logout");
        adb.setMessage("Do you want logout ?");
        adb.setNegativeButton("No", null);
        adb.setPositiveButton("Yes", new android.support.v7.app.AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("RememberLogin",false);
                editor.apply();

                MainActivity.super.onBackPressed();
            }});
        adb.show();
    }

    public void onSyncData(View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            intent = new Intent(this, SyncActivity.class);
            startActivity(intent);
        }
    }
    public void onViewData(View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            intent = new Intent(this, ViewDataActivity.class);
            startActivity(intent);
        }
    }
    public void onGenerateQRCode(View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            intent = new Intent(this, GenerateQRActivity.class);
            startActivity(intent);
        }
    }

    public void onScanQRCode(View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            intent = new Intent(this, ScanQRActivity.class);
            startActivity(intent);
        }
    }

    public void onSetupTag (View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            if(nfcAdapter!=null && nfcAdapter.isEnabled()){
                //Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
                intent = new Intent(this, SetupTagActivity.class);
                startActivity(intent);

            }else{
                Toast.makeText(this, "NFC is not available\nPlease make sure your phone have NFC and Enabled.", Toast.LENGTH_LONG).show();

            }
        }
    }
    public void onWriteNFC (View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            if(nfcAdapter!=null && nfcAdapter.isEnabled()){
                //Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
                intent = new Intent(this, WriteNFCActivity.class);
                startActivity(intent);

            }else{
                Toast.makeText(this, "NFC is not available\nPlease make sure your phone have NFC and Enabled.", Toast.LENGTH_LONG).show();

            }
        }
    }

    public void onStoreData (View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            if(MainActivity.mydb.getCountFromTable("STORE_HISTORY_ASSET") > 0){
                intent = new Intent(this, StoreDataActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(MainActivity.this,"You don't have any STORED DATA.",Toast.LENGTH_SHORT).show();
                intent = new Intent(this, StoreDataActivity.class);
                startActivity(intent);
            }
        }
    }
    public void onCheckRoomAsset (View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
                new SyncHistoryAssetRecent().execute();
                intent = new Intent(this, CheckRoomAsset.class);
                startActivity(intent);
            }
            else{
                final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                adb.setTitle("Your internet is not available");
                adb.setMessage("Your Asset Data maybe not shown correctly, do you want to continue ?");
                adb.setNegativeButton("No", null);
                adb.setPositiveButton("Yes", new android.support.v7.app.AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gotoCheckRoomAsset();
                    }});
                adb.show();
            }
        }
    }
    public void onSearchAsset (View v){
        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            intent = new Intent(this, SearchAssetActivity.class);
            startActivity(intent);

        }
    }
    public void onLogout (View v){
        final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        adb.setTitle("Confirm Logout");
        adb.setMessage("Do you want logout ?");
        adb.setNegativeButton("No", null);
        adb.setPositiveButton("Yes", new android.support.v7.app.AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("RememberLogin",false);
                editor.apply();
                MainActivity.super.onBackPressed();
            }});
        adb.show();
    }
    public void onMyAsset(View v){
        intent = new Intent(this, MyAssetActivity.class);
        startActivity(intent);
    }

    public void onIM1(View v){

        //Toast.makeText(MainActivity.this,"You clicked on Image 1 !!!",Toast.LENGTH_LONG).show();
    }

    public void gotoLogin(){
        super.onBackPressed();
        //intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
        //Toast.makeText(MainActivity.this,"You clicked on Image 1 !!!",Toast.LENGTH_LONG).show();
    }

    public void gotoCheckRoomAsset(){
        intent = new Intent(this, CheckRoomAsset.class);
        startActivity(intent);
        //Toast.makeText(MainActivity.this,"You clicked on Image 1 !!!",Toast.LENGTH_LONG).show();
    }

    public void gotoSearchAsset(){
        intent = new Intent(this, SearchAssetActivity.class);
        startActivity(intent);
        //Toast.makeText(MainActivity.this,"You clicked on Image 1 !!!",Toast.LENGTH_LONG).show();
    }

    public void onScanInfo (View v){

        if(syncStatus == true){
            scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
            Toast.makeText(MainActivity.this,"Please wait until Synchronization finish.",Toast.LENGTH_SHORT).show();
        }
        else{
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Please Focus Your Camera on QR Code.");
            integrator.setOrientationLocked(true);
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // SCAN_BY_CAMERA ==========================================================================
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {

                Toast.makeText(this, "Scanning Cancelled", Toast.LENGTH_SHORT).show();

            } else {
                Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?", new String[]{result.getContents().toString()});
                Cursor C_LOCATION_BARCODE = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BARCODE = ?", new String[]{result.getContents().toString()});

                if( (c != null) && (c.getCount() > 0) ){

                    Cursor C_REFER = MainActivity.mydb.rawQuery("SELECT * FROM REFER WHERE REFERID = ?",new String[]{ c.getString(2) } );
                    if( (C_REFER != null) && (C_REFER.getCount() > 0) ){
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(getResources().getString(R.string.ASSET_ID) + "\n" + c.getString(1) + "\n\n");
                        buffer.append(getResources().getString(R.string.REFER_ID) + "\n" + c.getString(2) + "\n\n");
                        buffer.append(getResources().getString(R.string.REFER_NAME) + "\n" + C_REFER.getString(1) + "\n\n");
                        buffer.append(getResources().getString(R.string.ASSET_NAME) + "\n" + c.getString(3) + "\n\n");
                        buffer.append(getResources().getString(R.string.RECEIVEDATE) + "\n" + c.getString(4) + "\n\n");
                        buffer.append(getResources().getString(R.string.SPEC) + "\n" + c.getString(5) + "\n\n");
                        buffer.append(getResources().getString(R.string.UNITNAME) + "\n" + c.getString(6) + "\n\n");

                        // Show all data
                        final String res = result.getContents().toString();

                        Cursor CHECK_HISTORY = MainActivity.mydb.rawQuery("SELECT * FROM HISTORY_ASSET WHERE HISTORY_ASSET_ID = ? ORDER BY HISTORY_YEAR DESC,HISTORY_MONTH DESC,HISTORY_DAY DESC,HISTORY_HOUR DESC,HISTORY_MINUTE DESC",
                                new String[]{ res });

                        if (CHECK_HISTORY.getCount() == 0) {
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                            builder.setCancelable(true);
                            builder.setTitle(res);
                            builder.setMessage(buffer.toString());
                            builder.show();
                        }else{
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                            builder.setCancelable(true);
                            builder.setTitle(res);
                            builder.setMessage(buffer.toString());
                            builder.setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                                    intent.putExtra("ASSETID", res);
                                    startActivity(intent);
                                }
                            });
                            builder.show();
                        }

                    }

                }
                else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                    Toast.makeText(this, "LOCATION QR CODE : " + result.getContents().toString(), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "UNKNOWN QR CODE : " + result.getContents().toString(), Toast.LENGTH_LONG).show();
                }


            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);

        }

    }
    public void showMessage (String title, String Message){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private void countForSync(){
        //progressBar_Loading.setVisibility(View.VISIBLE);
        //button_Login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "count_for_sync.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("count");
                            if(success.equals("1")){
                                JSONObject object = new JSONObject();
                                //Toast.makeText(MainActivity.this,"START COUNTING",Toast.LENGTH_LONG).show();
                                for(int i = 0; i < jsonArray.length(); i++){
                                    object = jsonArray.getJSONObject(i);
                                }
                                String tempAssetCount = object.getString("ASSET_COUNT").trim();
                                String tempDepartmentCount = object.getString("DEPARTMENT_COUNT").trim();
                                String tempStatusCount = object.getString("STATUS_COUNT").trim();
                                String tempReferCount = object.getString("REFER_COUNT").trim();
                                String tempLocationCount = object.getString("LOCATION_COUNT").trim();
                                String tempHistoryAssetCount = object.getString("HISTORY_ASSET_COUNT").trim();
                                String tempHistoryAssetRecentCount = object.getString("HISTORY_ASSET_RECENT_COUNT").trim();
                                String tempHistoryImageCount = object.getString("HISTORY_IMAGE_COUNT").trim();
/*
                                Toast.makeText(MainActivity.this,"Counting Success !" +
                                                "\nASSET_COUNT : " + tempAssetCount + " , " + MainActivity.mydb.getCountFromTable("ASSET") +
                                                "\nDEPARTMENT_COUNT: " + tempDepartmentCount + " , " + MainActivity.mydb.getCountFromTable("DEPARTMENT") +
                                                "\nSTATUS_COUNT : " + tempStatusCount + " , " + MainActivity.mydb.getCountFromTable("STATUS") +
                                                "\nREFER_COUNT : " + tempReferCount + " , " + MainActivity.mydb.getCountFromTable("REFER") +
                                                "\nLOCATION_COUNT : " + tempLocationCount + " , " + MainActivity.mydb.getCountFromTable("LOCATION") +
                                                "\nHISTORY_ASSET_COUNT : " + tempHistoryAssetCount + " , " + MainActivity.mydb.getCountFromTable("HISTORY_ASSET") +
                                                "\nHISTORY_ASSET_RECENT_COUNT : " + tempHistoryAssetRecentCount + " , " + MainActivity.mydb.getCountFromTable("HISTORY_ASSET_RECENT") +
                                                "\nHISTORY_IMAGE_COUNT : " + tempHistoryImageCount + " , " + MainActivity.mydb.getCountFromTable("HISTORY_IMAGE")
                                        ,Toast.LENGTH_LONG).show();
*/

                                //CHECK FOR FIRST INSTALLED APP ?
                                if((MainActivity.mydb.getCountFromTable("ASSET") < Integer.parseInt(tempAssetCount) ) &&
                                        (MainActivity.mydb.getCountFromTable("DEPARTMENT") < Integer.parseInt(tempDepartmentCount) ) &&
                                        (MainActivity.mydb.getCountFromTable("STATUS") < Integer.parseInt(tempStatusCount) ) &&
                                        (MainActivity.mydb.getCountFromTable("REFER") < Integer.parseInt(tempReferCount) ) &&
                                        (MainActivity.mydb.getCountFromTable("LOCATION") < Integer.parseInt(tempLocationCount) ) &&
                                        (MainActivity.mydb.getCountFromTable("HISTORY_ASSET") < Integer.parseInt(tempHistoryAssetCount) ) &&
                                        (MainActivity.mydb.getCountFromTable("HISTORY_ASSET_RECENT") < Integer.parseInt(tempHistoryAssetRecentCount) ) &&
                                        (MainActivity.mydb.getCountFromTable("HISTORY_IMAGE") < Integer.parseInt(tempHistoryImageCount) )
                                        ){
                                    final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                                    adb.setTitle("Synchronization");
                                    adb.setMessage("Please wait about 2 - 3 minutes until first synchronization finished.");
                                    adb.show();
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncAll().execute();
                                }

                                //CHECK FOR SYNC ASSET ?
                                if (Long.parseLong(tempAssetCount) != MainActivity.mydb.getCountFromTable("ASSET")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("ASSET");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncAsset().execute();
                                }

                                //CHECK FOR SYNC DEPARTMENT ?
                                if (Long.parseLong(tempDepartmentCount) - 4 != MainActivity.mydb.getCountFromTable("DEPARTMENT")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("DEPARTMENT");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncDepartment().execute();
                                }

                                //CHECK FOR SYNC STATUS ?
                                if (Long.parseLong(tempStatusCount) != MainActivity.mydb.getCountFromTable("STATUS")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("STATUS");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncStatus().execute();
                                }

                                //CHECK FOR SYNC LOCATION ?
                                if (Long.parseLong(tempLocationCount) != MainActivity.mydb.getCountFromTable("LOCATION")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("LOCATION");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncLocation().execute();
                                }

                                //CHECK FOR SYNC HISTORY ASSET ?
                                if (Long.parseLong(tempHistoryAssetCount) != MainActivity.mydb.getCountFromTable("HISTORY_ASSET")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("HISTORY_ASSET");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncHistoryAsset().execute();
                                }

                                //CHECK FOR SYNC HISTORY ASSET RECENT ?
                                if (Long.parseLong(tempHistoryAssetRecentCount) != MainActivity.mydb.getCountFromTable("HISTORY_ASSET_RECENT")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("HISTORY_ASSET_RECENT");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncHistoryAssetRecent().execute();

                                }

                                //CHECK FOR SYNC HISTORY IMAGE ?
                                if (Long.parseLong(tempHistoryImageCount) != MainActivity.mydb.getCountFromTable("HISTORY_IMAGE")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("HISTORY_IMAGE");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncHistoryImage().execute();

                                }

                                //CHECK FOR SYNC REFER ?
                                if (Long.parseLong(tempReferCount) != MainActivity.mydb.getCountFromTable("REFER")) {
                                    //Toast.makeText(MainActivity.this,"Updating Data !",Toast.LENGTH_LONG).show();
                                    MainActivity.mydb.onDeleteTable("REFER");
                                    progressBar_Update.setVisibility(View.VISIBLE);
                                    scrollView_MainMenu.fullScroll(ScrollView.FOCUS_UP);
                                    syncStatus = true;
                                    new SyncRefer().execute();
                                }

                            }
                            else{
                                Toast.makeText(MainActivity.this,"FAIL IN",Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(MainActivity.this,"Incorrect Username or Password." + e.toString(),Toast.LENGTH_LONG).show();
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
                params.put("username",LoginActivity.globalUsername);
                //params.put("password",LoginActivity.globalPassword);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    class SyncAsset extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET ASSET DATA TO TABLE
            try {
                URL url =  new URL(BASE_URL + "api/create_json_ASSET.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_ASSET(jsonObject.get("ASSETID").toString(),jsonObject.get("BARCODE").toString(),jsonObject.get("REFERIDITEM").toString(),jsonObject.get("ASSETNAME").toString(),
                            jsonObject.get("RECEIVEDATE").toString(),jsonObject.get("SPEC").toString(),jsonObject.get("UNITNAME").toString());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update ASSET finished !",Toast.LENGTH_LONG).show();
        }
    }

    class SyncDepartment extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET DEPARTMENT DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_DEPARTMENT.php/");
                URL url =  new URL(BASE_URL + "api/create_json_DEPARTMENT.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_DEPARTMENT(Integer.parseInt(jsonObject.get("DEPARTMENTID").toString()),jsonObject.get("DEPARTMENTNAME").toString());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update DEPARTMENT finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    class SyncStatus extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET STATUS DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_STATUS.php/");
                URL url =  new URL(BASE_URL + "api/create_json_STATUS.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_STATUS( Integer.parseInt( jsonObject.get("STATUS_ID").toString() ),jsonObject.get("STATUS_NAME").toString() );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update STATUS finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    class SyncRefer extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET REFER DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_REFER.php/");
                URL url =  new URL(BASE_URL + "api/create_json_REFER.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_REFER(jsonObject.get("REFERID").toString(),jsonObject.get("REFERNAME").toString(),Integer.parseInt(jsonObject.get("DEPARTMENTID").toString()) );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update REFER finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    class SyncLocation extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET LOCATION DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_LOCATION.php/");
                URL url =  new URL(BASE_URL + "api/create_json_LOCATION.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_LOCATION(Integer.parseInt(jsonObject.get("LOCATION_ID").toString()),jsonObject.get("LOCATION_BARCODE").toString(),
                            Integer.parseInt(jsonObject.get("LOCATION_BUILDING_ID").toString()),
                            jsonObject.get("LOCATION_BUILDING_NAME").toString(),Integer.parseInt(jsonObject.get("LOCATION_FLOOR_ID").toString()),
                            jsonObject.get("LOCATION_ROOM_ID").toString(),Integer.parseInt(jsonObject.get("DEPARTMENTID").toString()) );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update LOCATION finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    class SyncHistoryAsset extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET HISTORY_ASSET DATA TO TABLE
            try {
                URL url =  new URL(BASE_URL + "api/create_json_HISTORY_ASSET.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_HISTORY_ASSET(
                            Integer.parseInt(jsonObject.get("HISTORY_RID").toString()),
                            jsonObject.get("HISTORY_ASSETID").toString(),
                            jsonObject.get("HISTORY_ASSET_NAME").toString(),
                            jsonObject.get("HISTORY_REFERID").toString(),
                            jsonObject.get("HISTORY_REFERNAME").toString(),
                            jsonObject.get("HISTORY_RECEIVEDATE").toString(),
                            jsonObject.get("HISTORY_SPEC").toString(),
                            jsonObject.get("HISTORY_UNITNAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_BUILDING_ID").toString()),
                            jsonObject.get("HISTORY_BUILDING_NAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_FLOOR_ID").toString()),
                            jsonObject.get("HISTORY_ROOM_ID").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_DAY").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MONTH").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_YEAR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_HOUR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MINUTE").toString()),
                            jsonObject.get("HISTORY_STATUS_NAME").toString(),
                            jsonObject.get("HISTORY_USERNAME").toString(),
                            jsonObject.get("HISTORY_NOTE").toString()
                            //jsonObject.get("HISTORY_PHOTO").toString()
                    );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update HISTORY_ASSET finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    class SyncHistoryAssetRecent extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET HISTORY_ASSET_RECENT DATA TO TABLE
            try {
                MainActivity.mydb.onDeleteTable("HISTORY_ASSET_RECENT");
                URL url =  new URL(BASE_URL + "api/create_json_HISTORY_ASSET_RECENT.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_HISTORY_ASSET_RECENT(
                            jsonObject.get("HISTORY_ASSETID").toString(),
                            jsonObject.get("HISTORY_ASSET_NAME").toString(),
                            jsonObject.get("HISTORY_REFERID").toString(),
                            jsonObject.get("HISTORY_REFERNAME").toString(),
                            jsonObject.get("HISTORY_RECEIVEDATE").toString(),
                            jsonObject.get("HISTORY_SPEC").toString(),
                            jsonObject.get("HISTORY_UNITNAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_BUILDING_ID").toString()),
                            jsonObject.get("HISTORY_BUILDING_NAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_FLOOR_ID").toString()),
                            jsonObject.get("HISTORY_ROOM_ID").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_DAY").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MONTH").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_YEAR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_HOUR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MINUTE").toString()),
                            jsonObject.get("HISTORY_STATUS_NAME").toString(),
                            jsonObject.get("HISTORY_USERNAME").toString(),
                            jsonObject.get("HISTORY_NOTE").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_RID").toString())

                            //jsonObject.get("HISTORY_PHOTO").toString()
                    );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update HISTORY_ASSET_RECENT finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    class SyncHistoryImage extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET HISTORY_IMAGE DATA TO TABLE
            try {
                MainActivity.mydb.onDeleteTable("HISTORY_IMAGE");
                URL url =  new URL(BASE_URL + "api/create_json_HISTORY_IMAGE.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_HISTORY_IMAGE(
                            Integer.parseInt(jsonObject.get("HISTORY_IMAGE_RID").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_IMAGE_HISTORY_RID").toString()),
                            jsonObject.get("HISTORY_IMAGE_PATH").toString()
                    );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
            syncStatus = true;
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update HISTORY_IMAGE finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    class SyncAll extends AsyncTask<String, Void,Long > {

        protected Long doInBackground(String... urls) {
            // GET ASSET DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_ASSET.php/");
                URL url =  new URL(BASE_URL + "api/create_json_ASSET.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_ASSET(jsonObject.get("ASSETID").toString(),jsonObject.get("BARCODE").toString(),jsonObject.get("REFERIDITEM").toString(),jsonObject.get("ASSETNAME").toString(),
                            jsonObject.get("RECEIVEDATE").toString(),jsonObject.get("SPEC").toString(),jsonObject.get("UNITNAME").toString());
                    //dataParsed = dataParsed + singleParsed;
                }
                //textView_Text.setText(dataParsed);
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            // GET DEPARTMENT DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_DEPARTMENT.php/");
                URL url =  new URL(BASE_URL + "api/create_json_DEPARTMENT.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_DEPARTMENT(Integer.parseInt(jsonObject.get("DEPARTMENTID").toString()),jsonObject.get("DEPARTMENTNAME").toString());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            // GET STATUS DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_STATUS.php/");
                URL url =  new URL(BASE_URL + "api/create_json_STATUS.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_STATUS( Integer.parseInt( jsonObject.get("STATUS_ID").toString() ),jsonObject.get("STATUS_NAME").toString() );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            // GET LOCATION DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_LOCATION.php/");
                URL url =  new URL(BASE_URL + "api/create_json_LOCATION.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_LOCATION(Integer.parseInt(jsonObject.get("LOCATION_ID").toString()),jsonObject.get("LOCATION_BARCODE").toString(),
                            Integer.parseInt(jsonObject.get("LOCATION_BUILDING_ID").toString()),
                            jsonObject.get("LOCATION_BUILDING_NAME").toString(),Integer.parseInt(jsonObject.get("LOCATION_FLOOR_ID").toString()),
                            jsonObject.get("LOCATION_ROOM_ID").toString(),Integer.parseInt(jsonObject.get("DEPARTMENTID").toString()) );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            // GET HISTORY_ASSET DATA TO TABLE
            try {
                URL url =  new URL(BASE_URL + "api/create_json_HISTORY_ASSET.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_HISTORY_ASSET(
                            Integer.parseInt(jsonObject.get("HISTORY_RID").toString()),
                            jsonObject.get("HISTORY_ASSETID").toString(),
                            jsonObject.get("HISTORY_ASSET_NAME").toString(),
                            jsonObject.get("HISTORY_REFERID").toString(),
                            jsonObject.get("HISTORY_REFERNAME").toString(),
                            jsonObject.get("HISTORY_RECEIVEDATE").toString(),
                            jsonObject.get("HISTORY_SPEC").toString(),
                            jsonObject.get("HISTORY_UNITNAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_BUILDING_ID").toString()),
                            jsonObject.get("HISTORY_BUILDING_NAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_FLOOR_ID").toString()),
                            jsonObject.get("HISTORY_ROOM_ID").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_DAY").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MONTH").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_YEAR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_HOUR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MINUTE").toString()),
                            jsonObject.get("HISTORY_STATUS_NAME").toString(),
                            jsonObject.get("HISTORY_USERNAME").toString(),
                            jsonObject.get("HISTORY_NOTE").toString()
                            //jsonObject.get("HISTORY_PHOTO").toString()
                    );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

            // GET HISTORY_ASSET_RECENT DATA TO TABLE
            try {
                URL url =  new URL(BASE_URL + "api/create_json_HISTORY_ASSET_RECENT.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_HISTORY_ASSET_RECENT(
                            jsonObject.get("HISTORY_ASSETID").toString(),
                            jsonObject.get("HISTORY_ASSET_NAME").toString(),
                            jsonObject.get("HISTORY_REFERID").toString(),
                            jsonObject.get("HISTORY_REFERNAME").toString(),
                            jsonObject.get("HISTORY_RECEIVEDATE").toString(),
                            jsonObject.get("HISTORY_SPEC").toString(),
                            jsonObject.get("HISTORY_UNITNAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_BUILDING_ID").toString()),
                            jsonObject.get("HISTORY_BUILDING_NAME").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_FLOOR_ID").toString()),
                            jsonObject.get("HISTORY_ROOM_ID").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_DAY").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MONTH").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_YEAR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_HOUR").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_MINUTE").toString()),
                            jsonObject.get("HISTORY_STATUS_NAME").toString(),
                            jsonObject.get("HISTORY_USERNAME").toString(),
                            jsonObject.get("HISTORY_NOTE").toString(),
                            Integer.parseInt(jsonObject.get("HISTORY_RID").toString())
                            //jsonObject.get("HISTORY_PHOTO").toString()
                    );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

            // GET HISTORY_IMAGE DATA TO TABLE
            try {
                MainActivity.mydb.onDeleteTable("HISTORY_IMAGE");
                URL url =  new URL(BASE_URL + "api/create_json_HISTORY_IMAGE.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_HISTORY_IMAGE(
                            Integer.parseInt(jsonObject.get("HISTORY_IMAGE_RID").toString()),
                            Integer.parseInt(jsonObject.get("HISTORY_IMAGE_HISTORY_RID").toString()),
                            jsonObject.get("HISTORY_IMAGE_PATH").toString()
                    );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

            // GET REFER DATA TO TABLE
            try {
                //URL url =  new URL("http://amsapp.net/create_json_REFER.php/");
                URL url =  new URL(BASE_URL + "api/create_json_REFER.php/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(inputStream));
                String data = "";
                String line = "";
                while(line != null){
                    line =  bufferedReader.readLine();
                    data = data + line;
                }
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    MainActivity.mydb.onInsert_REFER(jsonObject.get("REFERID").toString(),jsonObject.get("REFERNAME").toString(),Integer.parseInt(jsonObject.get("DEPARTMENTID").toString()) );
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {
            progressBar_Update.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            progressBar_Update.setVisibility(View.GONE);
            syncStatus = false;
            //Toast.makeText(MainActivity.this,"Update ALL DATA finished !",Toast.LENGTH_LONG).show();
            //countForSync();
        }
    }

    private void dismissAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
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
/*
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Toast.makeText(this, "NfcIntent!", Toast.LENGTH_SHORT).show();

            if(tglReadWrite.isChecked())
            {
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                if(parcelables != null && parcelables.length > 0)
                {
                    readTextFromMessage((NdefMessage) parcelables[0]);
                }else{
                    Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
                }

            }else{
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                NdefMessage ndefMessage = createNdefMessage(txtTagContent.getText()+"");

                writeNdefMessage(tag, ndefMessage);
            }

        }
        */
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

            Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?", new String[]{ result });
            Cursor C_LOCATION_BARCODE = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BARCODE = ?", new String[]{ result });

            if( (c != null) && (c.getCount() > 0) ){

                Cursor C_REFER = MainActivity.mydb.rawQuery("SELECT * FROM REFER WHERE REFERID = ?",new String[]{ c.getString(2) } );
                if( (C_REFER != null) && (C_REFER.getCount() > 0) ){
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(getResources().getString(R.string.ASSET_ID) + "\n" + c.getString(1) + "\n\n");
                    buffer.append(getResources().getString(R.string.REFER_ID) + "\n" + c.getString(2) + "\n\n");
                    buffer.append(getResources().getString(R.string.REFER_NAME) + "\n" + C_REFER.getString(1) + "\n\n");
                    buffer.append(getResources().getString(R.string.ASSET_NAME) + "\n" + c.getString(3) + "\n\n");
                    buffer.append(getResources().getString(R.string.RECEIVEDATE) + "\n" + c.getString(4) + "\n\n");
                    buffer.append(getResources().getString(R.string.SPEC) + "\n" + c.getString(5) + "\n\n");
                    buffer.append(getResources().getString(R.string.UNITNAME) + "\n" + c.getString(6) + "\n\n");

                    // Show all data
                    final String res = result;

                    Cursor CHECK_HISTORY = MainActivity.mydb.rawQuery("SELECT * FROM HISTORY_ASSET WHERE HISTORY_ASSET_ID = ? ORDER BY HISTORY_YEAR DESC,HISTORY_MONTH DESC,HISTORY_DAY DESC,HISTORY_HOUR DESC,HISTORY_MINUTE DESC",
                            new String[]{ res });

                    if (CHECK_HISTORY.getCount() == 0) {
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                        builder.setCancelable(true);
                        builder.setTitle(res);
                        builder.setMessage(buffer.toString());
                        builder.show();
                    }else{
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                        builder.setCancelable(true);
                        builder.setTitle(res);
                        builder.setMessage(buffer.toString());
                        builder.setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                                intent.putExtra("ASSETID", res);
                                startActivity(intent);
                            }
                        });
                        builder.show();
                    }
                }
            }
            else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                Intent intent = new Intent(getApplicationContext(), CheckRoomAsset.class);
                intent.putExtra("RESULT", result);
                startActivity(intent);
                Toast.makeText(this, "LOCATION QR CODE : " + result, Toast.LENGTH_LONG).show();
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


    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if(ndefRecords != null && ndefRecords.length>0){

            NdefRecord ndefRecord = ndefRecords[0];

            String tagContent = getTextFromNdefRecord(ndefRecord);

            txtTagContent.setText(tagContent);

        }else
        {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }

    }

    private void formatTag(Tag tag, NdefMessage ndefMessage){
        try{
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null){
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_LONG).show();
                return;
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(this, "Tag is written !", Toast.LENGTH_LONG).show();
        } catch (Exception e){
            Log.e("formatTag",e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage){
        try{
            if(tag == null){
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_LONG).show();
                return;
            }
            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                formatTag(tag,ndefMessage);
            }
            else{
                ndef.connect();
                if(!ndef.isWritable()){
                    Toast.makeText(this, "Tag is not writable !", Toast.LENGTH_LONG).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag is written !", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e){
            Log.e("writeNdefMessage",e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }

    public void tglReadWriteOnClick(View view){
        txtTagContent.setText("");
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }


    public void Backup() {
    }

}