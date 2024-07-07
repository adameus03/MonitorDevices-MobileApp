package com.example.myapplication.monitordevices;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetDevicesManager {
    private static String URL;

    private String email;
    private String token;

    private static String errorMessage = null;

    static ServerCallback serverCallback;

    public GetDevicesManager(String URL, ServerCallback serverCallback) {
        this.URL = URL;
        this.serverCallback = serverCallback;
    }

    public void getDevices() {
        new getDevicesTask().execute();
    }

    private static class getDevicesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(URL)
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
                serverCallback.onServerResponse(result);
            } else {
                Log.e("AddUserTask", "Error during request!:"  + errorMessage);
                serverCallback.onServerResponse(null);
            }
        }
    }
}
