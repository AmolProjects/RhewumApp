package com.rhewumapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.rhewumapp.DrawerBaseActivity;
import com.rhewumapp.R;

public class NewsActivity extends DrawerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Create an Intent to open the web browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rhewum.com/news/all-news/"));
        startActivity(browserIntent);
      // Close the WebsiteActivity after redirecting to the web browser
        finish();

    }
}