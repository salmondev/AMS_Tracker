package com.example.administrator.myapplicationqr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

    private String BASE_URL;

    Cursor c;
    ArrayList<HashMap<String, String>> MyArrList;
    HashMap<String, String> hashMap;
    ListView listView;

    TextView textView_Title;
    String intent_AssetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        intent_AssetId = getIntent().getStringExtra("ASSETID");

        listView = (ListView)findViewById(R.id.listview_1);
        textView_Title = findViewById(R.id.textView_Title);
        textView_Title.setText("HISTORY ASSET ID : " + intent_AssetId);


        //Toast.makeText(this,"ASSETID : " + intent_AssetId,Toast.LENGTH_LONG).show();
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

    public class TextAdapter extends BaseAdapter
    {
        private Boolean addfinish = false;
        private Context context;

        public TextAdapter(Context contextIn)
        {
            // TODO Auto-generated method stub
            context = contextIn;
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
                convertView = inflater.inflate(R.layout.list_row5, null);
                //Toast.makeText(HistoryActivity.this,"ASSETID : " + intent_AssetId,Toast.LENGTH_LONG).show();

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

            Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM HISTORY_IMAGE INNER JOIN HISTORY_ASSET ON " +
                    "HISTORY_ASSET.HISTORY_RID = HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ MyArrList.get(position).get("ASSETRID") });
            c.moveToFirst();
            if( c.getCount() <= 0 ){

            }else{
                c.moveToFirst();
                do{
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300,300);
                    //layoutParams.setMargins(10,10,10,10);

                    ImageView imageViewAdd = new ImageView(HistoryActivity.this);
                    //imageViewAdd.setLayoutParams(layoutParams);
                    Glide
                            .with(context)
                            .load( BASE_URL + c.getString(2) )
                            .override(300,300)
                            .centerCrop()
                            .into(imageViewAdd);

                    final String s = BASE_URL + c.getString(2);

                    imageViewAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(HistoryActivity.this);
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

                    gridLayout_PhotoAlbum.addView(imageViewAdd);

                }while(c.moveToNext());
            }

/*
            LinearLayout linearLayout_PhotoAlbum = (LinearLayout)findViewById(R.id.linearLayout_PhotoAlbum);
            Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM HISTORY_IMAGE INNER JOIN HISTORY_ASSET ON " +
                    "HISTORY_ASSET.HISTORY_RID = HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ MyArrList.get(position).get("ASSETRID") });
            c.moveToFirst();
            if( ( c == null ) && ( c.getCount() <= 0) ){

            }else{
                c.moveToFirst();
                do{
                    ImageView imageViewAdd = new ImageView(HistoryActivity.this);
                    Glide
                            .with(context)
                            .load( c.getString(2) )
                            .centerCrop()
                            .into(imageViewAdd);


                    final String s = c.getString(2);

                    imageViewAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(HistoryActivity.this);
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

                    linearLayout_PhotoAlbum.addView(imageViewAdd);

                }while(c.moveToNext());
            }
*/
            //setRID(MyArrList.get(position).get("ASSETID"));

            //ImageView imageView_Photo = (ImageView) convertView.findViewById(R.id.imageView_Photo);
/*
            LinearLayout linearLayout_PhotoAlbum = (LinearLayout)convertView.findViewById(R.id.linearLayout_PhotoAlbum);
            //linearLayout_PhotoAlbum.view

            Cursor c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM HISTORY_IMAGE INNER JOIN HISTORY_ASSET ON " +
                    "HISTORY_ASSET.HISTORY_RID = HISTORY_IMAGE.HISTORY_IMAGE_HISTORY_RID WHERE HISTORY_ASSET.HISTORY_RID = ? ORDER BY HISTORY_IMAGE_RID ASC",new String[]{ MyArrList.get(position).get("ASSETRID") });
            if(addfinish == false){
                c.moveToFirst();
                if( ( c == null ) && ( c.getCount() <= 0) ){

                }else{
                    c.moveToFirst();
                    do{
                        ImageView imageViewAdd = new ImageView(HistoryActivity.this);
                    Glide
                            .with(convertView)
                            .load( c.getString(2) )
                            .centerCrop()
                            .into(imageViewAdd);


                        final String s = c.getString(2);

                    imageViewAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(HistoryActivity.this);
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

                        linearLayout_PhotoAlbum.addView(imageViewAdd);

                    }while(c.moveToNext());
                }
                addfinish = true;
            }
            else{

            }
            */


/*
            if( MyArrList.get(position).get("PHOTO").isEmpty() ){
                //Toast.makeText(HistoryActivity.this,"Empty",Toast.LENGTH_LONG).show();
                imageView_Photo.setImageResource(0);
                imageView_Photo.setVisibility(View.GONE);
            }else{
                imageView_Photo.setVisibility(View.VISIBLE);
                Glide
                        .with(convertView)
                        .load(MyArrList.get(position).get("PHOTO"))
                        .centerCrop()
                        .into(imageView_Photo);
                imageView_Photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(HistoryActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.custom_photo_view, null);
                        PhotoView photoView = (PhotoView) mView.findViewById(R.id.imageView_PhotoZoom);
                        Glide
                                .with(mView)
                                .load(MyArrList.get(position).get("PHOTO"))
                                .centerCrop()
                                .into(photoView);
                        //photoView.setImageResource(R.drawable.easyqr_background);
                        mBuilder.setView(mView);
                        AlertDialog mDialog = mBuilder.create();
                        mDialog.show();
                    }
                });
            }
*/
            return convertView;

        }

    }

    public void refreshListView(){
        // FETCHING DATA ==================================================================
        MyArrList = new ArrayList<HashMap<String, String>>();
        c = MainActivity.mydb.rawQueryWithoutMoveToFirst("SELECT * FROM HISTORY_ASSET WHERE HISTORY_ASSET_ID = ? ORDER BY HISTORY_YEAR DESC,HISTORY_MONTH DESC,HISTORY_DAY DESC,HISTORY_HOUR DESC,HISTORY_MINUTE DESC",new String[]{ intent_AssetId });
        c.moveToFirst();

        if ( (c.getCount() == 0) && (c == null) ) {

        }
        else{
            c.moveToFirst();
            do {
                hashMap = new HashMap<String, String>();
                hashMap.put( "ASSETRID", String.valueOf(c.getInt(0)) );
                hashMap.put( "ASSETID", String.valueOf(c.getString(1)) );
                hashMap.put( "ASSETNAME", String.valueOf(c.getString(2)) );
                hashMap.put( "REFERID", String.valueOf(c.getString(3)) );
                hashMap.put( "REFERNAME", String.valueOf(c.getString(4)) );
                hashMap.put( "RECEIVEDATE", String.valueOf(c.getString(5)) );
                hashMap.put( "SPEC", String.valueOf(c.getString(6)) );
                hashMap.put( "UNITNAME", String.valueOf(c.getString(7)) );
                hashMap.put( "LOCATION", String.valueOf(c.getInt(8)) + "-" + String.valueOf(c.getInt(10)) + "-" + String.valueOf(c.getString(11)) + " : " + String.valueOf(c.getString(9)) );
                hashMap.put( "DATE", String.valueOf(c.getInt(14)) + "-" + String.valueOf(c.getInt(13)) + "-" + String.valueOf(c.getInt(12)) );
                hashMap.put( "TIME", String.valueOf(c.getInt(15)) + " : " + String.valueOf(c.getInt(16)) );
                hashMap.put( "STATUS", String.valueOf(c.getString(17)) );
                hashMap.put( "SCANBY", String.valueOf(c.getString(18)) );
                hashMap.put( "NOTE", String.valueOf(c.getString(19)) );
                //hashMap.put( "ADDFINISH", String.valueOf("FALSE") );
                //hashMap.put( "PHOTO", String.valueOf(c.getString(20)) );
                MyArrList.add(hashMap);

            }while(c.moveToNext());

            listView.setAdapter(new HistoryActivity.TextAdapter(HistoryActivity.this));

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
