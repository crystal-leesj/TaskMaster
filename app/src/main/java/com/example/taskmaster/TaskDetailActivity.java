package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class TaskDetailActivity extends AppCompatActivity {

    MyDatabase myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
    }

    @Override
    protected void onResume() {
        super.onResume();

        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "add_task").allowMainThreadQueries().build();


//        Long id = getIntent().getLongExtra("mTitleView", 0);
//        Task oneTask = myDb.taskDao().getOne(id);

        String title = getIntent().getStringExtra("title");
        TextView taskTitleTextView = findViewById(R.id.taskTitle);
        taskTitleTextView.setText(title);

        String body = getIntent().getStringExtra("body");
        TextView taskDescriptionTextView = findViewById(R.id.taskDescription);
        taskDescriptionTextView.setText(body);

        String state = getIntent().getStringExtra("state");
        TextView taskStateTextView = findViewById(R.id.taskState);
        taskStateTextView.setText(state);

        String image = getIntent().getStringExtra("image");
        ImageView imageView = findViewById(R.id.taskImage);

        String url = "https://taskmasterdd5d91692b1a4f63b7f25bcf159d85a1225754-todolist.s3-us-west-2.amazonaws.com/" + image;

        Picasso.get().load(url).resize(50,50).centerCrop().into(imageView);

//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String taskTitle = sharedPrefs.getString("taskTitle", "default title");
//
//        TextView taskTitleTextView = findViewById(R.id.taskTitle);
//        taskTitleTextView.setText(taskTitle);

//        String getTask1 = getIntent().getStringExtra("mTitleView");
//
//        TextView taskText1 = findViewById(R.id.taskTitle);
//        taskText1.setText(getTask1);
    }
}
