package com.example.myapplication.monitordevices;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Buffer implements Parcelable {
    private String type;
    private byte[] data;

    public Buffer(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    protected Buffer(Parcel in) {
        type = in.readString();
        data = in.createByteArray();
    }

    public static final Creator<Buffer> CREATOR = new Creator<Buffer>() {
        @Override
        public Buffer createFromParcel(Parcel in) {
            return new Buffer(in);
        }

        @Override
        public Buffer[] newArray(int size) {
            return new Buffer[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Buffer{" +
                "type='" + type + '\'' +
                ", data=" + Arrays.toString(convertToUnsigned(data)) +
                '}';
    }

    private int[] convertToUnsigned(byte[] bytes) {
        int[] unsignedBytes = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            unsignedBytes[i] = bytes[i] & 0xFF;
        }
        return unsignedBytes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeByteArray(data);
    }
}
