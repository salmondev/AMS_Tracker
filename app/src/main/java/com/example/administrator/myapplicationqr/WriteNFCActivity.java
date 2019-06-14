package com.example.administrator.myapplicationqr;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WriteNFCActivity extends AppCompatActivity {

    boolean password_protected = false;
    byte[] password;

    TextView textTest;
    ToggleButton tglButtonRW;
    Button button_Scan;
    TextView textPASSWORD,textITEM_UID;
    EditText etITEM_UID,etPASSWORD;
    NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_nfc);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        textTest = findViewById(R.id.textTest);
        tglButtonRW = findViewById(R.id.tglButtonRW);
        button_Scan = findViewById(R.id.button_Scan);
        textITEM_UID = findViewById(R.id.textITEM_UID);
        textPASSWORD = findViewById(R.id.textPASSWORD);
        etITEM_UID = findViewById(R.id.etITEM_UID);
        etPASSWORD = findViewById(R.id.etPASSWORD);

        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "NFC not available :( ", Toast.LENGTH_LONG).show();
        }

        if(tglButtonRW.isChecked()){
            Toast.makeText(this, "READ MODE", Toast.LENGTH_LONG).show();
            textPASSWORD.setVisibility(View.GONE);
            etPASSWORD.setVisibility(View.GONE);
            textITEM_UID.setVisibility(View.GONE);
            etITEM_UID.setVisibility(View.GONE);
            button_Scan.setVisibility(View.GONE);
            textTest.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(this, "WRITE MODE", Toast.LENGTH_LONG).show();
            textPASSWORD.setVisibility(View.VISIBLE);
            etPASSWORD.setVisibility(View.VISIBLE);
            textITEM_UID.setVisibility(View.VISIBLE);
            etITEM_UID.setVisibility(View.VISIBLE);
            button_Scan.setVisibility(View.VISIBLE);
            textTest.setVisibility(View.GONE);
        }
        //String s = StringUtils.rightPad("90001842-12", 20, '@').substring(0,3);

        //StringUtils.rightPad("foobar", 10, '@');
        //Toast.makeText(this, s + StringUtils.rightPad("abcdef", 20, '@'), Toast.LENGTH_SHORT).show(); //TOAST
/*
        byte n1 = 1;
        byte n2 = 4;
        byte n3 = 6;
        byte n4 = 9;
        Toast.makeText(this, new String(String.valueOf(n1)) + new String(String.valueOf(n4)), Toast.LENGTH_SHORT).show(); //TOAST
*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        disableForegroundDispatchSystem();
    }
    @Override
    protected void onResume() {
        super.onResume();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        enableForegroundDispatchSystem();
    }

    public void onChangeMode(View v){
        if(tglButtonRW.isChecked()){
            Toast.makeText(this, "READ MODE", Toast.LENGTH_LONG).show();
            textPASSWORD.setVisibility(View.GONE);
            etPASSWORD.setVisibility(View.GONE);
            textITEM_UID.setVisibility(View.GONE);
            etITEM_UID.setVisibility(View.GONE);
            button_Scan.setVisibility(View.GONE);
            textTest.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(this, "WRITE MODE", Toast.LENGTH_LONG).show();
            textPASSWORD.setVisibility(View.VISIBLE);
            etPASSWORD.setVisibility(View.VISIBLE);
            textITEM_UID.setVisibility(View.VISIBLE);
            etITEM_UID.setVisibility(View.VISIBLE);
            button_Scan.setVisibility(View.VISIBLE);
            textTest.setVisibility(View.GONE);
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

    private String[] getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String prefix = "android.nfc.tech.";
        String[] info = new String[2];

        // UID
        byte[] uid = tag.getId();
        info[0] = "UID In Hex: " + uid.toString() + "\n" +
                  "UID In Dec: " + uid.toString() + "\n\n";

        // Tech List
        String[] techList = tag.getTechList();
        String techListConcat = "Technologies: ";
        for(int i = 0; i < techList.length; i++) {
            techListConcat += techList[i].substring(prefix.length()) + ",";
        }
        info[0] += techListConcat.substring(0, techListConcat.length() - 1) + "\n\n";

        // Mifare Classic/UltraLight Info
        info[0] += "Card Type: ";
        String type = "Unknown";
        for(int i = 0; i < techList.length; i++) {
            if(techList[i].equals(MifareClassic.class.getName())) {
                info[1] = "Mifare Classic";
                MifareClassic mifareClassicTag = MifareClassic.get(tag);

                // Type Info
                switch (mifareClassicTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                info[0] += "Mifare " + type + "\n";

                // Size Info
                info[0] += "Size: " + mifareClassicTag.getSize() + " bytes \n" +
                        "Sector Count: " + mifareClassicTag.getSectorCount() + "\n" +
                        "Block Count: " + mifareClassicTag.getBlockCount() + "\n";
            } else if(techList[i].equals(MifareUltralight.class.getName())) {
                info[1] = "Mifare UltraLight";
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);

                // Type Info
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                info[0] += "Mifare " + type + "\n";
            } else if(techList[i].equals(IsoDep.class.getName())) {
                info[1] = "IsoDep";
                IsoDep isoDepTag = IsoDep.get(tag);
                info[0] += "IsoDep \n";
            } else if(techList[i].equals(Ndef.class.getName())) {
                Ndef ndefTag = Ndef.get(tag);
                info[0] += "Is Writable: " + ndefTag.isWritable() + "\n" +
                        "Can Make ReadOnly: " + ndefTag.canMakeReadOnly() + "\n";
            } else if(techList[i].equals(NdefFormatable.class.getName())) {
                NdefFormatable ndefFormatableTag = NdefFormatable.get(tag);
            }
        }

        return info;
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,WriteNFCActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
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

                Toast.makeText(this, "TAG PROTECTED PAGE START AT : " + new String(String.valueOf(readP40[7])), Toast.LENGTH_SHORT).show(); //TOAST
                if(readP40[7] >= 0 && readP40[7] <= 44){
                    password_protected = true;
                }
                else{
                    password_protected = false;
                }

                // READ_MODE
                if(tglButtonRW.isChecked()){
                    showAllPages(readP0,readP4,readP8,readP12,
                            readP16,readP20,readP24,readP28,
                            readP32,readP36,readP40,readP44);
                    showreadablePage(readP4,readP8,readP12,
                            readP16,readP20,readP24,readP28,
                            readP32,readP36);

                }
                // WRITE_MODE
                else{
                    /*
                    if(etPASSWORD.length() == 4){

                    }
                    if(etOWNER_UID.length() == 6){

                    }
                    if(etITEM_UID.length() == 20){

                    }
*/
                    //int intBuffer3 = (int) Math.ceil(etITEM_UID.length()/4);

                    password = etPASSWORD.getText().toString().getBytes();

                    // PASSWORD AUTHENTICATION =========================================================
                    byte[] response = mifareUltralight.transceive(new byte[] {
                            (byte) 0x1B, // PWD_AUTH
                            password[0],password[1],password[2],password[3]
                    });
                    if ((response != null) && (response.length >= 2)) {
                        byte[] pack = Arrays.copyOf(response, 2);
                        // TODO: verify PACK to confirm that tag is authentic (not really,
                        // but that whole PWD_AUTH/PACK authentication mechanism was not
                        // really meant to bring much security, I hope; same with the
                        // NTAG signature btw.)
                        Toast.makeText(this, "PASSWORD IS CORRECT !", Toast.LENGTH_SHORT).show(); //TOAST

                        String[] textBuffer = new String[6];

                        String stringBuffer1 = StringUtils.rightPad(etITEM_UID.getText().toString(),24,'@');
                        textBuffer[0] = stringBuffer1.substring(0,4);
                        textBuffer[1] = stringBuffer1.substring(4,8);
                        textBuffer[2] = stringBuffer1.substring(8,12);
                        textBuffer[3] = stringBuffer1.substring(12,16);
                        textBuffer[4] = stringBuffer1.substring(16,20);
                        textBuffer[5] = stringBuffer1.substring(20,24);

                        // WRITE_ITEM_UID_AT_PAGE4
                        for(int i = 4,j = 0 ; i < 10 ; i++,j++){
                            mifareUltralight.writePage(i, textBuffer[j].getBytes("TIS-620"));
                        }

/*
                        String stringBuffer1 = StringUtils.rightPad(etITEM_UID.getText().toString(),20,'@');
                        textBuffer[0] = stringBuffer1.substring(0,4);
                        textBuffer[1] = stringBuffer1.substring(4,8);
                        textBuffer[2] = stringBuffer1.substring(8,12);
                        textBuffer[3] = stringBuffer1.substring(12,16);
                        textBuffer[4] = stringBuffer1.substring(16,20);

                        // WRITE_ITEM_UID_AT_PAGE6
                        for(int i = 6,j = 0 ; i < 5 + 6 ; i++,j++){
                            mifareUltralight.writePage(i, textBuffer[j].getBytes("TIS-620"));
                        }

                        String stringBuffer2 = StringUtils.rightPad(etOWNER_UID.getText().toString(),20,'@');
                        textBuffer[0] = stringBuffer2.substring(0,4);
                        textBuffer[1] = stringBuffer2.substring(4,8);
                        textBuffer[2] = stringBuffer2.substring(8,12);
                        textBuffer[3] = stringBuffer2.substring(12,16);
                        textBuffer[4] = stringBuffer2.substring(16,20);
                        // WRITE_ITEM_UID_AT_PAGE6
                        for(int i = 11,j = 0 ; i < 5 + 11 ; i++,j++){
                            mifareUltralight.writePage(i, textBuffer[j].getBytes("TIS-620"));
                        }

                        String stringBuffer3 = StringUtils.rightPad(etITEM_STATUS.getText().toString(),20,'@');
                        textBuffer[0] = stringBuffer3.substring(0,4);
                        textBuffer[1] = stringBuffer3.substring(4,8);
                        textBuffer[2] = stringBuffer3.substring(8,12);
                        textBuffer[3] = stringBuffer3.substring(12,16);
                        textBuffer[4] = stringBuffer3.substring(16,20);
                        // WRITE_ITEM_UID_AT_PAGE6
                        for(int i = 16,j = 0 ; i < 5 + 16 ; i++,j++){
                            mifareUltralight.writePage(i, textBuffer[j].getBytes("TIS-620"));
                        }
*/
                        //Toast.makeText(this, "WRITE SUCCESSFULLY !", Toast.LENGTH_SHORT).show(); //TOAST
                        showMessage("System", "WRITE SUCCESSFULLY !");


                    }


                }


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
        if(mifareUltralight.isConnected()){
            Toast.makeText(this, "Connection : " + "YES", Toast.LENGTH_SHORT).show(); //TOAST
            if(tglButtonRW.isChecked()){

                try {
                    // PASSWORD AUTHENTICATION =========================================================
                    byte[] response = mifareUltralight.transceive(new byte[] {
                            (byte) 0x1B, // PWD_AUTH
                            (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04
                            //(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x03
                    });
                    if ((response != null) && (response.length >= 2)) {
                        byte[] pack = Arrays.copyOf(response, 2);
                        // TODO: verify PACK to confirm that tag is authentic (not really,
                        // but that whole PWD_AUTH/PACK authentication mechanism was not
                        // really meant to bring much security, I hope; same with the
                        // NTAG signature btw.)
                        Toast.makeText(this, "PACK : " + new String(String.valueOf(pack[0])) + new String(String.valueOf(pack[1])), Toast.LENGTH_SHORT).show(); //TOAST
                    }

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

                    byte bb[] = mifareUltralight.readPages(4);
                    Toast.makeText(this, "S 0 : " + (char) bb[0] + "\n" + "S 1 : " + (char) bb[1] + "\n" + "S 2 : " + (char) bb[2] + "\n" + "S 3 : " + (char) bb[3] + "\n", Toast.LENGTH_SHORT).show(); //TOAST

                    int pageNumber = 32;
                    String pageData = "NDEF";
                    mifareUltralight.writePage(pageNumber, pageData.getBytes("US-ASCII"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{

            }
            */
            //Toast.makeText(this, "Tag Type :" + mifareUltralight.getType() + "\n Tag Technologies : " + tag.toString(), Toast.LENGTH_SHORT).show(); //TOAST
            //Toast.makeText(this, "Max Length Transceive : " + mifareUltralight.getMaxTransceiveLength() + " bytes", Toast.LENGTH_SHORT).show(); //TOAST
                /*
                int[] i1 = bytearray2intarray(mifareUltralight.readPages(0));
                Toast.makeText(this, "I 0 : " + i1[0] + "\nI 1 : " + i1[1] + "\nI 2 : " + i1[2] + "\nI 3 : " + i1[3], Toast.LENGTH_SHORT).show(); //TOAST
                String s0 = new String(String.valueOf(i1[0]));
                String s1 = new String(String.valueOf(i1[1]));
                String s2 = new String(String.valueOf(i1[2]));
                String s3 = new String(String.valueOf(i1[3]));
                Toast.makeText(this, "S 0 : " + s0 + "\nS 1 : " + s1 + "\nS 2 : " + s2 + "\nS 3 : " + s3, Toast.LENGTH_SHORT).show(); //TOAST

                byte bb[] = mifareUltralight.readPages(4);
                String s0 = new String(String.valueOf(bb[0]));
                String s1 = new String(String.valueOf(bb[1]));
                String s2 = new String(String.valueOf(bb[2]));
                String s3 = new String(String.valueOf(bb[3]));
                Toast.makeText(this, "S 0 : " + s0 + "\nS 1 : " + s1 + "\nS 2 : " + s2 + "\nS 3 : " + s3, Toast.LENGTH_SHORT).show(); //TOAST

                int pageNumber = 4;
                String pageData = "TEST";
                mifareUltralight.writePage(pageNumber, pageData.getBytes("US-ASCII"));
                */

/*
                // WRITE PWD at page 43 ============================================================
                try {
                    byte[] response1 = mifareUltralight.transceive(new byte[] {
                            (byte) 0xA2, // WRITE
                            (byte) 0x2B,   // page address 2B = 43 (PWD B0,B1,B2,B3)
                            (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04 // (PWD 1,2,3,4)
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "ERROR1 : " + e, Toast.LENGTH_SHORT).show(); //TOAST
                }

                // WRITE PACK at page 44 ===========================================================
                try {
                    byte[] response2 = mifareUltralight.transceive(new byte[] {
                            (byte) 0xA2, // WRITE
                            (byte) 0x2C,   // page address 2C = 44 (PACK B0,B1,null,null)
                            (byte) 0x05, (byte) 0x06,   // bytes 0-1 are PACK value (PACK 5,6,0,0)
                            (byte) 0, (byte) 0  // other bytes are RFU and must be written as 0
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "ERROR2 : " + e, Toast.LENGTH_SHORT).show(); //TOAST
                }

                // WRITE ACCESS (AUTHLIM and PROT) at page 42 b0 (start at b0) =============================
                try {
                    byte[] response3 = mifareUltralight.transceive(new byte[] {
                            (byte) 0x30, // READ
                            (byte) 0x2A    // page address 2C = 42 (ACCESS B0,null,null,null)
                    });
                    if ((response3 != null) && (response3.length >= 16)) {  // read always returns 4 pages
                        boolean prot = false;  // false = PWD_AUTH for write only, true = PWD_AUTH for read and write
                        int authlim = 0; // value between 0 and 7
                        byte[] response4 = mifareUltralight.transceive(new byte[] {
                                (byte) 0xA2, // WRITE
                                (byte) 0x2A,   // page address 2C = 42 (ACCESS B0,null,null,null)
                                (byte) ((response3[0] & 0x078) | (prot ? 0x080 : 0x000) | (authlim & 0x007)),
                                response3[1], response3[2], response3[3]  // keep old value for bytes 1-3, you could also simply set them to 0 as they are currently RFU and must always be written as 0 (response[1], response[2], response[3] will contain 0 too as they contain the read RFU value)
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "ERROR3 : " + e, Toast.LENGTH_SHORT).show(); //TOAST
                }

                // WRITE AUTH0 at page 41 b3 (start at b0) =============================
                try {
                    byte[] response5 = mifareUltralight.transceive(new byte[] {
                            (byte) 0x30, // READ
                            (byte) 0x29    // page address 29 = 41 (AUTH0 null,null,null,0)
                    });
                    if ((response5 != null) && (response5.length >= 16)) {  // read always returns 4 pages
                        boolean prot = false;  // false = PWD_AUTH for write only, true = PWD_AUTH for read and write
                        int auth0 = 0; // first page to be protected, set to a value between 0 and 37 for NTAG212
                        byte[] response6 = mifareUltralight.transceive(new byte[] {
                                (byte) 0xA2, // WRITE
                                (byte) 0x29,   // page address 29 = 41 (AUTH0 null,null,null,0)
                                response5[0], // keep old value for byte 0
                                response5[1], // keep old value for byte 1
                                response5[2], // keep old value for byte 2
                                (byte) (auth0 & 0x0ff)
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "ERROR4 : " + e, Toast.LENGTH_SHORT).show(); //TOAST
                }
*/

/*
                // PASSWORD AUTHENTICATION =========================================================
                byte[] response = mifareUltralight.transceive(new byte[] {
                        (byte) 0x1B, // PWD_AUTH
                        (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04
                });
                if ((response != null) && (response.length >= 2)) {
                    byte[] pack = Arrays.copyOf(response, 2);
                    // TODO: verify PACK to confirm that tag is authentic (not really,
                    // but that whole PWD_AUTH/PACK authentication mechanism was not
                    // really meant to bring much security, I hope; same with the
                    // NTAG signature btw.)
                }
                int pageNumber = 4;
                String pageData = "ABCD";
                mifareUltralight.writePage(pageNumber, pageData.getBytes("US-ASCII"));
*/
/*
        }
        else{
            Toast.makeText(this, "Connection : " + "NO", Toast.LENGTH_SHORT).show(); //TOAST
        }
        //==========================================================================================
        try {
            mifareUltralight.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        /*
        super.onNewIntent(intent);
        Toast.makeText(this, "onNewIntent Method", Toast.LENGTH_SHORT).show(); //TOAST
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        String s = new String(tag.getId());
        Toast.makeText(this, "Tag ID : " + s, Toast.LENGTH_SHORT).show(); //TOAST

        NfcA nfcA = NfcA.get(tag);
        try {
            nfcA.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(nfcA.isConnected()){
            Toast.makeText(this, "Connection : " + "YES", Toast.LENGTH_SHORT).show(); //TOAST
            Toast.makeText(this, "Type of Tag : " + tag.toString(), Toast.LENGTH_SHORT).show(); //TOAST
            Toast.makeText(this, "Max Length Transceive : " + nfcA.getMaxTransceiveLength() + " bytes", Toast.LENGTH_SHORT).show(); //TOAST
        }
        else{
            Toast.makeText(this, "Connection : " + "NO", Toast.LENGTH_SHORT).show(); //TOAST
        }
        */

    }

    public void showAllPages(byte[] readP0,byte[] readP4,byte[] readP8,byte[] readP12,
                             byte[] readP16,byte[] readP20,byte[] readP24,byte[] readP28,
                             byte[] readP32,byte[] readP36,byte[] readP40,byte[] readP44){
        textTest.setText("");
        int j = 0;
        // PAGE 0 - 3
        String[] a1 = new String(readP0,Charset.forName("US-ASCII")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP0[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP0[k] + " " );
                    textTest.append( a1[k+1]);
                }
            }
        }
        // PAGE 4 - 7
        String[] a2 = new String(readP4,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP4[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP4[k] + " " );
                    textTest.append( a2[k+1]);
                }
            }
        }
        // PAGE 8 - 11
        String[] a3 = new String(readP8,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP8[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP8[k] + " " );
                    textTest.append( a3[k+1]);
                }
            }
        }
        // PAGE 12 - 15
        String[] a4 = new String(readP12,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP12[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP12[k] + " " );
                    textTest.append( a4[k+1]);
                }
            }
        }
        // PAGE 16 - 19
        String[] a5 = new String(readP16,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP16[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP16[k] + " " );
                    textTest.append( a5[k+1]);
                }
            }
        }
        // PAGE 20 - 23
        String[] a6 = new String(readP20,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP20[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP20[k] + " " );
                    textTest.append( a6[k+1]);
                }
            }
        }
        // PAGE 24 - 27
        String[] a7 = new String(readP24,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP24[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP24[k] + " " );
                    textTest.append( a7[k+1]);
                }
            }
        }
        // PAGE 28 - 31
        String[] a8 = new String(readP28,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP28[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP28[k] + " " );
                    textTest.append( a8[k+1]);
                }
            }
        }
        // PAGE 32 - 35
        String[] a9 = new String(readP32,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP32[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP32[k] + " " );
                    textTest.append( a9[k+1]);
                }
            }
        }
        // PAGE 36 - 39
        String[] a10 = new String(readP36,Charset.forName("TIS-620")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP36[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP36[k] + " " );
                    textTest.append( a10[k+1]);
                }
            }
        }
        // PAGE 40 - 43
        String[] a11 = new String(readP40,Charset.forName("US-ASCII")).toString().split("");
        for(int i = 0; i < 16; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP40[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP40[k] + " " );
                    textTest.append( a11[k+1]);
                }
            }
        }
        // PAGE 44
        String[] a12 = new String(readP44,Charset.forName("US-ASCII")).toString().split("");
        for(int i = 0; i < 4; i++){
            if(i%4 == 0){
                textTest.append("\nP" + j + " | ");
                j++;
            }
            textTest.append(String.format("%8s", Integer.toBinaryString(readP44[i] & 0xFF)).replace(' ', '0') + " ");
            if(i == 3 || i == 7 || i == 11 || i == 15){
                textTest.append( " | " );
                for(int k = i - 3; k <= i; k++){
                    //textTest.append( (char) readP44[k] + " " );
                    textTest.append( a12[k+1]);
                }
            }
        }

    }

    public void showreadablePage(byte[] readP4,byte[] readP8,byte[] readP12,
                                 byte[] readP16,byte[] readP20,byte[] readP24,byte[] readP28,
                                 byte[] readP32,byte[] readP36){
        String alltext = "";

        alltext = alltext + read4Pages(readP4);
        alltext = alltext + read4Pages(readP8);
        alltext = alltext + read4Pages(readP12);
        alltext = alltext + read4Pages(readP16);
        alltext = alltext + read4Pages(readP20);
        alltext = alltext + read4Pages(readP24);
        alltext = alltext + read4Pages(readP28);
        alltext = alltext + read4Pages(readP32);
        alltext = alltext + read4Pages(readP36);
        alltext = alltext.replaceAll("@","").trim();

        showMessage("System", "READ SUCCESSFULLY !\nData : " + alltext);
       //Toast.makeText(this, "Your data : \n" + alltext, Toast.LENGTH_LONG).show(); //TOAST
    }

    public String read4Pages(byte[] pages){
        String readtext = "";

        String[] a1 = new String(pages,Charset.forName("TIS-620")).toString().split("");
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
                Cursor C_LOCATION_BARCODE = MainActivity.mydb.rawQuery("SELECT * FROM LOCATION WHERE LOCATION_BARCODE = ?",new String[]{ result.getContents().toString() } );
                Cursor c = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ result.getContents().toString() });
                if( (c != null) && (c.getCount() > 0) ){
                    etITEM_UID.setText(result.getContents().toString());
                    Toast.makeText(this,"ASSET QR CODE : " + result.getContents().toString(),Toast.LENGTH_SHORT).show();
                }
                else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                    etITEM_UID.setText(result.getContents().toString());
                    Toast.makeText(this,"LOCATION QR CODE : " + result.getContents().toString(),Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(this, "UNKNOWN QR CODE : " + result.getContents().toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    void backuponNewIntent(){
                /*
        Toast.makeText(this, "onNewIntent", Toast.LENGTH_SHORT).show();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String prefix = "android.nfc.tech.";
        String[] info = new String[2];
        NfcA nfcA = NfcA.get(tag);
        try {
            nfcA.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(nfcA.isConnected()){
            Toast.makeText(this, "NfcA is connected.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "NfcA is not connected.", Toast.LENGTH_SHORT).show();
        }

        // UID
        byte[] uid = tag.getId();
        String s = new String(uid);
        info[0] = "UID In Hex: " + s + "\n" +
                "UID In Dec: " + s + "\n\n";

        //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();

        // Tech List
        String[] techList = tag.getTechList();
        String techListConcat = "Technologies: ";
        for(int i = 0; i < techList.length; i++) {
            techListConcat += techList[i].substring(prefix.length()) + ",";
        }
        info[0] += techListConcat.substring(0, techListConcat.length() - 1) + "\n\n";

        Toast.makeText(this, info[0], Toast.LENGTH_SHORT).show();

        // Mifare Classic/UltraLight Info
        info[0] += "Card Type: ";
        String type = "Unknown";
        for(int i = 0; i < techList.length; i++) {
            if(techList[i].equals(MifareClassic.class.getName())) {
                info[1] = "Mifare Classic";
                MifareClassic mifareClassicTag = MifareClassic.get(tag);

                // Type Info
                switch (mifareClassicTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                info[0] += "Mifare " + type + "\n";

                // Size Info
                info[0] += "Size: " + mifareClassicTag.getSize() + " bytes \n" +
                        "Sector Count: " + mifareClassicTag.getSectorCount() + "\n" +
                        "Block Count: " + mifareClassicTag.getBlockCount() + "\n";
            } else if(techList[i].equals(MifareUltralight.class.getName())) {
                info[1] = "Mifare UltraLight";
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);

                // Type Info
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                info[0] += "Mifare " + type + "\n";
            } else if(techList[i].equals(IsoDep.class.getName())) {
                info[1] = "IsoDep";
                IsoDep isoDepTag = IsoDep.get(tag);
                info[0] += "IsoDep \n";
            } else if(techList[i].equals(Ndef.class.getName())) {
                Ndef ndefTag = Ndef.get(tag);
                info[0] += "Is Writable: " + ndefTag.isWritable() + "\n" +
                        "Can Make ReadOnly: " + ndefTag.canMakeReadOnly() + "\n";
            } else if(techList[i].equals(NdefFormatable.class.getName())) {
                NdefFormatable ndefFormatableTag = NdefFormatable.get(tag);
            }
        }
        Toast.makeText(this, info[0], Toast.LENGTH_SHORT).show();



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



    public int[] bytearray2intarray(byte[] barray)
    {
        int[] iarray = new int[barray.length];
        int i = 0;
        for (byte b : barray)
            iarray[i++] = b & 0xff;
        return iarray;
    }
}
