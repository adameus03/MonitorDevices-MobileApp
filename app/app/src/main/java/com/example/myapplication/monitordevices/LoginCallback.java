package com.example.myapplication.monitordevices;

public interface LoginCallback {
    void onLoginSuccess();
    void onLoginFailure(String error);
}
