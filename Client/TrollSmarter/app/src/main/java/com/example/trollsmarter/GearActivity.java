package com.example.trollsmarter;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.trollsmarter.Alerts.Alert;
import com.example.trollsmarter.Alerts.InputDialog;
import com.example.trollsmarter.Alerts.LeaderLengthAlert;
import com.example.trollsmarter.Alerts.LineDialog;
import com.example.trollsmarter.HelperClasses.Line;
import com.example.trollsmarter.HelperClasses.Lure;
import com.example.trollsmarter.HelperClasses.RefreshEvent;
import com.example.trollsmarter.HelperClasses.UserData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GearActivity extends AppCompatActivity {
    public List<Lure> lureList;
    public List<Lure> trollingDeviceList;
    public List<Line> lineList;
    private String user;
    private UserData userData;


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(RefreshEvent event) throws ExecutionException, InterruptedException {
        RefreshLists();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gear);
        Intent intent = getIntent();
        Button saveButton = findViewById(R.id.saveButton);
        Button addTrollingDeviceButton = findViewById(R.id.AddTrollingDevice);
        Button addLureButton = findViewById(R.id.addLure);
        Button addLineButton = findViewById(R.id.addLine);
        user = intent.getStringExtra("User");
        userData = (UserData) intent.getSerializableExtra("UserData");
        addTrollingDeviceButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputDialog inputDialog = new InputDialog(GearActivity.this, user, "AddTrollingDevice");
                inputDialog.show();
            }

        });
        addLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LineDialog lineDialog = new LineDialog(GearActivity.this, user);
                lineDialog.show();
            }
        });
        addLureButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputDialog inputDialog = new InputDialog(GearActivity.this, user, "AddLure");
                inputDialog.show();
            }

        });
        saveButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Spinner trollingSpinner = findViewById(R.id.TrollingDeviceSpinner);
                Spinner lureSpinner = findViewById(R.id.lureSpinner);
                Spinner lineSpinner = findViewById(R.id.lineSpinner);
                TextView tv = findViewById(R.id.editLeaderLength);
                userData.SetTrollingDevice((Lure) trollingSpinner.getSelectedItem());
                userData.SetLure((Lure) lureSpinner.getSelectedItem());
                userData.SetLineThickness((Line) lineSpinner.getSelectedItem());
                if(tv.getText().toString().length() == 0){
                    Alert dialog = new LeaderLengthAlert();
                    dialog.show(getSupportFragmentManager(), "LeaderLengthAlert");
                    TextView leaderlength = findViewById(R.id.leaderLengthText);
                    leaderlength.setTextColor(Color.RED);
                    return;
                }

                userData.SetLeaderLength(tv.getText().toString());
                Intent mainIntent = new Intent(GearActivity.this, MainActivity.class);

                mainIntent.putExtra("User", user);
                mainIntent.putExtra("UserData", userData);
                startActivity(mainIntent);
                finish();
            }
        });

        try {
            RefreshLists();


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    private void RefreshLists() throws ExecutionException, InterruptedException {
        Lure[] lures = new LureGetTask().execute(user).get();
        Lure[] trollingDevice = new TrollingGetTask().execute(user).get();
        Line[] lines = new LineGetTask().execute(user).get();

        Spinner lureSpinner = findViewById(R.id.lureSpinner);
        ArrayAdapter userAdapter = new ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, lureList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lureSpinner.setAdapter(userAdapter);

        Spinner trollingDeviceSpinner = findViewById(R.id.TrollingDeviceSpinner);
        ArrayAdapter trollingAdapter = new ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, trollingDeviceList);
        trollingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trollingDeviceSpinner.setAdapter(trollingAdapter);

        Spinner lineSpinner = findViewById(R.id.lineSpinner);
        ArrayAdapter lineAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, lineList);
        lineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lineSpinner.setAdapter(lineAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        MenuItem gearItem = menu.findItem(R.id.Gear);
        gearItem.setVisible(false);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.Troll == item.getItemId()) {
            Intent intent = new Intent(GearActivity.this, MainActivity.class);
            intent.putExtra("User", user);
            intent.putExtra("UserData", userData);
            startActivity(intent);
            finish();
        }
        else if(R.id.History == item.getItemId()){
            Intent intent = new Intent(GearActivity.this, HistoryActivity.class);
            intent.putExtra("User", user);
            intent.putExtra("UserData", userData);
            startActivity(intent);
            finish();
        }
        return true;
    }

    public void refresh(){
        try {
            RefreshLists();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class LureGetTask extends AsyncTask<String, String, Lure[]>{

        @Override
        protected Lure[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "GetLures");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            int responseCode = 0;
            String credentials = user;
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
                        lureList = mapper.reader().forType(new TypeReference<List<Lure>>() {}).readValue(jsonStr);
                        for(Lure l : lureList){
                            System.out.println(l);
                        }
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
    public class TrollingGetTask extends AsyncTask<String, String, Lure[]>{

        @Override
        protected Lure[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "GetTrollingDevices");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            int responseCode = 0;
            String credentials = user;
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
                        trollingDeviceList = mapper.reader().forType(new TypeReference<List<Lure>>() {}).readValue(jsonStr);
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
    public class LineGetTask extends AsyncTask<String, String, Line[]>{

        @Override
        protected Line[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "GetLines");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            int responseCode = 0;
            String credentials = user;
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
                        lineList = mapper.reader().forType(new TypeReference<List<Line>>() {}).readValue(jsonStr);
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