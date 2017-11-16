package com.um56.jefhunts.windows.inshortapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        WebView webView = (WebView) findViewById(R.id.webview);

       /* webView.getSettings().setJavaScriptEnabled(true); */
        webView.setWebViewClient(new WebViewClient());

        Intent i= getIntent();
        String url = i.getStringExtra("articleURL");

        webView.loadUrl(url);
       // webView.loadData(content,"tect/html","UTF-8");
    }
}
