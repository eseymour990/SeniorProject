package com.example.trollsmarter.data;

import android.os.AsyncTask;
import android.util.Base64;

import com.example.trollsmarter.data.model.LoggedInUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        try{

            LoggedInUser User = new LoginTask(username, password).execute().get();
            if(User != null)
                return new Result.Success<>(User);
            else
                return new Result.Error(new IOException("Error logging in"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<LoggedInUser> register(String username, String password) {
        try{
            LoggedInUser user = new RegisterTask(username, password).execute().get();
            if(user != null)
                return new Result.Success<>(user);
            else
                return new Result.Error(new IOException("Error logging in"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
    public class LoginTask extends AsyncTask<String, String, LoggedInUser> {

        private String username;
        private String password;


        public LoginTask(String Username, String Password){
            username = Username;
            password = Password;
        }

        public void execute(String username, String password) throws MalformedURLException {

        }

        @Override
        protected LoggedInUser doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "AuthUser");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            int responseCode = 0;
            String credentials = username + ":" + password;
            final String header =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
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
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String json = buffer.toString();
                JSONObject obj = new JSONObject(json);
                String token = obj.getString("token");

                responseCode = con.getResponseCode();
                con.disconnect();

                if (responseCode == 200) {
                    return new LoggedInUser(token, username);
                } else {
                    return null;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public class RegisterTask extends AsyncTask<String, String, LoggedInUser> {

        private String username;
        private String password;


        public RegisterTask(String Username, String Password){
            username = Username;
            password = Password;
        }

        public void execute(String username, String password) throws MalformedURLException {

        }

        @Override
        protected LoggedInUser doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL("http","euclid.nmu.edu", 8002, "AddUser");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection con = null;
            int responseCode = 0;
            String credentials = username + ":" + password;
            final String header =
                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
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
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                String json = buffer.toString();
                JSONObject obj = new JSONObject(json);
                String token = obj.getString("token");

                responseCode = con.getResponseCode();
                con.disconnect();

                if (responseCode == 200) {
                    return new LoggedInUser(token, username);
                } else {
                    return null;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
    public void logout() {
        // TODO: revoke authentication
    }
}