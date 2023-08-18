package com.globalitians.employees.employee.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.globalitians.employees.R;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;

public class EmployeeProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(EmployeeProfileActivity.this);
        setContentView(R.layout.activity_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
