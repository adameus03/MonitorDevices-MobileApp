package com.example.myapplication.monitordevices;

public interface RegistrationCallback {
    void onRegistrationSuccess();
    void onRegistrationFailure(String error);
}