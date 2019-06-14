package com.example.administrator.myapplicationqr;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class GenerateQRActivity extends AppCompatActivity {

    TextView textView;
    EditText et1,et2,et3;
    ImageView img_qrcode;
    String txt_number;
    Spinner sp1;
    Cursor c1;

    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        et1 = (EditText)findViewById(R.id.et_number);
        et2 = (EditText)findViewById(R.id.et_number2);
        et3 = (EditText)findViewById(R.id.et_number3);
        img_qrcode = (ImageView) findViewById(R.id.img_qrcode);
        textView = (TextView)findViewById(R.id.textResult);


        // ITEM_SPINNER ============================================================================
        List<String> splist1 = new ArrayList<String>();
        splist1.add("ITEM LISTS");
        c1 = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM ASSET ORDER BY ASSETID ASC",null);
        if(c1.getCount() != 0) {
            while (c1.moveToNext()) {
                splist1.add( c1.getString(0) );
            }
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, splist1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1 = (Spinner) findViewById(R.id.spinner_addItemFromData);
        sp1.setAdapter(adapter1);

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if(position > 0){
                    Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE ASSETID = ?",new String[] {adapterView.getItemAtPosition(position).toString()});
                    et1.setText( String.valueOf(c.getString(0)) );
                    et2.setText( String.valueOf(c.getString(1)) );
                    et3.setText( String.valueOf(c.getString(2)) );
                }
                else{
                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // =========================================================================================
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

    public void onGenerate(View v){
        if( TextUtils.isEmpty( et1.getText().toString() ) ) { et1.setError("Please fill an Unique ID."); } else { i++; }
        if( TextUtils.isEmpty( et2.getText().toString() ) ) { et2.setError("Please fill a Serial Number."); } else { i++; }
        if( TextUtils.isEmpty( et3.getText().toString() ) ) { et3.setError("Please fill an Item's Name."); } else { i++; }
        if( i == 3){
            txt_number = et1.getText().toString() + "/" + et2.getText().toString() + "/" + et3.getText().toString();
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(txt_number, BarcodeFormat.QR_CODE,200,200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                img_qrcode.setImageBitmap(bitmap);
                textView.setText("Your QR Code is ready !!!");
                CurrentTime time = new CurrentTime();
                //MainActivity.mydb.onUpdate_ITEM_TABLE(ITEM_RID,OWNER_RID);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "Please fill all the informations.", Toast.LENGTH_SHORT).show();
        }
        i = 0;
    }

    public void onPrint(View v) {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = ((BitmapDrawable)img_qrcode.getDrawable()).getBitmap();
        if(bitmap == null){
            textView.setText("NULL");
        }
        photoPrinter.printBitmap("TEST JPEG.jpg", bitmap);
    }
}
