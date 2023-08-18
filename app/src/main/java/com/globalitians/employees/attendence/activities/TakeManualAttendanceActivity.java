package com.globalitians.employees.attendence.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.attendence.model.ModelPostAttendance;
import com.globalitians.employees.students.activities.StudentListActivity;
import com.globalitians.employees.students.models.ModelStudentDetailsResponse;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import java.util.Date;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_POST_ATTENDENCE;
import static com.globalitians.employees.utility.Constants.CODE_STUDENT_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_STUDENT_SELECTION_FOR_ATTENDANCE;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_COURSE_NAME;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_ID;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_IMAGE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_INOUT;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_STUDENT_NAME;
import static com.globalitians.employees.utility.Constants.KEY_STUDENT_SELECTION;

public class TakeManualAttendanceActivity extends AppCompatActivity implements View.OnClickListener, OkHttpInterface {

    private EditText edtStudentId;
    private TextView tvSearchStudent;
    private ImageView ivScanQR;
    private String strSelectedCourseId = "";
    private String strSelectedCourseName = "";
    private String strSelectedStudentId = "";
    private String strSelectedStudentImage = "";
    private String strSelectedStudentName = "";
    private TextToSpeech textToSpeechAttendance;
    private TextView tvTakeAttendance;
    private String strInOutValue = "";
    private TextView tvDialogCurrentCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(TakeManualAttendanceActivity.this);
        setContentView(R.layout.activity_take_manual_attendance);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        playSoundForAttendance("G I T  Attendance", TakeManualAttendanceActivity.this);
    }

    private void findViews() {
        edtStudentId = findViewById(R.id.edt_student_id);
        tvSearchStudent = findViewById(R.id.tvSearch);
        tvSearchStudent.setOnClickListener(this);
        //tvSearchStudent.setText(Html.fromHtml(getResources().getString(R.string.forgotStudentId)));
        tvTakeAttendance = findViewById(R.id.tvTakeAttendance);
        tvTakeAttendance.setOnClickListener(this);
        ivScanQR=findViewById(R.id.iv_scan);
        ivScanQR.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTakeAttendance:
                if(!CommonUtil.isNullString(strSelectedStudentId)){
                    if(strSelectedStudentId.equals(edtStudentId.getText().toString().trim())){
                        validateAndPostAttendance();
                    }else{
                        callApiToLoadStudentDetails(edtStudentId.getText().toString().trim());
                    }
                }else{
                    callApiToLoadStudentDetails(edtStudentId.getText().toString().trim());
                }
                break;

            case R.id.tvSearch:
                startActivityForResult(new Intent(TakeManualAttendanceActivity.this,
                                StudentListActivity.class)
                                .putExtra(KEY_STUDENT_SELECTION, "")
                        , CODE_STUDENT_SELECTION_FOR_ATTENDANCE);
                break;
            case R.id.iv_scan:
                startActivity(new Intent(TakeManualAttendanceActivity.this,
                        TakeStudentAttendenceActivity.class));
                break;
        }
    }

    private void validateAndPostAttendance() {
        String studentId = "";
        if (CommonUtil.isNullString("" + edtStudentId.getText().toString())) {
            Toast.makeText(this, "Enter Student Id", Toast.LENGTH_SHORT).show();
            playSoundForAttendance("Please enter Student ID.", TakeManualAttendanceActivity.this);
            return;
        } else {
            studentId = "" + edtStudentId.getText().toString().trim();
            if (strSelectedStudentName != null && !CommonUtil.isNullString(strSelectedStudentName)) {
                displayStudentConfirmationDialog(strSelectedStudentName, strSelectedStudentImage, strSelectedCourseName);
            } else {
                callApiToLoadStudentDetails("" + studentId.trim());
            }
        }
    }

    private void callApiToLoadStudentDetails(String studentId) {
        if (!CommonUtil.isInternetAvailable(TakeManualAttendanceActivity.this)) {
            playSoundForAttendance("No Internet Connection. Please check your Internet connection.", TakeManualAttendanceActivity.this);
            return;
        }

        new OkHttpRequest(TakeManualAttendanceActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_DETAILS,
                RequestParam.studentDetails(studentId,
                        ""+getSharedPrefrencesInstance(TakeManualAttendanceActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_STUDENT_DETAILS,
                true, this);
    }


    private void callApiToMakeAttendance(String studentId, String courseId, String inOutValue) {
        if (!CommonUtil.isInternetAvailable(TakeManualAttendanceActivity.this)) {
            playSoundForAttendance("No Internet Connection. Please check your Internet connection.", TakeManualAttendanceActivity.this);
            return;
        }

        new OkHttpRequest(TakeManualAttendanceActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_POST_ATTENDENCE,
                RequestParam.postAttendance("" + studentId,
                        "" + inOutValue,
                        ""+getSharedPrefrencesInstance(TakeManualAttendanceActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_POST_ATTENDENCE,
                true, this);
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e(">> response comes >>", "" + response);

        if (response == null) {
            playSoundForAttendance("Could not get any response. Please try again.", TakeManualAttendanceActivity.this);
            response = "null";
            return;
        }
        switch (requestId) {
            case CODE_POST_ATTENDENCE:
                final Gson postAttendance = new Gson();
                try {
                    ModelPostAttendance modelPostAttendance = postAttendance
                            .fromJson(response, ModelPostAttendance.class);

                    if (modelPostAttendance.getStatus().equals(Constants.SUCCESS_CODE)) {
                        //setStudentData(modelPostAttendance);
                        playSoundForAttendance("Attendance Successful.", TakeManualAttendanceActivity.this);
                        Toast.makeText(this, "Attendance Successful.", Toast.LENGTH_SHORT).show();
                        clearSelections();
                    } else {
                        Toast.makeText(this, "" + modelPostAttendance.getMessage(), Toast.LENGTH_SHORT).show();
                        playSoundForAttendance("" + modelPostAttendance.getMessage() + ".   Please Try Again.", TakeManualAttendanceActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    playSoundForAttendance("Exception. Please Try Again.", TakeManualAttendanceActivity.this);
                }
                break;
            case CODE_STUDENT_DETAILS:
                final Gson gson = new Gson();
                try {
                    ModelStudentDetailsResponse modelStudent = gson
                            .fromJson(response, ModelStudentDetailsResponse.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        strInOutValue=""+modelStudent.getStudentDetail().getIn_out_status().toLowerCase();
                        strSelectedStudentName = "" + modelStudent.getStudentDetail().getFname()
                                + " " +
                                modelStudent.getStudentDetail().getLname();
                        strSelectedStudentImage = "" + modelStudent.getStudentDetail().getImage();
                        strSelectedCourseName = "" + modelStudent.getStudentDetail().getCourses().get(0).getName();
                        displayStudentConfirmationDialog(strSelectedStudentName, strSelectedStudentImage, strSelectedCourseName);
                    } else {
                        Toast.makeText(this, "Could not get response.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                        playSoundForAttendance("Please Try Again.", TakeManualAttendanceActivity.this);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Student details not found.", Toast.LENGTH_SHORT).show();
                    playSoundForAttendance("Please Try Again.", TakeManualAttendanceActivity.this);
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {
        playSoundForAttendance("Networking Failure Occured. Try again", TakeManualAttendanceActivity.this);
    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    private void setStudentData(ModelPostAttendance modelPostAttendance) {

        final Dialog dialogAttendence = new Dialog(TakeManualAttendanceActivity.this);
        dialogAttendence.setContentView(R.layout.dialog_student_attendence);
        dialogAttendence.setCancelable(false);

        TextView tvStudentId;
        TextView tvStudentName;
        TextView tvCurrentCourse;
        TextView tvBranchName;
        TextView tvDateTime;
        TextView tvIn;
        TextView tvOut;
        CircularImageView ivStudentImage;
        Button btnDismiss;

        tvStudentId = (TextView) dialogAttendence.findViewById(R.id.tvStudentId);
        tvStudentName = (TextView) dialogAttendence.findViewById(R.id.tvStudentName);
        tvCurrentCourse = (TextView) dialogAttendence.findViewById(R.id.tvCurrentCourse);
        tvBranchName = (TextView) dialogAttendence.findViewById(R.id.tvBranchName);
        tvDateTime = (TextView) dialogAttendence.findViewById(R.id.tvDateTime);
        btnDismiss = (Button) dialogAttendence.findViewById(R.id.btnConfirm);
        ivStudentImage = dialogAttendence.findViewById(R.id.iv_student);

        if (!CommonUtil.isNullString(modelPostAttendance.getStudentDetail().getStudentImage())) {
            CommonUtil.setImageToGlide(TakeManualAttendanceActivity.this, ivStudentImage, modelPostAttendance.getStudentDetail().getStudentImage());
        }

        tvStudentId.setText("STUDENT ID: " + modelPostAttendance.getStudentDetail().getStudentId());
        tvBranchName.setText("");

        /*Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        String formattedDate = df.format(c);*/

        tvDateTime.setText("Attendance Time: " + modelPostAttendance.getStudentDetail().getDateTime());
        tvCurrentCourse.setText("Studying:" + modelPostAttendance.getStudentDetail().getCourseName());
        tvStudentName.setText("" + modelPostAttendance.getStudentDetail().getStudentName());


        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogAttendence != null && dialogAttendence.isShowing()) {
                    dialogAttendence.dismiss();
                }
            }
        });

        if (dialogAttendence != null && !dialogAttendence.isShowing()) {
            dialogAttendence.show();
        }

        clearSelections();

    }

    private void displayStudentConfirmationDialog(String studentName, String studentImage, String studentCourse) {

        final Dialog dialogAttendence = new Dialog(TakeManualAttendanceActivity.this);
        dialogAttendence.setContentView(R.layout.dialog_student_attendence);
        dialogAttendence.setCancelable(false);

        TextView tvStudentId;
        TextView tvStudentName;
        TextView tvBranchName;
        TextView tvDateTime;
        CircularImageView ivStudentImage;
        Button btnConfirm;
        Button btnCancel;

        tvStudentId = (TextView) dialogAttendence.findViewById(R.id.tvStudentId);
        tvStudentName = (TextView) dialogAttendence.findViewById(R.id.tvStudentName);
        tvDialogCurrentCourse = (TextView) dialogAttendence.findViewById(R.id.tvCurrentCourse);
        tvBranchName = (TextView) dialogAttendence.findViewById(R.id.tvBranchName);
        tvDateTime = (TextView) dialogAttendence.findViewById(R.id.tvDateTime);
        btnConfirm = (Button) dialogAttendence.findViewById(R.id.btnConfirm);
        btnCancel = (Button) dialogAttendence.findViewById(R.id.btnCancel);
        ivStudentImage = dialogAttendence.findViewById(R.id.iv_student);

        tvStudentId.setVisibility(View.GONE);
        tvBranchName.setVisibility(View.GONE);

        tvDateTime.setVisibility(View.VISIBLE);
        Date d=new Date();
        tvDateTime.setText("Time: "+d.getHours()+":"+d.getMinutes());
        tvDialogCurrentCourse.setText("" + studentCourse);
        tvStudentName.setText("" + studentName);

        if (!CommonUtil.isNullString(studentImage)) {
            CommonUtil.setCircularImageForUser(TakeManualAttendanceActivity.this, ivStudentImage, studentImage);
        }


        if (strInOutValue.equalsIgnoreCase("Out")) {
            btnConfirm.setText("In");
            btnConfirm.setTextColor(Color.BLACK);
            btnConfirm.setBackgroundColor(getResources().getColor(R.color.colorGreenIn));
        } else {
            btnConfirm.setText("Out");
            btnConfirm.setTextColor(Color.WHITE);
            btnConfirm.setBackgroundColor(getResources().getColor(R.color.colorRedOut));
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogAttendence != null && dialogAttendence.isShowing()) {
                    dialogAttendence.dismiss();
                }
                callApiToMakeAttendance("" + edtStudentId.getText().toString().trim(), "" + strSelectedCourseId, "" + strInOutValue);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelections();
                if (dialogAttendence != null && dialogAttendence.isShowing()) {
                    dialogAttendence.dismiss();
                }
            }
        });

        if (dialogAttendence != null && !dialogAttendence.isShowing()) {
            dialogAttendence.show();
            playSoundForAttendance("" + studentName, TakeManualAttendanceActivity.this);
        }
    }

    private void clearSelections() {
        edtStudentId.setText("");
        strSelectedStudentId = "";
        strSelectedStudentName = "";
        strSelectedCourseName = "";
        strSelectedCourseId = "";
        strInOutValue = "";
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == CODE_STUDENT_SELECTION_FOR_ATTENDANCE) {
            if (data.getStringExtra(KEY_INTENT_STUDENT_NAME) != null) {
                strSelectedStudentName = "" + data.getStringExtra(KEY_INTENT_STUDENT_NAME);
            }
            if (data.getStringExtra(KEY_INTENT_STUDENT_ID) != null) {
                strSelectedStudentId = "" + data.getStringExtra(KEY_INTENT_STUDENT_ID);
            }
            if (data.getStringExtra(KEY_INTENT_STUDENT_IMAGE) != null) {
                strSelectedStudentImage = "" + data.getStringExtra(KEY_INTENT_STUDENT_IMAGE);
            }
            if (data.getStringExtra(KEY_INTENT_STUDENT_INOUT) != null) {
                strInOutValue = "" + data.getStringExtra(KEY_INTENT_STUDENT_INOUT);
            }
            if (data.getStringExtra(KEY_INTENT_COURSE_NAME) != null) {
                strSelectedCourseName = "" + data.getStringExtra(KEY_INTENT_COURSE_NAME);
            }
            edtStudentId.setText("" + strSelectedStudentId);
            edtStudentId.setSelection(strSelectedStudentId.toString().length());
        }
    }
}
