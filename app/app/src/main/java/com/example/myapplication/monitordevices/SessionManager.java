package com.example.myapplication.monitordevices;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SessionManager {
    private static final String PREF_NAME = "token";
    private static final String KEY_TOKEN = "JWT";
    private static final String KEY_NAME = "name";

    private static final String KEY_EMAIL = "email";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void saveEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void saveName(String name) {
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    // TODO use exceptions instead of retuning false for multiple reasons
    public boolean saveNetwork(String ssid, String psk) {
        String netsJson = sharedPreferences.getString("nets", "[]");
        List<SavedNetwork> nets = new Gson().fromJson(netsJson, new TypeToken<List<SavedNetwork>>() {}.getType());

        // Check if the SSID already exists
        if (nets.stream().anyMatch(n -> n.ssid.equals(ssid))) {
            System.out.println("SSID already found in SharedPreferences");
            return false;
        }

        // Add new network to the list
        nets.add(new SavedNetwork(ssid, psk));

        // Save the list back to SharedPreferences
        editor.putString("nets", new Gson().toJson(nets));
        if (!editor.commit()) {
            System.out.println("Failed to save network to SharedPreferences");
            return false;
        }
        return true;

//        Set<String> ssids = new HashSet<>(CustomUtils.requireNonNullElse(sharedPreferences.getStringSet("ssids", null), new HashSet<>()));
//        Set<String> psks = new HashSet<>(CustomUtils.requireNonNullElse(sharedPreferences.getStringSet("psks", null), new HashSet<>()));
//
//        // Check if the SSID already exists
//        if (ssids.contains(ssid)) {
//            System.out.println("SSID already found in SharedPreferences");
//            return false;
//        }
//
//
//
//        // Add the SSID and PSK to the sets
//        ssids.add(ssid);
//        psks.add(psk);
//        // Save the sets back to SharedPreferences
//        editor.putStringSet("ssids", ssids);
//        editor.putStringSet("psks", psks);
//        if (!editor.commit()) {
//            System.out.println("Failed to save network to SharedPreferences");
//            return false;
//        }
//        return true;

    }

    // TODO use exceptions instead of retuning false for multiple reasons
    public boolean removeNetwork(String ssid) {
        String netsJson = sharedPreferences.getString("nets", "[]");
        List<SavedNetwork> nets = new Gson().fromJson(netsJson, new TypeToken<List<SavedNetwork>>() {}.getType());

        // Check if the SSID exists
        if (!nets.stream().anyMatch(n -> n.ssid.equals(ssid))) {
            System.out.println("SSID not found in SharedPreferences");
            return false;
        }

        // Remove the network from the list
        nets.removeIf(n -> n.ssid.equals(ssid));

        // Save the list back to SharedPreferences
        editor.putString("nets", new Gson().toJson(nets));
        if (!editor.commit()) {
            System.out.println("Failed to remove network from SharedPreferences");
            return false;
        }
        return true;

//        Set<String> ssids = new HashSet<>(CustomUtils.requireNonNullElse(sharedPreferences.getStringSet("ssids", null), new HashSet<>()));
//        Set<String> psks = new HashSet<>(CustomUtils.requireNonNullElse(sharedPreferences.getStringSet("psks", null), new HashSet<>()));
//
//        // Check if the SSID exists
//        if (!ssids.contains(ssid)) {
//            System.out.println("SSID not found in SharedPreferences");
//            return false;
//        }
//
//        List<String> ssidsList = new ArrayList<>(ssids);
//        List<String> psksList = new ArrayList<>(psks);
//
//        int networkIndex = ssidsList.indexOf(ssid);
//        ssidsList.remove(networkIndex);
//        psksList.remove(networkIndex);
//
//        Set<String> newSsids = new HashSet<>(ssidsList);
//        Set<String> newPsks = new HashSet<>(psksList);
//
//        // Save new sets back to SharedPreferences
//        editor.putStringSet("ssids", newSsids);
//        editor.putStringSet("psks", newPsks);
//        if (!editor.commit()) {
//            System.out.println("Failed to remove network from SharedPreferences");
//            return false;
//        }
//        return true;
    }

    public String getName(){return sharedPreferences.getString(KEY_NAME, null);}

    public String getEmail(){return sharedPreferences.getString(KEY_EMAIL, null);}

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public List<SavedNetwork> getSavedNetworks() {
        String netsJson = sharedPreferences.getString("nets", "[]");
        return new Gson().fromJson(netsJson, new TypeToken<List<SavedNetwork>>() {}.getType());

//        Set<String> ssids = new HashSet<>(CustomUtils.requireNonNullElse(sharedPreferences.getStringSet("ssids", null), new HashSet<>()));
//        Set<String> psks = new HashSet<>(CustomUtils.requireNonNullElse(sharedPreferences.getStringSet("psks", null), new HashSet<>()));
//        List<String> ssidsList = new ArrayList<>(ssids);
//        List<String> psksList = new ArrayList<>(psks);
//
//        if (ssidsList.size() != psksList.size()) {
//            throw new RuntimeException("Number of SSIDs and PSKs read from SharedPreferences didn't match - number of SSIDs: " + ssidsList.size() + ", number of PSKs: " + psksList.size());
//        }
//
//        List<SavedNetwork> savedNetworks = new ArrayList<>();
//        for (int i = 0; i < ssidsList.size(); i++) {
//            savedNetworks.add(new SavedNetwork(ssidsList.get(i), psksList.get(i)));
//        }
//        return savedNetworks;
    }

    // TODO Refactor - Extract to a dedicated file?
    public static class SavedNetwork implements Parcelable {
        public final String ssid;
        public final String psk;
        public SavedNetwork(String ssid, String psk) {
            this.ssid = ssid;
            this.psk = psk;
        }

        protected SavedNetwork(Parcel in) {
            ssid = in.readString();
            psk = in.readString();
        }

        // Parcelable CREATOR - required for implementing Parcelable
        public static final Creator<SavedNetwork> CREATOR = new Creator<SavedNetwork>() {
            @Override
            public SavedNetwork createFromParcel(Parcel in) {
                return new SavedNetwork(in);
            }

            @Override
            public SavedNetwork[] newArray(int size) {
                return new SavedNetwork[size];
            }
        };

        // Parcelable implementation (used for RecyclerView)
        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
//            dest.writeParcelable(device_id, flags);
//            dest.writeParcelable(mac_address, flags);
//            dest.writeParcelable(auth_key, flags);
//            dest.writeByte((byte) (registration_first_stage ? 1 : 0));
//            dest.writeParcelable(user_id, flags);
//            dest.writeString(createdAt);
//            dest.writeString(updatedAt);
//            dest.writeInt(number);

            dest.writeString(ssid);
            dest.writeString(psk);
        }
    }
}
