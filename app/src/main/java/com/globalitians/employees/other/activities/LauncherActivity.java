package com.globalitians.employees.other.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.globalitians.employees.R;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.employee.activities.EmployeeLoginActivity;
import com.globalitians.employees.utility.Constants;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.util.Random;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.KEY_IS_LOGGED_IN;
import static com.globalitians.employees.utility.Constants.LOGIN_PREFRENCES;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

public class LauncherActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private RelativeLayout mRelSplashBackground = null;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private AppUpdateManager mAppUpdateManager = null;
    private AppUpdateInfo mAppUpdateInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(LauncherActivity.this);
//        playSoundForAttendance("Welcome", LauncherActivity.this);
        setContentView(R.layout.activity_launcher);
        initialize();
        findViews();
        setRandomSplashBackground();
        try {
            getSupportActionBar().hide();

        // Creates instance of the manager.
        mAppUpdateManager = AppUpdateManagerFactory.create(LauncherActivity.this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
       /* appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                mAppUpdateInfo = appUpdateInfo;
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    requestApplicationImmediateUpdate();
                } else {
                    navigateToScreens();
                }
            }
        });*/
        navigateToScreens();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToScreens() {
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (mSharedPreferences.getString(KEY_IS_LOGGED_IN, "false").equals("true")) {
                    Intent i = new Intent(LauncherActivity.this, DashboardEmployeeActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                } else {
                    Intent i = new Intent(LauncherActivity.this, EmployeeLoginActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    private void requestApplicationImmediateUpdate() {
        // Request the update.
        try {
            mAppUpdateManager.startUpdateFlowForResult(mAppUpdateInfo,
                    IMMEDIATE,
                    LauncherActivity.this,
                    Constants.REQUEST_APP_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    private void setRandomSplashBackground() {
        try {
            final int min = 1;
            final int max = 4;
            final int random = new Random().nextInt((max - min) + 1) + min;

            switch (random) {
                case 1:
                    mRelSplashBackground.setBackgroundResource(R.mipmap.ic_splash1);
                    break;
                case 2:
                    mRelSplashBackground.setBackgroundResource(R.mipmap.ic_splash2);
                    break;
                case 3:
                    mRelSplashBackground.setBackgroundResource(R.mipmap.ic_splash3);
                    break;
                case 4:
                    mRelSplashBackground.setBackgroundResource(R.mipmap.ic_splash4);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViews() {
        mRelSplashBackground = findViewById(R.id.relSplashContainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAppUpdateManager != null) {
            mAppUpdateManager
                    .getAppUpdateInfo()
                    .addOnSuccessListener(
                            new OnSuccessListener<AppUpdateInfo>() {
                                @Override
                                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                                    if (appUpdateInfo.updateAvailability()
                                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                        // If an in-app update is already running, resume the update.
                                        requestApplicationImmediateUpdate();
                                    }
                                }
                            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_APP_UPDATE) {
            if (resultCode != RESULT_OK
                    || resultCode == RESULT_CANCELED
                    || resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
                // If the update is cancelled or fails,
                // you can request to start the update again.
                requestApplicationImmediateUpdate();
            }
        }
    }
}


