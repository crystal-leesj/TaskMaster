package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AllTasksActivity extends AppCompatActivity {

    String TAG = "crystal.AllTasks";
    static List<Task> listOfTask = new ArrayList<>();
    MyDatabase myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);
        Log.d(TAG, "we are onCreate");

        TextView allTasksTextView = findViewById(R.id.allTasksTextView);
        allTasksTextView.setTextColor(getResources().getColor(R.color.navy));
        allTasksTextView.setBackgroundColor(getResources().getColor(R.color.yellow));

        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "add_task").allowMainThreadQueries().build();

        this.listOfTask = myDb.taskDao().getAll();

        RecyclerView recyclerView = findViewById(R.id.allTaskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MytaskRecyclerViewAdapter(this.listOfTask, null, this));


    }
}
