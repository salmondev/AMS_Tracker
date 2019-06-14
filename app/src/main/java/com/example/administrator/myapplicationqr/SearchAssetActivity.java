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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import org.json.JSONArray;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchAssetActivity extends AppCompatActivity {

    private String BASE_URL;

    Cursor c;
    ArrayList<HashMap<String, String>> MyArrList;
    HashMap<String, String> hashMap;
    ListView listView;
    Button button_Search;
    EditText editText_Search;
    NfcAdapter nfcAdapter;

    Cursor STATUS;

    String status_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_asset);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        MainActivity.mydb.onDeleteTable("TEMP_HISTORY_ASSET");

        listView = (ListView)findViewById(R.id.listview_1);

        button_Search = (Button) findViewById(R.id.button_Search);
        editText_Search = (EditText) findViewById(R.id.editText_Search);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter!=null && nfcAdapter.isEnabled()){
            Toast.makeText(this, "NFC available!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "NFC not available :( ", Toast.LENGTH_LONG).show();
        }

        Cursor TEMP_STATUS = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM STATUS ORDER BY STATUS_ID ASC",null);
        // STATUS_SPINNER ========================================================================
        List<String> splist1 = new ArrayList<String>();
        splist1.add("STATUS");
        STATUS = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM STATUS ORDER BY STATUS_ID ASC",null);
        if(STATUS.getCount() != 0) {
            while (STATUS.moveToNext()) {
                splist1.add( STATUS.getString(1) );
            }
        }
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(SearchAssetActivity.this, android.R.layout.simple_spinner_item, splist1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sp1 = (Spinner) findViewById(R.id.spinner_Status2);
        sp1.setAdapter(adapter1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positionSpinner, long id) {
                if(positionSpinner > 0){
                    status_Name = adapterView.getItemAtPosition(positionSpinner).toString();
                    Toast.makeText(getApplicationContext(), "Status Name : " + status_Name, Toast.LENGTH_SHORT).show();
                }
                else{
                    status_Name = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //refreshListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);

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

    public void onClear(View v){
        editText_Search.setText("");
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
                    editText_Search.append(result.getContents().toString());
                    Toast.makeText(this,"ASSET QR CODE : " + result.getContents().toString(),Toast.LENGTH_SHORT).show();
                }
                else if( (C_LOCATION_BARCODE != null) && (C_LOCATION_BARCODE.getCount() > 0) ){
                    editText_Search.append(result.getContents().toString());
                    Toast.makeText(this,"LOCATION QR CODE : " + result.getContents().toString(),Toast.LENGTH_SHORT).show();

                }
                else{
                    editText_Search.append(result.getContents().toString());
                    Toast.makeText(this, "UNKNOWN QR CODE : " + result.getContents().toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onSearch (View v){
        refreshListView();
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
                convertView = inflater.inflate(R.layout.list_row4, null);
            }

            TextView textView_ASSET_ID = (TextView) convertView.findViewById(R.id.textView_ASSET_ID);
            textView_ASSET_ID.setText(getResources().getString(R.string.ASSET_ID) + MyArrList.get(position).get("ASSETID"));

            TextView textView_ASSET_NAME = (TextView) convertView.findViewById(R.id.textView_ASSET_NAME);
            textView_ASSET_NAME.setText(getResources().getString(R.string.ASSET_NAME) + MyArrList.get(position).get("ASSETNAME"));

            TextView textView_RECEIVEDATE = (TextView) convertView.findViewById(R.id.textView_RECEIVEDATE);
            textView_RECEIVEDATE.setText(getResources().getString(R.string.RECEIVEDATE) + MyArrList.get(position).get("RECEIVEDATE"));

            TextView textView_SPEC = (TextView) convertView.findViewById(R.id.textView_SPEC);
            textView_SPEC.setText(getResources().getString(R.string.SPEC) + MyArrList.get(position).get("SPEC"));

            TextView textView_UNITNAME = (TextView) convertView.findViewById(R.id.textView_UNITNAME);
            textView_UNITNAME.setText(getResources().getString(R.string.UNITNAME) + MyArrList.get(position).get("UNITNAME"));

            Button button_History = (Button) convertView.findViewById(R.id.button_History);
            final AlertDialog.Builder adb = new AlertDialog.Builder(SearchAssetActivity.this);
            button_History.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Cursor C_SORT_HISTORY = MainActivity.mydb.rawQuery("SELECT * FROM HISTORY_ASSET WHERE HISTORY_ASSET_ID = ? ORDER BY HISTORY_YEAR DESC,HISTORY_MONTH DESC,HISTORY_DAY DESC,HISTORY_HOUR DESC,HISTORY_MINUTE DESC",
                            new String[]{ MyArrList.get(position).get("ASSETID") });

                    if (C_SORT_HISTORY.getCount() == 0) {
                        // show message
                        showMessage("System", "This asset doesn't have any histories.");
                    }else{
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
                        */
                    }


                }
            });

            Button button_ManualSubmit = (Button) convertView.findViewById(R.id.button_ManualSubmit);
            button_ManualSubmit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(status_Name == null || status_Name == ""){

                        Toast.makeText(getApplicationContext(), "Please select Status for Manual Submit.", Toast.LENGTH_SHORT).show();

                    }
                    else{

                        MainActivity.mydb.onDeleteTable("TEMP_HISTORY_ASSET");

                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchAssetActivity.this);

                        CurrentTime currentTime = new CurrentTime();

                        Cursor C_STATUS = MainActivity.mydb.rawQuery("SELECT * FROM STATUS WHERE STATUS_ID = ?",new String[]{ String.valueOf(1) } );
                        Cursor C_ASSET = MainActivity.mydb.rawQuery("SELECT * FROM ASSET WHERE BARCODE = ?",new String[]{ MyArrList.get(position).get("ASSETID") } );
                        Cursor C_REFER = MainActivity.mydb.rawQuery("SELECT * FROM REFER WHERE REFERID = ?",new String[]{ C_ASSET.getString(2) } );

                        MainActivity.mydb.onInsert_TEMP_HISTORY_ASSET(-1,C_ASSET.getString(1),C_ASSET.getString(3),
                                C_REFER.getString(0),C_REFER.getString(1),C_ASSET.getString(4),
                                C_ASSET.getString(5),C_ASSET.getString(6),-1,
                                null,-1,null,
                                currentTime.getDay(),currentTime.getMonth(),currentTime.getYear(),
                                currentTime.getHour(),currentTime.getMin(),status_Name,
                                LoginActivity.globalRefername,"");

                        Cursor TT = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM TEMP_HISTORY_ASSET",new String[]{});
                        TT.moveToFirst();

                        builder.setTitle("Confirm Submit");
                        builder.setMessage(
                                getResources().getString(R.string.ASSET_ID) + TT.getString(1) + "\n" +
                                        getResources().getString(R.string.ASSET_NAME) + TT.getString(2) + "\n" +
                                        getResources().getString(R.string.REFER_ID) + TT.getString(3) + "\n" +
                                        getResources().getString(R.string.REFER_NAME) + TT.getString(4) + "\n" +
                                        getResources().getString(R.string.RECEIVEDATE) + TT.getString(5) + "\n" +
                                        getResources().getString(R.string.SPEC) + TT.getString(6) + "\n" +
                                        getResources().getString(R.string.UNITNAME) + TT.getString(7) + "\n" +
                                        getResources().getString(R.string.DATE) + String.valueOf(TT.getInt(14)) + "-" + String.valueOf(TT.getInt(13)) + "-" + String.valueOf(TT.getInt(12)) + "\n" +
                                        getResources().getString(R.string.TIME) + String.valueOf(TT.getInt(15)) + " : " + String.valueOf(TT.getInt(16)) + "\n" +
                                        getResources().getString(R.string.STATUS) + String.valueOf(TT.getString(17)) + "\n" +
                                        getResources().getString(R.string.SCAN_BY) + String.valueOf(TT.getString(18)) + "\n"
                        );

                        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                boolean connected = false;
                                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                                    //we are connected to a network
                                    connected = true;
                                    new Send().execute();
                                }
                                else{
                                    connected = false;
                                    final android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(SearchAssetActivity.this);
                                    adb.setTitle("System");
                                    adb.setMessage("Can't submit the record because your internet is not available.");
                                    adb.show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.show();

                    }

                }

            });


            return convertView;

        }
    }


    public void refreshListView(){
        String search_text = editText_Search.getText().toString().trim();
        //Toast.makeText(this,search_text,Toast.LENGTH_LONG).show();

        // FETCHING DATA ==================================================================
        MyArrList = new ArrayList<HashMap<String, String>>();
        c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM ASSET WHERE BARCODE LIKE '%" + search_text + "%' OR ASSETNAME LIKE '%" + search_text + "%'" +
                " OR SPEC LIKE '%" + search_text + "%'" + " OR REFERIDITEM LIKE '%" + search_text + "%'" +
                " ORDER BY BARCODE ASC",new String[]{ });
        /*
        c.moveToFirst();
        if (c.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            //return;
        }
        else{
            int i = 1;
            StringBuffer buffer = new StringBuffer();
            c.moveToFirst();
            do{
                buffer.append(i + "=-------------------=" + "\n\n");
                buffer.append("ASSETID : " + c.getString(0) + "\n");
                buffer.append("BARCODE : " + c.getString(1) + "\n");
                buffer.append("REFERIDITEM : " + c.getString(2) + "\n");
                buffer.append("ASSETNAME : " + c.getString(3) + "\n");
                buffer.append("RECEIVEDATE : " + c.getString(4) + "\n");
                buffer.append("SPEC : " + c.getString(5) + "\n");
                buffer.append("UNITNAME : " + c.getString(6) + "\n\n");
                i++;
            }while (c.moveToNext());

            // Show all data
            showMessage("ASSET", buffer.toString());
        }
*/

        c.moveToFirst();
        if (c.getCount() == 0) {
            Toast.makeText(this,c.getCount() + " result matches word : '" + search_text + "'",Toast.LENGTH_LONG).show();
            listView.setAdapter(null);
        }
        else{

            c.moveToFirst();
            do {
                hashMap = new HashMap<String, String>();
                hashMap.put( "ASSETID", String.valueOf(c.getString(1)) );
                hashMap.put( "ASSETNAME", String.valueOf(c.getString(3)) );
                hashMap.put( "RECEIVEDATE", String.valueOf(c.getString(4)) );
                hashMap.put( "SPEC", String.valueOf(c.getString(5)));
                hashMap.put( "UNITNAME", String.valueOf(c.getString(6)));
                MyArrList.add(hashMap);
            }while(c.moveToNext());
            Toast.makeText(this,c.getCount() + " results match word : '" + search_text + "'",Toast.LENGTH_LONG).show();
            listView.setAdapter(new SearchAssetActivity.TextAdapter(SearchAssetActivity.this));
        }


    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this,SearchAssetActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
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

        editText_Search.append(alltext);
        //Toast.makeText(this, "Your data : \n" + alltext, Toast.LENGTH_LONG).show(); //TOAST
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

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
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
            //finish();
            Toast.makeText(getApplicationContext(), "Submit Successfully !", Toast.LENGTH_SHORT).show();
        }

    }
}
