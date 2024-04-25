package com.example.myapplication.monitordevices;

public class ServerManager {
    private String REGISTER_URL = "http://192.168.100.4:8090/register/user";

    public void registerUser(String username, String password, String email) {
        RegistrationManager registrationManager = new RegistrationManager(REGISTER_URL,
                username, password, email);
        registrationManager.registerUser();
    }


}
