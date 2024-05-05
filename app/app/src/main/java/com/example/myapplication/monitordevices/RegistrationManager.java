package com.example.myapplication.monitordevices;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegistrationManager {
    private String URL;

    private String username;
    private String password;
    private String email;

    private RegistrationCallback registrationCallback;

    public RegistrationManager(String URL, String username, String password, String email,
                               RegistrationCallback registrationCallback) {
        this.URL = URL;
        this.username = username;
        this.password = password;
        this.email = email;
        this.registrationCallback = registrationCallback;
    }

    public void registerUser() {
        new RegisterUserTask().execute();
    }

    private class RegisterUserTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            String json = "{\"username\": \"" + username +"\", " +
                    "\"password\": \"" + password +"\", " +
                    "\"email\": \"" + email + "\"}";
            System.out.println(json);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    System.out.println(response);
                    throw new IOException("Unexpected code " + response);
                }
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("AddUserTask", "Result : " + result);
                registrationCallback.onRegistrationSuccess();
            } else {
                Log.e("AddUserTask", "Error during request!");
                registrationCallback.onRegistrationFailure("Error");
            }
        }
    }
}
