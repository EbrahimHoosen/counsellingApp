package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class CounsellorChatListUI extends AppCompatActivity {
    OkHttpClient client;
    TextView textView, settings;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_counsellor_chat_list_ui);

        Button buttonGet = findViewById(R.id.button2);
        client = new OkHttpClient();
        //textView = findViewById(R.id.textData);
        settings = findViewById(R.id.settingsTextView);
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String email = sharedPref.getString("EmailAddress", null);//shared preference is how i keep track of the stored email and can tell which user is logged in

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
                .url("https://lamp.ms.wits.ac.za/home/s2663134/chatListCounsellor.php?EmailAddress=" + EmailAddress )
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
        System.out.println("Processing JSON");
        layout = (LinearLayout) findViewById(R.id.listChats);
        try {
            JSONArray all = new JSONArray(json);
            StringBuilder test = new StringBuilder();
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String username = item.getString("Username");
                test.append(username);
                System.out.println("Processed JSON: " + test);
                addChatToList(layout, test, i,username);
                test.replace(0, test.length(), "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void addChatToList(LinearLayout layout, StringBuilder str, int position, String username) {
        System.out.println("Adding Chat to ChatList");
        TextView chat = new TextView(this);
        chat.setText(str);
        chat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        chat.setPadding(7,7,7,7);
        if (position % 2 == 0) {//whenever we have an alternating entry, change the background of it
            chat.setBackgroundColor(Color.parseColor("#F9F9EB"));
        } else {
            chat.setBackgroundColor(Color.parseColor("#F3B35E"));
        }
    chat.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent toChatUI = new Intent(getApplicationContext(), counsellorChatUI.class);
            toChatUI.putExtra("Username", username);//this will open me to the chat with the username i click on
            startActivity(toChatUI);
        }
    });
        int dpTop = 30;
        int dpSide = 12;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int marginTopInPx = dpToPx(dpTop);
        int marginSideInPx = dpToPx(dpSide);
        layoutParams.setMargins(marginSideInPx, marginTopInPx, marginSideInPx, 0);
        chat.setLayoutParams(layoutParams);
        layout.addView(chat);
    }
}