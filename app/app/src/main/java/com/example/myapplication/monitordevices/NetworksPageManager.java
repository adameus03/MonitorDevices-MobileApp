package com.example.myapplication.monitordevices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

//TODO i18n
public class NetworksPageManager extends AppCompatActivity {

    static int NETWORKS_PAGE_MANAGER_RESULT_NETWORK_ADDED = 3;

    Context context;
    Activity activity;
    boolean isDeviceIntermediate = false;

    TextView textView_title;

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

        textView_title = findViewById(R.id.textView_title);

        isDeviceIntermediate = getIntent().getBooleanExtra("isDeviceIntermediate", false);
        if (isDeviceIntermediate) {
            textView_title.setText("Select target network");
        } else {
            textView_title.setText("My networks");
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView_networks);
        recyclerView.setHasFixedSize(true); // TODO for sure?

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        List<SessionManager.SavedNetwork> adapterNetworksList = new ArrayList<>();

        NetworksListAdapter adapter = new NetworksListAdapter(adapterNetworksList, new NetworksListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SessionManager.SavedNetwork network, NetworksListAdapter adapter) {
//                Intent intent = new Intent(getBaseContext(), StreamPageManager.class);
//                intent.putExtra("network", network);
//                startActivity(intent);

                if (isDeviceIntermediate) {
                    Intent intent = new Intent(getBaseContext(), DeviceRegistrationManager.class);
                    intent.putExtra("network", network);
                    startActivityForResult(intent, 0);
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle(network.ssid)
                            .setMessage(network.psk)
                            .show();
                }
            }
        }, new NetworksListAdapter.OnItemDeleteClickListener() {

            @Override
            public void onItemDeleteClick(SessionManager.SavedNetwork network, NetworksListAdapter adapter) {
                //delete network
                adapter.removeNetwork(network);
                new SessionManager(getBaseContext()).removeNetwork(network.ssid);
            }
        });

        List<SessionManager.SavedNetwork> savedNetworks = new SessionManager(getBaseContext()).getSavedNetworks();

        for (SessionManager.SavedNetwork network : savedNetworks) {
            System.out.println("PROVIDING NETWORK TO ADAPTER");
            // device.setNumber(camNumber);
            //adapterNetworksList.add(network);
            adapter.addNetwork(network);
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        System.out.println("NUMBER OF NETWORKS: " + adapterNetworksList.size());
    }

    static int ADD_NETWORK_REQ_CODE = 1;

    public void addNetworkButton_Clicked(View view) {
        Intent intent = new Intent(this, NewNetworkPageManager.class);
        //startActivity(intent);
        startActivityForResult(intent, ADD_NETWORK_REQ_CODE);
    }

    // This together with `startActivityForResult(intent, 0)` (in `addNetworkButton_Clicked`) is used to refresh the list of networks after returning from the NewNetworkPageManager activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if (!isDeviceIntermediate) { // reload the view
        if (requestCode == ADD_NETWORK_REQ_CODE) { // reload the view
            if (isDeviceIntermediate) {
                System.out.println("IS DEVICE INTERMEDIATE");
                setResult(NETWORKS_PAGE_MANAGER_RESULT_NETWORK_ADDED);
                finish();
            } else {
                System.out.println("IS NOT DEVICE INTERMEDIATE");
                finish();
                Intent intent = new Intent(this, NetworksPageManager.class);
                startActivity(intent);
            }
        } else {
            System.out.println("IS DEVICE INTERMEDIATE");
            // When DeviceRegistrationManager activity finishes, we should go back to MainPageManager activity, therefore this activity shouldn't be recreated
            // Passing activity result from DeviceRegistrationManager activity to MainPageManager activity
            setResult(resultCode);
            finish();
        }
    }
}