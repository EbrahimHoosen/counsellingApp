package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class registerCounsellor extends AppCompatActivity {

    Dialog problemSelectDialog;
    ArrayList<Integer> problems = new ArrayList<>();
    EditText txtFirstName, txtLastName, txtEmail, txtPassword, txtConfirmPassword;
    Button counsellorSignUp, selectProblems;
    OkHttpClient client;
    final String url_Register = "https://lamp.ms.wits.ac.za/home/s2663134/counsellorReg.php";
    final String url_counsellorExists = "https://lamp.ms.wits.ac.za/home/s2663134/counsellorExists.php";

    final String url_getID = "https://lamp.ms.wits.ac.za/home/s2663134/getCID.php";
    final String url_insertProblems = "https://lamp.ms.wits.ac.za/home/s2663134/insertCProblems.php";
    String fname, lname, email, password, confirmPassword;

    int counsellorID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_counsellor);

        client = new OkHttpClient();

        txtFirstName = (EditText) findViewById(R.id.counsellorFName);
        txtLastName = (EditText) findViewById(R.id.counsellorLName);
        txtEmail = (EditText) findViewById(R.id.counsellorEmail);
        txtPassword= (EditText) findViewById(R.id.userPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.counsellorPConfirm);
        counsellorSignUp = (Button) findViewById(R.id.signUp);
        selectProblems = (Button) findViewById(R.id.select_problems);
        Spinner dropdown = findViewById(R.id.spinner1);

        //create a list of items for the spinner.
        String[] items = new String[]{"Undergraduate", "Postgraduate", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        //dropdown.setPrompt("select qualification");

        problemSelectDialog = new Dialog(registerCounsellor.this);
        problemSelectDialog.setContentView(R.layout.problems_dialog);
        problemSelectDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        problemSelectDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.create_account_bg));
        problemSelectDialog.setCancelable(true);

        CheckBox cbxDepression = problemSelectDialog.findViewById(R.id.checkbox_depression);
        CheckBox cbxAnxiety = problemSelectDialog.findViewById(R.id.checkbox_anxiety);
        CheckBox cbxPTSD = problemSelectDialog.findViewById(R.id.checkbox_PTSD);
        CheckBox cbxAddiction = problemSelectDialog.findViewById(R.id.checkbox_addiction);
        CheckBox cbxParanoia = problemSelectDialog.findViewById(R.id.checkbox_paranoia);
        CheckBox cbxInsomnia = problemSelectDialog.findViewById(R.id.checkbox_insomnia);
        CheckBox cbxBodyDysmorphia = problemSelectDialog.findViewById(R.id.checkbox_bodyDysmorphia);
        CheckBox cbxBPD = problemSelectDialog.findViewById(R.id.checkbox_bpd);
        CheckBox cbxSchizo = problemSelectDialog.findViewById(R.id.checkbox_schizo);

        Button confirmProblems = problemSelectDialog.findViewById(R.id.confirm_button);

        confirmProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbxDepression.isChecked()) problems.add(1);
                if (cbxAnxiety.isChecked()) problems.add(2);
                if (cbxPTSD.isChecked()) problems.add(3);
                if (cbxAddiction.isChecked()) problems.add(4);
                if (cbxParanoia.isChecked()) problems.add(5);
                if (cbxInsomnia.isChecked()) problems.add(6);
                if (cbxBodyDysmorphia.isChecked()) problems.add(7);
                if (cbxBPD.isChecked()) problems.add(8);
                if (cbxSchizo.isChecked()) problems.add(9);
                problemSelectDialog.dismiss();
            }
        });

        selectProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problemSelectDialog.show();
            }
        });

        counsellorSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = txtFirstName.getText().toString();
                lname = txtLastName.getText().toString();
                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                confirmPassword = txtConfirmPassword.getText().toString();
                String spinnerString = dropdown.getSelectedItem().toString(); //getting the string in my dropdown menu

                //Check to see if email has already been registered with
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder urlBuilder = HttpUrl.parse(url_counsellorExists).newBuilder();
                urlBuilder.addQueryParameter("email", email);

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

                                    if (password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty() || fname.isEmpty() || lname.isEmpty()) {
                                        Toast.makeText(registerCounsellor.this, "Fields Cannot Be Left Empty", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (email.equals(jsonEmail)) { //email already in use
                                            Toast.makeText(registerCounsellor.this, "Email Already In Use", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (!password.equals(confirmPassword)) {
                                            Toast.makeText(registerCounsellor.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                            Toast.makeText(registerCounsellor.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                                        }
                                        else if (spinnerString.equals("select qualification")) {
                                            Toast.makeText(registerCounsellor.this, "Please Select A Qualification", Toast.LENGTH_SHORT).show();
                                        } else {
                                            registerCounsellor(fname, lname, email, spinnerString, password);
                                            Toast.makeText(registerCounsellor.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                            Intent backToRegCounsellor = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(backToRegCounsellor);
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
    public void registerCounsellor(String firstName, String lastName, String Email, String spinner, String Pass) {

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

        System.out.println(request.toString());
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
                            HttpUrl.Builder urlID = HttpUrl.parse(url_getID).newBuilder();
                            urlID.addQueryParameter("email", email);

                            String url1 = urlID.build().toString();

                            Request reqID = new Request.Builder().url(url1).build();

                            client.newCall(reqID).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    final String responseData = response.body().string();
                                    try {
                                        JSONArray all = new JSONArray(responseData);
                                        int jsonID = 0;
                                        for (int i = 0; i < all.length(); i++) {
                                            JSONObject item = all.getJSONObject(i);
                                            jsonID = item.getInt("CounsellorID");
                                        }
                                        counsellorID = jsonID;

                                        HttpUrl.Builder urlBuilder = HttpUrl.parse(url_insertProblems).newBuilder();
                                        urlBuilder.addQueryParameter("cID", String.valueOf(counsellorID));

                                        for (int i = 0; i < problems.size(); i++) {

                                            urlBuilder.addQueryParameter("problem", String.valueOf(problems.get(i)));
                                            String url = urlBuilder.build().toString();

                                            Request req = new Request.Builder().url(url).build();

                                            client.newCall(req).enqueue(new Callback() {
                                                @Override
                                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                    e.printStackTrace();
                                                }

                                                @Override
                                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                String jsonResponse = response.body().string();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                            urlBuilder.removeAllQueryParameters("problem");
                                        }
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

}