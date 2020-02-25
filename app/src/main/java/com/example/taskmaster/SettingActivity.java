package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;

public class SettingActivity extends AppCompatActivity {
    String TAG = "crystal.SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button saveUsernameButton = (Button) findViewById(R.id.saveUsernameButton);
        saveUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputUsername = findViewById(R.id.usernameEditText);
                String username = inputUsername.getText().toString();
                Log.d("-----username :", username);
                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = p.edit();
                editor.putString("username", username);
                editor.apply();
                Intent goToSetting = new Intent(SettingActivity.this, MainActivity.class);
                SettingActivity.this.startActivity(goToSetting);
            }
        });

        Button signOutButton = (Button) findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AWSMobileClient.getInstance().signOut(SignOutOptions.builder().invalidateTokens(true).build(), new Callback<Void>() {
                    @Override
                    public void onResult(Void result) {
                        Log.d(TAG, "onResult: signed out!");
                        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

                                    @Override
                                    public void onResult(UserStateDetails userStateDetails) {
                                        Log.i(TAG + " INIT", "onResult: " + userStateDetails.getUserState());
                                        if (userStateDetails.getUserState().equals(UserState.SIGNED_OUT)) {
                                            // 'this' refers the the current active activity
                                            AWSMobileClient.getInstance().showSignIn(SettingActivity.this, new Callback<UserStateDetails>() {
                                                @Override
                                                public void onResult(UserStateDetails result) {
                                                    Log.d(TAG, "onResult: " + result.getUserState());
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Log.e(TAG, "onError: ", e);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e(TAG + " INIT", "Initialization error.", e);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError: ", e);
                    }
                });
            }
        });
    }





}
