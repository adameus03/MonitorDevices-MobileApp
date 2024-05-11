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

public class TokenLoginManager {
    private String URL;

    private String email;
    private String token;

    private String errorMessage = null;

    private LoginCallback loginCallback;

    public TokenLoginManager(String URL, String email, String token,
                        LoginCallback loginCallback) {
        this.URL = URL;
        this.email = email;
        this.token = token;
        this.loginCallback = loginCallback;
    }

    public void loginUser() {
        new LoginUserTask().execute();
    }

    private class LoginUserTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            String json = "{\"email\": \"" + email +"\", " +
                    "\"token\": \"" + token +"\"}";

            System.out.println(json);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
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
                System.out.println("headerss:" + response.headers().get("Authorization"));
                return response.headers().get("Authorization");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("AddUserTask", "Result : " + result);
                loginCallback.onLoginSuccess(result);
            } else {
                Log.e("AddUserTask", "Error during request!:"  + errorMessage);
                loginCallback.onLoginFailure(errorMessage);
            }
        }
    }
}
