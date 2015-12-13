package com.swati.directory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ExtendInfo extends AppCompatActivity {

    ImageView img;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend_info);

        // initialising the TextViews
        TextView first, mid, surname, address1, address2, city, house, mobile, blood, birth;
        first = (TextView) findViewById(R.id.first);
        mid = (TextView) findViewById(R.id.middle);
        surname = (TextView) findViewById(R.id.surname);
        address1 = (TextView) findViewById(R.id.address1);
        address2 = (TextView) findViewById(R.id.address2);
        city = (TextView) findViewById(R.id.city);
        house = (TextView) findViewById(R.id.house_no);
        mobile = (TextView) findViewById(R.id.mobile_no);
        birth = (TextView) findViewById(R.id.birthdate);
        blood = (TextView) findViewById(R.id.blood);


        // getting JSON data through Intent from DirectoryClass
        Bundle getData = getIntent().getExtras();
        String response = getData.getString("response");
        long item = getData.getLong("item");

        String imageurl = null;

        // getting JSON objects and setting them to TextView
        try {
            JSONArray array = new JSONArray(response);
            first.setText(array.getJSONObject((int) item).getString("First_Name"));
            mid.setText(array.getJSONObject((int) item).getString("Middle_Name"));
            surname.setText(array.getJSONObject((int) item).getString("Surname"));
            address1.setText(array.getJSONObject((int) item).getString("Address1"));
            address2.setText(array.getJSONObject((int) item).getString("Address2"));
            city.setText(array.getJSONObject((int) item).getString("City"));
            house.setText(array.getJSONObject((int) item).getString("Home_No"));
            mobile.setText(array.getJSONObject((int) item).getString("Mobile_No"));
            birth.setText(array.getJSONObject((int) item).getString("Birth_Date"));
            blood.setText(array.getJSONObject((int) item).getString("Blood"));
            imageurl = array.getJSONObject((int) item).getString("Profile_Pic");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        img = (ImageView) findViewById(R.id.imageView);

        //call to load the proficpic
        new LoadImage().execute(imageurl);

    }



    // class to load the profic pic from url specified in json and setting to ImageView
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        ProgressBar progresBar=(ProgressBar)findViewById(R.id.progresBar);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progresBar.setVisibility(View.VISIBLE);

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        // setting the profic_Pic to ImageView
        protected void onPostExecute(Bitmap image) {
            progresBar.setVisibility(View.GONE);
            if(image != null){
                img.setImageBitmap(image);
            }else{
               Toast.makeText(ExtendInfo.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
