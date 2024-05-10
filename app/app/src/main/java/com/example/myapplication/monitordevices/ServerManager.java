package com.example.myapplication.monitordevices;

public class ServerManager {
    private String REGISTER_URL = "http://192.168.100.4:8090/register/user";
    private String LOGIN_URL = "http://192.168.100.4:8090/login";

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
                        serverCallback.onServerResponse("SUCCESS");
                    }

                    @Override
                    public void onRegistrationFailure(String error) {
                        System.out.println("Error: " + error);
                        serverCallback.onServerResponse(error);
                    }
                });
        registrationManager.registerUser();
    }

    public void loginUser(String email, String password, ServerCallback serverCallback) {
        LoginManager loginManager = new LoginManager(
                LOGIN_URL,
                email,
                password,
                new LoginCallback() {
                    @Override
                    public void onLoginSuccess(String token) {
                        System.out.println("Login success!!");
                        serverCallback.onServerResponse(token);
                    }

                    @Override
                    public void onLoginFailure(String error) {
                        System.out.println("Error: " + error);
                        serverCallback.onServerResponse(error);
                    }
                });
        loginManager.loginUser();
    }


}
