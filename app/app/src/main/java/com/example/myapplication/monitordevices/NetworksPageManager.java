package com.example.myapplication.monitordevices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NetworksPageManager extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_networks_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView_networks);
        recyclerView.setHasFixedSize(true); // TODO for sure?

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        List<SessionManager.SavedNetwork> adapterNetworksList = new ArrayList<>();

        NetworksListAdapter adapter = new NetworksListAdapter(adapterNetworksList, new NetworksListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SessionManager.SavedNetwork network) {
//                Intent intent = new Intent(getBaseContext(), StreamPageManager.class);
//                intent.putExtra("network", network);
//                startActivity(intent);

                new AlertDialog.Builder(context)
                        .setTitle(network.ssid)
                        .setMessage(network.psk)
                        .show();
            }
        });

        List<SessionManager.SavedNetwork> savedNetworks = new SessionManager(getBaseContext()).getSavedNetworks();

        for (SessionManager.SavedNetwork network : savedNetworks) {
            System.out.println("PROVIDING NETWORK TO ADAPTER");
            // device.setNumber(camNumber);
            adapterNetworksList.add(network);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        System.out.println("NUMBER OF NETWORKS: " + adapterNetworksList.size());
    }

    public void addNetworkButton_Clicked(View view) {
        Intent intent = new Intent(this, NewNetworkPageManager.class);
        //startActivity(intent);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
        Intent intent = new Intent(this, NetworksPageManager.class);
        startActivity(intent);
    }
}