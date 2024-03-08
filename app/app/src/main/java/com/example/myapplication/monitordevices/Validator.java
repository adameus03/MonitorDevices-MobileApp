package com.example.myapplication.monitordevices;

import java.util.regex.Pattern;

public class Validator {
    private static String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static String PASSWORD_REGEX = "^.{8,}$";
    private static String NAME_REGEX = "^[a-zA-Z]{1,32}$";

    public boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    public boolean isValidRepeatedPassword(String password, String repeated) {
        return repeated.equals(password);
    }

    public boolean isValidName(String name) {
        return Pattern.matches(NAME_REGEX, name);
    }
}
