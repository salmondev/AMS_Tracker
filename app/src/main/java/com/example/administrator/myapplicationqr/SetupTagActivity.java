package com.example.administrator.myapplicationqr;

import android.app.PendingIntent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class SetupTagActivity extends AppCompatActivity {

    boolean password_protected = false;
    byte[] current_password;
    byte[] new_password;

    NfcAdapter nfcAdapter;
    ToggleButton tglButtonSETUP;
    TextView textNEWPASSWORD,textCURRENTPASSWORD;
    EditText etNEWPASSWORD,etCURRENTPASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_tag);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        tglButtonSETUP = findViewById(R.id.tglButtonSETUP);
        etNEWPASSWORD = findViewById(R.id.etNEWPASSWORD);
        etCURRENTPASSWORD = findViewById(R.id.etCURRENTPASSWORD);
        textNEWPASSWORD = findViewById(R.id.textNEWPASSWORD);
        textCURRENTPASSWORD = findViewById(R.id.textCURRENTPASSWORD);

        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "NFC not available :( ", Toast.LENGTH_LONG).show();
        }

        /*
        tglButtonSETUP.setChecked(true);
        checkBox_FirstTimeSetup.setVisibility(View.VISIBLE);
        textNEWPASSWORD.setVisibility(View.VISIBLE);
        etNEWPASSWORD.setVisibility(View.VISIBLE);
        textCURRENTPASSWORD.setVisibility(View.GONE);
        etCURRENTPASSWORD.setVisibility(View.GONE);

*/
        if(tglButtonSETUP.isChecked()){
            Toast.makeText(this, "SETUP TAG MODE", Toast.LENGTH_LONG).show();
            textNEWPASSWORD.setVisibility(View.VISIBLE);
            etNEWPASSWORD.setVisibility(View.VISIBLE);
            textCURRENTPASSWORD.setVisibility(View.GONE);
            etCURRENTPASSWORD.setVisibility(View.GONE);
        }
        else{
            Toast.makeText(this, "CHANGE TAG PASSWORD MODE", Toast.LENGTH_LONG).show();
            textNEWPASSWORD.setVisibility(View.VISIBLE);
            etNEWPASSWORD.setVisibility(View.VISIBLE);
            textCURRENTPASSWORD.setVisibility(View.VISIBLE);
            etCURRENTPASSWORD.setVisibility(View.VISIBLE);
        }
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
        if(tglButtonSETUP.isChecked()){
            Toast.makeText(this, "SETUP TAG MODE", Toast.LENGTH_LONG).show();
            textNEWPASSWORD.setVisibility(View.VISIBLE);
            etNEWPASSWORD.setVisibility(View.VISIBLE);
            textCURRENTPASSWORD.setVisibility(View.GONE);
            etCURRENTPASSWORD.setVisibility(View.GONE);
        }
        else{
            Toast.makeText(this, "CHANGE TAG PASSWORD MODE", Toast.LENGTH_LONG).show();
            textNEWPASSWORD.setVisibility(View.VISIBLE);
            etNEWPASSWORD.setVisibility(View.VISIBLE);
            textCURRENTPASSWORD.setVisibility(View.VISIBLE);
            etCURRENTPASSWORD.setVisibility(View.VISIBLE);
        }
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,SetupTagActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
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

                // SETUP_TAG_MODE
                if(tglButtonSETUP.isChecked()){
                    if(password_protected == true){
                        current_password = etCURRENTPASSWORD.getText().toString().getBytes();
                        // PASSWORD AUTHENTICATION =========================================================
                        byte[] response = mifareUltralight.transceive(new byte[] {
                                (byte) 0x1B, // PWD_AUTH
                                current_password[0],current_password[1],current_password[2],current_password[3]
                        });
                        if ((response != null) && (response.length >= 2)) {
                            byte[] pack = Arrays.copyOf(response, 2);
                            // TODO: verify PACK to confirm that tag is authentic (not really,
                            // but that whole PWD_AUTH/PACK authentication mechanism was not
                            // really meant to bring much security, I hope; same with the
                            // NTAG signature btw.)
                            Toast.makeText(this, "PASSWORD IS CORRECT !", Toast.LENGTH_SHORT).show(); //TOAST
                        }
                    }

                    new_password = etNEWPASSWORD.getText().toString().getBytes();
                    // WRITE PWD at page 43 ============================================================
                    try {
                        byte[] response1 = mifareUltralight.transceive(new byte[] {
                                (byte) 0xA2, // WRITE
                                (byte) 0x2B,   // page address 2B = 43 (PWD B0,B1,B2,B3)
                                new_password[0],new_password[1],new_password[2],new_password[3]
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
                            int auth0 = 0; // first page to be protected, set to a value between 0 and 37 for NTAG213
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
                    //CLEAR ALL PAGES TO @@@@
                    for(int i = 4,j = 0 ; i < 40 ; i++,j++){
                        mifareUltralight.writePage(i, "@@@@".getBytes("TIS-620"));
                    }

                    //Toast.makeText(this, "SETUP SUCCESSFULLY ! : ", Toast.LENGTH_SHORT).show(); //TOAST
                    showMessage("System", "SETUP SUCCESSFULLY !");


                }
                // CHANGE_PASSWORD_MODE
                else{
                    if(password_protected == true){
                        current_password = etCURRENTPASSWORD.getText().toString().getBytes();
                        // PASSWORD AUTHENTICATION =========================================================
                        byte[] response = mifareUltralight.transceive(new byte[] {
                                (byte) 0x1B, // PWD_AUTH
                                current_password[0],current_password[1],current_password[2],current_password[3]
                        });
                        if ((response != null) && (response.length >= 2)) {
                            byte[] pack = Arrays.copyOf(response, 2);
                            // TODO: verify PACK to confirm that tag is authentic (not really,
                            // but that whole PWD_AUTH/PACK authentication mechanism was not
                            // really meant to bring much security, I hope; same with the
                            // NTAG signature btw.)
                            Toast.makeText(this, "PASSWORD IS CORRECT !", Toast.LENGTH_SHORT).show(); //TOAST
                        }
                    }
                    new_password = etNEWPASSWORD.getText().toString().getBytes();
                    // WRITE PWD at page 43 ============================================================
                    try {
                        byte[] response1 = mifareUltralight.transceive(new byte[] {
                                (byte) 0xA2, // WRITE
                                (byte) 0x2B,   // page address 2B = 43 (PWD B0,B1,B2,B3)
                                new_password[0],new_password[1],new_password[2],new_password[3]
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "ERROR1 : " + e, Toast.LENGTH_SHORT).show(); //TOAST
                    }
                    //Toast.makeText(this, "CHANGE PASSWORD SUCCESSFULLY ! : ", Toast.LENGTH_SHORT).show(); //TOAST
                    showMessage("System", "CHANGE PASSWORD SUCCESSFULLY !");
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

    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
