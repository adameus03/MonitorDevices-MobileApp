package com.example.myapplication.monitordevices;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
public class ServerManager {
    private String REGISTER_URL = "http://192.168.100.4:8090/register/user";
    private String LOGIN_URL = "http://192.168.100.4:8090/login";
    private String TOKEN_LOGIN_URL = "http://192.168.100.4:8090/check-token";

    public void getUserDevices(String username, GetDevicesCallback getDevicesCallback){
        String URL = "http://192.168.100.4:8090/users/" + username + "/devices";
        GetDevicesManager getDevicesManager = new GetDevicesManager(URL, new ServerCallback() {
            @Override
            public void onServerResponse(String result) {
                System.out.println("RESPONSE FROM GET DEVICES:" +  result);
                Gson gson = new Gson();
                Device[] devices = gson.fromJson(result, Device[].class);

                for (Device device : devices) {
                    System.out.println(device);
                }
                getDevicesCallback.getDevices(new ArrayList<>(Arrays.asList(devices)));
            }
        });
        getDevicesManager.getDevices();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

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

    public void tokenLogin(String email, String token, ServerCallback serverCallback){
        TokenLoginManager tokenLoginManager = new TokenLoginManager(
                TOKEN_LOGIN_URL,
                email,
                token,
                new LoginCallback() {
                    @Override
                    public void onLoginSuccess(String token) {

                        System.out.println("success!");
                        serverCallback.onServerResponse("OK");
                    }

                    @Override
                    public void onLoginFailure(String error) {
                        System.out.println("Failure!");
                    }
                });
        tokenLoginManager.loginUser();
    }


}
