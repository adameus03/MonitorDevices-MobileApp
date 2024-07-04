package com.example.myapplication.monitordevices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //new SessionManager(getBaseContext()).saveName("bartek");
        tryLogin();
    }

    public void openLoginForm(View view) {
        Intent intent = new Intent(this, LoginFormManager.class);
        startActivity(intent);
    }

    public void openRegistrationForm(View view) {
        Intent intent = new Intent(this, RegistrationFormManager.class);
        startActivity(intent);
    }

    public void tryLogin() {
        SessionManager sessionManager = new SessionManager(this);
        String email = sessionManager.getEmail();
        String token = sessionManager.getToken();
        ServerManager serverManager = new ServerManager();
        serverManager.tokenLogin(email, token, new ServerCallback() {
            @Override
            public void onServerResponse(String result) {
                if (result.equals("OK")) {
                    Intent intent = new Intent(getBaseContext(), MainPageManager.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}