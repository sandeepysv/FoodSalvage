package com.app.food.salvage;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Dashboard extends AppCompatActivity {

    TextView email;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        email = (TextView) findViewById(R.id.tvProfileName);

        email.setText("Logged in Successfully!");
    }
}
