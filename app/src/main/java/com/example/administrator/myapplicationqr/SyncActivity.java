
package com.example.administrator.myapplicationqr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class SyncActivity extends AppCompatActivity {

    //TextView TEXT_ITEM_UID,TEXT_ITEM_SERIAL,TEXT_ITEM_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        new Send().execute();
    }

    public void send(View v){

        new Send().execute();

    }



    class Send extends AsyncTask<String, Void,Long > {



        protected Long doInBackground(String... urls) {

            //String ITEM_UID = TEXT_ITEM_UID.getText().toString();
            //String ITEM_SERIAL = TEXT_ITEM_SERIAL.getText().toString();
            //String ITEM_NAME = TEXT_ITEM_NAME.getText().toString();

            String HISTORY_ASSET_NAME = URLEncoder.encode("FFFหหห");//"FFFหหห";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.amsapp.net/save.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("HISTORY_ASSET_NAME", HISTORY_ASSET_NAME));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                System.out.println(HISTORY_ASSET_NAME);


                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }
    
}
