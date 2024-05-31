package com.example.myapplication;

import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.Looper;

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

public class Login {

    private String username, email, password, type, result, imageID, firstName, lastName, qualification;
    private int userID, counsellorID;
    Login(String inEmail, String inPassword, String inType) {
        email = inEmail;
        password = inPassword;
        type = inType;
    }

    public void login(LoginCallback callback) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://lamp.ms.wits.ac.za/home/s2663134/login.php").newBuilder();
        urlBuilder.addQueryParameter("email", email);
        urlBuilder.addQueryParameter("password", password);
        urlBuilder.addQueryParameter("table", type);
        String url = urlBuilder.build().toString();

        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                result = "Network Error";
                runOnUiThread(() -> callback.onResult(result));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    JSONArray all = new JSONArray(responseData);
                    String jsonEmail = "";
                    for (int i = 0; i < all.length(); i++) {
                        JSONObject item = all.getJSONObject(i);
                        jsonEmail = item.getString("EmailAddress");
                        if (type.equals("Users")) {
                            username = item.getString("Username");
                            imageID = item.getString("ImageID");
                            userID = item.getInt("UserID");
                        } else if (type.equals("Counsellors")) {
                            firstName = item.getString("FirstName");
                            lastName = item.getString("LastName");
                            counsellorID = item.getInt("CounsellorID");
                            qualification = item.getString("Qualification");
                        }
                    }
                    if (email.equals(jsonEmail)) {
                        result = "Login Successful";
                    } else {
                        result = "Email or Password Incorrect";
                    }
                    runOnUiThread(() -> callback.onResult(result));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }

    public interface LoginCallback {
        void onResult(String result);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getImageID() {
        return imageID;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getUserID() {
        return userID;
    }

    public int getCounsellorID() {
        return counsellorID;
    }

    public String getLastName() {
        return lastName;
    }

    public String getQualification() {
        return qualification;
    }
}
