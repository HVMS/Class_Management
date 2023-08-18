package com.globalitians.employees.dashboard.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.Homework.activity.AddHomeWorkActivity;
import com.globalitians.employees.Homework.activity.HomeworkListActivity;
import com.globalitians.employees.R;
import com.globalitians.employees.attendence.activities.AttendanceListActivity;
import com.globalitians.employees.attendence.activities.TakeManualAttendanceActivity;
import com.globalitians.employees.attendence.activities.TakeStudentAttendenceActivity;
import com.globalitians.employees.batches.activities.AddNewBatchActivity;
import com.globalitians.employees.batches.activities.BatchListActivity;
import com.globalitians.employees.branches.activities.BranchListActivity;
import com.globalitians.employees.courses.activities.AddCourseActivity;
import com.globalitians.employees.courses.activities.CourseListActivity;
import com.globalitians.employees.employee.activities.AddEmployeeActivity;
import com.globalitians.employees.employee.activities.EmployeeListActivity;
import com.globalitians.employees.employee.activities.EmployeeLoginActivity;
import com.globalitians.employees.employee.adapter.EmployeeOptionsAdapter;
import com.globalitians.employees.employee.model.EmployeeLoginModel;
import com.globalitians.employees.employee.model.EmployeeOptionsModel;
import com.globalitians.employees.employee.model.UserAccessDataSource;
import com.globalitians.employees.faculty.activities.AddNewFacultyActivity;
import com.globalitians.employees.faculty.activities.FacultyListActivity;
import com.globalitians.employees.inquiry.activities.AddInquiryActivity;
import com.globalitians.employees.inquiry.activities.InquiryListActivity;
import com.globalitians.employees.multiple_attendance.activities.MultipleBatchWiseAttendanceListActivity;
import com.globalitians.employees.multiple_attendance.activities.MultipleMyNewStudentAttendanceActivity;
import com.globalitians.employees.other.activities.AppSettingsActivity;
import com.globalitians.employees.payments.activities.AddStudentFeesActivity;
import com.globalitians.employees.payments.activities.PaymentListTabbedActivity;
import com.globalitians.employees.students.activities.AddStudentActivity;
import com.globalitians.employees.students.activities.StudentListActivity;
import com.globalitians.employees.test.activity.AddTestActivity;
import com.globalitians.employees.test.activity.TestListActivity;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
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
import static com.globalitians.employees.utility.Constants.USER_ROLES;


public class DashboardEmployeeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, EmployeeOptionsAdapter.EmployeeOptionClickListener, OkHttpInterface {

    private CircularImageView mIvEmployeeProfile;

    private RelativeLayout mRelHome;
    private RelativeLayout mRelCources;
    private RelativeLayout mRelPhotoGallery;
    private RelativeLayout mRelViewOnMap;
    private RelativeLayout mRelScanner;
    private RelativeLayout mRelTimeSchedules;
    private RelativeLayout mRelShare;
    private RelativeLayout mRelSettings;
    private TextView mTvEmployeeName;

    private TextView mTvLogout;

    private DrawerLayout mDrawer;

    private SharedPreferences mSharedPrefrences;
    private SharedPreferences.Editor mEditor;

    private ArrayList<EmployeeOptionsModel> mArrListStudentOptions = new ArrayList<>();
    private RecyclerView mRvStudentOptions;
    private EmployeeOptionsAdapter mAdapterStudentOptions;
    private boolean fabExpanded = false;
    private ImageView mFabDashboardEmployeeOptions;
    private LinearLayout layoutFabCourse;
    private LinearLayout layoutFabInquiry;
    private LinearLayout layoutFabStudent;
    private LinearLayout layoutFabBranch;
    private LinearLayout layoutFabAddFees;
    private LinearLayout layoutFabAddFaculty;
    private LinearLayout layoutFabAddBatch;
    private LinearLayout layoutContainerFabs;
    private LinearLayout layoutFabAddEmployee;
    private LinearLayout layoutFabAddTest;
    private LinearLayout layoutFabAddHomeWork;

    private RelativeLayout layoutFabSubMenu;

    private Animation animSlideUp;
    private Animation animSlideDown;

    private EmployeeLoginModel mLoggedInUserData = null;
    private MenuItem itemAddStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(DashboardEmployeeActivity.this);
        setContentView(R.layout.activity_dashboard_employee);

       /* Uri mUri = Uri.parse("https://api.whatsapp.com/send?phone=919408776491&text='Hello User'");
        Intent intent = new Intent("android.intent.action.VIEW", mUri);
        intent.setPackage("com.whatsapp");
        startActivity(intent);*/

        initialize();
        findViews();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Class Management");
        toolbar.setBackgroundColor(Color.parseColor("#258DFF"));
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mLoggedInUserData =UserAccessDataSource.getInstance().getData();
    }

    private void setAddStudentOptionVisibility(EmployeeLoginModel mLoggedInUserData) {
        if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
            itemAddStudent.setVisible(true);
        } else {
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(2).getAccessItems().get(1).getIsActive() == 1) {
                itemAddStudent.setVisible(true);
            } else {
                itemAddStudent.setVisible(false);
            }
        }
    }


    private void initialize() {
        mSharedPrefrences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPrefrences.edit();
        mArrListStudentOptions = new ArrayList<>();

        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideup);
        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidedown);
    }

    private void callApiToLoginEmployee(String strUsername, String strPassword) {
        if (!CommonUtil.isInternetAvailable(DashboardEmployeeActivity.this)) {
            return;
        }

        new OkHttpRequest(DashboardEmployeeActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_EMPLOYEE_LOGIN,
                RequestParam.loginEmployee(strUsername, strPassword),
                RequestParam.getNull(),
                CODE_LOGIN_EMPLOYEE,
                true, this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
        MenuItem itemPdf = menu.findItem(R.id.action_pdf);
        itemPdf.setVisible(false);
        MenuItem itemExcel = menu.findItem(R.id.action_excel);
        itemExcel.setVisible(false);
        MenuItem itemEdit = menu.findItem(R.id.action_edit);
        itemEdit.setVisible(false);
        itemAddStudent = menu.findItem(R.id.action_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(DashboardEmployeeActivity.this, AppSettingsActivity.class));
            return true;
        }
        if (id == R.id.action_add) {
            navigateToAddStudentActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToAddStudentActivity() {
        startActivity(new Intent(DashboardEmployeeActivity.this, AddStudentActivity.class));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void findViews() {
        mFabDashboardEmployeeOptions = findViewById(R.id.fabDashboardEmployeeOptions);

        layoutFabSubMenu = findViewById(R.id.relFab);

        layoutFabCourse = findViewById(R.id.layoutFabAddCourse);
        layoutFabInquiry = findViewById(R.id.layoutFabInquiry);
        layoutFabStudent = findViewById(R.id.layoutFabStudent);
        layoutFabBranch = findViewById(R.id.layoutFabBranch);
        layoutFabAddFees = findViewById(R.id.layoutFabAddFees);
        layoutFabAddFaculty = findViewById(R.id.layoutFabAddFaculty);
        layoutFabAddBatch = findViewById(R.id.layoutFabAddBatch);
        layoutContainerFabs = findViewById(R.id.linContainerFabs);
        layoutFabAddEmployee = findViewById(R.id.layoutFabAddEmployee);
        layoutFabAddTest = findViewById(R.id.layoutFabAddTest);
        layoutFabAddHomeWork = findViewById(R.id.layoutFabAddHomeWork);

        layoutFabCourse.setVisibility(View.GONE);
        layoutFabInquiry.setVisibility(View.GONE);
        layoutFabStudent.setVisibility(View.GONE);
        layoutFabBranch.setVisibility(View.GONE);
        layoutFabAddFees.setVisibility(View.GONE);
        layoutFabAddFaculty.setVisibility(View.GONE);
        layoutFabAddBatch.setVisibility(View.GONE);
        layoutContainerFabs.setVisibility(View.GONE);
        layoutFabAddEmployee.setVisibility(View.GONE);
        layoutFabAddTest.setVisibility(View.GONE);
        layoutFabAddHomeWork.setVisibility(View.GONE);

        mIvEmployeeProfile = findViewById(R.id.iv_employee_profile);
        mTvEmployeeName = findViewById(R.id.tv_employee_name);
        mRelHome = findViewById(R.id.rel_home);
        mRelCources = findViewById(R.id.rel_cources);
        mRelPhotoGallery = findViewById(R.id.rel_photo_gallery);
        mRelViewOnMap = findViewById(R.id.rel_view_on_map);
        mRelScanner = findViewById(R.id.rel_scanner);
        mRelTimeSchedules = findViewById(R.id.rel_time_schedules);
        mRelShare = findViewById(R.id.rel_share);
        mRelSettings = findViewById(R.id.rel_settings);

        mTvLogout = findViewById(R.id.tv_logout);

        mRvStudentOptions = findViewById(R.id.rv_student_options);

        mLoggedInUserData = UserAccessDataSource.getInstance().getData();

        setEmployeeOptionsAdapter();

        CommonUtil.setCircularImageForUser(DashboardEmployeeActivity.this, mIvEmployeeProfile, getSharedPrefrencesInstance(DashboardEmployeeActivity.this).getString(KEY_LOGGEDIN_EMPLOYEE_IMAGE, ""));
        mTvEmployeeName.setText(mSharedPrefrences.getString(KEY_LOGGEDIN_EMPLOYEE_NAME, ""));
        setListeners();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setAddStudentOptionVisibility(mLoggedInUserData);
            }
        }, 1500);
    }

    private void setFloatingButtonAccess() {

        String strRole = "" + mSharedPrefrences.getString(KEY_LOGGEDIN_EMPLOYEE_ROLE, "");
        if (strRole.equals(USER_ROLES[0])) {
            //Admin
            layoutFabCourse.setVisibility(View.VISIBLE);
            layoutFabInquiry.setVisibility(View.VISIBLE);
            layoutFabStudent.setVisibility(View.VISIBLE);
            layoutFabBranch.setVisibility(View.VISIBLE);
            layoutFabAddFees.setVisibility(View.VISIBLE);
            layoutFabAddFaculty.setVisibility(View.VISIBLE);
            layoutFabAddBatch.setVisibility(View.VISIBLE);
            layoutFabAddEmployee.setVisibility(View.VISIBLE);
            layoutFabAddTest.setVisibility(View.VISIBLE);
            layoutFabAddHomeWork.setVisibility(View.VISIBLE);
            layoutContainerFabs.setVisibility(View.VISIBLE);
        }

        if (strRole.equals(USER_ROLES[1])) {
            //Employee
            //Add Students
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(2).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabStudent.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }

            //Add Faculty
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(7).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabAddFaculty.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }

            //Add Test
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(6).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabAddTest.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }

            //Add Batch
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(12).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabAddBatch.setVisibility(View.VISIBLE);
            }

            //Add Inquiry
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(11).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabInquiry.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }

            //Add Fees
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(4).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabAddFees.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }

            //Add HomeWork
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(10).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabAddHomeWork.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }
        }
        if (strRole.equals(USER_ROLES[2])) {
            //Employee

            //Add Test
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(1).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabAddTest.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }

            //Add HomeWork
            if (mLoggedInUserData.getEmployeeDetail().getAccess().get(2).getAccessItems().get(1).getIsActive() == 1) {
                layoutFabAddHomeWork.setVisibility(View.VISIBLE);
                layoutContainerFabs.setVisibility(View.VISIBLE);
            }
        }

    }

    private void setEmployeeOptionsAdapter() {

        /*//mLoggedInUserData = UserAccessDataSource.getInstance().getData();

        if (mLoggedInUserData != null) {
            addDashboardModules(mLoggedInUserData);
        } else {*/
            SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
            String strUsername = "" + sharedPreferences.getString(KEY_LOGGEDIN_EMPLOYEE_USERNAME, "");
            String strPassword = "" + sharedPreferences.getString(KEY_LOGGEDIN_EMPLOYEE_PASSWORD, "");

            LogUtil.e("USERNAME >>>", "" + strUsername);
            LogUtil.e("PASSWORD >>>", "" + strPassword);
            callApiToLoginEmployee("" + strUsername, "" + strPassword);
        //}
    }

    private void setAdapterForOptions() {

        LogUtil.e(">>> SET ADAPTER", "CALLLEDDDDDD");

        mAdapterStudentOptions = new EmployeeOptionsAdapter(DashboardEmployeeActivity.this, mArrListStudentOptions, this);
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRvStudentOptions.setLayoutManager(manager);
        mRvStudentOptions.setAdapter(mAdapterStudentOptions);
    }


    private void addDashboardModules(EmployeeLoginModel mLoggedInUserData) {
        /*
         * Checking that logged in user is admin or not.
         * If Admin, Adding option to assign roles to Employee and Faculty.
         * */
        if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
            String strBranchId = "" + mSharedPrefrences.getString(KEY_EMPLOYEE_BRANCH_ID, "");
            if (CommonUtil.isNullString(strBranchId)) {
                openDialogToSelectBranchIdForAdmin();
            }

            /*
             * Currently only admin can Assign Access, Manage Branches and Manage Cameras.
             * */
            EmployeeOptionsModel assignRoles = new EmployeeOptionsModel();
            assignRoles.setStrOptionTitle("Assign\nAccess");
            assignRoles.setBackgroundImageId(R.drawable.drawable_assign_access_gradient);
            assignRoles.setOptionImageId(R.drawable.ic_take_attendance_04);
            assignRoles.setStrColor("#33bbaa");
            mArrListStudentOptions.add(assignRoles);

            EmployeeOptionsModel modelBranches = new EmployeeOptionsModel();
            modelBranches.setStrOptionTitle("Branches");
            modelBranches.setBackgroundImageId(R.drawable.drawable_branches_gradient);
            modelBranches.setOptionImageId(R.drawable.ic_location_02);
            modelBranches.setStrColor("#aab766");
            mArrListStudentOptions.add(modelBranches);

            EmployeeOptionsModel model13 = new EmployeeOptionsModel();
            model13.setStrOptionTitle("Cameras");
            model13.setBackgroundImageId(R.drawable.drawable_cameras_gradient);
            model13.setOptionImageId(R.drawable.icon_camera);
            model13.setStrColor("#339999");
            mArrListStudentOptions.add(model13);

            createEmployeeModules();
            //createFacultyModules();
        }

        /*
         * Checking that logged in user is employee or not.
         * If Employee, Adding options for Employees.
         * */
        if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[1])) {
            createEmployeeModules();
        }

        /*
         * Checking that logged in user is faculty or not.
         * If Faculty, Adding options for Faculties.
         * */
        if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[2])) {
            createFacultyModules();
        }

        setAdapterForOptions();
    }


    /*
     * Method to show the dialog to choose branch for Admin.
     * */
    private void openDialogToSelectBranchIdForAdmin() {
        final Dialog dialogSelectBranch = new Dialog(DashboardEmployeeActivity.this);
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
                //2 for Odhav
                mSharedPrefrences.edit();
                mEditor.putString(KEY_EMPLOYEE_BRANCH_ID, "2");
                mEditor.commit();
                if (dialogSelectBranch != null && dialogSelectBranch.isShowing()) {
                    dialogSelectBranch.dismiss();
                }
            }
        });

        tvVastral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1 for Vastral
                mSharedPrefrences.edit();
                mEditor.putString(KEY_EMPLOYEE_BRANCH_ID, "1");
                mEditor.commit();
                if (dialogSelectBranch != null && dialogSelectBranch.isShowing()) {
                    dialogSelectBranch.dismiss();
                }
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

    private void createFacultyModules() {
        //5 for Student Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(0).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel modelTakeAttendance = new EmployeeOptionsModel();
            modelTakeAttendance.setStrOptionTitle("Take\nAttendance");
            modelTakeAttendance.setBackgroundImageId(R.drawable.drawable_attendance_gradient);
            modelTakeAttendance.setOptionImageId(R.drawable.ic_take_attendance_04);
            modelTakeAttendance.setStrColor("#448827");
            mArrListStudentOptions.add(modelTakeAttendance);

            EmployeeOptionsModel modelAttendanceReport = new EmployeeOptionsModel();
            modelAttendanceReport.setStrOptionTitle("Attendance\nReport");
            modelAttendanceReport.setBackgroundImageId(R.drawable.drawable_attendance_report_gradient);
            modelAttendanceReport.setOptionImageId(R.drawable.ic_upcoming_report);
            modelAttendanceReport.setStrColor("#09A9FF");
            mArrListStudentOptions.add(modelAttendanceReport);
        } else {
            //if admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel modelTakeAttendance = new EmployeeOptionsModel();
                modelTakeAttendance.setStrOptionTitle("Take\nAttendance");
                modelTakeAttendance.setBackgroundImageId(R.drawable.drawable_attendance_gradient);
                modelTakeAttendance.setOptionImageId(R.drawable.ic_take_attendance_04);
                modelTakeAttendance.setStrColor("#448827");
                mArrListStudentOptions.add(modelTakeAttendance);

                EmployeeOptionsModel modelAttendanceReport = new EmployeeOptionsModel();
                modelAttendanceReport.setStrOptionTitle("Attendance\nReport");
                modelAttendanceReport.setBackgroundImageId(R.drawable.drawable_attendance_report_gradient);
                modelAttendanceReport.setOptionImageId(R.drawable.ic_upcoming_report);
                modelAttendanceReport.setStrColor("#09A9FF");
                mArrListStudentOptions.add(modelAttendanceReport);
            }
        }

        //6 for Test Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(1).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel modelTest = new EmployeeOptionsModel();
            modelTest.setStrOptionTitle("Tests");
            modelTest.setBackgroundImageId(R.drawable.drawable_test_gradient);
            modelTest.setOptionImageId(R.drawable.ic_test_14);
            modelTest.setStrColor("#A9F409");
            mArrListStudentOptions.add(modelTest);
        } else {
            //if admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel modelTest = new EmployeeOptionsModel();
                modelTest.setStrOptionTitle("Tests");
                modelTest.setBackgroundImageId(R.drawable.drawable_test_gradient);
                modelTest.setOptionImageId(R.drawable.ic_test_14);
                modelTest.setStrColor("#A9F409");
                mArrListStudentOptions.add(modelTest);
            }
        }

        //8 for HomeWork Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(2).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel model15 = new EmployeeOptionsModel();
            model15.setStrOptionTitle("HomeWork");
            model15.setBackgroundImageId(R.drawable.drawable_howtouse_gradient);
            model15.setOptionImageId(R.drawable.ic_students_05);
            model15.setStrColor("#335555");
            mArrListStudentOptions.add(model15);
        } else {
            //if Admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel model15 = new EmployeeOptionsModel();
                model15.setStrOptionTitle("HomeWork");
                model15.setBackgroundImageId(R.drawable.drawable_howtouse_gradient);
                model15.setOptionImageId(R.drawable.ic_students_05);
                model15.setStrColor("#335555");
                mArrListStudentOptions.add(model15);
            }
        }
    }

    private void createEmployeeModules() {
        //2 for Student Management //if not Admin
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && mLoggedInUserData.getEmployeeDetail().getAccess().get(2).getAccessItems().get(0).getIsActive() == 1) {
            EmployeeOptionsModel modelStudents = new EmployeeOptionsModel();
            modelStudents.setStrOptionTitle("Students");
            modelStudents.setBackgroundImageId(R.drawable.drawable_student_gradient);
            modelStudents.setOptionImageId(R.drawable.ic_student_06);
            modelStudents.setStrColor("#84570f");
            mArrListStudentOptions.add(modelStudents);
        } else {
            //if admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel modelStudents = new EmployeeOptionsModel();
                modelStudents.setStrOptionTitle("Students");
                modelStudents.setBackgroundImageId(R.drawable.drawable_student_gradient);
                modelStudents.setOptionImageId(R.drawable.ic_student_06);
                modelStudents.setStrColor("#84570f");
                mArrListStudentOptions.add(modelStudents);
            }
        }

        //7 for Faculty Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(7).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel modelFeesPayments = new EmployeeOptionsModel();
            modelFeesPayments.setStrOptionTitle("Faculty");
            modelFeesPayments.setBackgroundImageId(R.drawable.drawable_payment_gradient);
            modelFeesPayments.setOptionImageId(R.drawable.ic_students_05);
            modelFeesPayments.setStrColor("#99339a");
            mArrListStudentOptions.add(modelFeesPayments);
        } else {
            //if Admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel modelFeesPayments = new EmployeeOptionsModel();
                modelFeesPayments.setStrOptionTitle("Faculty");
                modelFeesPayments.setBackgroundImageId(R.drawable.drawable_payment_gradient);
                modelFeesPayments.setOptionImageId(R.drawable.ic_students_05);
                modelFeesPayments.setStrColor("#99339a");
                mArrListStudentOptions.add(modelFeesPayments);
            }

        }

        //5 for Student Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(5).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel modelTakeAttendance = new EmployeeOptionsModel();
            modelTakeAttendance.setStrOptionTitle("Take\nAttendance");
            modelTakeAttendance.setBackgroundImageId(R.drawable.drawable_attendance_gradient);
            modelTakeAttendance.setOptionImageId(R.drawable.ic_take_attendance_04);
            modelTakeAttendance.setStrColor("#448827");
            mArrListStudentOptions.add(modelTakeAttendance);

            EmployeeOptionsModel modelAttendanceReport = new EmployeeOptionsModel();
            modelAttendanceReport.setStrOptionTitle("Attendance\nReport");
            modelAttendanceReport.setBackgroundImageId(R.drawable.drawable_attendance_report_gradient);
            modelAttendanceReport.setOptionImageId(R.drawable.ic_upcoming_report);
            modelAttendanceReport.setStrColor("#09A9FF");
            mArrListStudentOptions.add(modelAttendanceReport);
        } else {
            //if admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel modelTakeAttendance = new EmployeeOptionsModel();
                modelTakeAttendance.setStrOptionTitle("Take\nAttendance");
                modelTakeAttendance.setBackgroundImageId(R.drawable.drawable_attendance_gradient);
                modelTakeAttendance.setOptionImageId(R.drawable.ic_take_attendance_04);
                modelTakeAttendance.setStrColor("#448827");
                mArrListStudentOptions.add(modelTakeAttendance);

                EmployeeOptionsModel modelAttendanceReport = new EmployeeOptionsModel();
                modelAttendanceReport.setStrOptionTitle("Attendance\nReport");
                modelAttendanceReport.setBackgroundImageId(R.drawable.drawable_attendance_report_gradient);
                modelAttendanceReport.setOptionImageId(R.drawable.ic_upcoming_report);
                modelAttendanceReport.setStrColor("#09A9FF");
                mArrListStudentOptions.add(modelAttendanceReport);
            }
        }

        //6 for Test Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(6).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel modelTest = new EmployeeOptionsModel();
            modelTest.setStrOptionTitle("Tests");
            modelTest.setBackgroundImageId(R.drawable.drawable_test_gradient);
            modelTest.setOptionImageId(R.drawable.ic_test_14);
            modelTest.setStrColor("#A9F409");
            mArrListStudentOptions.add(modelTest);
        } else {
            //if admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel modelTest = new EmployeeOptionsModel();
                modelTest.setStrOptionTitle("Tests");
                modelTest.setBackgroundImageId(R.drawable.drawable_test_gradient);
                modelTest.setOptionImageId(R.drawable.ic_test_14);
                modelTest.setStrColor("#A9F409");
                mArrListStudentOptions.add(modelTest);
            }
        }

        //12 for Batch Management
        if (!mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(12).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel model1 = new EmployeeOptionsModel();
            model1.setStrOptionTitle("Batch");
            model1.setBackgroundImageId(R.drawable.drawable_inquiry_gradient);
            model1.setOptionImageId(R.drawable.ic_attendance_03);
            model1.setStrColor("#000000");
            mArrListStudentOptions.add(model1);
        } else {
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel model1 = new EmployeeOptionsModel();
                model1.setStrOptionTitle("Batch");
                model1.setBackgroundImageId(R.drawable.drawable_inquiry_gradient);
                model1.setOptionImageId(R.drawable.ic_attendance_03);
                model1.setStrColor("#000000");
                mArrListStudentOptions.add(model1);
            }
        }

        //11 for Inquiry Management
        if (!mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(11).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel model9 = new EmployeeOptionsModel();
            model9.setStrOptionTitle("Inquiries");
            model9.setBackgroundImageId(R.drawable.drawable_courses_gradient);
            model9.setOptionImageId(R.drawable.ic_inquiry_07);
            model9.setStrColor("#33bbaa");
            mArrListStudentOptions.add(model9);
        } else {
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel model9 = new EmployeeOptionsModel();
                model9.setStrOptionTitle("Inquiries");
                model9.setBackgroundImageId(R.drawable.drawable_courses_gradient);
                model9.setOptionImageId(R.drawable.ic_inquiry_07);
                model9.setStrColor("#33bbaa");
                mArrListStudentOptions.add(model9);
            }
        }
        //4 for Payment Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(4).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel modelCameras = new EmployeeOptionsModel();
            modelCameras.setStrOptionTitle("Payments");
            modelCameras.setBackgroundImageId(R.drawable.drawable_cameras_gradient);
            modelCameras.setOptionImageId(R.drawable.ic_payments_06);
            modelCameras.setStrColor("#ff2333");
            mArrListStudentOptions.add(modelCameras);
        } else {
            //if Admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {

                EmployeeOptionsModel modelCameras = new EmployeeOptionsModel();
                modelCameras.setStrOptionTitle("Payments");
                modelCameras.setBackgroundImageId(R.drawable.drawable_cameras_gradient);
                modelCameras.setOptionImageId(R.drawable.ic_payments_06);
                modelCameras.setStrColor("#ff2333");
                mArrListStudentOptions.add(modelCameras);
            }
        }

        //10 for HomeWork Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(10).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel model14 = new EmployeeOptionsModel();
            model14.setStrOptionTitle("Employee");
            model14.setBackgroundImageId(R.drawable.drawable_howtouse_gradient);
            model14.setOptionImageId(R.drawable.ic_students_05);
            model14.setStrColor("#335555");
            mArrListStudentOptions.add(model14);
        } else {
            //if Admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel model14 = new EmployeeOptionsModel();
                model14.setStrOptionTitle("Employee");
                model14.setBackgroundImageId(R.drawable.drawable_howtouse_gradient);
                model14.setOptionImageId(R.drawable.ic_students_05);
                model14.setStrColor("#335555");
                mArrListStudentOptions.add(model14);
            }
        }
        //8 for HomeWork Management
        if (!(mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0]))
                && (mLoggedInUserData.getEmployeeDetail().getAccess().get(8).getAccessItems().get(0).getIsActive() == 1)) {
            EmployeeOptionsModel model15 = new EmployeeOptionsModel();
            model15.setStrOptionTitle("HomeWork");
            model15.setBackgroundImageId(R.drawable.drawable_howtouse_gradient);
            model15.setOptionImageId(R.drawable.ic_students_05);
            model15.setStrColor("#335555");
            mArrListStudentOptions.add(model15);
        } else {
            //if Admin
            if (mLoggedInUserData.getEmployeeDetail().getRole().equals(USER_ROLES[0])) {
                EmployeeOptionsModel model15 = new EmployeeOptionsModel();
                model15.setStrOptionTitle("HomeWork");
                model15.setBackgroundImageId(R.drawable.drawable_howtouse_gradient);
                model15.setOptionImageId(R.drawable.ic_students_05);
                model15.setStrColor("#335555");
                mArrListStudentOptions.add(model15);
            }
        }

        /*
         * If there is Course Suppost then only we have to show the Course Module.
         * */
        if (Constants.IS_COURSE_SUPPORT) {
            EmployeeOptionsModel modelCourses = new EmployeeOptionsModel();
            modelCourses.setStrOptionTitle("Courses");
            modelCourses.setBackgroundImageId(R.drawable.drawable_courses_gradient);
            modelCourses.setOptionImageId(R.drawable.ic_courses);
            modelCourses.setStrColor("#aab766");
            mArrListStudentOptions.add(modelCourses);
        }
    }

    private void setListeners() {

        /*
         * If there is Course Suppost then only we have to show the Course Module.
         * */
        if (Constants.IS_COURSE_SUPPORT) {
            mRelCources.setOnClickListener(this);
            layoutFabCourse.setOnClickListener(this);
            mRelCources.setVisibility(View.VISIBLE);
            layoutFabCourse.setVisibility(View.VISIBLE);
        } else {
            mRelCources.setVisibility(View.GONE);
            layoutFabCourse.setVisibility(View.GONE);
        }

        mRelHome.setOnClickListener(this);
        mRelPhotoGallery.setOnClickListener(this);
        mRelViewOnMap.setOnClickListener(this);
        mRelScanner.setOnClickListener(this);
        mRelTimeSchedules.setOnClickListener(this);
        mRelShare.setOnClickListener(this);
        mRelSettings.setOnClickListener(this);
        mIvEmployeeProfile.setOnClickListener(this);
        mTvLogout.setOnClickListener(this);
        mFabDashboardEmployeeOptions.setOnClickListener(this);

        layoutFabInquiry.setOnClickListener(this);
        layoutFabStudent.setOnClickListener(this);
        layoutFabBranch.setOnClickListener(this);
        layoutFabAddFees.setOnClickListener(this);
        layoutFabAddFaculty.setOnClickListener(this);
        layoutFabAddBatch.setOnClickListener(this);
        layoutFabAddEmployee.setOnClickListener(this);
        layoutFabAddTest.setOnClickListener(this);
        layoutFabAddHomeWork.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_employee_profile:
                //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rel_home:
                mDrawer.closeDrawers();
                navigateToHome();
                break;
            case R.id.rel_cources:
                mDrawer.closeDrawers();
                navigateToCources();
                break;
            case R.id.rel_photo_gallery:
                mDrawer.closeDrawers();
                navigateToPhotoGallery();
                break;
            case R.id.rel_view_on_map:
                mDrawer.closeDrawers();
                navigateToMap();
                break;
            case R.id.rel_scanner:
                mDrawer.closeDrawers();
                navigateToScannerActivity();
                break;
            case R.id.rel_time_schedules:
                mDrawer.closeDrawers();
                navigateToTimeScheduleActivity();
                break;
            case R.id.rel_share:
                mDrawer.closeDrawers();
                shareToSocialMedia();
                break;
            case R.id.rel_settings:
                startActivity(new Intent(DashboardEmployeeActivity.this, AppSettingsActivity.class));
                break;
            case R.id.tv_logout:
                mDrawer.closeDrawers();
                clearLoginPrefrences();
                navigateToLoginScreen();
                break;
            case R.id.fabDashboardEmployeeOptions:
                if (fabExpanded == true) {
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
                break;
            case R.id.layoutFabAddCourse:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddCourseActivity.class));
                break;
            case R.id.layoutFabInquiry:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddInquiryActivity.class));
                break;
            case R.id.layoutFabStudent:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddStudentActivity.class));
                break;
            case R.id.layoutFabBranch:
                closeSubMenusFab();
                playSoundForAttendance("This feature is in development. It will be avaiable soon", DashboardEmployeeActivity.this);
                Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layoutFabAddFees:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddStudentFeesActivity.class));
                break;
            case R.id.layoutFabAddFaculty:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddNewFacultyActivity.class));
                break;
            case R.id.layoutFabAddBatch:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddNewBatchActivity.class));
                break;
            case R.id.layoutFabAddEmployee:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddEmployeeActivity.class));
                break;
            case R.id.layoutFabAddTest:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddTestActivity.class));
                break;
            case R.id.layoutFabAddHomeWork:
                closeSubMenusFab();
                startActivity(new Intent(DashboardEmployeeActivity.this, AddHomeWorkActivity.class));
                break;
        }
    }

    private void slideUpAnimation() {
        layoutContainerFabs.startAnimation(animSlideUp);
    }

    private void slideDownAnimation() {
        layoutContainerFabs.startAnimation(animSlideDown);
    }

    private void clearLoginPrefrences() {
        if (mSharedPrefrences != null) {
            SharedPreferences.Editor editor = mSharedPrefrences.edit();
            editor.remove(KEY_IS_LOGGED_IN);
            editor.clear();
            editor.commit();
        }
    }

    public void openCameras() {
        Toast.makeText(this, "Opening Cameras", Toast.LENGTH_SHORT).show();
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.hikvision.hikconnect");

            if (intent.resolveActivity(getPackageManager()) != null) {  //Open Hik-Connect app if installed
                startActivity(intent);
            } else {
//show toast or handle however you want
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToLoginScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardEmployeeActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startActivity(new Intent(DashboardEmployeeActivity.this, EmployeeLoginActivity.class));
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

    private void shareToSocialMedia() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Checkout our Global It Student's Android Application at: https://drive.google.com/open?id=1PXUdPuopyI-De6lU5S1Ni8QFQmzJkYrr");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void navigateToTimeScheduleActivity() {
    }

    private void navigateToScannerActivity() {
        startActivity(new Intent(DashboardEmployeeActivity.this, TakeStudentAttendenceActivity.class));
    }

    private void navigateToMap() {
        String map = "http://maps.google.co.in/maps?q=" + getResources().getString(R.string.strOdhavBranchAddress);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(intent);
    }

    private void navigateToPhotoGallery() {
    }

    private void navigateToCources() {
        startActivity(new Intent(DashboardEmployeeActivity.this, CourseListActivity.class));
    }

    private void navigateToHome() {
    }

    @Override
    public void onEmployeeOptionClick(int position) {
        switch ("" + mArrListStudentOptions.get(position).getStrOptionTitle()) {
            case "Assign\nAccess":
                showDialogForUserTypeSelection();
                break;
            case "Cameras":
                openCameras();
                break;
            case "Courses":
                startActivity(new Intent(DashboardEmployeeActivity.this, CourseListActivity.class));
                break;
            case "Inquiries":
                startActivity(new Intent(DashboardEmployeeActivity.this, InquiryListActivity.class));
                break;
            case "Students":
                startActivity(new Intent(DashboardEmployeeActivity.this, StudentListActivity.class));
                break;
            case "Branches":
                startActivity(new Intent(DashboardEmployeeActivity.this, BranchListActivity.class));
                break;
            case "Take\nAttendance":
                //showAttendanceOptionDialog();
                if (Constants.IS_MULTIPLE_ATTENDANCE_SUPPORT) {
                    //multiple attendance
                    startActivity(new Intent(DashboardEmployeeActivity.this, MultipleMyNewStudentAttendanceActivity.class));
                } else {
                    //singal attendance
                    startActivity(new Intent(DashboardEmployeeActivity.this, TakeManualAttendanceActivity.class));
                }

                break;
            case "Attendance\nReport":
                //startActivity(new Intent(DashboardEmployeeActivity.this, NewAttendanceListActivity.class));
                if (Constants.IS_MULTIPLE_ATTENDANCE_SUPPORT) {
                    /*
                     * BatchWiseAttendanceReport is detailed Activity
                     * */
                    startActivity(new Intent(DashboardEmployeeActivity.this, MultipleBatchWiseAttendanceListActivity.class));
                } else {
                    //report for single attendance list
                    startActivity(new Intent(DashboardEmployeeActivity.this, AttendanceListActivity.class));
                }
                break;
            case "Payments":
                startActivity(new Intent(DashboardEmployeeActivity.this, PaymentListTabbedActivity.class));
                //startActivity(new Intent(DashboardEmployeeActivity.this, AllFeesListActivity.class));
                //startActivity(new Intent(DashboardEmployeeActivity.this, FeesHistoryListActivity .class));
                break;
            case "Tests":
                startActivity(new Intent(DashboardEmployeeActivity.this, TestListActivity.class));
                break;
           /* case "How to Use ?":
                //playSoundForAttendance("This feature is in development. It will be avaiable soon",DashboardEmployeeActivity.this);
                //Toast.makeText(this, "In Progress..", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardEmployeeActivity.this, HowToUseActivity.class));
                break;*/
            case "Faculty":
                startActivity(new Intent(DashboardEmployeeActivity.this, FacultyListActivity.class));
                break;
            case "Batch":
                startActivity(new Intent(DashboardEmployeeActivity.this, BatchListActivity.class));
                break;
            case "Employee":
                startActivity(new Intent(DashboardEmployeeActivity.this, EmployeeListActivity.class));
                break;
            case "HomeWork":
                startActivity(new Intent(DashboardEmployeeActivity.this, HomeworkListActivity.class));
                break;
        }
    }

    private void showDialogForUserTypeSelection() {
        final Dialog dialogSelectUserType = new Dialog(DashboardEmployeeActivity.this);
        dialogSelectUserType.setContentView(R.layout.dialog_choose_user_to_assign_roles);
        dialogSelectUserType.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSelectUserType.setCancelable(false);

        TextView tvEmployee = dialogSelectUserType.findViewById(R.id.tvEmployee);
        TextView tvFaculty = dialogSelectUserType.findViewById(R.id.tvFaculty);
        TextView tvCancel = dialogSelectUserType.findViewById(R.id.tvCancel);
        TextView tvClose = dialogSelectUserType.findViewById(R.id.tvClose);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectUserType != null && dialogSelectUserType.isShowing()) {
                    dialogSelectUserType.dismiss();
                }
            }
        });

        tvEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectUserType != null && dialogSelectUserType.isShowing()) {
                    dialogSelectUserType.dismiss();
                }
                startActivity(new Intent(DashboardEmployeeActivity.this, EmployeeListActivity.class)
                        .putExtra("from", "dashboard"));
            }
        });

        tvFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectUserType != null && dialogSelectUserType.isShowing()) {
                    dialogSelectUserType.dismiss();
                }
                startActivity(new Intent(DashboardEmployeeActivity.this, FacultyListActivity.class)
                        .putExtra("from", "dashboard"));
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelectUserType != null && dialogSelectUserType.isShowing()) {
                    dialogSelectUserType.dismiss();
                }
            }
        });

        if (dialogSelectUserType != null && !dialogSelectUserType.isShowing()) {
            dialogSelectUserType.show();
        }
    }

    private void closeSubMenusFab() {
        slideDownAnimation();
        fabExpanded = false;
        mFabDashboardEmployeeOptions.setImageResource(R.drawable.ic_floating_add);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layoutFabSubMenu.setVisibility(View.GONE);
            }
        }, 400);
    }

    //Opens FAB submenus
    private void openSubMenusFab() {
        slideUpAnimation();
        layoutFabSubMenu.setVisibility(View.VISIBLE);
        fabExpanded = true;
        mFabDashboardEmployeeOptions.setImageResource(R.drawable.ic_floating_close);
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if (response == null) {
            LogUtil.e("Login Response >>", "" + response);
            response = "null";
        }

        switch (requestId) {
            case CODE_LOGIN_EMPLOYEE:
                final Gson courseListGson = new Gson();
                try {
                    EmployeeLoginModel modelEmployeeLogin = courseListGson
                            .fromJson(response, EmployeeLoginModel.class);

                    if (modelEmployeeLogin.getStatus().equals(Constants.SUCCESS_CODE)) {

                        UserAccessDataSource.getInstance().setData(modelEmployeeLogin);
                        mLoggedInUserData = UserAccessDataSource.getInstance().getData();

                        saveLoginPrefrences(modelEmployeeLogin);
                        addDashboardModules(modelEmployeeLogin);
                        setFloatingButtonAccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void saveLoginPrefrences(EmployeeLoginModel modelEmployeeLogin) {
        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_PREFRENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_IS_LOGGED_IN, "true");
        editor.putString(KEY_IS_ACTION_VOICE, "true");
        editor.putString(KEY_EMPLOYEE_BRANCH_ID, "" + modelEmployeeLogin.getEmployeeDetail().getBranchId());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_ID, "" + modelEmployeeLogin.getEmployeeDetail().getId());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_USERNAME, "" + modelEmployeeLogin.getEmployeeDetail().getUsername());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_NAME, "" + modelEmployeeLogin.getEmployeeDetail().getName());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_IMAGE, "" + modelEmployeeLogin.getEmployeeDetail().getImage());
        editor.putString(KEY_LOGGEDIN_EMPLOYEE_ROLE, "" + modelEmployeeLogin.getEmployeeDetail().getRole());

        editor.commit();
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }
}
