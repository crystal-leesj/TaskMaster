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

public class SettingActivity extends AppCompatActivity {

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
    }




}
