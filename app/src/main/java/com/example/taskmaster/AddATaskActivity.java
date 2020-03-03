package com.example.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.amazonaws.amplify.generated.graphql.CreateTaskMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

import javax.annotation.Nonnull;

import type.CreateTaskInput;

public class AddATaskActivity extends AppCompatActivity {
    static String TAG = "crystal.AddATaskActivity";
    MyDatabase myDb;
    private AWSAppSyncClient mAWSAppSyncClient;
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_task);
        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

//        myDb = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "add_task").allowMainThreadQueries().build();

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        // Initialize the AWSMobileClient if not initialized
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                Log.i(TAG, "AWSMobileClient initialized. User State is " + userStateDetails.getUserState());

                Button uploadImageButton = findViewById(R.id.uploadImageButton);
                uploadImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        pickImage(view);

                     }

                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Initialization error.", e);
            }
        });


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


                runAddTaskMutation(inputStringTitle, inputStringDescription, inputStringState, "public/" + uuid);


//                Context context = getApplicationContext();
//                CharSequence submitText = inputStringTitle + " task is submitted!";
//                int duration = Toast.LENGTH_SHORT;
//                Toast toast = Toast.makeText(context, submitText, duration);
//                toast.show();
//                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);

//                Intent goToMain =  new Intent(AddATaskActivity.this, MainActivity.class);
//                AddATaskActivity.this.startActivity(goToMain);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 777 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Log.d(TAG, "------------selectedImage" + selectedImage);


//            imageView.setImageURI(selectedImage);

            uploadWithTransferUtility(selectedImage);

        }
    }

    public void pickImage(View v) {
        Log.d(TAG, "Image button clicked!");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.setType("image/*");
            startActivityForResult(i, 777);
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode != 0) {
            return;
        }
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent grabFileIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            grabFileIntent.setType("image/*");
//                grabFileIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,);
            startActivityForResult(grabFileIntent, 777);
        }
    }


    public void uploadWithTransferUtility(Uri uri) {

        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance(), Region.getRegion(Regions.DEFAULT_REGION)))
                        .build();

        uuid = UUID.randomUUID().toString();
        TransferObserver uploadObserver =
                transferUtility.upload(
                        "taskmasterdd5d91692b1a4f63b7f25bcf159d85a1225754-todolist",
                        "public/" + uuid,
                        new File(picturePath));

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


    public void runAddTaskMutation(String title, String body, String state, String uuid){
        CreateTaskInput createTodoInput = CreateTaskInput.builder().
                title(title).
                body(body).
                state(state).
                image(uuid).
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
