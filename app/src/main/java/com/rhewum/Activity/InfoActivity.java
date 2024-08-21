package com.rhewum.Activity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.rhewum.Activity.MeshConveterData.ResponsiveAndroidBars;
import com.rhewum.Activity.MeshConveterData.Utils;
import com.rhewum.Activity.interfaces.JsInterface;
import com.rhewum.DrawerBaseActivity;
import com.rhewum.R;

public class InfoActivity extends DrawerBaseActivity implements View.OnClickListener{
    private RelativeLayout backLayout;
    private WebView wv;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ResponsiveAndroidBars.setNotificationBarColor(this, getResources().getColor(R.color.header_background), false);
        if (Build.VERSION.SDK_INT >= 32) {
            ResponsiveAndroidBars.setNavigationBarColor(this, getColor(R.color.white), true, false);
        } else {
            ResponsiveAndroidBars.setNavigationBarColor(this, getResources().getColor(R.color.grey_background), false, false);
        }
        setUpViews();
        this.backLayout.setOnClickListener(this);
        this.wv.loadUrl("file:///android_asset/info.html");
        this.wv.getSettings().setJavaScriptEnabled(true);
        this.wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.wv.setHorizontalScrollBarEnabled(false);
        this.wv.getSettings().setBuiltInZoomControls(true);
        this.wv.getSettings().setSupportZoom(false);
        this.wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.wv.addJavascriptInterface(new JsInterface(this), "Android");
        this.wv.setInitialScale(getScale());
        this.wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                if (str.startsWith("tel:")) {
                    InfoActivity.this.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(str)));
                } else if (str.startsWith("http:") || str.startsWith("https:")) {
                    webView.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
                } else if (str.startsWith(MailTo.MAILTO_SCHEME)) {
                    android.net.MailTo parse = android.net.MailTo.parse(str);
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("message/rfc822");
                    intent.putExtra("android.intent.extra.EMAIL", new String[]{parse.getTo()});
                    intent.putExtra("android.intent.extra.SUBJECT", parse.getSubject());
                    intent.putExtra("android.intent.extra.CC", parse.getCc());
                    intent.putExtra("android.intent.extra.TEXT", parse.getBody());
                    InfoActivity.this.startActivity(intent);
                    webView.reload();
                }
                return true;
            }
        });
    }
    private void setUpViews() {
        this.backLayout = (RelativeLayout) findViewById(R.id.activity_info_back_layout);
        this.wv = (WebView) findViewById(R.id.activity_info_webview);
    }

    public void onClick(View view) {
        if (view.equals(this.backLayout)) {
            finish();
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }
    }

    private int getScale() {
        return Double.valueOf(Double.valueOf(new Double((double) ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()).doubleValue() / new Double((double) (Utils.isTablet(this) ? 900 : 350)).doubleValue()).doubleValue() * 100.0d).intValue();
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}