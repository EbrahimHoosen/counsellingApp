package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.navigation.NavigationBarView;

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

    BottomNavigationView navView;
    OkHttpClient client;
    TextView textView, settings;
    LinearLayout chats, profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_counsellor_chat_list_ui);

        navView = findViewById(R.id.bottom_navigation);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.menu_chats) {
                    showChats();
                }
                if (item.getItemId()==R.id.menu_profile) {
                    showProfile();
                }
                return true;
            }
        });

        chats = findViewById(R.id.listChats);
        profile = findViewById(R.id.counsellorProfile);

        profile.setVisibility(View.GONE);

        //Button buttonGet = findViewById(R.id.button2);
        client = new OkHttpClient();
        //textView = findViewById(R.id.textData);
        //settings = findViewById(R.id.settingsTextView);
        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String email = sharedPref.getString("EmailAddress", null); //shared preference is how i keep track of the stored email and can tell which user is logged in


        getOther(email);
    }

    private void showChats() {
        chats.setVisibility(View.VISIBLE);
        profile.setVisibility(View.GONE);
    }

    private void showProfile() {
        chats.setVisibility(View.GONE);
        profile.setVisibility(View.VISIBLE);
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
        try {
            JSONArray all = new JSONArray(json);
            StringBuilder test = new StringBuilder();
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String username = item.getString("Username");
                test.append(username);
                System.out.println("Processed JSON: " + test);
                addChatToList(chats, test, i,username);
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
        chat.setTextColor(Color.parseColor("#000000"));
        chat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        chat.setPadding(dpToPx(10),dpToPx(10),dpToPx(10),dpToPx(10));
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
        MaterialDivider divider = new MaterialDivider(this);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(2)); //thickness of divider
        //dividerParams.setMargins(0,marginTopInPx,0,0);
        divider.setLayoutParams(dividerParams);
        layoutParams.setMargins(marginSideInPx, marginTopInPx, marginSideInPx, 0);
        //chat.setLayoutParams(layoutParams);
        layout.addView(chat);
        layout.addView(divider);
    }
}