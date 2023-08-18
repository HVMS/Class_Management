package com.globalitians.employees.employee.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomEditText;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.employee.model.EmployeeLoginModel;
import com.globalitians.employees.employee.model.UserAccessDataSource;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_LOGIN_EMPLOYEE;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_IS_ACTION_VOICE;
import static com.globalitians.employees.utility.Constants.KEY_IS_LOGGED_IN;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_ID;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_IMAGE;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_NAME;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_PASSWORD;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_ROLE;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_USERNAME;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EmployeeLoginActivity extends AppCompatActivity implements View.OnClickListener, OkHttpInterface {

    private CustomButton mTvLoginNow;
    private CustomEditText mEdtUsername;
    private CustomEditText mEdtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFullScreenMode(EmployeeLoginActivity.this);
        setContentView(R.layout.activity_employee_login);

        findViews();
    }

    private void findViews() {
        mTvLoginNow = findViewById(R.id.tv_login_now);
        mEdtUsername = findViewById(R.id.edt_username);
        mEdtPassword = findViewById(R.id.edt_password);

        setListeners();
    }

    private void setListeners() {
        mTvLoginNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_now:
                validateAndNavigateToStudentDashboard();
                break;
        }
    }

    private void validateAndNavigateToStudentDashboard() {
        String strUsername = mEdtUsername.getText().toString().trim();
        String strPassword = mEdtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername)) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_username), EmployeeLoginActivity.this);
            mEdtUsername.setError("" + getResources().getString(R.string.toast_invalid_username));
            return;
        }
        if (TextUtils.isEmpty(strPassword)) {
            playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_password), EmployeeLoginActivity.this);
            mEdtPassword.setError("" + getResources().getString(R.string.toast_invalid_password));
            return;
        }
        callApiToLoginEmployee(strUsername, strPassword);
    }

    private void callApiToLoginEmployee(String strUsername, String strPassword) {
        if (!CommonUtil.isInternetAvailable(EmployeeLoginActivity.this)) {
            return;
        }

        new OkHttpRequest(EmployeeLoginActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_EMPLOYEE_LOGIN,
                RequestParam.loginEmployee(strUsername, strPassword),
                RequestParam.getNull(),
                CODE_LOGIN_EMPLOYEE,
                true, this);
    }

    private void saveLoginPrefrences(EmployeeLoginModel modelEmployeeLogin) {
        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_IS_LOGGED_IN, "true");
        editor.putString(KEY_IS_ACTION_VOICE, "true");
        editor.putString(KEY_EMPLOYEE_BRANCH_ID, "" + modelEmployeeLogin.getEmployeeDetail().getBranchId());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_ID,""+ modelEmployeeLogin.getEmployeeDetail().getId());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_USERNAME,""+ modelEmployeeLogin.getEmployeeDetail().getUsername());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_PASSWORD,""+ modelEmployeeLogin.getEmployeeDetail().getPassword());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_NAME,""+ modelEmployeeLogin.getEmployeeDetail().getName());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_IMAGE,""+ modelEmployeeLogin.getEmployeeDetail().getImage());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_ROLE,""+modelEmployeeLogin.getEmployeeDetail().getRole());

        editor.commit();
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        LogUtil.e("Login Response >>",""+response);
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_LOGIN_EMPLOYEE:
                final Gson courseListGson = new Gson();
                try {
                    EmployeeLoginModel modelEmployeeLogin = courseListGson
                            .fromJson(response, EmployeeLoginModel.class);
                    UserAccessDataSource.getInstance().setData(modelEmployeeLogin);
                    /*// get data
                    JsonData data =  JsonDataSource.getInstance().getData();*/

                    if (modelEmployeeLogin.getStatus().equals(Constants.SUCCESS_CODE)) {
                        startActivity(new Intent(EmployeeLoginActivity.this, DashboardEmployeeActivity.class));
                        saveLoginPrefrences(modelEmployeeLogin);
                        finish();
                    }

                    playSoundForAttendance("" + modelEmployeeLogin.getMessage(), EmployeeLoginActivity.this);
                    Toast.makeText(this, "" + "" + modelEmployeeLogin.getMessage(), Toast.LENGTH_SHORT).show();
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
}
