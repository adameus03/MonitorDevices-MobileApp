package com.example.myapplication.monitordevices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DevicesListAdapter extends RecyclerView.Adapter<DevicesListAdapter.DeviceViewHolder> {

    private List<String> deviceList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String deviceName);
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public DeviceViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick((String) v.getTag());
                    }
                }
            });
        }
    }

    public DevicesListAdapter(List<String> deviceList, OnItemClickListener listener) {
        this.deviceList = deviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_list_item, parent, false);
        return new DeviceViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        String currentDevice = deviceList.get(position);
        holder.textView.setText(currentDevice);
        holder.itemView.setTag(currentDevice);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void addDevice(String deviceName) {
        deviceList.add(deviceName);
        notifyItemInserted(deviceList.size() - 1);
    }
}
