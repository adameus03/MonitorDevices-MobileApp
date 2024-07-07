package com.example.myapplication.monitordevices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// TODO Use fragments or find a way to handle button clicks in list items or a different approach
public class DevicesListAdapter extends RecyclerView.Adapter<DevicesListAdapter.DeviceViewHolder> {

    private List<Device> devices;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Device device);
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
                        listener.onItemClick((Device) v.getTag());
                    }
                }
            });
        }
    }

    public DevicesListAdapter(List<Device> devices, OnItemClickListener listener) {
        this.devices = devices;
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
        Device currentDevice = devices.get(position);
        holder.textView.setText("Camera " + currentDevice.getNumber());
        holder.itemView.setTag(currentDevice);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevice(Device device) {
        devices.add(device);
        notifyItemInserted(devices.size() - 1);
    }
}
