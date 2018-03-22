package com.app.food.salvage;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class charityDashboard extends AppCompatActivity {

    TextView email,notify;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_dashboard);

        email = (TextView) findViewById(R.id.tvProfileName);
        notify = (TextView) findViewById(R.id.tvNotify);

        email.setText("Charity Member");

    }
}
