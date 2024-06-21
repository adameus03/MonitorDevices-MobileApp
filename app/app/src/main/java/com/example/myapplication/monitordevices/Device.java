package com.example.myapplication.monitordevices;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {
    private Buffer device_id;
    private Buffer mac_address;
    private Buffer auth_key;
    private boolean registration_first_stage;
    private Buffer user_id;
    private String createdAt;
    private String updatedAt;
    private int number;

    public Device(Buffer device_id, Buffer mac_address, Buffer auth_key, boolean registration_first_stage, Buffer user_id, String createdAt, String updatedAt, int number) {
        this.device_id = device_id;
        this.mac_address = mac_address;
        this.auth_key = auth_key;
        this.registration_first_stage = registration_first_stage;
        this.user_id = user_id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.number = number;
    }

    protected Device(Parcel in) {
        device_id = in.readParcelable(Buffer.class.getClassLoader());
        mac_address = in.readParcelable(Buffer.class.getClassLoader());
        auth_key = in.readParcelable(Buffer.class.getClassLoader());
        registration_first_stage = in.readByte() != 0;
        user_id = in.readParcelable(Buffer.class.getClassLoader());
        createdAt = in.readString();
        updatedAt = in.readString();
        number = in.readInt();
    }

    // Parcelable Creator
    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    // Implementacja Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device_id, flags);
        dest.writeParcelable(mac_address, flags);
        dest.writeParcelable(auth_key, flags);
        dest.writeByte((byte) (registration_first_stage ? 1 : 0));
        dest.writeParcelable(user_id, flags);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeInt(number);
    }

    public Buffer getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Buffer device_id) {
        this.device_id = device_id;
    }

    public Buffer getMac_address() {
        return mac_address;
    }

    public void setMac_address(Buffer mac_address) {
        this.mac_address = mac_address;
    }

    public Buffer getAuth_key() {
        return auth_key;
    }

    public void setAuth_key(Buffer auth_key) {
        this.auth_key = auth_key;
    }

    public boolean isRegistration_first_stage() {
        return registration_first_stage;
    }

    public void setRegistration_first_stage(boolean registration_first_stage) {
        this.registration_first_stage = registration_first_stage;
    }

    public Buffer getUser_id() {
        return user_id;
    }

    public void setUser_id(Buffer user_id) {
        this.user_id = user_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Device{" +
                "device_id=" + device_id +
                ", mac_address=" + mac_address +
                ", auth_key=" + auth_key +
                ", registration_first_stage=" + registration_first_stage +
                ", user_id=" + user_id +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
