package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddATaskActivity extends AppCompatActivity {
    static String TAG = "crystal.AddATaskActivity";
    MyDatabase myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_task);
        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "add_task").allowMainThreadQueries().build();

        Button submitATaskButton = findViewById(R.id.addTaskButton);
        submitATaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                EditText inputTitle = findViewById(R.id.taskTitleEditText);
                String inputStringTitle = inputTitle.getText().toString();

                EditText inputDescription = findViewById(R.id.taskDescriptionEditText);
                String inputStringDescription = inputDescription.getText().toString();

                Log.i(TAG, "before------->" + MainActivity.listOfTask.size());

                Task newTask = new Task(inputStringTitle, inputStringDescription, "status");
                MainActivity.listOfTask.add(0, newTask);
                myDb.taskDao().save(newTask);

                Log.i(TAG, "after------->" + MainActivity.listOfTask.size());


//                Context context = getApplicationContext();
//                CharSequence submitText = inputStringTitle + " task is submitted!";
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, submitText, duration);
//                toast.show();
//                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);

                Intent goToMain =  new Intent(AddATaskActivity.this, MainActivity.class);
                AddATaskActivity.this.startActivity(goToMain);
            }
        });

    }


}
