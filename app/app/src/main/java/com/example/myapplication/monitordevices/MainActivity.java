package com.example.myapplication.monitordevices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openLoginForm(View view) {
        Intent intent = new Intent(this, LoginFormManager.class);
        startActivity(intent);
    }

    public void openRegistrationForm(View view) {
        Intent intent = new Intent(this, RegistrationFormManager.class);
        startActivity(intent);
    }

}