package com.example.myapplication.monitordevices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewNetworkPageManager extends AppCompatActivity {

    private EditText editText_networkSSID;
    private EditText editText_networkPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_network_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText_networkSSID = findViewById(R.id.editText_networkSSID);
        editText_networkPassword = findViewById(R.id.editText_networkPSK);
    }

    public void newNetworkButton_Clicked(View view) {
        //Intent intent = new Intent(this, NewNetworkPageManager.class);
        //startActivity(intent);

        String ssid = editText_networkSSID.getText().toString();
        String psk = editText_networkPassword.getText().toString();

        if (ssid.isEmpty() || psk.isEmpty()) {
            Toast.makeText(getBaseContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the new network to SharedPreferences
        SessionManager sessionManager = new SessionManager(getBaseContext());
        if (sessionManager.saveNetwork(ssid, psk)) {
            Toast.makeText(getBaseContext(), "Network added. Now it can be used to connect cameras", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Failed to add network. Wasn't the network name already taken? If it was taken, you should first delete this network. Otherwise try again", Toast.LENGTH_SHORT).show();
        }
    }
}