package com.example.myapplication;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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

    private Dialog createAccountDialog;
    private String email, password, userType;
    private Login loginAttempt;

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

        Button btnLogin = findViewById(R.id.btnLogin);
        RadioButton user = findViewById(R.id.radioUser);
        RadioButton counsellor = findViewById(R.id.radioCounsellor);

        EditText email_txt = (EditText) findViewById(R.id.txtEmail);
        EditText password_txt = (EditText) findViewById(R.id.txtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_txt.getText().toString();
                password = password_txt.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Email and Password Required", LENGTH_SHORT).show();
                } else {
                    if (!user.isChecked() && !counsellor.isChecked()) {
                        Toast.makeText(MainActivity.this, "User Type Must Be Selected", LENGTH_SHORT).show();
                    } else {
                        if (user.isChecked()) {
                            //Creates an instance of the Login class to verify User credentials
                            loginAttempt = new Login(email, password, "Users");
                            userType = "user";
                        } else if (counsellor.isChecked()) {
                            //Creates an instance of the Login class to verify Counsellor credentials
                            loginAttempt = new Login(email, password, "Counsellors");
                            userType = "counsellor";
                        }
                        loginAttempt.login(new Login.LoginCallback() {
                            @Override
                            public void onResult(String result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, result, LENGTH_SHORT).show();
                                        if (result.equals("Login Successful")) {
                                            if (userType.equals("user")) {
                                                User newLogin = new User(loginAttempt.getUsername(), loginAttempt.getEmail(), loginAttempt.getImageID(), loginAttempt.getUserID());
                                                Intent userChatListUI = new Intent(getApplicationContext(), userChatUI.class);
                                                startActivity(userChatListUI);
                                                SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);// this is how you store the value of username to compare it in the php
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putInt("UserID", loginAttempt.getUserID()); //store the UserID to use in other activities
                                                editor.apply();
                                                System.out.println(newLogin);
                                            } else if (userType.equals("counsellor")) {
                                                Counsellor newLogin = new Counsellor(loginAttempt.getFirstName(), loginAttempt.getLastName(), loginAttempt.getEmail(), loginAttempt.getCounsellorID());
                                                Intent counsellorChatListUI = new Intent(getApplicationContext(), CounsellorChatListUI.class);
                                                startActivity(counsellorChatListUI);
                                                SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);// this is how you store the value of username to compare it in the php
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putInt("CounsellorID", loginAttempt.getCounsellorID()); //store the CounsellorID to use in other activities
                                                editor.apply();
                                                System.out.println(newLogin);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }

                }

            }
        });

        //Declares a dialog popup window that lets users decide what to register as
        createAccountDialog = new Dialog(MainActivity.this);
        createAccountDialog.setContentView(R.layout.create_account_dialog);
        createAccountDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createAccountDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.create_account_bg));
        createAccountDialog.setCancelable(false);

        //Creates a user account
        Button btnCreateAccountUser = createAccountDialog.findViewById(R.id.btnCreateUser);
        btnCreateAccountUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userRegPage = new Intent(getApplicationContext(), registerUser.class);
                startActivity(userRegPage);
                //createAccountDialog.dismiss();
            }
        });

        //Creates a counsellor account
        Button btnCreateAccountCounsellor = createAccountDialog.findViewById(R.id.btnCreateCounsellor);
        btnCreateAccountCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counsellorRegPage = new Intent(getApplicationContext(), registerCounsellor.class);
                startActivity(counsellorRegPage);
                //createAccountDialog.dismiss();
            }
        });

        Button btnCreateAccountCancel = createAccountDialog.findViewById(R.id.btnCancel);
        btnCreateAccountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountDialog.dismiss();
            }
        });

        //Initializes the create account dialog popup
        TextView stringToSignUpPage = findViewById(R.id.textViewToSignUp);
        stringToSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountDialog.show();
            }
        });
    }

}