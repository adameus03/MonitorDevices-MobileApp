package com.example.myapplication.monitordevices;

import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// DONE: Support for removing networks
public class NetworksListAdapter extends RecyclerView.Adapter<NetworksListAdapter.NetworkViewHolder> {

    private final List<SessionManager.SavedNetwork> networks;
    private final OnItemClickListener itemClickListener;
    private final OnItemDeleteClickListener itemDeleteListener;

    public interface OnItemClickListener {
        void onItemClick(SessionManager.SavedNetwork network, NetworksListAdapter adapter);
    }

    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(SessionManager.SavedNetwork network, NetworksListAdapter adapter);
    }


    public static class NetworkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        public ImageView imageView_delete;

        private OnItemClickListener itemClicklistener;
        private OnItemDeleteClickListener itemDeleteListener;

        private NetworksListAdapter adapter;

        public NetworkViewHolder(View itemView, final OnItemClickListener itemClicklistener, final OnItemDeleteClickListener itemDeleteListener, NetworksListAdapter adapter) {
            super(itemView);

            this.itemClicklistener = itemClicklistener;
            this.itemDeleteListener = itemDeleteListener;
            this.adapter = adapter;

            textView = itemView.findViewById(R.id.textView);
            imageView_delete = itemView.findViewById(R.id.imageView_delete);

            textView.setOnClickListener(this);
            imageView_delete.setOnClickListener(this);
            itemView.setOnClickListener(this);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
////                    if (position != RecyclerView.NO_POSITION && listener != null) {
////                        listener.onItemClick((SessionManager.SavedNetwork) v.getTag());
////                    }
//                    if (position != RecyclerView.NO_POSITION) {
//                        if (v.getId() == imageView_delete.getId()) {
//                            if (itemDeleteListener != null) {
//                                itemDeleteListener.onItemDeleteClick((SessionManager.SavedNetwork) v.getTag());
//                            } else {
//                                System.out.println("onItemClick: itemDeleteListener is null");
//                            }
//                        } else {
//                            if (itemClicklistener != null) {
//                                itemClicklistener.onItemClick((SessionManager.SavedNetwork) v.getTag());
//                            } else {
//                                System.out.println("onItemClick: itemClicklistener is null");
//                            }
//                        }
//                    }
//                }
//            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION && listener != null) {
//                        listener.onItemClick((SessionManager.SavedNetwork) v.getTag());
//                    }
            if (position != RecyclerView.NO_POSITION) {
                if (v.equals(imageView_delete)) {
                    if (itemDeleteListener != null) {
                        //Object tag = v.getTag();
                        Object tag = itemView.getTag();
                        if (tag == null) {
                            throw new RuntimeException("onItemClick: tag is null");
                        }
                        //itemDeleteListener.onItemDeleteClick((SessionManager.SavedNetwork) v.getTag());
                        itemDeleteListener.onItemDeleteClick((SessionManager.SavedNetwork) tag, adapter);
                    } else {
                        System.out.println("onItemClick: itemDeleteListener is null");
                    }
                } else {
                    if (itemClicklistener != null) {
                        //Object tag = v.getTag();
                        Object tag = itemView.getTag();
                        if (tag == null) {
                            throw new RuntimeException("onItemClick: tag is null");
                        }
                        itemClicklistener.onItemClick((SessionManager.SavedNetwork) tag, adapter);
                    } else {
                        System.out.println("onItemClick: itemClicklistener is null");
                    }
                }
            }
        }
    }

    public NetworksListAdapter(List<SessionManager.SavedNetwork> networks, OnItemClickListener itemClickListener, OnItemDeleteClickListener itemDeleteListener) {
        this.networks = networks;
        //this.listener = listener;
        this.itemClickListener = itemClickListener;
        this.itemDeleteListener = itemDeleteListener;
    }

    @NonNull
    @Override
    public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.network_list_item, parent, false);
        //return new NetworkViewHolder(itemView, listener);
        return new NetworkViewHolder(itemView, itemClickListener, itemDeleteListener, this);
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

    public void removeNetwork(SessionManager.SavedNetwork network) {
        int position = networks.indexOf(network);
        if (position > -1) {
            networks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, networks.size());
        }
        System.out.println("removeNetwork: networks.size(): " + networks.size());
        System.out.println("removeNetwork: position: " + position);
    }
}
