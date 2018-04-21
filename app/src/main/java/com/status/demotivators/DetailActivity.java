package com.status.demotivators;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Demotivator");

        setContentView(R.layout.activity_detail);

        WebView wv = (WebView) findViewById(R.id.wv);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            Log.d("MyLog","TEST2    " + extras.getString("url"));
            wv.loadUrl(extras.getString("url", ""));
        }
    }
}
