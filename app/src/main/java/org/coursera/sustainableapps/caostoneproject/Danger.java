package org.coursera.sustainableapps.caostoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class Danger extends AppCompatActivity {

    /**
     *website display window
      */
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger);

        webView = findViewById(R.id.webView);

        /**
         *buttons for selecting information about the type of hazard
         */
        Button buttonRadiation = findViewById(R.id.buttonRadiation);
        Button buttonBio = findViewById(R.id.buttonBio);
        Button buttonChem = findViewById(R.id.buttonChem);
        Button buttonLaser = findViewById(R.id.buttonLaser);
        Button buttonMagnetic = findViewById(R.id.buttonMagnetic);
        Button buttonRadio = findViewById(R.id.buttonRadio);

        // assign a listener
        buttonRadiation.setOnClickListener(viewClickListener);
        buttonBio.setOnClickListener(viewClickListener);
        buttonChem.setOnClickListener(viewClickListener);
        buttonLaser.setOnClickListener(viewClickListener);
        buttonMagnetic.setOnClickListener(viewClickListener);
        buttonRadio.setOnClickListener(viewClickListener);

        // включаем поддержку JavaScript
        // includes support JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

    }

    /**
     * button click listener
     */
    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.buttonRadiation:
                    // указываем страницу загрузки
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Radiation");
                    break;
                case R.id.buttonBio:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Biodefense");
                    break;

                case R.id.buttonChem:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Chemical_hazard");
                    break;

                case R.id.buttonLaser:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Laser_safety");
                    break;

                case R.id.buttonMagnetic:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Electromagnetic_radiation");
                    break;

                case R.id.buttonRadio:
                    //specify the download page
                    webView.loadUrl("https://en.wikipedia.org/wiki/Radio_wave");
                    break;
            }
        }
    };

    /**
     * loading a web page into a window webView
     */

    private static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }

    /**
     *  back button processing
     *  The method handles clicking the back button
      */
//    public void onBackPressed(){
//        if (webView.canGoBack()) {
//            webView.goBack();
//        } else {
//            super.onBackPressed();
//        }
//    }

}