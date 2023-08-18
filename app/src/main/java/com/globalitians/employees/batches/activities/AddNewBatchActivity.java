package com.globalitians.employees.batches.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.customviews.CustomEditText;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.students.activities.StudentDetailsActivity;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Calendar;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_ADD_BATCH;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_DETAILS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_ID;

public class AddNewBatchActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener {
    private CustomEditText edtName;
    private CustomEditText edtAlias;
    private Spinner spinnerStatus;
    private CustomTextView tvAddNewBatch;
    private CustomEditText mEdtcratedDate;

    private CustomEditText mEdtassignfaculty;
    private CustomEditText mEdtassignstudents;
    private BatchDetailsModel batchDetailsModel;

    private DatePickerDialog mDatePickerDiaogCreatedDate = null;
    private String mStrbatchId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AddNewBatchActivity.this);
        setContentView(R.layout.activity_add_new_batch);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create Batch");
        findViews();
        initCreatedDate();

        getBatchIdfromBatchDetails();

    }

    private void getBatchIdfromBatchDetails() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrbatchId = intent.getStringExtra("modelBatch");
            if(!CommonUtil.isNullString(""+mStrbatchId)){
                CallApiToupdateBatch(mStrbatchId);
                getSupportActionBar().setTitle("Edit Batch");
                tvAddNewBatch.setText("Update");
            }
        }
    }

    private void CallApiToupdateBatch(String mStrbatchId) {
        if(!CommonUtil.isInternetAvailable(AddNewBatchActivity.this)){
            Toast.makeText(this, "Internet is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        new OkHttpRequest(AddNewBatchActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_DETAIL,
                RequestParam.batchDetails(""+mStrbatchId),
                RequestParam.getNull(),
                CODE_BATCH_DETAILS,
                true,this);
    }

    private void initCreatedDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener birthdateListener;
        birthdateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if ((month + 1) < 10 && dayOfMonth < 10) {
                    mEdtcratedDate.setText("0" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                } else if ((month + 1) < 10) {
                    mEdtcratedDate.setText("" + dayOfMonth + "-0" + (month + 1) + "-" + year);
                } else if (dayOfMonth < 10) {
                    mEdtcratedDate.setText("0" + dayOfMonth + "" + (month + 1) + "-" + year);
                } else {
                    mEdtcratedDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
                }
            }
        };

        mDatePickerDiaogCreatedDate = new DatePickerDialog(
                AddNewBatchActivity.this, birthdateListener, year, month, day);
        mDatePickerDiaogCreatedDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

    }

    private void findViews() {
        edtName = (CustomEditText) findViewById(R.id.edt_name);
        edtAlias = (CustomEditText) findViewById(R.id.edt_alias);
        spinnerStatus = (Spinner) findViewById(R.id.spinner_status);
        tvAddNewBatch = (CustomTextView) findViewById(R.id.tv_add_new_batch);
        mEdtassignfaculty = findViewById(R.id.edt_assigned_faculties);
        mEdtassignstudents = findViewById(R.id.edt_assigned_students);
        mEdtcratedDate = findViewById(R.id.edt_created_date);

        mEdtassignstudents.setOnClickListener(this);
        mEdtassignfaculty.setOnClickListener(this);
        tvAddNewBatch.setOnClickListener(this);
        mEdtcratedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDiaogCreatedDate.show();
            }
        });
    }

    private void callApiToAddNewBatch() {

        if (!CommonUtil.isInternetAvailable(AddNewBatchActivity.this)) {
            return;
        }

        if(CommonUtil.isNullString(""+mStrbatchId)){
            new OkHttpRequest(AddNewBatchActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_BATCH,
                    RequestParam.addBatch("" + edtName.getText().toString().trim(),
                            "" + edtAlias.getText().toString().trim(),
                            "" + getSharedPrefrencesInstance(AddNewBatchActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, ""),
                            "" + spinnerStatus.getSelectedItem().toString(),
                            "" + getSharedPrefrencesInstance(AddNewBatchActivity.this).getString(KEY_LOGGEDIN_EMPLOYEE_ID, ""),
                            ""),
                    RequestParam.getNull(),
                    CODE_ADD_BATCH,
                    true, this);
        }else{
            new OkHttpRequest(AddNewBatchActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_BATCH,
                    RequestParam.addBatch("" + edtName.getText().toString().trim(),
                            "" + edtAlias.getText().toString().trim(),
                            "" + getSharedPrefrencesInstance(AddNewBatchActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID, ""),
                            "" + spinnerStatus.getSelectedItem().toString(),
                            "" + getSharedPrefrencesInstance(AddNewBatchActivity.this).getString(KEY_LOGGEDIN_EMPLOYEE_ID, ""),
                            ""+mStrbatchId),
                    RequestParam.getNull(),
                    CODE_ADD_BATCH,
                    true, this);
        }

    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_ADD_BATCH:
                try {
                    JSONObject jObjResponse = new JSONObject(response);
                    if (jObjResponse.has("status")) {
                        if (jObjResponse.getString("status").equals("success")) {
                            finish();
                        }
                    }
                    Toast.makeText(this, "" + jObjResponse.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LogUtil.e("Add Batch>>", "" + response);
                break;
            case CODE_BATCH_DETAILS:
                final Gson batchDetailsGson = new Gson();
                try {
                    batchDetailsModel = batchDetailsGson
                            .fromJson(response, BatchDetailsModel.class);
                    if(batchDetailsModel.getStatus().equals(Constants.SUCCESS_CODE)){
                        setBatchDetails(batchDetailsModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setBatchDetails(BatchDetailsModel batchDetailsModel) {

        edtAlias.setText(""+batchDetailsModel.getBatchDetail().getAlias());
        mEdtcratedDate.setText(""+batchDetailsModel.getBatchDetail().getCreatedAt());
        edtName.setText(""+batchDetailsModel.getBatchDetail().getName());

        String[] arrBatchStatus = getResources().getStringArray(R.array.batch_status);
        for (int i = 0; i < arrBatchStatus.length; i++) {
            if (arrBatchStatus[i].equalsIgnoreCase("" + StudentDetailsActivity.modelStudent.getStudentDetail().getReference())) {
                spinnerStatus.setSelection(i);
                return;
            }
        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_new_batch:
                validateAndCallApiToAddBatch();
                break;
            case R.id.edt_assigned_faculties:
                startActivity(new Intent(AddNewBatchActivity.this,FacultyAccessActivity.class));
                break;
        }
    }

    private void validateAndCallApiToAddBatch() {

        if (CommonUtil.isNullString("" + edtName.getText().toString().trim())) {
            Toast.makeText(this, "" + getResources().getString(R.string.toast_add_batch_name), Toast.LENGTH_SHORT).show();
            return;
        }

        if (CommonUtil.isNullString("" + edtAlias.getText().toString().trim())) {
            Toast.makeText(this, "" + getResources().getString(R.string.toast_add_alias_name), Toast.LENGTH_SHORT).show();
            return;
        }

        callApiToAddNewBatch();
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
