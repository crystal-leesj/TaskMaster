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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.amplify.generated.graphql.ListTasksQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

import javax.annotation.Nonnull;

import type.CreateTaskInput;

public class MainActivity extends AppCompatActivity {

    String TAG = "crystal.main";
    static List<Task> listOfTask = new ArrayList<>();
    MyDatabase myDb;

    private AWSAppSyncClient mAWSAppSyncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "we are onCreate");
        

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "add_task").allowMainThreadQueries().build();

        this.listOfTask = new ArrayList<Task>();
//        this.listOfTask = myDb.taskDao().getAll();
//        for(Task item : listOfTask){
//            Log.i(TAG, item.title);
//        }

        runTaskQuery();

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

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));


        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        Log.i(TAG + " INIT", "onResult: " + userStateDetails.getUserState());
                        if (userStateDetails.getUserState().equals(UserState.SIGNED_OUT)) {
                            // 'this' refers the the current active activity
                            AWSMobileClient.getInstance().showSignIn(MainActivity.this, new Callback<UserStateDetails>() {
                                @Override
                                public void onResult(UserStateDetails result) {
                                    Log.d(TAG, "onResult: " + result.getUserState());
                                    if (result.getUserState().equals(UserState.SIGNED_IN)) {
                                        uploadWithTransferUtility();
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, "onError: ", e);
                                }
                            });
                        }
//                        uploadWithTransferUtility();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG + " INIT", "Initialization error.", e);
                    }
                }
        );
    }


    public void runTaskQuery(){
        mAWSAppSyncClient.query(ListTasksQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(tasksCallback);
    }

    private GraphQLCall.Callback<ListTasksQuery.Data> tasksCallback = new GraphQLCall.Callback<ListTasksQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListTasksQuery.Data> response) {
            Log.i(TAG + "Results", response.data().listTasks().items().toString());
            if(listOfTask.size() == 0 || response.data().listTasks().items().size() != listOfTask.size()) {

                listOfTask.clear();

                for (ListTasksQuery.Item item : response.data().listTasks().items()) {
                    Task task = new Task(item.title(), item.body(), item.state());
                    listOfTask.add(task);
                }
            }
            Handler handlerForMainThread = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message inputMessage){
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            };

            handlerForMainThread.obtainMessage().sendToTarget();

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG + "ERROR", e.toString());
        }
    };




    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "resumed");

        String username = AWSMobileClient.getInstance().getUsername();
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

    public void uploadWithTransferUtility() {
        Log.d(TAG, "uploadWithTransferUtility: ");
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                        .build();

        File file = new File(getApplicationContext().getFilesDir(), "sample1.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append("Howdy World!");
            writer.close();
        }
        catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }

        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/sample.txt",
                        new File(getApplicationContext().getFilesDir(),"sample.txt"));

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d(TAG, "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d(TAG, "Bytes Transferred: " + uploadObserver.getBytesTransferred());
        Log.d(TAG, "Bytes Total: " + uploadObserver.getBytesTotal());
    }

}
