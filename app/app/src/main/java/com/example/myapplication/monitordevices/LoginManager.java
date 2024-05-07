package com.example.myapplication.monitordevices;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginManager {
    private String URL;

    private String email;
    private String password;

    private String errorMessage = null;

    private LoginCallback loginCallback;

    public LoginManager(String URL, String email, String password,
                               LoginCallback loginCallback) {
        this.URL = URL;
        this.email = email;
        this.password = password;
        this.loginCallback = loginCallback;
    }

    public void loginUser() {
        new LoginUserTask().execute();
    }

    private class LoginUserTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            String json = "{\"username\": \"" + email +"\", " +
                    "\"password\": \"" + password +"\", ";

            System.out.println(json);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    System.out.println(response.headers("Authorization"));
                    String responseStr = response.body().string();
                    Pattern pattern = Pattern.compile("BadRequestError: (.*?)<br>");
                    Matcher matcher = pattern.matcher(responseStr);

                    if (matcher.find()) {
                        System.out.println("Error: " + matcher.group(1));
                        errorMessage = matcher.group(1);
                    } else {
                        System.out.println("No error for pattern!!!");
                    }
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
                loginCallback.onLoginSuccess();
            } else {
                Log.e("AddUserTask", "Error during request!:"  + errorMessage);
                loginCallback.onLoginFailure(errorMessage);
            }
        }
    }
}
