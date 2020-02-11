package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToAddATaskButton = (Button) findViewById(R.id.addTaskButton);
        goToAddATaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddATask =  new Intent(MainActivity.this, AddATaskActivity.class);
                MainActivity.this.startActivity(goToAddATask);
            }
        });

        Button goToAllTasksTaskButton = (Button) findViewById(R.id.allTasksButton);
        goToAllTasksTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAllTasks =  new Intent(MainActivity.this, AllTasksActivity.class);
                MainActivity.this.startActivity(goToAllTasks);
            }
        });
    }

}
