package com.example.myapplication.monitordevices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class RegistrationFormManager extends AppCompatActivity {

    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etRepeatedPassword;

    private TextView tvWrongEmail;
    private TextView tvWrongName;
    private TextView tvWrongPassword;
    private TextView tvWrongRepeatedPassword;

    private TextView tvEmailTaken;
    private TextView tvNameTaken;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_register_form);

        etEmail = findViewById(R.id.etRegisterFormEmail);
        etName = findViewById(R.id.etRegisterFormName);
        etPassword = findViewById(R.id.etRegisterFormPassword);
        etRepeatedPassword = findViewById(R.id.etRegisterFormRepeatPassword);

        tvWrongEmail = findViewById(R.id.tvRegisterWrongEmail);
        tvWrongName = findViewById(R.id.tvRegisterWrongName);
        tvWrongPassword = findViewById(R.id.tvRegisterWrongPassword);
        tvWrongRepeatedPassword = findViewById(R.id.tvRegisterWrongRepeatPassword);

        tvEmailTaken = findViewById(R.id.tvRegisterEmailTaken);
        tvNameTaken = findViewById(R.id.tvRegisterNameTaken);
    }

    public void register(View view) {
        String email = etEmail.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPassword.getText().toString();
        String repeatedPassword = etRepeatedPassword.getText().toString();

        if (validateRegistrationData(email, name, password, repeatedPassword)) {
            ServerManager serverManager = new ServerManager();
            serverManager.registerUser(name, password, email, new ServerCallback() {
                @Override
                public void onServerResponse(String result) {
                    System.out.println("FORM MANAGER ERROR: " + result);
                    if (result.equals("EMAIL ALREADY TAKEN")) {
                        vibrate();
                        hideErrorMessage(tvWrongEmail);
                        hideErrorMessage(tvNameTaken);
                        showErrorMessage(tvEmailTaken);
                    }
                    if (result.equals("USERNAME ALREADY EXISTS")) {
                        vibrate();
                        hideErrorMessage(tvWrongName);
                        hideErrorMessage(tvEmailTaken);
                        showErrorMessage(tvNameTaken);
                    }
                    if (result.equals("SUCCESS")) {
                        Toast.makeText(getBaseContext(), "Account created. Now, you can log in", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else {
            vibrate();
        }


    }

    public void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    public boolean validateRegistrationData(String email,
                                            String name,
                                            String password,
                                            String repeatedPassword) {
        boolean isGood = true;
        Validator validator = new Validator();

        if (validator.isValidEmail(email)){
            hideErrorMessage(tvWrongEmail);
        } else {
            hideErrorMessage(tvEmailTaken);
            showErrorMessage(tvWrongEmail);
            isGood = false;
        }

        if (validator.isValidName(name)) {
            hideErrorMessage(tvWrongName);
        } else {
            hideErrorMessage(tvNameTaken);
            showErrorMessage(tvWrongName);
            isGood = false;
        }

        if (validator.isValidPassword(password)) {
            hideErrorMessage(tvWrongPassword);
        } else {
            showErrorMessage(tvWrongPassword);
            isGood = false;
        }

        if (validator.isValidRepeatedPassword(password, repeatedPassword)) {
            hideErrorMessage(tvWrongRepeatedPassword);
        } else {
            showErrorMessage(tvWrongRepeatedPassword);
            isGood = false;
        }

        return isGood;
    }

    public void showErrorMessage(TextView textView) {
        textView.setVisibility(View.VISIBLE);
    }

    public void hideErrorMessage(TextView textView) {
        textView.setVisibility(View.INVISIBLE);
    }
}
