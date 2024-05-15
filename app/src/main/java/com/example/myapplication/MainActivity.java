package com.example.myapplication;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn = findViewById(R.id.btnLogin);
        Button testing = findViewById(R.id.userPage);
        RadioButton user = findViewById(R.id.radioUser);
        RadioButton counsellor = findViewById(R.id.radioCounsellor);
        Button crtbtn = findViewById(R.id.btnCreateAccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.isChecked()) {
                    userLogin();
                } else if (counsellor.isChecked()) {
                    //call counsellorLogin()
                } else {
                    Toast.makeText(MainActivity.this, "User Type Must Be Selected", LENGTH_SHORT).show();
                }

            }
        });
        testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(getApplicationContext(), registerUser.class);
                startActivity(j);
            }
        });
        crtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), registerCounsellor.class);
                startActivity(i);
            }
        });
    }

    public void userLogin() {

        System.out.println("Login Attempt Recorded");

        OkHttpClient client =new OkHttpClient();

        String email, password;
        EditText emailTxt = (EditText) findViewById(R.id.txtEmail);
        EditText passwordTxt = (EditText) findViewById(R.id.txtPassword);

        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2663134/login.php").newBuilder();
        urlBuilder.addQueryParameter("email", email);
        urlBuilder.addQueryParameter("password", password);
        String url = urlBuilder.build().toString();

        Request req =new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseData = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray all = new JSONArray(responseData);
                            String jsonEmail = "", jsonPassword = "";
                            for (int i = 0; i < all.length(); i++) {
                                JSONObject item = all.getJSONObject(i);
                                jsonEmail = item.getString("EmailAddress");
                                jsonPassword = item.getString("Password");
                            }
                            if (email.equals(jsonEmail) && password.equals(jsonPassword)) {
                                Toast.makeText(MainActivity.this, "Login Successful", LENGTH_SHORT).show();
                                //System.out.println("Login Successful");
                            } else {
                                Toast.makeText(MainActivity.this, "Email or Password Incorrect", LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }
}