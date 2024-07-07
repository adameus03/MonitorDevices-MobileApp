package com.example.myapplication.monitordevices;

import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// TODO: Support for removing networks
public class NetworksListAdapter extends RecyclerView.Adapter<NetworksListAdapter.NetworkViewHolder> {

    private List<SessionManager.SavedNetwork> networks;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SessionManager.SavedNetwork network);
    }

    public static class NetworkViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public NetworkViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick((SessionManager.SavedNetwork) v.getTag());
                    }
                }
            });
        }
    }

    public NetworksListAdapter(List<SessionManager.SavedNetwork> networks, OnItemClickListener listener) {
        this.networks = networks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.network_list_item, parent, false);
        return new NetworkViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkViewHolder holder, int position) {
        SessionManager.SavedNetwork currentNetwork = networks.get(position);
        holder.textView.setText(currentNetwork.ssid);
        holder.itemView.setTag(currentNetwork);
    }

    @Override
    public int getItemCount() {
        int size = networks.size();
        System.out.println("getItemCount: networks.size(): " + size);
        return size;

    }

    public void addNetwork(SessionManager.SavedNetwork network) {
        networks.add(network);
        int position = networks.size() - 1;
        System.out.println("addNetwork: position:" + position);
        notifyItemInserted(position);
    }
}
