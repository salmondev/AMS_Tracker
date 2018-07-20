package com.example.administrator.myapplicationqr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements View.OnTouchListener,View.OnClickListener {

    ImageView imageView;
    TextView txtResult;
    TextView txtResult2;
    RelativeLayout rl;
    Spinner sp1;
    Cursor c1;
    ImageButtonText btn1;
    public int MAP_RID = 0,MAP_POS_X = 0,MAP_POS_Y = 0;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        i = 0;
        imageView = (ImageView)findViewById(R.id.imageView);
        txtResult = (TextView)findViewById(R.id.txtResult);
        txtResult2 = (TextView)findViewById(R.id.txtResult2);
        rl = (RelativeLayout)findViewById(R.id.rl1);
        rl.setOnTouchListener(this);
/*
        btn1 = new ImageButtonText(MapActivity.this,0,0,0,ScanQRActivity.DAY,ScanQRActivity.MONTH,ScanQRActivity.YEAR, // INFO FOR MAP_TABLE
                ScanQRActivity.ITEM_RID,ScanQRActivity.UNIQUE_ID,ScanQRActivity.SERIAL,ScanQRActivity.ITEM_NAME, // INFO FOR ITEM_TABLE
                ScanQRActivity.OWNER_RID,ScanQRActivity.OWNER_UID,ScanQRActivity.FIRST_NAME,ScanQRActivity.LAST_NAME); // INFO FOR OWNER_TABLE
                */
        refreshText();

        // ITEM_SPINNER ============================================================================
        List<String> splist1 = new ArrayList<String>();
        splist1.add("Choose Map");
        //c1 = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM MAP_TABLE WHERE MAP_STATUS = ? ORDER BY MAP_NAME ASC",new String[] {String.valueOf(1)});
        if(c1.getCount() != 0) {
            while (c1.moveToNext()) {
                splist1.add( c1.getString(2) );
            }
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splist1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1 = (Spinner) findViewById(R.id.spinner_addMapFromData);
        sp1.setAdapter(adapter1);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position > 0){
                    /*
                    Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM MAP_TABLE WHERE MAP_NAME = ? AND MAP_STATUS = ?",new String[] {adapterView.getItemAtPosition(position).toString(),String.valueOf(1)});
                    byte[] imgByte = c.getBlob(1);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte,0,imgByte.length);
                    imageView.setImageBitmap(bitmap);
                    MAP_RID = c.getInt(0);
                    */
                }
                else{
                    //MAP_RID = 0;
                }
                btn1.MAP_RID = MAP_RID;
                refreshText();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
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
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.rl1 && event.getAction() == MotionEvent.ACTION_DOWN && i == 0){
            /*
            btn1 = new ImageButtonText(MapActivity.this,0,0,0,MAP_RID,ScanQRActivity.ITEM_RID,
                    ScanQRActivity.UNIQUE_ID,ScanQRActivity.SERIAL,ScanQRActivity.ITEM_NAME,ScanQRActivity.FIRST_NAME,ScanQRActivity.LAST_NAME,ScanQRActivity.OWNER_UID,
                    ScanQRActivity.DAY,ScanQRActivity.MONTH,ScanQRActivity.YEAR);
                    */
            btn1.setImageResource(R.drawable.presence_online);
            btn1.setBackgroundColor(Color.TRANSPARENT);
            btn1.POS_X = (int) event.getX();
            btn1.POS_Y = (int) event.getY();
            RelativeLayout.LayoutParams bp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            bp.leftMargin = (int) btn1.POS_X - 18;
            bp.topMargin = (int) btn1.POS_Y - 25;
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // onClick do something
                    Toast.makeText(MapActivity.this,"You clicked on button",Toast.LENGTH_SHORT).show();
                }
            });
            btn1.setLayoutParams(bp);
            rl.addView(btn1);
            txtResult.setText("Touch again to change your location.");
            refreshText();
            i++;
            return true;
        }
        else if(v.getId() == R.id.rl1 && i > 0){
            btn1.MAP_RID = MAP_RID;
            btn1.POS_X = (int) event.getX();
            btn1.POS_Y = (int) event.getY();
            RelativeLayout.LayoutParams bp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            bp.leftMargin = (int) btn1.POS_X - 18;
            bp.topMargin = (int) btn1.POS_Y - 25;
            btn1.setLayoutParams(bp);
            refreshText();
            i++;
            return true;
        }
        return false;
    }

    public void refreshText(){
        txtResult2.setText(" Item ID : " + btn1.ITEM_RID + " Owner ID : " + btn1.OWNER_RID + " Map ID : " + btn1.MAP_RID + "\n"
                + " Item Unique ID : " + btn1.UNIQUE_ID + "\n"
                + " Item Serial Number : " + btn1.SERIAL + "\n"
                + " Item name : " + btn1.ITEM_NAME + "\n"
                + " Owner Unique ID : " + btn1.OWNER_UID + "\n"
                + " Owner Name : " + btn1.FIRST_NAME + " " + btn1.LAST_NAME + "\n"
                + " Date : " + btn1.DAY + "/" + btn1.MONTH + "/" + btn1.YEAR + "\n"
                + " X : " + btn1.POS_X + " Y : " + btn1.POS_Y + "\n");
    }

    public void onSubmit(View v){
        if(i >= 1){
            //MainActivity.mydb.onInsert_HISTORY_TABLE(btn1.POS_X,btn1.POS_Y,btn1.DAY,btn1.MONTH,btn1.YEAR,btn1.ITEM_RID,btn1.OWNER_RID,btn1.MAP_RID);

            Intent itn = new Intent(this,MainActivity.class);
            startActivity(itn);
        }
        else{
            Toast.makeText(MapActivity.this,"Please mark your position on map first",Toast.LENGTH_SHORT).show();
        }
    }
}
