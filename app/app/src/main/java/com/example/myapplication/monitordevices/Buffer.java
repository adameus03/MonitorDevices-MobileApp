package com.example.myapplication.monitordevices;

import java.util.Arrays;

class Buffer {
    private String type;
    private byte[] data;

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
}

