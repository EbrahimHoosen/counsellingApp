package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class registerUser extends AppCompatActivity {
    EditText userName, email, password, confirmPassword;

    Button UserSignUp, select_items;
    OkHttpClient client;
    final String url_Register= "https://lamp.ms.wits.ac.za/home/s2321330/registrationUser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        client = new OkHttpClient();
        userName = (EditText) findViewById(R.id.UserFname);
        email= (EditText) findViewById(R.id.Useremail);
        password= (EditText) findViewById(R.id.UserpassWord);
        confirmPassword= (EditText) findViewById(R.id.Userconfirm);
        UserSignUp= (Button) findViewById(R.id.UsersignUp);
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
                String username = userName.getText().toString();

                String Email = email.getText().toString();
                String Pass = password.getText().toString();
                String confirmPass = confirmPassword.getText().toString();
                if (Pass.isEmpty()||Email.isEmpty()||confirmPass.isEmpty()){
                    Toast.makeText(registerUser.this, "please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!Pass.equals(confirmPass)){
                    Toast.makeText(registerUser.this, "passwords are different", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(username, Email, Pass, confirmPass);
                    Toast.makeText(registerUser.this, "sign up was successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerUser(String userName, String Email, String Pass, String confirmPass) {

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
                            //textView.setText(jsonResponse);
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

