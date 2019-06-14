package com.example.administrator.myapplicationqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreDataActivity extends AppCompatActivity {

    private String BASE_URL;

    Cursor c;
    ArrayList<HashMap<String, String>> MyArrList;
    HashMap<String, String> hashMap;
    ListView listView;

    Spinner sp1,sp2,sp3;
    ArrayAdapter<String> adapter1,adapter2,adapter3;

    String BUILDING_NAME;
    int BUILDING_RID,FLOOR_RID;
    String ROOM_RID;

    Cursor BUILDING_COLUMN,FLOOR_COLUMN,ROOM_COLUMN;
    Cursor C_BUILDING_RID,C_FLOOR_RID,C_ROOM_RID,C_LOCATION;

    Cursor STATUS,OWNER,INDOORMAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_data);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Toast.makeText(StoreDataActivity.this,"Login Success !" + "\nUsername : " + LoginActivity.globalUsername + "\nRefer ID : " + LoginActivity.globalReferID + "\nRefer Name : " + LoginActivity.globalRefername +  "\nPassword : " + LoginActivity.globalPassword
                //,Toast.LENGTH_LONG).show();

        listView = (ListView)findViewById(R.id.listview_1);

        refreshListView();
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

    public void onClearAllData(View v){
        final AlertDialog.Builder adb = new AlertDialog.Builder(StoreDataActivity.this);
        adb.setTitle("Confirm Clear All Data");
        adb.setMessage("Are you sure to clear all stored data ?");
        adb.setNegativeButton("No", null);
        adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.mydb.onDeleteTable("STORE_HISTORY_ASSET");
                Toast.makeText(StoreDataActivity.this,"ALL STORED DATA has been deleted !",Toast.LENGTH_LONG).show();
                refreshListView();
            }});
        adb.show();
    }

    public void onSubmit(View v){
        final AlertDialog.Builder adb = new AlertDialog.Builder(StoreDataActivity.this);
        adb.setTitle("Confirm Submit");
        adb.setMessage("Are you sure to submit ?");
        adb.setNegativeButton("No", null);
        adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
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
                    Toast.makeText(StoreDataActivity.this, "Your internet is not available !", Toast.LENGTH_SHORT).show();


                }
            }});
        adb.show();
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
                convertView = inflater.inflate(R.layout.list_row3, null);
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


            Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT HISTORY_IMAGE_RID,HISTORY_IMAGE FROM STORE_HISTORY_IMAGE INNER JOIN STORE_HISTORY_ASSET ON " +
                    "STORE_HISTORY_ASSET.HISTORY_RID = STORE_HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE STORE_HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ MyArrList.get(position).get("ASSETRID") });

            c.moveToFirst();

            if( ( c.getCount() <= 0) ){
            }else{
                c.moveToFirst();
                do{

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300,300);
                    layoutParams.setMargins(10,10,10,10);

                    ImageView imageViewAdd = new ImageView(StoreDataActivity.this);
                    imageViewAdd.setLayoutParams(layoutParams);

                    final byte[] byteArray = c.getBlob(1);
                    Bitmap resizedBitmap = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                    //Bitmap resizedBitmap = (Bitmap) Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length), 30, 40, true);
                    imageViewAdd.setImageBitmap(resizedBitmap);
                    imageViewAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bitmap bitmaptest2 = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(StoreDataActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.custom_photo_view, null);
                            PhotoView photoView = (PhotoView) mView.findViewById(R.id.imageView_PhotoZoom);
                            photoView.setImageBitmap(bitmaptest2);
                            //photoView.setImageResource(R.drawable.easyqr_background);
                            mBuilder.setView(mView);
                            AlertDialog mDialog = mBuilder.create();
                            mDialog.show();


                        }
                    });
                    gridLayout_PhotoAlbum.addView(imageViewAdd);

                }while(c.moveToNext());
            }

            Button button_Delete = (Button) convertView.findViewById(R.id.button_Delete);
            final AlertDialog.Builder adb = new AlertDialog.Builder(StoreDataActivity.this);
            button_Delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    adb.setTitle("Confirm Delete");
                    adb.setMessage("Are you sure to delete [" + MyArrList.get(position).get("ASSETID") +"]");
                    adb.setNegativeButton("No", null);
                    adb.setPositiveButton("Yes", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.mydb.onDelete_STORE_DATA2(MyArrList.get(position).get("ASSETRID"));

                            refreshListView();
                            listView.setSelection(position - 1);
                        }});
                    adb.show();
                }
            });

            return convertView;

        }
    }

    public void refreshListView(){

        // FETCHING DATA ==================================================================
        MyArrList = new ArrayList<HashMap<String, String>>();
        c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM STORE_HISTORY_ASSET ORDER BY HISTORY_YEAR ASC,HISTORY_MONTH ASC,HISTORY_DAY ASC,HISTORY_HOUR ASC,HISTORY_MINUTE ASC",null);
        while (c.moveToNext()) {
            hashMap = new HashMap<String, String>();
            hashMap.put( "ASSETRID", String.valueOf(c.getString(0)) );
            hashMap.put( "ASSETID", String.valueOf(c.getString(1)) );
            hashMap.put( "ASSETNAME", String.valueOf(c.getString(2)) );
            hashMap.put( "REFERID", String.valueOf(c.getString(3)));
            hashMap.put( "REFERNAME", String.valueOf(c.getString(4)));
            hashMap.put( "RECEIVEDATE", String.valueOf(c.getString(5)));
            hashMap.put( "SPEC", String.valueOf(c.getString(6)) );
            hashMap.put( "UNITNAME", String.valueOf(c.getString(7)) );
            hashMap.put( "LOCATION", String.valueOf(c.getInt(8)) + "-" + String.valueOf(c.getInt(10)) + "-" + String.valueOf(c.getString(11)) + " : " + String.valueOf(c.getString(9)) );
            hashMap.put( "DATE", String.valueOf(c.getInt(14)) + "-" + String.valueOf(c.getInt(13)) + "-" + String.valueOf(c.getInt(12)) );
            hashMap.put( "TIME", String.valueOf(c.getInt(15)) + " : " + String.valueOf(c.getInt(16)) );
            hashMap.put( "STATUS", String.valueOf(c.getString(17)) );
            hashMap.put( "SCANBY", String.valueOf(c.getString(18)) );
            hashMap.put( "NOTE", String.valueOf(c.getString(19)) );
            MyArrList.add(hashMap);
        }
        listView.setAdapter(new StoreDataActivity.TextAdapter(StoreDataActivity.this));

    }

    class Send extends AsyncTask<String, Void,Long > {



        protected Long doInBackground(String... urls) {

            /*
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.amsapp.net/save.php");
            Cursor C_STORE_HISTORY_ASSET = MainActivity.mydb.getAllRowsFromTable("STORE_HISTORY_ASSET");

            try {
                if (C_STORE_HISTORY_ASSET.getCount() != 0) {
                    while (C_STORE_HISTORY_ASSET.moveToNext()) {
                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ASSET_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(1)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ASSET_NAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(2)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_REFERID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(3)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_REFERNAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(4)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_RECEIVEDATE", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(5)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_SPEC", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(6)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_UNITNAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(7)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_BUILDING_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(8)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_BUILDING_NAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(9)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_FLOOR_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(10)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ROOM_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(11)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_DAY", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(12)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_MONTH", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(13)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_YEAR", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(14)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_HOUR", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(15)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_MINUTE", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(16)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_STATUS_NAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(17)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_USERNAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(18)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_NOTE", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(19)), "UTF-8").replace("+", "%20")) );
                        byte[] byteArray = C_STORE_HISTORY_ASSET.getBlob(20);
                        String encoded_string = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_PHOTO", encoded_string) );


                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httppost);
                    }
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;
            */
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(BASE_URL + "save.php");
            Cursor C_STORE_HISTORY_ASSET = MainActivity.mydb.getAllRowsFromTable("STORE_HISTORY_ASSET");

            try {
                if (C_STORE_HISTORY_ASSET.getCount() != 0) {
                    while (C_STORE_HISTORY_ASSET.moveToNext()){
                        JSONArray jArry=new JSONArray();
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ASSET_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(1)), "UTF-8").replace("+", "%20")) );

                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ASSET_NAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(2)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_REFERID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(3)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_REFERNAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(4)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_RECEIVEDATE", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(5)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_SPEC", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(6)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_UNITNAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(7)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_BUILDING_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(8)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_BUILDING_NAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(9)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_FLOOR_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(10)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_ROOM_ID", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(11)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_DAY", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(12)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_MONTH", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(13)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_YEAR", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(14)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_HOUR", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(15)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_MINUTE", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getInt(16)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_STATUS_NAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(17)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_USERNAME", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(18)), "UTF-8").replace("+", "%20")) );
                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_NOTE", URLEncoder.encode(String.valueOf(C_STORE_HISTORY_ASSET.getString(19)), "UTF-8").replace("+", "%20")) );


                        Cursor C_STORE_HISTORY_IMAGE = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT HISTORY_IMAGE_RID,HISTORY_IMAGE FROM STORE_HISTORY_IMAGE INNER JOIN STORE_HISTORY_ASSET ON " +
                                "STORE_HISTORY_ASSET.HISTORY_RID = STORE_HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE STORE_HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ String.valueOf(C_STORE_HISTORY_ASSET.getInt(0)) });
                        C_STORE_HISTORY_IMAGE.moveToFirst();

                        if( C_STORE_HISTORY_IMAGE.getCount() != 0 ){

                            C_STORE_HISTORY_IMAGE.moveToFirst();
                            do{
                                if(C_STORE_HISTORY_IMAGE.getBlob(1) != null){
                                    byte[] byteArray = C_STORE_HISTORY_IMAGE.getBlob(1);
                                    String encoded_string = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
                                    jArry.put(encoded_string);
                                }else{
                                    String encoded_string = null;
                                    jArry.put(encoded_string);
                                }
                            }while(C_STORE_HISTORY_IMAGE.moveToNext());

                        }

                        nameValuePairs.add(new BasicNameValuePair( "HISTORY_PHOTO", jArry.toString() ) );

                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpclient.execute(httppost);

                    }

                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

            return null;



        }
        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {
            Toast.makeText(StoreDataActivity.this,"ALL STORED DATA has been sent !",Toast.LENGTH_LONG).show();
            MainActivity.mydb.onDeleteTable("STORE_HISTORY_ASSET");
            //Intent intent = new Intent(StoreDataActivity.this,MainActivity.class);
            //startActivity(intent);
            finish();
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
}
