package com.swati.directory;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
 /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 10;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 10;

    private View mContentView;
    private boolean mVisible;
    String mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_fullscreen);
        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
 // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        //setting click events for login button
        final Button login=(Button)findViewById(R.id.login);
        final EditText mob_no=(EditText)findViewById(R.id.mobile_no);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                 Button register=(Button)findViewById(R.id.register);
                register.setVisibility(View.GONE);
                login.setVisibility(View.GONE);
                mob_no.setVisibility(View.VISIBLE);
                final Button oklogin=(Button)findViewById(R.id.okLogin);
                oklogin.setVisibility(View.VISIBLE);
                oklogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    //checking if internet is present by using ConnectionDetector class
                        cd = new ConnectionDetector(getApplicationContext());
                        isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            // Using asynctask to prevent network on main thread exception
                             new PostDataAsyncTask().execute();
                        }
                        else{
                            Toast.makeText(FullscreenActivity.this, "No Data Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(10);

    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
     //   mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        //    mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private class PostDataAsyncTask extends AsyncTask<String, String, String>{
        ProgressBar loginprogress=(ProgressBar)findViewById(R.id.loginprogress);
        String data ;
        EditText mobil=(EditText)findViewById(R.id.mobile_no);
        String mobile=mobil.getText().toString();

        protected void onPreExecute() {
            super.onPreExecute();
            loginprogress.setVisibility(View.VISIBLE);
            // do stuff before posting data
        }

        @Override
        protected String doInBackground(String... strings) {

           // String mobil=
                data="mobile_no="+mobile;
            String text = "";
            BufferedReader reader = null;

            // Send data
            try {

                // Defined URL  where to send data

                // Send POST data request
                URL url = new URL("http://thirpur.netii.net/thirpur.php?method=login");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                //Toast.makeText(MainActivity.this,data, Toast.LENGTH_SHORT).show();
                // Get the server response

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "\n");
                }


                text = sb.toString();
                return text;
            }
            catch(Exception ex)
            {
                return (ex.getMessage());
            }

        }
        @Override
        protected void onPostExecute(String response) {
            loginprogress.setVisibility(View.GONE);
            String answer;
            try {
                JSONObject json=new JSONObject(response);
                answer=json.getString("success");
                if (answer.equals("yes")) {
                    Intent i = new Intent(FullscreenActivity.this, DirectoryClass.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(FullscreenActivity.this, "Incorrect Number", Toast.LENGTH_SHORT).show();
                    EditText mob_no=(EditText)findViewById(R.id.mobile_no);
                    mob_no.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}
