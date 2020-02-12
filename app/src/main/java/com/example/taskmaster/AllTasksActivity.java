package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AllTasksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        TextView allTasksTextView = findViewById(R.id.allTasksTextView);
        allTasksTextView.setTextColor(getResources().getColor(R.color.navy));
        allTasksTextView.setBackgroundColor(getResources().getColor(R.color.yellow));
    }
}
