package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String taskTitle = sharedPrefs.getString("taskTitle", "default title");
//
//        TextView taskTitleTextView = findViewById(R.id.taskTitle);
//        taskTitleTextView.setText(taskTitle);
        String getTask1 = getIntent().getStringExtra("mTitleView");

        TextView taskText1 = findViewById(R.id.taskTitle);
        taskText1.setText(getTask1);
    }
}
