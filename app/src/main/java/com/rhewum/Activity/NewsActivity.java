package com.rhewum.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;

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