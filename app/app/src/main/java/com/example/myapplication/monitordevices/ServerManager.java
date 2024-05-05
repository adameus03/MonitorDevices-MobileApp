package com.example.myapplication.monitordevices;

public class ServerManager {
    private String REGISTER_URL = "http://192.168.100.4:8090/register/user";

    public void registerUser(String username, String password, String email, ServerCallback serverCallback) {
        RegistrationManager registrationManager = new RegistrationManager(
                REGISTER_URL,
                username,
                password,
                email,
                new RegistrationCallback() {
                    @Override
                    public void onRegistrationSuccess() {
                        System.out.println("Registration success!!");
                        serverCallback.onServerResponse("success");
                    }

                    @Override
                    public void onRegistrationFailure(String error) {
                        System.out.println("User already exist!!");
                        serverCallback.onServerResponse("failed!");
                    }
                });
        registrationManager.registerUser();
    }


}
