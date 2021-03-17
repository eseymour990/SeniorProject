package com.example.trollsmarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.CarrierConfigManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.trollsmarter.data.model.LoggedInUser;
import com.example.trollsmarter.ui.login.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public String User;
    public UserData userData;

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

    }


    public void FishCatught(View view){
        //clean this up it looks terrible
        Location a= new Location(Context.LOCATION_SERVICE);
        a.setSpeed(2.5f);
        String tdeq = userData.GetTrollingDevice().getDiveCurve().substring(2);
        String leq = userData.GetLure().getDiveCurve().substring(2);
        String ldb = leq.substring(0,leq.indexOf("x") - 1);
        String lde = leq.substring(leq.indexOf("x") + 3);
        String tdb = tdeq.substring(0,tdeq.indexOf("x") - 1);
        String tde = tdeq.substring(tdeq.indexOf("x") + 3);
        String depth = "50";
        double y = Double.parseDouble(depth);
        double top = -y+Double.parseDouble(tde);
        double bottom = Double.parseDouble(tdb);
        double logx = top/-bottom;
        double fin = Math.pow(10,logx);
        y = Double.parseDouble(userData.GetLeaderLength());
        top = -y+Double.parseDouble(lde);
        bottom = Double.parseDouble(ldb);
        if(bottom != 0.0) {
            logx = top / -bottom;
            fin += Math.pow(10, logx);
        }
        TextView tv = findViewById(R.id.textBox1);
        tv.setText("Line out: " + fin);



    }


}