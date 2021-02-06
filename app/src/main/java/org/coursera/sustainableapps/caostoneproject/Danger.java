package org.coursera.sustainableapps.caostoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

public class Danger extends AppCompatActivity {

     WebView webView;
     Button imageRadiation, imageBio, imageChem, imageLaser, imageMagnetic, imageRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger);

        webView = findViewById(R.id.webView);

        // initialization
        // picture
        imageRadiation = findViewById(R.id.imageRadiation);
        imageBio = findViewById(R.id.imageBio);
        imageChem = findViewById(R.id.imageChem);
        imageLaser = findViewById(R.id.imageLaser);
        imageMagnetic = findViewById(R.id.imageMagnetic);
        imageRadio = findViewById(R.id.imageRadio);

        // assign a listener
        imageRadiation.setOnClickListener(viewClickListener);
        imageBio.setOnClickListener(viewClickListener);
        imageChem.setOnClickListener(viewClickListener);
        imageLaser.setOnClickListener(viewClickListener);
        imageMagnetic.setOnClickListener(viewClickListener);
        imageRadio.setOnClickListener(viewClickListener);

        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.imageRadiation:
                    // указываем страницу загрузки
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Radiation");
                    break;
                case R.id.imageBio:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Biodefense");
                    break;

                case R.id.imageChem:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Chemical_hazard");
                    break;

                case R.id.imageLaser:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Laser_safety");
                    break;

                case R.id.imageMagnetic:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Electromagnetic_radiation");
                    break;

                case R.id.imageRadio:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Radio_wave");
                    break;

            }
        }
    };

    // Loading a webpage
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    //back button processing
    public void onBackPressed(){
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}