package com.example.administrator.myapplicationqr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    private String BASE_URL;

    WebView webView_SearchAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        BASE_URL = sharedPreferences.getString("BaseUrl",getResources().getString(R.string.BASE_URL));

        webView_SearchAsset = (WebView) findViewById(R.id.webView_SearchAsset);
        webView_SearchAsset.setWebViewClient(new WebViewClient());
        webView_SearchAsset.loadUrl(BASE_URL);
        WebSettings webSettings = webView_SearchAsset.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if(webView_SearchAsset.canGoBack()){
            webView_SearchAsset.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}
