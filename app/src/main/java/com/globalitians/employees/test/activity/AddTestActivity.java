package com.globalitians.employees.test.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.activities.BatchListActivity;
import com.globalitians.employees.courses.model.ModelCourseDurationList;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomEditText;
import com.globalitians.employees.students.activities.AddStudentActivity;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Calendar;

import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ADD_NEW_TEST;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.CODE_GET_COURSE_DURATIONS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_USERNAME;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_USER_ID;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;

public class AddTestActivity extends AppCompatActivity implements View.OnClickListener, OkHttpInterface {
    private CustomEditText mEdtTestName;
    private CustomEditText mEdtTestDate;
    private CustomEditText mEdtTestTime;
    private CustomEditText mEdtTotalMarks;
    private CustomEditText mEdtPassingMarks;
    private CustomEditText mEdtBatchName;
    private CustomButton mBtnAddNewTest;

    private DatePickerDialog mDatePickerDiaogTestDate = null;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private String mStrSelectedBatchId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AddTestActivity.this);
        setContentView(R.layout.activity_add_test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        initializeDatePickers();
        mSharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private void openTimePickerToChooseTestTiming() {
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour1 = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute1 = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddTestActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub
                int hour = hourOfDay;
                int minutes = minute;
                String timeSet = "";
                if (hour > 12) {
                    hour -= 12;
                    timeSet = "PM";
                } else if (hour == 0) {
                    hour += 12;
                    timeSet = "AM";
                } else if (hour == 12){
                    timeSet = "PM";
                }else{
                    timeSet = "AM";
                }

                String min = "";
                if (minutes < 10)
                    min = "0" + minutes ;
                else
                    min = String.valueOf(minutes);

                // Append in a StringBuilder
                String aTime = new StringBuilder().append(hour).append(':')
                        .append(min ).append(" ").append(timeSet).toString();

                mEdtTestTime.setText(aTime);

            }
        }, hour1, minute1, true);
        mTimePicker.setTitle("Select Test Time");
        mTimePicker.show();
    }

    private void findViews() {
        mEdtTestName = (CustomEditText) findViewById(R.id.edt_test_name);
        mEdtTestDate = (CustomEditText) findViewById(R.id.edt_test_date);
        mEdtTestTime = (CustomEditText) findViewById(R.id.edt_test_time);
        mEdtTotalMarks = (CustomEditText) findViewById(R.id.edt_total_marks);
        mEdtPassingMarks = (CustomEditText) findViewById(R.id.edt_passing_marks);
        mEdtBatchName = (CustomEditText) findViewById(R.id.edt_batch_name);
        mBtnAddNewTest = (CustomButton) findViewById(R.id.btn_add_new_test);
        mBtnAddNewTest.setOnClickListener(this);
        mEdtTestDate.setOnClickListener(this);
        mEdtTestTime.setOnClickListener(this);
        mEdtBatchName.setOnClickListener(this);
    }

    private void initializeDatePickers() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener testDateListener;
        testDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEdtTestDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };
        //initializing date filter date picker dialog
        mDatePickerDiaogTestDate = new DatePickerDialog(
                AddTestActivity.this, testDateListener, year, month, day);
        mDatePickerDiaogTestDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_test_date:
                mDatePickerDiaogTestDate.show();
                break;
            case R.id.edt_test_time:
                openTimePickerToChooseTestTiming();
                break;
            case R.id.btn_add_new_test:
                validateTestData();
                break;
            case R.id.edt_batch_name:
                Intent intent = new Intent(AddTestActivity.this, BatchListActivity.class);
                intent.putExtra("from", "AddTestActivity");
                startActivityForResult(intent, Constants.REQUEST_BATCH_ID);
                break;
        }
    }

    private void validateTestData() {
        if (CommonUtil.isNullString("" + mEdtTestName.getText().toString().trim())) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.total_invalid_test_name), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.total_invalid_test_name), Toast.LENGTH_SHORT).show();
            mEdtTestName.requestFocus();
            return;
        } else if (CommonUtil.isNullString("" + mEdtTestDate.getText().toString().trim())) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.total_invalid_test_date), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.total_invalid_test_date), Toast.LENGTH_SHORT).show();
            mDatePickerDiaogTestDate.show();
            return;
        } else if (CommonUtil.isNullString("" + mEdtTestTime.getText().toString().trim())) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.total_invalid_test_time), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.total_invalid_test_time), Toast.LENGTH_SHORT).show();
            openTimePickerToChooseTestTiming();
            return;
        } else if (CommonUtil.isNullString("" + mEdtTotalMarks.getText().toString().trim())) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.total_invalid_total_marks), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.total_invalid_total_marks), Toast.LENGTH_SHORT).show();
            mEdtTotalMarks.requestFocus();
            return;
        } else if (Integer.parseInt("" + mEdtTotalMarks.getText().toString().trim()) == 0) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.total_marks_more_than_zero), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.total_marks_more_than_zero), Toast.LENGTH_SHORT).show();
            mEdtTotalMarks.requestFocus();
        } else if (CommonUtil.isNullString("" + mEdtPassingMarks.getText().toString().trim())) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.total_invalid_passing_marks), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.total_invalid_passing_marks), Toast.LENGTH_SHORT).show();
            mEdtPassingMarks.requestFocus();
            return;
        } else if (Integer.parseInt("" + mEdtPassingMarks.getText().toString().trim()) == 0) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.passing_marks_more_than_zero), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.passing_marks_more_than_zero), Toast.LENGTH_SHORT).show();
            mEdtPassingMarks.requestFocus();
        } else if (Integer.parseInt("" + mEdtPassingMarks.getText().toString().trim()) >
                Integer.parseInt("" + mEdtTotalMarks.getText().toString().trim())) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.toast_passing_marks_validation), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_passing_marks_validation), Toast.LENGTH_SHORT).show();
            mEdtPassingMarks.requestFocus();
        } else if (CommonUtil.isNullString("" + mEdtBatchName.getText().toString().trim())) {
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.toast_select_batch), AddTestActivity.this);
            Toast.makeText(this, "" + getResources().getString(R.string.toast_select_batch), Toast.LENGTH_SHORT).show();
            mEdtBatchName.performClick();
            return;
        } else {
            callApiToAddNewTest();
        }
    }

    private void callApiToAddNewTest() {
        if (!CommonUtil.isInternetAvailable(AddTestActivity.this)) {
            return;
        }

        new OkHttpRequest(AddTestActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ADD_NEW_TEST,
                RequestParam.addNewTest("" + mEdtTestName.getText().toString().trim(),
                        "" + mEdtTestDate.getText().toString().trim(),
                        "" + mEdtTestTime.getText().toString().trim(),
                        "" + mSharedPreferences.getString(KEY_EMPLOYEE_BRANCH_ID, ""),
                        "" + mSharedPreferences.getString(KEY_LOGGEDIN_USER_ID, ""),
                        "" + mEdtTotalMarks.getText().toString().trim(),
                        "" + mEdtPassingMarks.getText().toString().trim(),
                        "" + mStrSelectedBatchId),
                RequestParam.getNull(), CODE_ADD_NEW_TEST,
                true, this);
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e(">>>", "" + response);

        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_ADD_NEW_TEST:
                try {
                    JSONObject objResponse = new JSONObject(response);
                    if (objResponse != null) {
                        Toast.makeText(this, "" + objResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_BATCH_ID) {
            if (data.hasExtra("batch_id")) {
                mStrSelectedBatchId = "" + data.getStringExtra("batch_id");
            }

            if (data.hasExtra("batch_name")) {
                mEdtBatchName.setText("" + data.getStringExtra("batch_name"));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
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
