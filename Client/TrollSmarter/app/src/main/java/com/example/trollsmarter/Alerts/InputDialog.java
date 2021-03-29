package com.example.trollsmarter.Alerts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.trollsmarter.GearActivity;
import com.example.trollsmarter.HelperClasses.Lure;
import com.example.trollsmarter.HelperClasses.UserData;
import com.example.trollsmarter.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class InputDialog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button save;
    public JSONArray jsonArray;
    public JSONObject jsonObject;
    public JSONObject fullJson;
    public String user;
    public String Path;
    public InputDialog(Activity a, String u, String p) {
        super(a);
        // TODO Auto-generated constructor stub
        c = a;
        user = u;
        Path = p;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gear_input);
        save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Name");
        alert.setMessage("Enter Name :");

        // Set an EditText view to get user input
        final EditText input = new EditText(getContext());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                fullJson = new JSONObject();
                try {
                    fullJson.put("Name", value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    fullJson.put("Depths", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    new AddDeviceTask().execute(user).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            }
        });

        alert.show();
        try {
            GetData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dismiss();
    }

    public void GetData() throws JSONException {
        Integer[] ids = {R.id.five,
                R.id.onezero, R.id.onefive,
                R.id.twozero, R.id.twofive,
                R.id.threezero, R.id.threefive,
                R.id.fourzero, R.id.fourfive,
                R.id.fivezero, R.id.fivefive,
                R.id.sixzero, R.id.sixfive};
        Integer nullIndex = null;
        EditText editText;
        for(int i = 0; i < ids.length; i++){
            editText = findViewById(ids[i]);
            if(editText.getText().toString().length() == 0){
                nullIndex = i;
                break;
            }
        }
        if(nullIndex == null)
            nullIndex = ids.length;
        jsonArray = new JSONArray();
        jsonObject = new JSONObject();
        jsonObject.put("0", "0");
        jsonArray.put(jsonObject);
        int depth = 5;
        for(int i = 0; i < nullIndex; i++){
            jsonObject = new JSONObject();
            editText = findViewById(ids[i]);
            jsonObject.put(String.valueOf(depth), editText.getText().toString());
            jsonArray.put(jsonObject);

            depth += 5;
        }
        depth = 0;
    }

    public class AddDeviceTask extends AsyncTask<String, String, Lure[]> {

        @Override
        protected Lure[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, Path);
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
                con.setDoOutput( true );
                con.setInstanceFollowRedirects( false );
                con.setRequestMethod( "POST" );
                con.setRequestProperty("Authorization", header);
                con.setRequestProperty( "Content-Type", "application/json");
                con.setRequestProperty( "charset", "utf-8");
                con.setRequestProperty( "Content-Length", Integer.toString( fullJson.toString().length() ));
                con.setUseCaches( false );
                con.connect();
                DataOutputStream ds = new DataOutputStream(con.getOutputStream ());
                ds.writeBytes(fullJson.toString());
                ds.flush ();
                ds.close ();
                responseCode = con.getResponseCode();

                //stream code taken from https://stackoverflow.com/questions/33229869/get-json-
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