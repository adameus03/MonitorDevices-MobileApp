package com.example.myapplication.monitordevices;

public interface LoginCallback {
    void onLoginSuccess(String token);
    void onLoginFailure(String error);
}
