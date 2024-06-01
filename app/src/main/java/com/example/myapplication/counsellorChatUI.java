package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

public class counsellorChatUI extends AppCompatActivity {
    OkHttpClient client;
    TextView usernameTextView;

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText editTextMessage;
    private Button sendButton;
    private int chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counsellor_chat_ui);

        usernameTextView = findViewById(R.id.username);
        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.sendButton);

        client = new OkHttpClient();

        SharedPreferences sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        int counsellorID = sharedPref.getInt("CounsellorID", 0); // Retrieve the stored CounsellorID
        chatID = sharedPref.getInt("ChatID", 0); // Retrieve the stored ChatID
        System.out.println("CounsellorID: " + counsellorID);
        getOther(counsellorID);

        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

// Fetch messages from the server
        fetchMessages(MainActivity.currentCounsellor.getCounsellorID());
        sendButton.setOnClickListener(v -> sendMessage(MainActivity.currentCounsellor.getCounsellorID()));;
        //editTextMessage.setText(counsellorID);
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

    private void fetchMessages(int counsellorID) {
        OkHttpClient client = new OkHttpClient();

        // Build the URL with the counsellorID parameter
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2663134/counsellorDisplayMessages.php").newBuilder();
        urlBuilder.addQueryParameter("counsellorID", String.valueOf(counsellorID));
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
                runOnUiThread(() -> Toast.makeText(counsellorChatUI.this, "Failed to fetch messages", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the JSON response
                    String responseData = response.body().string();
                    Log.d("CounsellorChatUI", "Response data: " + responseData);
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
                            int type = senderID.equals(String.valueOf(counsellorID)) ? Message.SENT : Message.RECEIVED;

                            // Create a new Message object
                            Message message = new Message(messageId, senderID, timestamp, content, type);
                            messages.add(message);
                        }

                        // Update the UI with the fetched messages
                        runOnUiThread(() -> {
                            messageAdapter.setMessages(messages); // Pass the messageList to the adapter
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("CounsellorChatUI", "JSON parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e("CounsellorChatUI", "Response not successful: " + response.code());
                }
            }
        });
    }



    private void sendMessage(int cID) {
        String senderID = String.valueOf(cID);
        String content = editTextMessage.getText().toString();

        OkHttpClient client = new OkHttpClient();

        // Create form body with sender ID, receiver ID (user ID), and message content
        RequestBody requestBody = new FormBody.Builder()
                .add("counsellorID", senderID)
                .add("messageContent", content)
                .build();

        // Create POST request with the PHP script URL and form body
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2663134/counsellorSend.php")
                .post(requestBody)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.e("SendMessage", "Failed to send message", e);
                runOnUiThread(() -> Toast.makeText(counsellorChatUI.this, "Failed to send message", Toast.LENGTH_SHORT).show());
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
                            Log.d("SendMessage", "Message sent successfully");
                            runOnUiThread(() -> {
                                Toast.makeText(counsellorChatUI.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                                // Clear input field
                                editTextMessage.setText("");

                                // Add the new message to the message list and update the UI
                                Message newMessage = new Message(messageList.size() + 1, senderID, getCurrentTimestamp(), content, Message.SENT);
                                messageList.add(newMessage);
                                messageAdapter.notifyItemInserted(messageList.size() - 1);
                            });
                        } else {
                            // Failed to send message
                            Log.e("SendMessage", "Failed to send message");
                            runOnUiThread(() -> Toast.makeText(counsellorChatUI.this, "Failed to send message", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("SendMessage", "Failed to parse JSON response", e);
                    }
                } else {
                    Log.e("SendMessage", "Failed to send message: " + response.code());
                    runOnUiThread(() -> Toast.makeText(counsellorChatUI.this, "Failed to send message: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}