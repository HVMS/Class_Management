package com.globalitians.employees.students.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.adapters.CourseListStudentProfileAdapter;
import com.globalitians.employees.courses.adapters.CourseListWhileAddingOrEditingStudentAdapter;
import com.globalitians.employees.students.models.ModelStudentDetailsResponse;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.Calendar;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_STUDENT_DETAILS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_ID;

public class StudentDetailsActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, CourseListWhileAddingOrEditingStudentAdapter.OnCourseListListener {
    private ImageView ivCourseBanner;
    private CircularImageView ivStudentPic;
    private EditText edtName;
    private EditText edtMobileNo;
    private EditText edtBirthDate;
    private EditText edtEmail;
    private TextView tvCourseEdit;
    private EditText edtBranch;
    private EditText edtParentName;
    private EditText edtParentMobile;
    private EditText edtAddress;
    private EditText edtReference;
    private EditText edtJoinedOn;
    private String strIntentStudentId;
    private RecyclerView rvCourseList;

    private DatePickerDialog mDatePickerDiaogBirthDate = null;
    private DatePickerDialog mDatePickerDialogJoinedDate = null;
    public static ModelStudentDetailsResponse modelStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(StudentDetailsActivity.this);
        setContentView(R.layout.activity_student_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getStudentIdFromIntent();

        findViews();

        initializeDatePickers();

        callApiToLoadStudentDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
        MenuItem itemPdf = menu.findItem(R.id.action_pdf);
        itemPdf.setVisible(false);
        MenuItem itemExcel = menu.findItem(R.id.action_excel);
        itemExcel.setVisible(false);
        MenuItem itemEdit = menu.findItem(R.id.action_edit);
        itemEdit.setVisible(true);
        return true;
    }

    private void initializeDatePickers() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener birthdateListener;
        birthdateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtBirthDate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        DatePickerDialog.OnDateSetListener joinedDateListener;
        joinedDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edtJoinedOn.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        //initializing date filter date picker dialog
        mDatePickerDiaogBirthDate = new DatePickerDialog(
                StudentDetailsActivity.this, birthdateListener, year, month, day);
        mDatePickerDiaogBirthDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        //initializing date filter date picker dialog
        mDatePickerDialogJoinedDate = new DatePickerDialog(
                StudentDetailsActivity.this, joinedDateListener, year, month, day);
        mDatePickerDialogJoinedDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if(id==R.id.action_edit) {
            if(modelStudent!=null){
                Intent yourIntent = new Intent(this, AddStudentActivity.class);
                //Bundle b = new Bundle();
                //b.putSerializable("user",modelStudent);
                //modelStudent is static
                yourIntent.putExtra("modelStudent","student"); //pass bundle to your intent
                startActivity(yourIntent);
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getStudentIdFromIntent() {
        Intent intentStudentId = getIntent();
        if (intentStudentId != null) {
            strIntentStudentId = "" + intentStudentId.getStringExtra(KEY_INTENT_STUDENT_ID);
        }
    }

    private void callApiToLoadStudentDetails() {
        if (!CommonUtil.isInternetAvailable(StudentDetailsActivity.this)) {
            finish();
            return;
        }

        new OkHttpRequest(StudentDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_DETAILS,
                RequestParam.studentDetails(strIntentStudentId,
                        ""+getSharedPrefrencesInstance(StudentDetailsActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_STUDENT_DETAILS,
                true, this);
    }

    private void findViews() {
        rvCourseList=(RecyclerView)findViewById(R.id.rv_course_list);
        ivCourseBanner = (ImageView) findViewById(R.id.iv_course_banner);
        ivStudentPic = (CircularImageView) findViewById(R.id.iv_student_pic);
        edtName = (EditText) findViewById(R.id.edtName);
        edtMobileNo = (EditText) findViewById(R.id.edtMobileNo);
        edtBirthDate = (EditText) findViewById(R.id.edtBirthDate);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        tvCourseEdit = (TextView) findViewById(R.id.tvCourseEdit);
        edtBranch = (EditText) findViewById(R.id.edtBranch);
        edtParentName = (EditText) findViewById(R.id.edtParentName);
        edtParentMobile = (EditText) findViewById(R.id.edtParentMobile);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtReference = (EditText) findViewById(R.id.edtReference);
        edtJoinedOn = (EditText) findViewById(R.id.edtJoinedOn);

        edtBirthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mDatePickerDiaogBirthDate.show();
                }else{
                    //nothing to do here
                }
            }
        });
        edtJoinedOn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mDatePickerDialogJoinedDate.show();
                }else{
                    //nothing to do here
                }
            }
        });


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
            case CODE_STUDENT_DETAILS:

                final Gson gson = new Gson();
                try {
                    modelStudent = gson
                            .fromJson(response, ModelStudentDetailsResponse.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        setStudentData(modelStudent);
                        //playSoundForAttendance("Student detailed fetched successfully.", StudentDetailsActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setStudentData(ModelStudentDetailsResponse modelStudent) {
        getSupportActionBar().setTitle("" + modelStudent.getStudentDetail().getFname() + " " +
                modelStudent.getStudentDetail().getLname());

        CourseListStudentProfileAdapter adapter=new CourseListStudentProfileAdapter(modelStudent.getStudentDetail().getCourses());
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCourseList.setLayoutManager(manager);
        rvCourseList.setAdapter(adapter);

        edtName.setText("" + modelStudent.getStudentDetail().getFname() + " " +
                modelStudent.getStudentDetail().getLname());
        edtMobileNo.setText("" + modelStudent.getStudentDetail().getMobileno());
        edtEmail.setText("" + modelStudent.getStudentDetail().getEmail());
        edtBirthDate.setText("" + modelStudent.getStudentDetail().getDob());
        edtBranch.setText("" + modelStudent.getStudentDetail().getBranch().getName());
        edtParentName.setText("" + modelStudent.getStudentDetail().getParentname());
        edtParentMobile.setText("" + modelStudent.getStudentDetail().getParentmobileno());
        edtAddress.setText("" + modelStudent.getStudentDetail().getAddress());
        edtReference.setText("" + modelStudent.getStudentDetail().getReference());
        edtJoinedOn.setText("" + modelStudent.getStudentDetail().getJoiningDate());
        //Add Cours Details here

        CommonUtil.setCircularImageToGlide(StudentDetailsActivity.this, ivStudentPic, modelStudent.getStudentDetail().getImage());
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable
            error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
        }
    }

    @Override
    public void onCourseRawClick(int position) {

    }

    @Override
    public void onDeleteCourse(int position) {

    }
}
