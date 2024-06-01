package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class userChatUI extends AppCompatActivity {
    OkHttpClient client;
    TextView usernameTextView;
    ImageButton btnSettings;
    Dialog userSettingsDialog;

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText editTextMessage;
    private Button sendButton;
    private int userID;
    private int counsellorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat_ui);

        usernameTextView = findViewById(R.id.username);
        btnSettings = findViewById(R.id.user_settings);
        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.sendButton);

        client = new OkHttpClient();

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        userID = sharedPref.getInt("UserID", 0); // retrieve the stored UserID
        counsellorID = sharedPref.getInt("CounsellorID", 0); // retrieve the stored CounsellorID
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

        btnCloseSettings.setOnClickListener(v -> userSettingsDialog.dismiss());

        btnLogout.setOnClickListener(v -> {
            Intent loginPage = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(loginPage);
        });

        btnSettings.setOnClickListener(v -> userSettingsDialog.show());

        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadExampleMessages();

        fetchMessages(MainActivity.currentUser.getUserID());

        sendButton.setOnClickListener(v -> sendMessage());
    }

    public void getOther(int id) {
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2663134/userChat.php?UserID=" + id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                runOnUiThread(() -> {
                    try {
                        String jsonResponse = response.body().string();
                        JSONArray all = new JSONArray(jsonResponse);
                        StringBuilder name = new StringBuilder();

                        for (int i = 0; i < all.length(); i++) {
                            JSONObject item = all.getJSONObject(i);
                            name.append(item.getString("FirstName")).append(" ").append(item.getString("LastName"));
                        }
                        usernameTextView.setText(name);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void loadExampleMessages() {
        messageList.add(new Message(1, "1", "2024-05-30 14:00:00", "Hello!", Message.SENT));
        messageList.add(new Message(2, "2", "2024-05-30 14:01:00", "Hi there!", Message.RECEIVED));
        messageList.add(new Message(3, "1", "2024-05-30 14:02:00", "How are you?", Message.SENT));
        messageList.add(new Message(4, "2", "2024-05-30 14:03:00", "I'm doing well, thank you!", Message.RECEIVED));

        messageAdapter.setMessages(messageList);
    }

    private void fetchMessages(int userID) {
        OkHttpClient client = new OkHttpClient();

        // Build the URL with the userID parameter
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2663134/userDisplayMessages.php").newBuilder();
        urlBuilder.addQueryParameter("userID", String.valueOf(userID));
        String url = urlBuilder.build().toString();

        // Create a request object with the URL
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(userChatUI.this, "Failed to fetch messages", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the JSON response
                    String responseData = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        List<Message> messages = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int messageId = jsonObject.getInt("MessageID");
                            String senderID = jsonObject.getString("SenderID");
                            String timestamp = jsonObject.getString("Timestamp");
                            String content = jsonObject.getString("content");

                            // Determine the type of message based on senderID
                            int type = senderID.equals(String.valueOf(userID)) ? Message.SENT : Message.RECEIVED;

                            // Create a new Message object
                            Message message = new Message(messageId, senderID, timestamp, content, type);
                            messages.add(message);
                        }

                        // Update the UI with the fetched messages
                        runOnUiThread(() -> {
                            // Clear existing messages
                            messageList.clear();

                            // Add the fetched messages to the list
                            messageList.addAll(messages);

                            // Notify the adapter of the data change
                            messageAdapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void sendMessage() {
        String senderID = String.valueOf(userID);
        String content = editTextMessage.getText().toString();

        OkHttpClient client = new OkHttpClient();

        // Create form body with sender ID, receiver ID (counsellor ID), and message content
        RequestBody requestBody = new FormBody.Builder()
                .add("sender_id", senderID)
                .add("counsellor_id", String.valueOf(counsellorID))
                .add("message_content", content)
                .build();

        // Create POST request with the PHP script URL and form body
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2663134/sendMessages.php")
                .post(requestBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(userChatUI.this, "Failed to send message", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the response if needed
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            // Message sent successfully
                            runOnUiThread(() -> {
                                Toast.makeText(userChatUI.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                // Clear input field
                                editTextMessage.setText("");

                                // Add the new message to the message list and update the UI
                                Message newMessage = new Message(messageList.size() + 1, senderID, getCurrentTimestamp(), content, Message.SENT);
                                messageList.add(newMessage);
                                messageAdapter.notifyItemInserted(messageList.size() - 1);
                            });
                        } else {
                            // Failed to send message
                            runOnUiThread(() -> Toast.makeText(userChatUI.this, "Failed to send message", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // Helper method to get current timestamp
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}