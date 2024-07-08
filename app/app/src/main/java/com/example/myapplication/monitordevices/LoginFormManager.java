package com.example.myapplication.monitordevices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public class LoginFormManager extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_login_form);

        etEmail = findViewById(R.id.etLoginFormEmail);
        etPassword = findViewById(R.id.etLoginFormPassword);
        tvErrorMessage = findViewById(R.id.tvWrongLoginData);

    }

    public void login(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        System.out.println("email: " + email + ", password: " + password);

        ServerManager serverManager = new ServerManager();
        serverManager.loginUser(email, password, new ServerCallback() {
            @Override
            public void onServerResponse(String result) {
                System.out.println("result: " + result);
                if (result.equals("INVALID EMAIL OR PASSWORD")) {
                    showErrorMessage(tvErrorMessage);
                    vibrate();
                }
                else {
                    Gson gson = new Gson();
                    LoginManager.LoginResponseFlattened flatResponse = gson
                            .fromJson(result, LoginManager.LoginResponseFlattened.class);

                    SessionManager sessionManager = new SessionManager(getBaseContext());
                    sessionManager.saveToken(flatResponse.token);
                    //sessionManager.saveEmail("bartek@example.com");
                    sessionManager.saveEmail(email);
                    sessionManager.saveName(flatResponse.username);
                    sessionManager.saveUserId(flatResponse.user_id);
                    Intent intent = new Intent(getBaseContext(), MainPageManager.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }

    public void showErrorMessage(TextView textView) {
        textView.setVisibility(View.VISIBLE);
    }

    public void hideErrorMessage(TextView textView) {
        textView.setVisibility(View.INVISIBLE);
    }
}
