package com.example.trollsmarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.trollsmarter.Alerts.LeaderLengthAlert;
import com.example.trollsmarter.Alerts.UserDataAlert;
import com.example.trollsmarter.HelperClasses.DiveEquationHelper;
import com.example.trollsmarter.HelperClasses.UserData;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public String User;
    public UserData userData;
    public boolean started;

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
        }
        else if(R.id.History == item.getItemId()){
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        User = intent.getStringExtra("User");
        userData = (UserData) intent.getSerializableExtra("UserData");
        if(userData == null){
            userData = new UserData();
        }
        setContentView(R.layout.activity_main);
        started = false;
    }


    public void Start(View view){
        TextView tv = findViewById(R.id.textBox1);
        Button startButton = findViewById(R.id.Start);
        TextView speed = findViewById(R.id.Speed);

        if(!started) {
            if(userData.GetLure() == null || userData.GetTrollingDevice() == null || userData.GetLeaderLength() == null){
                DialogFragment dialog = new UserDataAlert();
                dialog.show(getSupportFragmentManager(), "UserDataAlert");
                return;
            }
            if(tv.getText().length() == 0){
                DialogFragment dialog = new LeaderLengthAlert();
                dialog.show(getSupportFragmentManager(), "LeaderLengthAlert");
                return;
            }
            tv.setVisibility(View.VISIBLE);
            Location a = new Location(Context.LOCATION_SERVICE);
            speed.setText(Float.toString(a.getSpeed()));
            TextView textView = findViewById(R.id.desiredDepth);
            DiveEquationHelper lureHelper = new DiveEquationHelper(userData.GetLure().getDiveCurve());
            DiveEquationHelper trollingDeviceHelper = new DiveEquationHelper(userData.GetTrollingDevice().getDiveCurve());
            double lureDepth = lureHelper.FindDepth(Double.parseDouble(userData._leaderLength));
            double fin = trollingDeviceHelper.FindLineOut(Double.parseDouble(textView.getText().toString()) - lureDepth);
            started = true;
            startButton.setText("Stop");
            tv.setText("Line out: " + fin);
        }
        else {
            tv.setVisibility(View.INVISIBLE);
            startButton.setText("Start");
            started = false;
        }


    }


}