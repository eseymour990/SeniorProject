package com.example.trollsmarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.trollsmarter.HelperClasses.History;
import com.example.trollsmarter.HelperClasses.Lure;
import com.example.trollsmarter.HelperClasses.UserData;
import com.example.trollsmarter.data.model.LoggedInUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HistoryActivity extends AppCompatActivity {


    private String User;
    private UserData userData;
    private List<History> historyList;
    private History toDelete;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        MenuItem historyItem = menu.findItem(R.id.History);
        historyItem.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.Gear == item.getItemId()) {
            Intent intent = new Intent(HistoryActivity.this, GearActivity.class);
            intent.putExtra("User", User);
            intent.putExtra("UserData", userData);
            startActivity(intent);
            finish();
        }
        else if(R.id.Troll == item.getItemId()){
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            intent.putExtra("User", User);
            intent.putExtra("UserData", userData);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        User = intent.getStringExtra("User");
        userData = (UserData) intent.getSerializableExtra("UserData");
        ListView historyView = findViewById(R.id.historyListView);
        try {
            History[] histories = new HistoryGetTask().execute(User).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final ArrayAdapter<History> historyArrayAdapter = new ArrayAdapter<History>(this, R.layout.list_item, historyList);
        historyView.setAdapter(historyArrayAdapter);

        historyView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                toDelete = historyList.get(position);
                historyList.remove(position);
                historyArrayAdapter.notifyDataSetChanged();
                new HistoryDeleteTask().execute(User);
                return false;
            }
        });
    }

    public class HistoryDeleteTask extends AsyncTask<String, String, History[]>{
        @Override
        protected History[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "/DeleteHistory");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Date", toDelete.getDateTime());
                jsonObject.put("LureName", toDelete.getLureName());
                jsonObject.put("Depth", toDelete.getDepth());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HttpURLConnection con = null;
            int responseCode = 0;
            String credentials = User;
            final String header =
                    "Bearer " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            try {
                con = (HttpURLConnection) url.openConnection();
                con.setDoOutput( true );
                con.setInstanceFollowRedirects( false );
                con.setRequestMethod( "DELETE" );
                con.setRequestProperty("Authorization", header);
                con.setRequestProperty( "Content-Type", "application/json");
                con.setRequestProperty( "charset", "utf-8");
                con.setRequestProperty( "Content-Length", Integer.toString( jsonObject.toString().length() ));
                con.setUseCaches( false );
                con.connect();
                DataOutputStream ds = new DataOutputStream(con.getOutputStream ());
                ds.writeBytes(jsonObject.toString());
                ds.flush ();
                ds.close ();
                responseCode = con.getResponseCode();

                con.disconnect();


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class HistoryGetTask extends AsyncTask<String, String, History[]> {

        @Override
        protected History[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "GetHistory");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            int responseCode = 0;
            String credentials = User;
            final String header =
                    "Bearer " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            try {
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Authorization", header);

                //stream code taken from https://stackoverflow.com/questions/33229869/get-json-data-from-url-using-android
                InputStream stream = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                StringBuffer buffer = new StringBuffer();
                buffer.append(reader.readLine());
                String jsonStr = buffer.toString();
                jsonStr = jsonStr.replace("\\","");
                jsonStr = jsonStr.substring(1,jsonStr.length() - 1);
                ObjectMapper mapper = new ObjectMapper();
                responseCode = con.getResponseCode();
                con.disconnect();

                if (responseCode == 200) {
                    try{
                        historyList = mapper.reader().forType(new TypeReference<List<History>>() {}).readValue(jsonStr);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                } else {
                    return null;
                }

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}