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

    private String email, password, type, result;
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
                    JSONObject item = all.getJSONObject(0);
                    jsonEmail = item.getString("EmailAddress");
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
}
