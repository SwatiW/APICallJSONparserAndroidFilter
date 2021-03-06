package com.swati.directory;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DirectoryClass extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_class);

        new CallAPI().execute();

    }

    // Call to the Api to get directory data
    private class CallAPI extends AsyncTask<Void,Void,String> {
        ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
        //TextView responseView=(TextView)findViewById(R.id.responseView);
        ListView list=(ListView)findViewById(R.id.list);
        ArrayList<String> listitem = new ArrayList<>();


        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {
           try {
                URL url = new URL("http://thirpur.netii.net/thirpur.php?method=directory");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
         //   responseView.setText(response);
            try {
//adding items to listView through ArrayAdapter and getting JSON array and objects from string data
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(DirectoryClass.this,R.layout.list,R.id.listname,listitem);
                list.setAdapter(adapter);
                JSONArray art=new JSONArray(response);

                String str = "";
               for(int i=0;i<art.length();i++){
                   str +=art.getJSONObject(i).getString("Surname")+" "+art.getJSONObject(i).getString("First_Name");
                   listitem.add(str);
                   adapter.notifyDataSetChanged();
                   str="";
               }

                final String finalResponse1 = response;

                //setting listener for clicks on list items and sending data with intent to ExtendInfo
                // for showing persons full details
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(DirectoryClass.this, "clicked", Toast.LENGTH_SHORT).show();
                        Intent info = new Intent(DirectoryClass.this, ExtendInfo.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("response", finalResponse1);
                        bundle.putLong("item", id);
                        info.putExtras(bundle);
                        startActivity(info);
                    }
                });

                //search box

                final EditText editsearch = (EditText) findViewById(R.id.searchData);
                list.setTextFilterEnabled(true);


                // Capture Text in EditText and filter the list accordingly
                editsearch.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s.toString());
                    }
                });
         } catch (JSONException e) {
                e.printStackTrace();
            }

        }




    }
}
