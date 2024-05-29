package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class UserChatListUI extends AppCompatActivity {
    OkHttpClient client;
    TextView textView ,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_chat_list_ui);
        Button buttonGet = findViewById(R.id.button2);
            client = new OkHttpClient();
            textView = findViewById(R.id.textData);
            settings = findViewById(R.id.settingsTextView);
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String email = sharedPref.getString("EmailAddress", null);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSettings = new Intent(getApplicationContext(),settings.class);
                startActivity(toSettings);
            }
        });

        buttonGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOther(email);
            }
        });
    }



    public void getOther(String EmailAddress) {// this would usually have the parameters in the url

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2663134/chatListUser.php?EmailAddress=" + EmailAddress )
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
                            textView.setText(jsonResponse);
                            processJSON(jsonResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
    public void processJSON(String json){
        try {
            JSONArray all = new JSONArray(json);
            StringBuilder test = new StringBuilder();
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String firstName = item.getString("FirstName");
                test.append(firstName).append(" ").append("\n");
                textView.setText(test);
                System.out.println(firstName);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}