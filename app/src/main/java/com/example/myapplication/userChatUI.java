package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class userChatUI extends AppCompatActivity {
    OkHttpClient client;
    TextView usernameTextView;
    ImageButton btnSettings;
    Dialog userSettingsDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_chat_ui);

        usernameTextView = findViewById(R.id.username);
        btnSettings = findViewById(R.id.user_settings);

        client = new OkHttpClient();

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int userID = sharedPref.getInt("UserID", 0); //retrieve the stored UserID
        System.out.println("UserID: " + userID);
        getOther(userID);

        userSettingsDialog = new Dialog(userChatUI.this);
        userSettingsDialog.setContentView(R.layout.user_settings_dialog);
        userSettingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userSettingsDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.create_account_bg));
        userSettingsDialog.setCancelable(false);

        ImageButton btnCloseSettings = userSettingsDialog.findViewById(R.id.user_settings_close);
        ImageButton btnLogout = userSettingsDialog.findViewById(R.id.logout);

        TextView txtUsername = userSettingsDialog.findViewById(R.id.text_username);
        TextView txtEmail = userSettingsDialog.findViewById(R.id.text_email);

        String username = sharedPref.getString("Username", "");
        String email = sharedPref.getString("Email", "");

        txtUsername.setText(username);
        txtEmail.setText(email);

        btnCloseSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSettingsDialog.dismiss();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginPage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(loginPage);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSettingsDialog.show();
            }
        });

    }

    public void getOther(int id) {// this would usually have the parameters in the url

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2663134/userChat.php?UserID=" + id )
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

                            JSONArray all = new JSONArray(jsonResponse);
                            StringBuilder name = new StringBuilder();

                            for (int i = 0; i < all.length(); i++) {
                                JSONObject item = all.getJSONObject(i);
                                name.append(item.getString("FirstName")).append(" ").append(item.getString("LastName"));
                            }
                            usernameTextView.setText(name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            }
        });
    }
}