package com.globalitians.employees.faculty.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.adapters.CourseListAdapter;
import com.globalitians.employees.courses.model.ModelCourseList;
import com.globalitians.employees.customviews.CustomEditText;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.dashboard.activities.DashboardEmployeeActivity;
import com.globalitians.employees.employee.activities.AddEmployeeActivity;
import com.globalitians.employees.faculty.adapter.StandardListAdapter;
import com.globalitians.employees.faculty.model.FacultyDetailsModel;
import com.globalitians.employees.faculty.model.StandardListModel;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.PermissionManager;
import com.globalitians.employees.utility.Permissions;
import com.globalitians.employees.utility.PhotoPicker;
import com.globalitians.employees.utility.PhotoPickerCallback;
import com.globalitians.employees.utility.ResourceUtils;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_IMAGE;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_PDF;
import static com.globalitians.employees.utility.Constants.CODE_ADD_FACULTY;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.CODE_COURSE_SELECTION_FOR_ADD_STUDENT;
import static com.globalitians.employees.utility.Constants.CODE_FACULTY_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_STANDARD_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.WS_COURSE_LIST;
import static com.globalitians.employees.utility.Constants.WS_STANDARD_LIST;

public class AddNewFacultyActivity extends AppCompatActivity
        implements OkHttpInterface,
        CourseListAdapter.OnCourseListListener,
        StandardListAdapter.OnStandardClickListner, View.OnClickListener {

    private CustomTextView mTvAddNewFaculty;
    private CircularImageView mIvfacultyimage;
    private CustomEditText mEdtname;
    private CustomEditText mEdtmobileno;
    private CustomEditText mEdtemail;
    private CustomEditText mEdtbirthdate;
    private CustomEditText mEdtjoinigdate;
    private CustomEditText mEdtaddress;
    private CustomEditText mEdtjobtype;
    private CustomEditText mEdtdesignation;
    private CustomEditText mEdtsalary;
    private CustomEditText mEdtusername;
    private CustomEditText mEdtpassword;
    private CustomTextView mTxtpwd;
    private CustomTextView mTxtusername;
    private TextView tvCourses;
    private TextView tvStandards;
    private CustomEditText textInputOtherCourse;

    private DatePickerDialog mDatePickerDiaogBirthDate = null;
    private DatePickerDialog mDatePickerDialogJoinedDate = null;
    private Dialog dialogCourses = null;
    private Dialog dialogStandards = null;

    private ArrayList<ModelCourseList.Course> mArrListCourses = null;
    private ArrayList<ModelCourseList.Course> mArrListCoursesSelected = null;
    private ArrayList<StandardListModel.Standard> mArrListStandards=null;
    private ArrayList<StandardListModel.Standard> mArrListStandardSelected = null;

    private FacultyDetailsModel facultyDetailsModel;
    private String editedBy="";
    private String strSelectedCourses="";
    private String strSelectedStandards="";
    private CourseListAdapter mAdapterCourseList;
    private StandardListAdapter mAdapterStandardList;
    private String mStrfacultyid = "";

    private static String ATTACHMENT_TYPE = ""; // IMAGE
    private File mFileSelectedStudentImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFullScreenMode(AddNewFacultyActivity.this);
        setContentView(R.layout.activity_add_new_faculty);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        findViews();
        INIT();
        initDates();
        callApiToLoadCourseList();
        callApiToLoadStandardList();
        getAllDetailsFromFacultydetailsActivity();
    }

    private void callApiToLoadStandardList() {

        if (!CommonUtil.isInternetAvailable(AddNewFacultyActivity.this)) {
            return;
        }

        new OkHttpRequest(AddNewFacultyActivity.this,
                OkHttpRequest.Method.GET,
                WS_STANDARD_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_STANDARD_LIST,
                true, this);
    }

    private void INIT() {
        mArrListCourses = new ArrayList<>();
        mArrListCoursesSelected = new ArrayList<>();
        mArrListStandards = new ArrayList<>();
        mArrListStandardSelected = new ArrayList<>();
    }

    private void callApiToLoadCourseList() {

        if (!CommonUtil.isInternetAvailable(AddNewFacultyActivity.this)) {
            return;
        }

        new OkHttpRequest(AddNewFacultyActivity.this,
                OkHttpRequest.Method.GET,
                WS_COURSE_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_COURSE_LIST,
                true, this);
    }

    private void getAllDetailsFromFacultydetailsActivity() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrfacultyid = intent.getStringExtra("modelFaculty");
            if(!CommonUtil.isNullString(""+mStrfacultyid)){
                callApiToLoadFacultyDetails(""+mStrfacultyid);
                getSupportActionBar().setTitle("Edit Details");
                mTvAddNewFaculty.setText("Update");
            }
        }
    }

    private void callApiToLoadFacultyDetails(String facultyId) {
        if(!CommonUtil.isInternetAvailable(AddNewFacultyActivity.this)){
            return;
        }
        new OkHttpRequest(AddNewFacultyActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_FACULTY_DETAILS,
                RequestParam.getFacultyDetails(""+mStrfacultyid),
                RequestParam.getNull(),
                CODE_FACULTY_DETAILS,
                true,this);
    }

    private void initDates() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener birthdateListener;
        birthdateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEdtbirthdate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        DatePickerDialog.OnDateSetListener joinedDateListener;
        joinedDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mEdtjoinigdate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        };

        mDatePickerDiaogBirthDate = new DatePickerDialog(
                AddNewFacultyActivity.this, birthdateListener, year, month, day);
        mDatePickerDiaogBirthDate.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        //initializing date filter date picker dialog
        mDatePickerDialogJoinedDate = new DatePickerDialog(
                AddNewFacultyActivity.this, joinedDateListener, year, month, day);

    }

    private void findViews() {
        mTvAddNewFaculty=findViewById(R.id.tv_add_new_faculty);

        mIvfacultyimage = findViewById(R.id.iv_faculty_image_pick);

        mEdtname = findViewById(R.id.edt_name);
        mEdtmobileno = findViewById(R.id.edt_mobileno);
        mEdtusername = findViewById(R.id.edt_username);
        mEdtpassword = findViewById(R.id.edt_password);
        mEdtbirthdate = findViewById(R.id.edt_birthDate);
        mEdtemail = findViewById(R.id.edt_email);
        mEdtaddress = findViewById(R.id.edt_address);
        mEdtjobtype = findViewById(R.id.edt_jobtype);
        mEdtdesignation = findViewById(R.id.edt_designation);
        mEdtjoinigdate = findViewById(R.id.edt_joiningDate);
        mEdtsalary = findViewById(R.id.edt_salary);
        tvCourses = findViewById(R.id.edtCourses);
        tvStandards = findViewById(R.id.edtStandards);
        textInputOtherCourse = findViewById(R.id.edt_other_course);
        mTxtpwd = findViewById(R.id.tv_pwd);
        mTxtusername = findViewById(R.id.tv_username);

        mEdtbirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDiaogBirthDate.show();
            }
        });

        mEdtjoinigdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialogJoinedDate.show();
            }
        });

        mTvAddNewFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(AddNewFacultyActivity.this, "In Progress", Toast.LENGTH_SHORT).show();*/
                validateFirstData();
            }
        });

        mIvfacultyimage.setOnClickListener(this);
    }

    private void validateFirstData() {

        if(!CommonUtil.isInternetAvailable(AddNewFacultyActivity.this)){
            Toast.makeText(this, "Internet is not available", Toast.LENGTH_SHORT).show();
        }

        if(CommonUtil.isNullString(""+mEdtname.getText().toString().trim())){
            playSoundForAttendance("Please Enter Faculty Name", AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Faculty Name", Toast.LENGTH_SHORT).show();
        }else if(mEdtname.getText().toString().length()<2) {
            playSoundForAttendance("Name must be grater than 2 characters",AddNewFacultyActivity.this);
            Toast.makeText(this, "Name must be grater than 2 characters", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtmobileno.getText().toString())){
            playSoundForAttendance("Please Enter Your email", AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Your email", Toast.LENGTH_SHORT).show();
        }else if(mEdtmobileno.getText().toString().length()<10){
            playSoundForAttendance("Mobile number must contains 10 digits only",AddNewFacultyActivity.this);
            Toast.makeText(this, "Mobile number must contains 10 digits only", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtemail.getText().toString())){
            playSoundForAttendance("Please Enter Your email", AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Your email", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtbirthdate.getText().toString())){
            playSoundForAttendance("Please Enter Your birthDate", AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Your birthDate", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtaddress.getText().toString())){
            playSoundForAttendance("Please Enter Your address", AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Your address", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtjobtype.getText().toString())){
            playSoundForAttendance("Please Enter Job type",AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Job type", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtjoinigdate.getText().toString())){
            playSoundForAttendance("Please Enter joining date",AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter joining date", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtdesignation.getText().toString())){
            playSoundForAttendance("Please Enter Faculty Designation",AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Faculty Designation", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(""+mEdtsalary.getText().toString())){
            playSoundForAttendance("Please Enter Faculty Salary",AddNewFacultyActivity.this);
            Toast.makeText(this, "Please Enter Faculty Salary", Toast.LENGTH_SHORT).show();
        }else{
            callApiTovalidateAndAddFaculty();
        }
    }

    private void callApiTovalidateAndAddFaculty() {

        if(!CommonUtil.isInternetAvailable(AddNewFacultyActivity.this)){
            return;
        }

        for (int i = 0; i < mArrListCoursesSelected.size(); i++) {
            if (CommonUtil.isNullString(strSelectedCourses)) {
                strSelectedCourses = "" + mArrListCoursesSelected.get(i).getId();
            } else {
                strSelectedCourses = strSelectedCourses + "," + mArrListCoursesSelected.get(i).getId();
            }
        }

        for(int i = 0 ; i < mArrListStandardSelected.size() ; i++){
            if(CommonUtil.isNullString(strSelectedStandards)){
                strSelectedStandards = ""+mArrListStandardSelected.get(i).getId();
            }else{
                strSelectedStandards = strSelectedStandards + "," + mArrListStandardSelected.get(i).getId();
            }
        }

        if(CommonUtil.isNullString(""+mStrfacultyid)){
            new OkHttpRequest(AddNewFacultyActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_FACULTY,
                    RequestParam.addFaculty(""+CommonUtil.getSharedPrefrencesInstance(AddNewFacultyActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                            ""+mEdtname.getText().toString().trim(),
                            ""+mEdtusername.getText().toString().trim(),
                            ""+mEdtpassword.getText().toString().trim(),
                            ""+mEdtbirthdate.getText().toString().trim(),
                            ""+mEdtaddress.getText().toString().trim(),
                            ""+mEdtjobtype.getText().toString().trim(),
                            ""+mEdtsalary.getText().toString().trim(),
                            ""+mEdtmobileno.getText().toString().trim(),
                            ""+editedBy,
                            ""+mEdtjoinigdate.getText().toString().trim(),
                            ""+mEdtemail.getText().toString().trim(),
                            ""+mEdtdesignation.getText().toString().trim(),
                            ""+strSelectedCourses,
                            ""+strSelectedStandards,
                            ""),
                    RequestParam.getNull(),
                    CODE_ADD_FACULTY,
                    true, this);
        }else{
            new OkHttpRequest(AddNewFacultyActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_FACULTY,
                    RequestParam.addFaculty(""+CommonUtil.getSharedPrefrencesInstance(AddNewFacultyActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                            ""+mEdtname.getText().toString().trim(),
                            "",
                            "",
                            ""+mEdtbirthdate.getText().toString().trim(),
                            ""+mEdtaddress.getText().toString().trim(),
                            ""+mEdtjobtype.getText().toString().trim(),
                            ""+mEdtsalary.getText().toString().trim(),
                            ""+mEdtmobileno.getText().toString().trim(),
                            ""+editedBy,
                            ""+mEdtjoinigdate.getText().toString().trim(),
                            ""+mEdtemail.getText().toString().trim(),
                            ""+mEdtdesignation.getText().toString().trim(),
                            ""+strSelectedCourses,
                            ""+strSelectedStandards,
                            ""+mStrfacultyid),
                    RequestParam.getNull(),
                    CODE_ADD_FACULTY,
                    true, this);
        }

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

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e("add faculty",""+response);

        if(response==null){
            return;
        }

        switch (requestId){
            case CODE_STANDARD_LIST:
                Log.e("standards",""+response);
                final Gson standardListgson = new Gson();
                try{
                    StandardListModel standardListModel = standardListgson.fromJson(response,StandardListModel.class);
                    if(standardListModel.getStatus().equals(Constants.SUCCESS_CODE)){
                        mArrListStandards = standardListModel.getStandards();
                        if(mArrListStandards != null && mArrListStandards.size()>0){
                            setStandardListAdapter();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_COURSE_LIST:
                final Gson courseListGson = new Gson();
                try {
                    ModelCourseList modelCourseList = courseListGson
                            .fromJson(response, ModelCourseList.class);

                    if (modelCourseList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListCourses = modelCourseList.getCourses();

                        if (mArrListCourses != null && mArrListCourses.size() > 0) {
                            setCoursesListAdapter();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_ADD_FACULTY:
                Log.e(">>>",""+response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("success")){
                        startActivity(new Intent(AddNewFacultyActivity.this, DashboardEmployeeActivity.class));
                    }
                    Toast.makeText(this, ""+jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_FACULTY_DETAILS:
                Log.e("Faculty Details",""+response);
                final Gson gson = new Gson();
                try{
                    facultyDetailsModel = gson.fromJson(response,FacultyDetailsModel.class);
                    if(facultyDetailsModel.getFacultyDetail()!=null){
                        setFacultyDetails(facultyDetailsModel);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setFacultyDetails(FacultyDetailsModel facultyDetailsModel) {

        tvCourses.setText("");
        strSelectedCourses = "";

        for (int i = 0; i < facultyDetailsModel.getFacultyDetail().getCourses().size() ; i++) {
            for (int j = 0; j < mArrListCourses.size(); j++) {
                if (facultyDetailsModel.getFacultyDetail().getCourses().get(i).getId().equals(mArrListCourses.get(j).getId())) {
                    mArrListCourses.get(j).setSelected(true);
                    //set Selected Values...
                    if (tvCourses.getText().toString().length() > 0) {
                        tvCourses.append(", " + mArrListCourses.get(j).getName());
                        strSelectedCourses = strSelectedCourses + "," + mArrListCourses.get(j).getId();
                    } else {
                        tvCourses.setText(mArrListCourses.get(j).getName());
                        strSelectedCourses = "" + mArrListCourses.get(j).getId();
                    }
                }
            }
        }

        tvStandards.setText("");
        strSelectedStandards="";
        for(int i = 0 ; i < facultyDetailsModel.getFacultyDetail().getStandards().size() ; i++){
            for(int j = 0 ; j < mArrListStandards.size() ; j++){
                if(facultyDetailsModel.getFacultyDetail().getStandards().get(i).getId().equals(mArrListStandards.get(j).getId())){
                    mArrListStandards.get(j).setSelected(true);
                    //set selected values of standards...
                    if(tvStandards.getText().toString().length()>0){
                        tvStandards.append(", "+mArrListStandards.get(j).getName());
                        strSelectedStandards = strSelectedStandards + "," + mArrListStandards.get(j).getId();
                    }else{
                        tvStandards.setText(mArrListStandards.get(j).getName());
                        strSelectedStandards = ""+mArrListStandards.get(j).getId();
                    }
                }
            }
        }

        if(!CommonUtil.isNullString(""+facultyDetailsModel.getFacultyDetail().getImage())){
            CommonUtil.setCircularImageToGlide(AddNewFacultyActivity.this,mIvfacultyimage,""+facultyDetailsModel.getFacultyDetail().getImage());
        }

        mEdtname.setText(""+facultyDetailsModel.getFacultyDetail().getName());
        mEdtsalary.setText(""+facultyDetailsModel.getFacultyDetail().getSalary());
        mEdtemail.setText(""+facultyDetailsModel.getFacultyDetail().getEmail());
        mEdtaddress.setText(""+facultyDetailsModel.getFacultyDetail().getAddress());
        mEdtbirthdate.setText(""+facultyDetailsModel.getFacultyDetail().getDob());
        mEdtjoinigdate.setText(""+facultyDetailsModel.getFacultyDetail().getJoiningDate());
        mEdtjobtype.setText(""+facultyDetailsModel.getFacultyDetail().getJobType());
        mEdtdesignation.setText(""+facultyDetailsModel.getFacultyDetail().getDesignation());
        mEdtmobileno.setText(""+facultyDetailsModel.getFacultyDetail().getContactNo());
        mEdtusername.setText(""+facultyDetailsModel.getFacultyDetail().getUsername());

        mEdtusername.setVisibility(View.GONE);
        mTxtusername.setVisibility(View.GONE);
        mEdtpassword.setVisibility(View.GONE);
        mTxtpwd.setVisibility(View.GONE);

        editedBy = ""+facultyDetailsModel.getFacultyDetail().getEditedBy();
        mStrfacultyid = ""+facultyDetailsModel.getFacultyDetail().getId();
    }

    private void setStandardListAdapter() {
        dialogStandards = new Dialog(AddNewFacultyActivity.this);
        dialogStandards.setCancelable(true);
        dialogStandards.setContentView(R.layout.dialog_standards);

        ListView lvStandards = dialogStandards.findViewById(R.id.lv_standards);
        TextView tvSave = dialogStandards.findViewById(R.id.tvSave);
        TextView tvCancel = dialogStandards.findViewById(R.id.tvCancel);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvStandards.setText("");
                strSelectedStandards = "";

                for (int i = 0; i < mArrListStandards.size(); i++) {
                    if (mArrListStandards.get(i).isSelected()) {
                        mArrListStandardSelected.add(mArrListStandards.get(i));
                    }
                }

                for (int i = 0; i < mArrListStandardSelected.size(); i++) {
                    if (tvStandards.getText().toString().length() > 0) {
                        tvStandards.append(", " + mArrListStandardSelected.get(i).getName());
                    } else {
                        tvStandards.setText(mArrListStandardSelected.get(i).getName());
                    }
                }

                dialogStandards.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogStandards.dismiss();
            }
        });

        mAdapterStandardList = new StandardListAdapter(this, mArrListStandards,this,"add_standard");
        lvStandards.setAdapter(mAdapterStandardList);

        tvStandards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrListStandardSelected != null && mArrListStandardSelected.size() > 0) {
                    mArrListStandardSelected.clear();
                }
                dialogStandards.show();
            }
        });
    }

    private void setCoursesListAdapter() {
        dialogCourses = new Dialog(AddNewFacultyActivity.this);
        dialogCourses.setCancelable(true);
        dialogCourses.setContentView(R.layout.dialog_courses);

        ListView lvCourses = dialogCourses.findViewById(R.id.lv_courses);
        TextView tvSave = dialogCourses.findViewById(R.id.tvSave);
        TextView tvCancel = dialogCourses.findViewById(R.id.tvCancel);

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvCourses.setText("");
                strSelectedCourses = "";

                for (int i = 0; i < mArrListCourses.size(); i++) {
                    if (mArrListCourses.get(i).isSelected()) {
                        mArrListCoursesSelected.add(mArrListCourses.get(i));
                    }
                }

                for (int i = 0; i < mArrListCoursesSelected.size(); i++) {
                    if (tvCourses.getText().toString().length() > 0) {
                        tvCourses.append(", " + mArrListCoursesSelected.get(i).getName());
                    } else {
                        tvCourses.setText(mArrListCoursesSelected.get(i).getName());
                    }
                }
                dialogCourses.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCourses.dismiss();
            }
        });

        mAdapterCourseList = new CourseListAdapter(this, mArrListCourses, this, "add_inquiry");
        lvCourses.setAdapter(mAdapterCourseList);

        tvCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mArrListCoursesSelected != null && mArrListCoursesSelected.size() > 0) {
                    mArrListCoursesSelected.clear();
                }
                dialogCourses.show();
            }
        });
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onCourseRawClick(int position) {
        if (mArrListCourses.get(position).getName().equalsIgnoreCase("--Other--")) {
            dialogCourses.dismiss();
            textInputOtherCourse.setVisibility(View.VISIBLE);
        } else {
            textInputOtherCourse.setVisibility(View.GONE);
        }

        if (mArrListCourses.get(position).isSelected()) {
            mArrListCourses.get(position).setSelected(false);
        } else {
            mArrListCourses.get(position).setSelected(true);
        }
    }

    @Override
    public void onDeleteCourse(int position) {

    }

    @Override
    public void onStandardRawClick(int position) {
        if (mArrListStandards.get(position).getName().equalsIgnoreCase("--Other--")) {
            dialogStandards.dismiss();
        } else {
            // do nothing
        }

        if (mArrListStandards.get(position).isSelected()) {
            mArrListStandards.get(position).setSelected(false);
        } else {
            mArrListStandards.get(position).setSelected(true);
        }
    }

    @Override
    public void onDeleteStandard(int position) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_faculty_image_pick:
                showDialogToSelectUploadOption();
                break;
        }
    }

    private void showDialogToSelectUploadOption() {
        ATTACHMENT_TYPE = ATTACHMENT_IMAGE;
        askForPermission();
    }

    private void askForPermission() {
        if (PermissionManager.hasPermissions(AddNewFacultyActivity.this, Permissions.STORAGE_PERMISSIONS)) {
            showChooser();
        } else {
            PermissionManager.requestPermissions(AddNewFacultyActivity.this, Constants.CODE_RUNTIME_STORAGE_PERMISSION,
                    permissionListener, "", Permissions.STORAGE_PERMISSIONS);
        }
    }

    private final PermissionManager.PermissionListener permissionListener = new PermissionManager.PermissionListener() {
        @Override
        public void onPermissionsGranted(List<String> perms) {
            if (perms.size() == Permissions.STORAGE_PERMISSIONS.length) {
                updateImage();
            } else {
                LogUtil.i("Add Course Activity >>", "User denied some of required permissions! "
                        + "Even though we have following permissions now, "
                        + "task will still be aborted.\n" + CommonUtil.getStringFromList(perms));
            }
        }

        @Override
        public void onPermissionsDenied(List<String> perms) {
        }

        @Override
        public void onPermissionRequestRejected() {
        }

        @Override
        public void onPermissionNeverAsked(List<String> perms) {
        }

    };

    private void updateImage() {
        PhotoPicker.configuration(AddNewFacultyActivity.this)
                .setImagesFolderName(Constants.DIRECTORY_GIT_MEDIA);

        PhotoPicker.openChooserWithGallery(AddNewFacultyActivity.this, "" + ResourceUtils.getString(R.string.select_picture_from), 0);
    }

    private void showChooser() {
        if (ATTACHMENT_TYPE.equals(ATTACHMENT_IMAGE)) { // User selected image option
            PhotoPicker.configuration(AddNewFacultyActivity.this).setImagesFolderName(Constants.DIRECTORY_GIT_MEDIA);
            PhotoPicker.openChooserWithGallery(AddNewFacultyActivity.this, ResourceUtils.getString(R.string.select_picture_from), PhotoPicker.SELECT_PHOTO);
        } else if (ATTACHMENT_TYPE.equals(ATTACHMENT_PDF)) {
            playSoundForAttendance("This feature is in development mode. It will be available soon", AddNewFacultyActivity.this);
            Toast.makeText(this, "In Progress", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == CODE_COURSE_SELECTION_FOR_ADD_STUDENT) {
            //do nothing
        }

        PhotoPicker.handleActivityResult(requestCode, resultCode, data, AddNewFacultyActivity.this, new PhotoPickerCallback() {
            @Override
            public void onImagePickerError(Exception e, PhotoPicker.ImageSource source, int type) {
                //Some error handling
                Toast.makeText(AddNewFacultyActivity.this, "Some Error is there", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, PhotoPicker.ImageSource source, int type, int absoluteType) {
                if (imageFiles.size() > 0) {
                    onPhotosReturned(imageFiles.get(0), type, absoluteType);
                }
            }
        });
    }

    private void onPhotosReturned(File returnedFile, int type, int absoluteType) {
        if (type == PhotoPicker.SELECT_PHOTO) {
            mFileSelectedStudentImage = returnedFile;
            mIvfacultyimage.setImageURI(Uri.fromFile(mFileSelectedStudentImage));
        }
        System.gc();
    }
}
