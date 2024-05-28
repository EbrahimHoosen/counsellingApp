package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class registerUser extends AppCompatActivity {
    EditText txtUsername, txtEmail, txtPassword, txtConfirmPassword;
    Button UserSignUp, select_items;
    OkHttpClient client;
    final String url_Register= "https://lamp.ms.wits.ac.za/home/s2663134/userReg.php";
    final String url_userExists = "https://lamp.ms.wits.ac.za/home/s2663134/userExists.php";
    String username, email, password, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        client = new OkHttpClient();
        txtUsername = (EditText) findViewById(R.id.username);
        txtEmail = (EditText) findViewById(R.id.userEmail);
        txtPassword = (EditText) findViewById(R.id.userPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.userPConfirm);
        UserSignUp = (Button) findViewById(R.id.btnUserSignUp);
        select_items = findViewById(R.id.select_problems);

        select_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(getApplicationContext(), checkboxes.class);
                startActivity(j);
            }
        });

        UserSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = txtUsername.getText().toString();
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                confirmPassword = txtConfirmPassword.getText().toString();

                //Check to see if email or username has already been registered with
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder urlBuilder = HttpUrl.parse(url_userExists).newBuilder();
                urlBuilder.addQueryParameter("email", email);
                urlBuilder.addQueryParameter("username",username);

                String url = urlBuilder.build().toString();

                Request req = new Request.Builder().url(url).build();

                client.newCall(req).enqueue((new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                        final String responseData = response.body().string();
                        registerUser.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray out = new JSONArray(responseData);
                                    String jsonEmail = "", jsonUser = "";

                                    for (int i = 0; i < out.length(); i++) {
                                        JSONObject item = out.getJSONObject(i);
                                        jsonEmail = item.getString("EmailAddress");
                                        jsonUser = item.getString("Username");
                                    }

                                    if (password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty()) {
                                        Toast.makeText(registerUser.this, "Fields Cannot Be Left Empty", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (email.equals(jsonEmail)) { //email already in use
                                            Toast.makeText(registerUser.this, "Email Already In Use", Toast.LENGTH_SHORT).show();
                                        } else if (username.equals(jsonUser)) { //username already in use
                                            Toast.makeText(registerUser.this, "Username Already In Use", Toast.LENGTH_SHORT).show();
                                        } else if (!password.equals(confirmPassword)) {
                                            Toast.makeText(registerUser.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                                        } else {
                                            registerUser(username, email, password);
                                            Toast.makeText(registerUser.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                            Intent backToRegUser = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(backToRegUser);
                                        }
                                    }

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                    }
                }));
            }
        });

        TextView returnToLogin = (TextView) findViewById(R.id.textViewToLogin);
        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginPage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginPage);
            }
        });
    }

    public void registerUser(String userName, String Email, String Pass) {

        RequestBody requestBody = new FormBody.Builder()
                .add("username", userName)
                .add("email" , Email)
                .add("password", Pass)
                .build();
        Request request = new Request.Builder()
                .url(url_Register)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String jsonResponse = response.body().string();
                            System.out.println(jsonResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }
}

