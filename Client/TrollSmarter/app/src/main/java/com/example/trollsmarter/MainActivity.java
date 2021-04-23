package com.example.trollsmarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trollsmarter.Alerts.Alert;
import com.example.trollsmarter.Alerts.LeaderLengthAlert;
import com.example.trollsmarter.Alerts.UserDataAlert;
import com.example.trollsmarter.HelperClasses.DiveEquationHelper;
import com.example.trollsmarter.HelperClasses.GPSSpeedProvider;
import com.example.trollsmarter.HelperClasses.History;
import com.example.trollsmarter.HelperClasses.Lure;
import com.example.trollsmarter.HelperClasses.RefreshEvent;
import com.example.trollsmarter.HelperClasses.SpeedUpdateEvent;
import com.example.trollsmarter.HelperClasses.UserData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public String User;
    public UserData userData;
    public boolean started;
    public Alert dialog;
    public String depth;
    public GPSSpeedProvider gpsSpeedProvider;
    public LocationManager locationManager;
    public LocationListener locationListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        MenuItem trollItem = menu.findItem(R.id.Troll);
        trollItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (R.id.Gear == item.getItemId()) {
            Intent intent = new Intent(MainActivity.this, GearActivity.class);

            intent.putExtra("User", User);
            intent.putExtra("UserData", userData);
            startActivity(intent);
            finish();
        } else if (R.id.History == item.getItemId()) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("User", User);
            intent.putExtra("UserData", userData);
            startActivity(intent);
            finish();
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(SpeedUpdateEvent event) throws ExecutionException, InterruptedException {
        UpdateSpeed();
    }

    private void UpdateSpeed() {
        TextView textView = findViewById(R.id.Speed);
        textView.setText(locationListener.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        User = intent.getStringExtra("User");
        userData = (UserData) intent.getSerializableExtra("UserData");
        if (userData == null) {
            userData = new UserData();
        }
        setContentView(R.layout.activity_main);
        started = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GPSSpeedProvider();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }


    public void Start(View view){
        TextView tv = findViewById(R.id.textBox1);
        Button fishCaughtButton = findViewById(R.id.fishCaughtButton);
        Button startButton = findViewById(R.id.Start);
        final TextView speedView = findViewById(R.id.Speed);

        if(!started) {
            if(userData.GetLure() == null || userData.GetTrollingDevice() == null || userData.GetLeaderLength() == null){
                dialog = new UserDataAlert();
                dialog.show(getSupportFragmentManager(), "UserDataAlert");
                return;
            }
            if(tv.getText().length() == 0){
                dialog = new LeaderLengthAlert();
                dialog.show(getSupportFragmentManager(), "LeaderLengthAlert");
                return;
            }
            tv.setVisibility(View.VISIBLE);
            //code taken from https://stackoverflow.com/questions/4811920/why-getspeed-always-return-0-on-android
            TextView textView = findViewById(R.id.desiredDepth);
            DiveEquationHelper lureHelper = new DiveEquationHelper(userData.GetLure().getDiveCurve());
            DiveEquationHelper trollingDeviceHelper = new DiveEquationHelper(userData.GetTrollingDevice().getDiveCurve());
            double lureDepth = lureHelper.FindDepth(Double.parseDouble(userData._leaderLength));
            double fin = trollingDeviceHelper.FindLineOut(Double.parseDouble(textView.getText().toString()) - lureDepth);
            started = true;
            startButton.setText("Stop");
            gpsSpeedProvider = new GPSSpeedProvider();
            fishCaughtButton.setVisibility(View.VISIBLE);
            fishCaughtButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask<String, String, History[]> histories = new PostHistoryTask().execute(User);
                }
            });
            tv.setText("Line out: " + fin);
            depth = textView.getText().toString();
        }
        else {
            gpsSpeedProvider = null;
            fishCaughtButton.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
            startButton.setText("Start");
            started = false;
        }




    }


    public class PostHistoryTask extends AsyncTask<String, String, History[]> {

        @Override
        protected History[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "/AddHistory");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Depth", depth);
                jsonObject.put("LureName", userData.GetLure());
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
                con.setRequestMethod( "POST" );
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

}