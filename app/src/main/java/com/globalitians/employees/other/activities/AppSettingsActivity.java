package com.globalitians.employees.other.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomButton;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.employee.activities.EmployeeLoginActivity;
import com.globalitians.employees.utility.CommonUtil;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_IS_ACTION_VOICE;
import static com.globalitians.employees.utility.Constants.KEY_IS_LOGGED_IN;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_ROLE;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;

public class AppSettingsActivity extends AppCompatActivity {
    private Switch switchVoiceOff;
    private TextView tvLogout;
    private CustomButton mBtnChangeBranch;
    private CustomTextView mTvSelectedBranch;
    private SharedPreferences mSharedPrefrences;
    private SharedPreferences.Editor mEditor;

    private LinearLayout mLlBranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AppSettingsActivity.this);
        setContentView(R.layout.activity_app_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPrefrences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPrefrences.edit();
        mTvSelectedBranch = findViewById(R.id.tvBranchValue);

        mLlBranch = findViewById(R.id.ll_branch);


        String strRole = "" + mSharedPrefrences.getString(KEY_LOGGEDIN_EMPLOYEE_ROLE, "");
        if (strRole.equals("Admin")) {
            mLlBranch.setVisibility(View.VISIBLE);
            if (!CommonUtil.isNullString(mSharedPrefrences.getString(KEY_EMPLOYEE_BRANCH_ID, ""))) {
                if (mSharedPrefrences.getString(KEY_EMPLOYEE_BRANCH_ID, "").equals("1")) {
                    mTvSelectedBranch.setText("Vastral");
                } else {
                    mTvSelectedBranch.setText("Odhav");
                }
            }
        } else {
            mLlBranch.setVisibility(View.GONE);
        }

        mBtnChangeBranch = findViewById(R.id.btnChangeBranch);
        mBtnChangeBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogToSelectBranchIdForAdmin();
            }
        });

        switchVoiceOff = findViewById(R.id.switchVoiceOff);
        switchVoiceOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedPrefrences.getString(KEY_IS_ACTION_VOICE, "").equalsIgnoreCase("true")) {
                    switchVoiceOff.setChecked(false);
                    saveVoicePrefrence("false");
                } else {
                    switchVoiceOff.setChecked(true);
                    saveVoicePrefrence("true");
                }
            }
        });

        if (mSharedPrefrences.getString(KEY_IS_ACTION_VOICE, "").equalsIgnoreCase("true")) {
            switchVoiceOff.setChecked(true);
        } else {
            switchVoiceOff.setChecked(false);
        }

        tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLoginScreen();
            }
        });
    }


    /*
     * Method to show the dialog to choose branch for Admin.
     * */
    private void openDialogToSelectBranchIdForAdmin() {
        final Dialog dialogSelectBranch = new Dialog(AppSettingsActivity.this);
        dialogSelectBranch.setContentView(R.layout.dialog_select_brach_for_admin);
        dialogSelectBranch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSelectBranch.setCancelable(false);

        TextView tvOdhav = dialogSelectBranch.findViewById(R.id.tvOdhav);
        TextView tvVastral = dialogSelectBranch.findViewById(R.id.tvVastral);
        TextView tvCancel = dialogSelectBranch.findViewById(R.id.tvCancel);
        tvCancel.setVisibility(View.GONE);
        TextView tvClose = dialogSelectBranch.findViewById(R.id.tvClose);
        tvClose.setVisibility(View.GONE);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectBranch != null && dialogSelectBranch.isShowing()) {
                    dialogSelectBranch.dismiss();
                }
            }
        });

        tvOdhav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectBranch != null && dialogSelectBranch.isShowing()) {
                    dialogSelectBranch.dismiss();
                }
                //2 for Odhav
                mEditor.putString(KEY_EMPLOYEE_BRANCH_ID, "2");
                mEditor.commit();
                mTvSelectedBranch.setText("Odhav");
            }
        });

        tvVastral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectBranch != null && dialogSelectBranch.isShowing()) {
                    dialogSelectBranch.dismiss();
                }
                //1 for Vastral
                mEditor.putString(KEY_EMPLOYEE_BRANCH_ID, "1");
                mEditor.commit();
                mTvSelectedBranch.setText("Vastral");
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectBranch != null && dialogSelectBranch.isShowing()) {
                    dialogSelectBranch.dismiss();
                }
            }
        });

        if (dialogSelectBranch != null && !dialogSelectBranch.isShowing()) {
            dialogSelectBranch.show();
        }
    }

    private void navigateToLoginScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AppSettingsActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clearLoginPrefrences();
                startActivity(new Intent(AppSettingsActivity.this, EmployeeLoginActivity.class));
                finish();
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }


    private void clearLoginPrefrences() {
        if (mSharedPrefrences != null) {
            SharedPreferences.Editor editor = mSharedPrefrences.edit();
            editor.remove(KEY_IS_LOGGED_IN);
            editor.clear();
            editor.commit();
        }
    }


    @Override
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

    private void saveVoicePrefrence(String toggleVoice) {
        SharedPreferences.Editor editor = mSharedPrefrences.edit();
        if (toggleVoice.equalsIgnoreCase("false")) {
            playSoundForAttendance("Bye Bye Dear, I am silence now.", AppSettingsActivity.this);
        } else {
            playSoundForAttendance("Hello, I can Speak now. Yeppi", AppSettingsActivity.this);
        }
        editor.putString(KEY_IS_ACTION_VOICE, toggleVoice);
        editor.commit();
    }
}
