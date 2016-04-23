package uz.samtuit.samapp.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;


public class TravelTipsFaqActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traveltips_faq);

        webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/html/en_traveltips_faq.html");
    }
}
