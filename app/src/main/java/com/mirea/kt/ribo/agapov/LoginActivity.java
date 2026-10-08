package com.mirea.kt.ribo.agapov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editLogin;
    private EditText editPassword;
    private EditText editGroup;
    private TextView textError;
    private Button buttonLogin;
    private ProgressBar progressLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate");

        editLogin = findViewById(R.id.editLogin);
        editPassword = findViewById(R.id.editPassword);
        editGroup = findViewById(R.id.editGroup);
        textError = findViewById(R.id.textError);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressLogin = findViewById(R.id.progressLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });
    }

    private void tryLogin() {
        final String login = editLogin.getText().toString().trim();
        final String password = editPassword.getText().toString();
        final String group = editGroup.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty() || group.isEmpty()) {
            showError(getString(R.string.error_empty_fields));
            return;
        }

        textError.setVisibility(View.GONE);
        progressLogin.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AuthClient authClient = new AuthClient();
                final String response = authClient.sendLogin(login, password, group);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleResponse(response);
                    }
                });
            }
        }).start();
    }

    private void handleResponse(String response) {
        progressLogin.setVisibility(View.GONE);
        buttonLogin.setEnabled(true);

        if (response == null) {
            showError(getString(R.string.error_no_network));
            return;
        }

        try {
            JSONObject json = new JSONObject(response);
            int resultCode = json.getInt("result_code");
            if (resultCode == 1) {
                Log.i(TAG, "Вход выполнен");
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra(TaskActivity.EXTRA_TITLE, json.getString("title"));
                intent.putExtra(TaskActivity.EXTRA_VARIANT, json.getInt("variant"));
                intent.putExtra(TaskActivity.EXTRA_TASK, json.getString("task"));
                intent.putExtra(MainActivity.EXTRA_DATA, json.getJSONArray("data").toString());
                startActivity(intent);
            } else {
                Log.w(TAG, "Неверные данные для входа");
                showError(getString(R.string.error_wrong_credentials));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Ошибка разбора ответа", e);
            showError(getString(R.string.error_bad_response));
        }
    }

    private void showError(String message) {
        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }
}
