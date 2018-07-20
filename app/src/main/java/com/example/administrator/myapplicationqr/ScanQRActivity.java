package com.example.administrator.myapplicationqr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

public class ScanQRActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
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

    public void Backup(){
    /*
    public static int ITEM_RID,OWNER_RID;
    public static long UNIQUE_ID;
    public static String SERIAL;
    public static String ITEM_NAME;
    public static String FIRST_NAME;
    public static String LAST_NAME;
    public static int OWNER_UID;
    public static int DAY;
    public static int MONTH;
    public static int YEAR;
    public boolean MARK_ON_MAP_ENABLE = false;

    private static final int SCAN_BY_CAMERA = 1;
    private static final int SCAN_FROM_IMAGE = 2;
    private int STORAGE_PERMISSION_CODE = 10;

    Intent itn;
    TextView textResult;
    CurrentTime time1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        time1 = new CurrentTime();
        textResult = (TextView)findViewById(R.id.textResult);

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

    public void onScanFromImage(View v){

    }

    public void onScanByCamera(View v){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Please Focus Your Camera on QR Code.");
        integrator.setOrientationLocked(true);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SCAN_BY_CAMERA ==========================================================================
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this,"You cancelled the scanning",Toast.LENGTH_SHORT).show();
            }
            else {
                //IF Get the QR CODE ===============================================================
                //Toast.makeText(this,result.getContents(),Toast.LENGTH_SHORT).show();
                String[] split = result.getContents().toString().split("-");//textResult.getText().toString().split("-");
                Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ITEM_TABLE WHERE ITEM_UID = ?",new String[] { split[0] });
                ITEM_RID = c.getInt(0);
                c = MainActivity.mydb.rawQuery("SELECT * FROM OWNER_TABLE WHERE OWNER_UID = ?",new String[] { split[5] });
                OWNER_RID = c.getInt(0);
                UNIQUE_ID = Long.parseLong(split[0]);
                SERIAL = split[1];
                ITEM_NAME = split[2];
                FIRST_NAME = split[3];
                LAST_NAME = split[4];
                OWNER_UID = Integer.parseInt(split[5]);
                DAY = time1.getDay();
                MONTH = time1.getMonth() + 1;
                YEAR = time1.getYear();
                MARK_ON_MAP_ENABLE = true;
                textResult.setText(" Item ID : " + ITEM_RID + " Owner ID : " + OWNER_RID + "\n"
                        + " Item Unique ID : " + split[0] + "\n"
                        + " Item Serial Number : " + split[1] + "\n"
                        + " Item name : " + split[2] + "\n"
                        + " Owner Unique ID : " + split[5] + "\n"
                        + " Owner Name : " + split[3] + " " + split[4] + "\n"
                        + " Date : " + time1.getDay() + "/" + (time1.getMonth() + 1) + "/" + time1.getYear());
                //Toast.makeText(this,"ITEM_RID : " + ITEM_RID,Toast.LENGTH_LONG).show();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
        // SCAN_FROM_IMAGE =========================================================================
    }

    public void onMarkOnMap (View v){
        if(MARK_ON_MAP_ENABLE == true){
            itn = new Intent(this,MapActivity.class);
            startActivity(itn);
        }
        else{
            Toast.makeText(this,"Please Scan any QR Code first.",Toast.LENGTH_LONG).show();
        }

    }
}
*/
    }
}