package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class MainActivity extends AppCompatActivity {

    String TAG = "crystal.main";
    static List<Task> listOfTask = new ArrayList<>();
    MyDatabase myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "we are onCreate");

        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "add_task").allowMainThreadQueries().build();


        this.listOfTask = myDb.taskDao().getAll();
        for(Task item : listOfTask){
            Log.i(TAG, item.title);
        }
//        Task a = new Task("Code Challenge", "Quick sort", "complete");
//        Task b = new Task("Hot yoga", "Class starts at 7:30 PM", "new");
//        Task c = new Task("Meal prep", "Buy groceries", "assigned");


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MytaskRecyclerViewAdapter(this.listOfTask, null, this));




        // Go to Add task page
        Button goToAddATaskButton = (Button) findViewById(R.id.addTaskButton);
        goToAddATaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddATask =  new Intent(MainActivity.this, AddATaskActivity.class);
                MainActivity.this.startActivity(goToAddATask);
            }
        });

        // Go to All tasks page
        Button goToAllTasksTaskButton = (Button) findViewById(R.id.allTasksButton);
        goToAllTasksTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAllTasks =  new Intent(MainActivity.this, AllTasksActivity.class);
                MainActivity.this.startActivity(goToAllTasks);
            }
        });

        // Go to Setting page
        Button goToSettingButton = (Button) findViewById(R.id.settingButton);
        goToSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSetting = new Intent(MainActivity.this, SettingActivity.class);
                MainActivity.this.startActivity(goToSetting);
            }
        });


//        final Button goToTask1Button = (Button) findViewById(R.id.task1);
//        goToTask1Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String taskTitle = goToTask1Button.getText().toString();
//                Log.i(TAG, taskTitle);
//                saveTitleToSharePrefer(taskTitle);
//            }
//        });

//        final Button goToTask2Button = (Button) findViewById(R.id.task2);
//        goToTask2Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String taskTitle = goToTask2Button.getText().toString();
//                Log.i(TAG, taskTitle);
//                saveTitleToSharePrefer(taskTitle);
//            }
//        });

//        final Button goToTask3Button = (Button) findViewById(R.id.task3);
//        goToTask3Button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String taskTitle = goToTask3Button.getText().toString();
//                Log.i(TAG, taskTitle);
//                saveTitleToSharePrefer(taskTitle);
//            }
//        });
    }

    private void saveTitleToSharePrefer(String title) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("taskTitle", title);
        editor.commit();
        Intent goToTaskDetail = new Intent(MainActivity.this, TaskDetailActivity.class);
        MainActivity.this.startActivity(goToTaskDetail);
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "resumed");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPrefs.getString("username", "default username");

        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(username + "'s task");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "destroyed");
    }

}
