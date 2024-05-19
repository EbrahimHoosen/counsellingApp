package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class registerCounsellor extends AppCompatActivity {
    EditText firstName, lastName, email, password, confirmPassword;
    Button counsellorSignUp, selectProblems;
    OkHttpClient client;
    final String url_Register = "https://lamp.ms.wits.ac.za/home/s2663134/counsellorReg.php";
    final String url_counsellorExists = "https://lamp.ms.wits.ac.za/home/s2663134/counsellorExists.php";
    boolean exists = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_counsellor);

        client = new OkHttpClient();
        firstName = (EditText) findViewById(R.id.userFname);
        lastName= (EditText) findViewById(R.id.userLname);
        email = (EditText) findViewById(R.id.userEmail);
        password= (EditText) findViewById(R.id.UserpassWord);
        confirmPassword= (EditText) findViewById(R.id.cnsconfirm);
        counsellorSignUp= (Button) findViewById(R.id.signUp);
        selectProblems = (Button) findViewById(R.id.select_problems);
        Spinner dropdown = findViewById(R.id.spinner1);

        //create a list of items for the spinner.
        String[] items = new String[]{"Undergraduate", "Postgraduate", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        //dropdown.setPrompt("select qualification");
        selectProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), checkboxes.class);
                startActivity(i);
            }
        });

        counsellorSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = firstName.getText().toString();
                String lname = lastName.getText().toString();
                String Email = email.getText().toString();
                String Pass = password.getText().toString();
                String confirmPass = confirmPassword.getText().toString();
                String spinnerString = dropdown.getSelectedItem().toString();//getting the string in my dropdown menu
                if (Pass.isEmpty() || Email.isEmpty() || confirmPass.isEmpty() || fname.isEmpty() || lname.isEmpty()){
                    Toast.makeText(registerCounsellor.this, "Fields Cannot Be Left Empty", Toast.LENGTH_SHORT).show();
                }
                else if (!Pass.equals(confirmPass)){
                    Toast.makeText(registerCounsellor.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    Toast.makeText(registerCounsellor.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                }
                else if (spinnerString.equals("select qualification")) {
                    Toast.makeText(registerCounsellor.this, "Please Select A Qualification", Toast.LENGTH_SHORT).show();
                } else if (checkExists()){
                    Toast.makeText(registerCounsellor.this, "Email Already In Use", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(fname, lname, Email,spinnerString, Pass);
                    Toast.makeText(registerCounsellor.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void registerUser(String firstName, String lastName, String Email, String spinner, String Pass) {

        RequestBody requestBody = new FormBody.Builder()
                .add("first_name", firstName)
                .add("last_name", lastName)
                .add("email" , Email)
                .add("password", Pass)
                .add("qualifications",spinner)
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

    //Checks whether an email address has already been used to make an account
    public boolean checkExists() {

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(url_counsellorExists).build();

        client.newCall(req).enqueue((new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                final String responseData = response.body().string();
                registerCounsellor.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray out = new JSONArray(responseData);
                            String jsonEmail = "";

                            for (int i = 0; i < out.length(); i++) {
                                JSONObject item = out.getJSONObject(i);
                                jsonEmail = item.getString("EmailAddress");
                            }

                            if (email.equals(jsonEmail)) {
                                exists = true;
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

            }
        }));
        return exists;
    }
}