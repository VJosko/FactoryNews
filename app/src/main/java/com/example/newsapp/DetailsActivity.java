package com.example.newsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;


public class DetailsActivity extends AppCompatActivity {
    private TextView sadrzaj;
    private TextView naslov;
    String url = "";
    String slikaUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        slikaUrl = intent.getStringExtra("slikaUrl");
        url = intent.getStringExtra("url");
        String[] urls = url.split("p",2);
        urls[0] = urls[0] + "ps";
        url = urls[0] + urls[1];
        sadrzaj = findViewById(R.id.sadrzaj);
        naslov = findViewById(R.id.naslov);
        new DownloadImageTask((ImageView) findViewById(R.id.slika))
                .execute(slikaUrl);
        getWebsite();

    }

    private void getWebsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                final StringBuilder builder = new StringBuilder();
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements naslov = doc.getElementsByTag("h1");

                    builder.append(naslov.text()).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        naslov.setText(builder.toString());
                    }
                });

                final StringBuilder builder1 = new StringBuilder();
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements tekst = doc.select(".story-body__inner").select("p");
                    for(Element t : tekst){
                        String x = t.text() + "\n\n";
                        builder1.append(x);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sadrzaj.setText(builder1);
                    }
                });
            }
        }).start();
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
