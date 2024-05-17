package com.example.myapplication;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

    Dialog createAccountDialog;
    Button btnCreateAccountCancel, btnCreateAccountUser, btnCreateAccountCounsellor;

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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.isChecked()) {
                    login("Users");
                } else if (counsellor.isChecked()) {
                    login("Counsellors");
                } else {
                    Toast.makeText(MainActivity.this, "User Type Must Be Selected", LENGTH_SHORT).show();
                }

            }
        });

        createAccountDialog = new Dialog(MainActivity.this);
        createAccountDialog.setContentView(R.layout.create_account_dialog);
        createAccountDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createAccountDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.create_account_bg));
        createAccountDialog.setCancelable(false);

        btnCreateAccountUser = createAccountDialog.findViewById(R.id.btnCreateUser);
        btnCreateAccountUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Creating User Account", LENGTH_SHORT).show();
                Intent instRegisterUser = new Intent(getApplicationContext(), registerUser.class);
                startActivity(instRegisterUser);
                //createAccountDialog.dismiss();
            }
        });
        btnCreateAccountCounsellor = createAccountDialog.findViewById(R.id.btnCreateCounsellor);
        btnCreateAccountCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent instRegisterCounsellor = new Intent(getApplicationContext(), registerCounsellor.class);
                startActivity(instRegisterCounsellor);
                Toast.makeText(MainActivity.this, "Creating Cousellor Account", LENGTH_SHORT).show();
                //createAccountDialog.dismiss();
            }
        });

        btnCreateAccountCancel = createAccountDialog.findViewById(R.id.btnCancel);
        btnCreateAccountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountDialog.dismiss();
            }
        });

        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountDialog.show();
            }
        });
    }

    public void login(String type) {

        OkHttpClient client = new OkHttpClient();

        String email, password, table = type;
        EditText emailTxt = (EditText) findViewById(R.id.txtEmail);
        EditText passwordTxt = (EditText) findViewById(R.id.txtPassword);

        email = emailTxt.getText().toString();
        password = passwordTxt.getText().toString();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2663134/login.php").newBuilder();
        urlBuilder.addQueryParameter("email", email);
        urlBuilder.addQueryParameter("password", password);
        urlBuilder.addQueryParameter("table", type);
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
                            if (!email.equals("") || !password.equals("")) {
                                if (email.equals(jsonEmail) && password.equals(jsonPassword)) {
                                    Toast.makeText(MainActivity.this, "Login Successful", LENGTH_SHORT).show();
                                    //System.out.println("Login Successful");
                                } else {
                                    Toast.makeText(MainActivity.this, "Email or Password Incorrect", LENGTH_SHORT).show();
                                }
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