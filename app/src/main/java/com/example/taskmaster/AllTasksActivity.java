package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AllTasksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

//        Button goToHomeButton = (Button) findViewById(R.id.goBackHomeButton);
//        goToHomeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent goToMain =  new Intent(AllTasksActivity.this, MainActivity.class);
//                AllTasksActivity.this.startActivity(goToMain);
//            }
//        });
    }
}
