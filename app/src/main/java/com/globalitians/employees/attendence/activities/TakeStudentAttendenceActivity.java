package com.globalitians.employees.attendence.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.attendence.model.ModelPostAttendance;
import com.globalitians.employees.students.models.ModelStudentDetailsResponse;
import com.globalitians.employees.utility.CircularImageView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;

import java.io.IOException;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_POST_ATTENDENCE;
import static com.globalitians.employees.utility.Constants.CODE_STUDENT_DETAILS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class TakeStudentAttendenceActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener {

    private SurfaceView surfaceView;
    private TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private LinearLayout linScanner;
    private TextView tvScanNow;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private String intentData = "";
    boolean isEmail = false;
    private int attendenceProgress = 0;
    private String strAttendanceStatus = "";

    private Animation animSlideUp;
    private Animation animSlideDown;
    private String strSelectedStudentName = "";
    private String strSelectedStudentImage = "";
    private String strSelectedCourseName = "";
    private String inOutValue = "in";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(TakeStudentAttendenceActivity.this);
        setContentView(R.layout.activity_scan_barcode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    private void initViews() {

        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slideup);
        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidedown);

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        linScanner = findViewById(R.id.linScanner);
        tvScanNow = findViewById(R.id.tvScanAttendance);
        tvScanNow.setOnClickListener(this);
    }

    private void shareTextUrl() {
        if (TextUtils.isEmpty(intentData)) {
            Toast.makeText(this, "Nothing to Share..!", Toast.LENGTH_SHORT).show();
        } else {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, "Medical Helper: Scanned QR Code");
            share.putExtra(Intent.EXTRA_TEXT, "" + intentData);

            startActivity(Intent.createChooser(share, "Share.!"));
        }
    }

    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(TakeStudentAttendenceActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(TakeStudentAttendenceActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(final Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {
                            isEmail = false;
                            intentData = barcodes.valueAt(0).displayValue;
                            txtBarcodeValue.setText("" + intentData);
                            //isTakeAttendance=1;
                            /*if (ActivityCompat.checkSelfPermission(TakeStudentAttendenceActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            try {

                                if (attendenceProgress == 0) {
                                    attendenceProgress = 1;
                                    cameraSource.stop();
                                    if (intentData != null) {

                                        String arrStudentDetails[] = intentData.split("_");
                                        inOutValue = arrStudentDetails[2];
                                        if (arrStudentDetails[2].equalsIgnoreCase("in")) {
                                            arrStudentDetails[2] = "out";
                                        } else {
                                            arrStudentDetails[2] = "in";
                                        }
                                        strAttendanceStatus = arrStudentDetails[2];
                                        callApiToLoadStudentDetails(arrStudentDetails[0]);
                                        //callApiToMakeAttendance(arrStudentDetails[0],arrStudentDetails[2]);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                        }
                    });
                }
            }
        });
    }

    private void callApiToLoadStudentDetails(String studentId) {
        if (!CommonUtil.isInternetAvailable(TakeStudentAttendenceActivity.this)) {
            playSoundForAttendance("No Internet Connection. Please check your Internet connection.", TakeStudentAttendenceActivity.this);
            return;
        }

        new OkHttpRequest(TakeStudentAttendenceActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_STUDENT_DETAILS,
                RequestParam.studentDetails(studentId,
                        ""+getSharedPrefrencesInstance(TakeStudentAttendenceActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_STUDENT_DETAILS,
                true, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;

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
        Log.e(">> response comes >>", "" + response);

        if (response == null) {
            response = "null";
            return;
        }
        switch (requestId) {
            case CODE_POST_ATTENDENCE:
                linScanner.setVisibility(View.GONE);
                tvScanNow.setVisibility(View.VISIBLE);
                final Gson postAttendance = new Gson();
                try {
                    ModelPostAttendance modelPostAttendance = postAttendance
                            .fromJson(response, ModelPostAttendance.class);

                    if (modelPostAttendance.getStatus().equals(Constants.SUCCESS_CODE)) {
                        //setStudentData(modelPostAttendance);
                        playSoundForAttendance("Attendance Successful. Thank You.", TakeStudentAttendenceActivity.this);
                    } else {
                        playSoundForAttendance("" + modelPostAttendance.getMessage() + ". Please Try Again.", TakeStudentAttendenceActivity.this);
                    }
                } catch (Exception e) {
                    playSoundForAttendance("Exception. Please Try again.", TakeStudentAttendenceActivity.this);
                    e.printStackTrace();
                }
                attendenceProgress = 0;
                break;
            case CODE_STUDENT_DETAILS:
                final Gson gson = new Gson();
                try {
                    ModelStudentDetailsResponse modelStudent = gson
                            .fromJson(response, ModelStudentDetailsResponse.class);

                    if (modelStudent.getStatus().equals(Constants.SUCCESS_CODE)) {
                        strSelectedStudentName = "" + modelStudent.getStudentDetail().getFname()
                                + " " +
                                modelStudent.getStudentDetail().getLname();
                        strSelectedStudentImage = "" + modelStudent.getStudentDetail().getImage();
                        strSelectedCourseName = "" + modelStudent.getStudentDetail().getCourses().get(0).getName();
                        displayStudentConfirmationDialog(modelStudent.getStudentDetail().getId(), strSelectedStudentName, strSelectedStudentImage, strSelectedCourseName);
                    } else {
                        Toast.makeText(this, "Could not get response.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                        playSoundForAttendance("Please Try Again.", TakeStudentAttendenceActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    private void displayStudentConfirmationDialog(final int studentId, String studentName, String studentImage, String studentCourse) {

        final Dialog dialogAttendence = new Dialog(TakeStudentAttendenceActivity.this);
        dialogAttendence.setContentView(R.layout.dialog_student_attendence);
        dialogAttendence.setCancelable(false);

        TextView tvStudentId;
        TextView tvStudentName;
        TextView tvCurrentCourse;
        TextView tvBranchName;
        TextView tvDateTime;
        CircularImageView ivStudentImage;
        Button btnConfirm;
        Button btnCancel;

        tvStudentId = (TextView) dialogAttendence.findViewById(R.id.tvStudentId);
        tvStudentName = (TextView) dialogAttendence.findViewById(R.id.tvStudentName);
        tvCurrentCourse = (TextView) dialogAttendence.findViewById(R.id.tvCurrentCourse);
        tvBranchName = (TextView) dialogAttendence.findViewById(R.id.tvBranchName);
        tvDateTime = (TextView) dialogAttendence.findViewById(R.id.tvDateTime);
        btnConfirm = (Button) dialogAttendence.findViewById(R.id.btnConfirm);
        btnCancel = (Button) dialogAttendence.findViewById(R.id.btnCancel);
        ivStudentImage = dialogAttendence.findViewById(R.id.iv_student);

        tvStudentId.setVisibility(View.GONE);
        tvBranchName.setVisibility(View.GONE);

        /*Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        String formattedDate = df.format(c);*/

        tvDateTime.setVisibility(View.GONE);
        tvCurrentCourse.setText("Studying:" + studentCourse);
        tvStudentName.setText("" + studentName);

        if (!CommonUtil.isNullString(studentImage)) {
            CommonUtil.setImageToGlide(TakeStudentAttendenceActivity.this, ivStudentImage, studentImage);
        }


        btnConfirm.setText("" + getResources().getString(R.string.strConfirm));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogAttendence != null && dialogAttendence.isShowing()) {
                    dialogAttendence.dismiss();
                }
                callApiToMakeAttendance("" + studentId, "" + inOutValue);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cameraSource.stop();
                linScanner.setVisibility(View.GONE);
                tvScanNow.setVisibility(View.VISIBLE);
                attendenceProgress = 0;
                if (dialogAttendence != null && dialogAttendence.isShowing()) {
                    dialogAttendence.dismiss();
                }
            }
        });

        if (dialogAttendence != null && !dialogAttendence.isShowing()) {
            dialogAttendence.show();
            playSoundForAttendance("" + studentName, TakeStudentAttendenceActivity.this);
        }
    }

    private void setStudentData(ModelPostAttendance modelPostAttendance) {

        final Dialog dialogAttendence = new Dialog(TakeStudentAttendenceActivity.this);
        dialogAttendence.setContentView(R.layout.dialog_student_attendence);
        dialogAttendence.setCancelable(false);

        LinearLayout linStudentDetails;
        TextView tvStudentId;
        TextView tvStudentName;
        TextView tvCurrentCourse;
        TextView tvBranchName;
        TextView tvDateTime;
        LinearLayout linBottomActions;
        CircularImageView ivStudentImage;
        Button btnDismiss;

        linStudentDetails = (LinearLayout) dialogAttendence.findViewById(R.id.linStudentDetails);
        tvStudentId = (TextView) dialogAttendence.findViewById(R.id.tvStudentId);
        tvStudentName = (TextView) dialogAttendence.findViewById(R.id.tvStudentName);
        tvCurrentCourse = (TextView) dialogAttendence.findViewById(R.id.tvCurrentCourse);
        tvBranchName = (TextView) dialogAttendence.findViewById(R.id.tvBranchName);
        tvDateTime = (TextView) dialogAttendence.findViewById(R.id.tvDateTime);
        linBottomActions = (LinearLayout) dialogAttendence.findViewById(R.id.linBottomActions);
        btnDismiss = (Button) dialogAttendence.findViewById(R.id.btnConfirm);
        ivStudentImage = dialogAttendence.findViewById(R.id.iv_student);

        if (!CommonUtil.isNullString(modelPostAttendance.getStudentDetail().getStudentImage())) {
            CommonUtil.setImageToGlide(TakeStudentAttendenceActivity.this, ivStudentImage, modelPostAttendance.getStudentDetail().getStudentImage());
        }

        tvStudentId.setText("STUDENT ID: " + modelPostAttendance.getStudentDetail().getStudentId());
        tvBranchName.setText("");

        /*Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        String formattedDate = df.format(c);*/

        tvDateTime.setText("Attendance Time: " + modelPostAttendance.getStudentDetail().getDateTime());

        tvCurrentCourse.setText("Studying:" + modelPostAttendance.getStudentDetail().getCourseName());
        tvStudentName.setText("" + modelPostAttendance.getStudentDetail().getStudentName());

        if (strAttendanceStatus.equalsIgnoreCase("in")) {
            //tvIn.setVisibility(View.VISIBLE);
            //tvOut.setVisibility(View.GONE);
        } else {
            //tvIn.setVisibility(View.GONE);
            //tvOut.setVisibility(View.VISIBLE);
        }

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraSource.stop();
                linScanner.setVisibility(View.GONE);
                tvScanNow.setVisibility(View.VISIBLE);
                if (dialogAttendence != null && dialogAttendence.isShowing()) {
                    dialogAttendence.dismiss();
                }
            }
        });

        if (dialogAttendence != null && !dialogAttendence.isShowing()) {
            dialogAttendence.show();
            linStudentDetails.startAnimation(animSlideUp);
        }
    }

    private void callApiToMakeAttendance(String studentId, String inOutValue) {
        if (!CommonUtil.isInternetAvailable(TakeStudentAttendenceActivity.this)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            try {
                cameraSource.start(surfaceView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        new OkHttpRequest(TakeStudentAttendenceActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_POST_ATTENDENCE,
                RequestParam.postAttendance("" + studentId,
                        "" + inOutValue,
                        ""+getSharedPrefrencesInstance(TakeStudentAttendenceActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_POST_ATTENDENCE,
                true, this);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvScanAttendance:
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    linScanner.setVisibility(View.VISIBLE);
                    tvScanNow.setVisibility(View.GONE);
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
