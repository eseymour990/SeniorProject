package com.example.trollsmarter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    public String User;
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
        setContentView(R.layout.activity_main);
    }

    public void FishCatught(View view){
        TextView tv = (TextView)findViewById(R.id.textBox1);
        tv.setText("HI");

    }
}