package com.mirea.kt.ribo.agapov;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_VARIANT = "extra_variant";
    public static final String EXTRA_TASK = "extra_task";

    private static final String TAG = "TaskActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Log.d(TAG, "onCreate");

        TextView textTitle = findViewById(R.id.textTaskTitle);
        TextView textVariant = findViewById(R.id.textTaskVariant);
        TextView textTask = findViewById(R.id.textTaskText);
        Button buttonOpen = findViewById(R.id.buttonOpenDictionary);

        textTitle.setText(getIntent().getStringExtra(EXTRA_TITLE));
        textVariant.setText(getString(R.string.task_variant, getIntent().getIntExtra(EXTRA_VARIANT, 0)));
        textTask.setText(getIntent().getStringExtra(EXTRA_TASK));

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_DATA, getIntent().getStringExtra(MainActivity.EXTRA_DATA));
                startActivity(intent);
            }
        });
    }
}
