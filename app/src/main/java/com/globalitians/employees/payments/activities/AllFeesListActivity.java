package com.globalitians.employees.payments.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.globalitians.employees.R;


public class AllFeesListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_fees_list);
        setTitle("Payments");
    }
}
