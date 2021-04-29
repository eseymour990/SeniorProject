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
import android.widget.TextView;

import com.example.trollsmarter.HelperClasses.Line;
import com.example.trollsmarter.HelperClasses.Lure;
import com.example.trollsmarter.HelperClasses.RefreshEvent;
import com.example.trollsmarter.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LineDialog extends Dialog implements
        android.view.View.OnClickListener, DialogInterface {

    public Activity c;
    public Dialog d;
    public Button save;
    public JSONArray jsonArray;
    public JSONObject jsonObject;
    public JSONObject fullJson;
    public String user;
    public LineDialog(Activity a, String u) {
        super(a);
        // TODO Auto-generated constructor stub
        c = a;
        user = u;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.line_input);
        save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        TextView name = findViewById(R.id.NameOfLine);
        TextView lineThickness = findViewById(R.id.lineThickness);
        fullJson = new JSONObject();
        try {
            fullJson.put("Name", name.getText());
            fullJson.put("Thickness", lineThickness.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AddLineTask addLineTask = new AddLineTask();
            addLineTask.execute(user);
            this.dismiss();
    }

    public class AddLineTask extends AsyncTask<String, String, Line[]> {

        @Override
        protected Line[] doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "AddLine");
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

                con.disconnect();


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            EventBus.getDefault().post(new RefreshEvent());
            return null;
        }
    }
}