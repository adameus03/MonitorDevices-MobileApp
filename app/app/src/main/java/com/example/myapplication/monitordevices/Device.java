package com.example.myapplication.monitordevices;

class Device {
    private Buffer device_id;
    private Buffer mac_address;
    private Buffer auth_key;
    private boolean registration_first_stage;
    private Buffer user_id;
    private String createdAt;
    private String updatedAt;

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
