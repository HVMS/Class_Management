package com.globalitians.employees.other;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.globalitians.employees.R;

public class TempBottomSheetActivity extends AppCompatActivity {
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_bottom_sheet);

        //bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
    }
}
