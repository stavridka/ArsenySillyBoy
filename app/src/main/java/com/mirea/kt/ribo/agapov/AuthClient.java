package com.mirea.kt.ribo.agapov;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AuthClient {

    private static final String TAG = "AuthClient";
    private static final String LOGIN_URL = "https://android-for-students.ru/coursework/login.php";

    public String sendLogin(String login, String password, String group) {
        try {
            String body = "lgn=" + URLEncoder.encode(login, "UTF-8")
                    + "&pwd=" + URLEncoder.encode(password, "UTF-8")
                    + "&g=" + URLEncoder.encode(group, "UTF-8");

            URL url = new URL(LOGIN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body.getBytes("UTF-8"));
            outputStream.close();

            Log.d(TAG, "Код ответа: " + connection.getResponseCode());

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                response.append(line);
                line = reader.readLine();
            }
            reader.close();
            connection.disconnect();
            return response.toString();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка запроса", e);
            return null;
        }
    }
}
