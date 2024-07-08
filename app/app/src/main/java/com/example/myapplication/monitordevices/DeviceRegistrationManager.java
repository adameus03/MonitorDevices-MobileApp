package com.example.myapplication.monitordevices;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DeviceRegistrationManager extends AppCompatActivity {
    static final String TAG = "DeviceRegistrationManager";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SessionManager.SavedNetwork network = getIntent().getParcelableExtra("network");
        if (network == null) {
            RuntimeException runtimeException = new RuntimeException("No network provided");
            Log.wtf(TAG, runtimeException); // Side note: This is serious stuff, fact that the method is called "wtf" is kind of inappropriate, but what can we do?
            throw runtimeException;
        }

        System.out.println("Constructing BLEController...");

        SessionManager sessionManager = new SessionManager(getBaseContext());

        new BLEController(this, this, network.ssid, network.psk, sessionManager.getUserId());
        System.out.println("BLEController constructed");
    }
}