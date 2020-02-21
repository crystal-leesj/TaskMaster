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

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.CreateTaskInput;

public class AddATaskActivity extends AppCompatActivity {
    static String TAG = "crystal.AddATaskActivity";
    MyDatabase myDb;
    private AWSAppSyncClient mAWSAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_task);
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

//        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "add_task").allowMainThreadQueries().build();

        Button submitATaskButton = findViewById(R.id.addTaskButton);
        submitATaskButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                EditText inputTitle = findViewById(R.id.taskTitleEditText);
                String inputStringTitle = inputTitle.getText().toString();

                EditText inputDescription = findViewById(R.id.taskDescriptionEditText);
                String inputStringDescription = inputDescription.getText().toString();

                EditText inputState = findViewById(R.id.taskStateEditText);
                String inputStringState = inputState.getText().toString();

//                Log.i(TAG, "before------->" + MainActivity.listOfTask.size());
//
//                Task newTask = new Task(inputStringTitle, inputStringDescription, inputStringState);
//                MainActivity.listOfTask.add(0, newTask);
//                myDb.taskDao().save(newTask);
//
//                Log.i(TAG, "after------->" + MainActivity.listOfTask.size());


                runAddTaskMutation(inputStringTitle, inputStringDescription, inputStringState);


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


    public void runAddTaskMutation(String title, String body, String state){
        CreateTaskInput createTodoInput = CreateTaskInput.builder().
                title(title).
                body(body).
                state(state).
                build();

        mAWSAppSyncClient.mutate(CreateTaskMutation.builder().input(createTodoInput).build())
                .enqueue(addMutationCallback);
    }

    private GraphQLCall.Callback<CreateTaskMutation.Data> addMutationCallback = new GraphQLCall.Callback<CreateTaskMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateTaskMutation.Data> response) {
            Log.i(TAG, "Added a TASK");
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("Error", e.toString());
        }
    };



}
