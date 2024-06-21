package com.example.myapplication.monitordevices;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StreamPageManager extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_page);
        Device device = getIntent().getParcelableExtra("device");
        if (device != null) {
            System.out.println(device.toString());
        }
    }
}
