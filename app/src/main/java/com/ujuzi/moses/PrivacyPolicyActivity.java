package com.ujuzi.moses;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        initViews();
        showData("https://bingwa.cc/user/company-privacy-policy");
    }

    private void showData(String url) {
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }

    }

    private void initViews() {
        webView = findViewById(R.id.privacyPolicyWeb);
    }
}