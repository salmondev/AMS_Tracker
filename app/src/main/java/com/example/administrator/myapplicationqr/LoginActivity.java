package com.example.administrator.myapplicationqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private String BASE_URL;
    public static boolean SYNC_FROM_URL = false;

    private Button button_Login;
    private EditText editText_Username,editText_Password;
    private ProgressBar progressBar_Loading;
    private CheckBox checkBox_RememberUsername;
    ImageView imageView_ConfigureBaseUrl;

    Intent intent;

    public static String globalUsername,globalPassword,globalReferID,globalRefername,globalAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        button_Login = (Button) findViewById(R.id.button_Login);
        editText_Username = (EditText) findViewById(R.id.editText_Username);
        editText_Password = (EditText) findViewById(R.id.editText_Password);
        progressBar_Loading = (ProgressBar) findViewById(R.id.progressBar_Loading);
        checkBox_RememberUsername = (CheckBox) findViewById(R.id.checkBox_RememberUsername);
        imageView_ConfigureBaseUrl = (ImageView) findViewById(R.id.imageView_ConfigureBaseUrl);

        progressBar_Loading.setVisibility(View.GONE);
        button_Login.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("RememberLogin",false) == true){
            globalUsername = sharedPreferences.getString("globalUsername","");
            globalReferID = sharedPreferences.getString("globalReferID","");
            globalRefername = sharedPreferences.getString("globalRefername","");
            globalAuth = sharedPreferences.getString("globalAuth","");
            onGotoMainActivity();
        }

        imageView_ConfigureBaseUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.dialog_custom3, null);
                builder.setView(view);
                final EditText editText_BaseUrl = (EditText) view.findViewById(R.id.editText_BaseUrl);

                SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
                editText_BaseUrl.setText(sharedPreferences.getString("BaseUrl",getResources().getString(R.string.BASE_URL)));

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("BaseUrl",editText_BaseUrl.getText().toString());
                        editor.apply();

                        SYNC_FROM_URL = true;

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);

        BASE_URL = sharedPreferences.getString("BaseUrl",getResources().getString(R.string.BASE_URL));

        editText_Username.setText(null);
        editText_Password.setText(null);
        if(sharedPreferences.getString("Username",null) != null && sharedPreferences.getBoolean("Username_Checkbox",false) == true){
            editText_Username.setText(sharedPreferences.getString("Username",""));
            checkBox_RememberUsername.setChecked(true);
        }
        else{
            checkBox_RememberUsername.setChecked(false);
        }
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

    public void onLogin(View v){
        String mUsername = editText_Username.getText().toString().trim();
        String mPassword = editText_Password.getText().toString().trim();
        if(!mUsername.isEmpty() || !mPassword.isEmpty()){
            Login(mUsername,mPassword);
        }
        else{
            editText_Username.setError("Please insert username");
            editText_Password.setError("Please insert password");
        }
    }

    public void onVisitWebsite(View v){
        /*
        String url = "http://amsapp.net/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        */
        intent = new Intent(this, WebActivity.class);
        startActivity(intent);
    }

    private void Login(String username, final String password){
        progressBar_Loading.setVisibility(View.VISIBLE);
        button_Login.setVisibility(View.GONE);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else{
            connected = false;
        }

        if(connected == true){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "login_mobile.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                JSONArray jsonArray = jsonObject.getJSONArray("login");
                                if(success.equals("1")){
                                    Toast.makeText(LoginActivity.this,"LOGIN SUCCESS !",Toast.LENGTH_LONG).show();
                                    for(int i = 0; i < jsonArray.length(); i++){
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        String tempUsername = object.getString("USER_USERNAME").trim();
                                        String tempReferid = object.getString("REFERID").trim();
                                        String tempRefername = object.getString("REFERNAME").trim();
                                        String tempAuth = object.getString("AUTH").trim();
                                        //Toast.makeText(MainActivity.this,"Login Success !" + "\nYour Username : " + tempUsername + "\nYour Refer ID : " + tempReferid,Toast.LENGTH_LONG).show();
                                        //Toast.makeText(LoginActivity.this,"Login Success !" + "\nUsername : " + tempUsername + "\nRefer ID : " + tempReferid + "\nRefer Name : " + tempRefername,Toast.LENGTH_LONG).show();
                                        globalUsername = tempUsername;
                                        globalReferID = tempReferid;
                                        globalRefername = tempRefername;
                                        globalPassword = password;
                                        globalAuth = tempAuth;
                                        onGotoMainActivity();

                                    }
                                }
                                else{
                                    Toast.makeText(LoginActivity.this,"FAIL IN",Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e){
                                //Toast.makeText(MainActivity.this,"Error 1 : " + e.toString(),Toast.LENGTH_LONG).show();
                                Toast.makeText(LoginActivity.this,"Incorrect Username or Password.",Toast.LENGTH_LONG).show();
                                //progressBar_Loading.setVisibility(View.GONE);
                                //button_Login.setVisibility(View.VISIBLE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(MainActivity.this,"Error 2 : " + error.toString(),Toast.LENGTH_LONG).show();
                            //progressBar_Loading.setVisibility(View.GONE);
                            //button_Login.setVisibility(View.VISIBLE);
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("username",editText_Username.getText().toString());
                    params.put("password",editText_Password.getText().toString());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
        else{
            Toast.makeText(LoginActivity.this,"Please make sure you have connected to internet.",Toast.LENGTH_LONG).show();
        }
        progressBar_Loading.setVisibility(View.GONE);
        button_Login.setVisibility(View.VISIBLE);


    }

    public void onGotoMainActivity(){
        if(checkBox_RememberUsername.isChecked()){
            SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Username",editText_Username.getText().toString());
            editor.putBoolean("Username_Checkbox",true);

            editor.putString("globalUsername",globalUsername);
            editor.putString("globalReferID",globalReferID);
            editor.putString("globalRefername",globalRefername);
            editor.putString("globalAuth",globalAuth);
            editor.putBoolean("RememberLogin",true);

            editor.apply();
        }
        else{
            SharedPreferences sharedPreferences = getSharedPreferences("Login",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Username",null);
            editor.putBoolean("Username_Checkbox",false);

            editor.putString("globalUsername",globalUsername);
            editor.putString("globalReferID",globalReferID);
            editor.putString("globalRefername",globalRefername);
            editor.putString("globalAuth",globalAuth);
            editor.putBoolean("RememberLogin",true);

            editor.apply();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
