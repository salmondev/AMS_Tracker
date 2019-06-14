package com.example.administrator.myapplicationqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAssetActivity extends AppCompatActivity {
    ImageView imageView_Person;
    TextView textView_globalUsername,textView_globalRefername,textView_globalReferid;
    ArrayList<HashMap<String, String>> MyArrList;
    HashMap<String, String> hashMap;
    ListView listView;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_asset);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        imageView_Person = (ImageView) findViewById(R.id.imageView_DefaultPerson);
        textView_globalUsername = (TextView) findViewById(R.id.textView_globalUsername);
        textView_globalRefername = (TextView) findViewById(R.id.textView_globalRefername);
        textView_globalReferid = (TextView) findViewById(R.id.textView_globalReferid);
        listView = (ListView)findViewById(R.id.listview_1);

        if(LoginActivity.globalUsername != null){
            textView_globalUsername.setText(getResources().getString(R.string.USER_USERNAME) + LoginActivity.globalUsername);
        }
        if(LoginActivity.globalRefername != null){
            textView_globalRefername.setText(getResources().getString(R.string.USER_REFER_NAME) + LoginActivity.globalRefername);
        }
        if(LoginActivity.globalReferID != null){
            textView_globalReferid.setText(getResources().getString(R.string.USER_REFER_ID) + LoginActivity.globalReferID);
        }
        if(LoginActivity.globalAuth.equals("ADMIN")){
            imageView_Person.setImageResource(R.drawable.easyqr_defaultadmin);
        }
        else{
            imageView_Person.setImageResource(R.drawable.easyqr_defaultuser);
        }

        refreshListView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //mydb.onDeleteTable("TEMP_HISTORY");
    }
    @Override
    protected void onStop() {
        super.onStop();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //mydb.onDeleteTable("TEMP_HISTORY");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //mydb.onDeleteTable("TEMP_HISTORY");
    }
    @Override
    protected void onPause() {
        super.onPause();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //mydb.onDeleteTable("TEMP_HISTORY");
        //disableForegroundDispatchSystem();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //mydb.onDeleteTable("TEMP_HISTORY");
    }
    @Override
    protected void onResume() {
        super.onResume();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //mydb.onDeleteTable("TEMP_HISTORY");
        //enableForegroundDispatchSystem();
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
            final AlertDialog.Builder adb = new AlertDialog.Builder(MyAssetActivity.this);
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
/*
                    if (C_SORT_HISTORY.getCount() == 0) {
                        // show message
                        showMessage("System", "This asset doesn't have any histories.");
                    }else{
                        C_SORT_HISTORY.moveToFirst();
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
                    }
*/

                }
            });

            return convertView;

        }
    }

    public void refreshListView(){

        // FETCHING DATA ==================================================================
        MyArrList = new ArrayList<HashMap<String, String>>();
        c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM ASSET INNER JOIN REFER ON ASSET.REFERIDITEM = REFER.REFERID WHERE ASSET.REFERIDITEM = ? ORDER BY ASSETID ASC",new String[]{ LoginActivity.globalReferID });

        if (c.getCount() == 0) {
            // show message
            showMessage("System", "You don't own any assets.");
            return;
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
            listView.setAdapter(new MyAssetActivity.TextAdapter(MyAssetActivity.this));
        }
/*
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
            buffer.append("UNITNAME : " + c.getString(6) + "\n");
            buffer.append("REFER.REFERID : " + c.getString(7) + "\n");
            buffer.append("REFER.REFERNAME : " + c.getString(8) + "\n");
            buffer.append("REFER.DEPARTMENTID : " + c.getInt(9) + "\n");
            i++;
        }while(c.moveToNext());

        // Show all data
        showMessage("TEMP_HISTORY_ASSET", buffer.toString());
        */

    }

    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }
}
