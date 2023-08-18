package com.globalitians.employees.Homework.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.globalitians.employees.Homework.adapter.AssignHomwWorkStudentsAdapter;
import com.globalitians.employees.Homework.adapter.HomeworkImagesAdapter;
import com.globalitians.employees.Homework.adapter.ImagesAdapter;
import com.globalitians.employees.Homework.model.HomeWorkDetailsModelClass;
import com.globalitians.employees.R;
import com.globalitians.employees.batches.model.BatchDetailsModel;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.customviews.CustomEditText;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.utility.AppController;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.LogUtil;
import com.globalitians.employees.utility.PermissionManager;
import com.globalitians.employees.utility.Permissions;
import com.globalitians.employees.utility.PhotoPicker;
import com.globalitians.employees.utility.ResourceUtils;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_IMAGE;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_PDF;
import static com.globalitians.employees.utility.Constants.CODE_ADD_HOMEWORK;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_LIST;
import static com.globalitians.employees.utility.Constants.CODE_HOMEWORK_DETAILS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_LOGGEDIN_EMPLOYEE_ID;

public class AddHomeWorkActivity extends AppCompatActivity
        implements OkHttpInterface, View.OnClickListener,
        AssignHomwWorkStudentsAdapter.OnAssignedHomeworkListner,
        HomeworkImagesAdapter.OnImageClickListner{

    /* multiple images Recycler view*/
    private RecyclerView mRvimages;
    private ImagesAdapter imagesAdapter;

    // EASY IMAGE PICKER PERMISSIONS AND ALL KIND OF VARIABLES
    private static final String PHOTOS_KEY = "easy_image_photos_list";
    private static final int CHOOSER_PERMISSIONS_REQUEST_CODE = 7459;
    private static final int CAMERA_REQUEST_CODE = 7500;
    private static final int GALLERY_REQUEST_CODE = 7502;

    /* Easy image picker variable stuff */

    private EasyImage easyImage;
    private ArrayList<File> photos = new ArrayList<File>();
    private File mFileimage=null;

    private CustomEditText mEdthwtitle;
    private CustomEditText mEdthwdescription;
    private CustomTextView tvPdfFileName;
    private CustomTextView mTxtSelectBatchTitle;
    private Spinner spinnerBatch;
    private Button mBtnattach;
    private Button mBtnaddhomework;
    private CustomTextView mTxtshowstudents;

    private BatchListModel modelBatchList;

    private ArrayList<BatchListModel.Batch> mArrListBatches;
    private ArrayList<String> mAlbatches;
    private ArrayList<String> mAlselectedStudentsIds;

    private String mStrSelectedStudentsIds="";
    private String mStrSelectedBatchId="";
    private String mStrSelectedBatchName="";
    private String imageFilePath = "";
    private String displayName="";
    private String mStrHomeWorkId="";

    private static final int REQUEST_PDF = 1212;
    private static String ATTACHMENT_TYPE = ""; // IMAGE, PDF
    private File mFileSelectedCoursePdf = null;
    private ImageView ivHoweworkpdf;

    private RecyclerView mRvstudents;
    private AssignHomwWorkStudentsAdapter assignHomwWorkStudentsAdapter;

    private Button mBtnassignhw;
    private Button mBtnclose;
    private CheckBox chkAll;

    private Dialog dialog=null;

    private ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentsbatchwise;
    private BatchDetailsModel batchDetailsModel;

    private HomeWorkDetailsModelClass homeWorkDetailsModelClass;
    private ArrayList<HomeWorkDetailsModelClass.BatchDetail.Student> mArrlistassignedstudents;
    private String strBatchIdfromHomework="";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(AddHomeWorkActivity.this);
        setContentView(R.layout.activity_add_home_work);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Homework");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        callApiToLoadbatchlist();
        getHomeWorkId();

        if (savedInstanceState != null) {
            photos = (ArrayList<File>) savedInstanceState.getSerializable(PHOTOS_KEY);
        }

        imagesAdapter = new ImagesAdapter(this, photos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        mRvimages.setLayoutManager(linearLayoutManager);
        mRvimages.setHasFixedSize(true);
        mRvimages.setAdapter(imagesAdapter);
    }

    private void getHomeWorkId() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrHomeWorkId = ""+intent.getStringExtra("homework_id");
            if(!CommonUtil.isNullString(""+mStrHomeWorkId)){
                callApiForHomeWorkDetails(""+mStrHomeWorkId);
            }
        }
    }

    private void callApiForHomeWorkDetails(String mStrHomeWorkId) {
        if(!CommonUtil.isInternetAvailable(AddHomeWorkActivity.this)){
            return;
        }

        new OkHttpRequest(AddHomeWorkActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_HOMEWORK_DETAILS,
                RequestParam.getHomeWorkDetails(""+mStrHomeWorkId),
                RequestParam.getNull(),
                CODE_HOMEWORK_DETAILS,
                true,this);
    }

    private void init() {

        mArrListBatches = new ArrayList<>();
        mAlbatches = new ArrayList<>();
        mArrlistassignedstudents = new ArrayList<>();

        mAlstudentsbatchwise = new ArrayList<>();
        mAlselectedStudentsIds = new ArrayList<>();
    }

    private void callApiToLoadbatchlist() {

        if(!CommonUtil.isInternetAvailable(AddHomeWorkActivity.this)){
            return;
        }

        new OkHttpRequest(AddHomeWorkActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_LIST,
                RequestParam.batchlist(""+CommonUtil.getSharedPrefrencesInstance(AddHomeWorkActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_BATCH_LIST,
                false,
                this);

    }

    private void findViews() {

        mRvimages = findViewById(R.id.rv_images);

        mEdthwtitle = findViewById(R.id.edt_homework);
        mEdthwdescription = findViewById(R.id.edt_description);
        spinnerBatch = findViewById(R.id.spinner_batch);
        mBtnattach = findViewById(R.id.attach_file_btn);
        mTxtSelectBatchTitle = findViewById(R.id.tv_select_batch);

        ivHoweworkpdf = findViewById(R.id.iv_course_file_pick);
        tvPdfFileName = findViewById(R.id.tv_pdf_file_name);
        mBtnaddhomework = findViewById(R.id.btn_add_hw);
        mTxtshowstudents = findViewById(R.id.tv_show_students);
        mTxtshowstudents.setTextColor(getResources().getColor(R.color.my_blue));

        mBtnattach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        spinnerBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){
                    mStrSelectedBatchId = ""+modelBatchList.getBatches().get(position-1).getId();
                    mStrSelectedBatchName = ""+modelBatchList.getBatches().get(position-1).getName();
                    Toast.makeText(AddHomeWorkActivity.this, ""+mStrSelectedBatchName, Toast.LENGTH_SHORT).show();

                    if(mStrSelectedBatchName.equals("Select Batch")){
                        mTxtshowstudents.setVisibility(View.GONE);
                    }else{
                        mTxtshowstudents.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBtnaddhomework.setOnClickListener(this);
        mTxtshowstudents.setOnClickListener(this);
    }

    private void showDialogForAssignStudents(final String mStrSelectedBatchId) {

        dialog = new Dialog(AddHomeWorkActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_for_assigning_students_for_homework);
        dialog.setCancelable(false);

        mRvstudents = dialog.findViewById(R.id.assign_students_homework);
        mBtnassignhw = dialog.findViewById(R.id.assign_students_btn);
        chkAll = dialog.findViewById(R.id.chkAll);
        mBtnclose = dialog.findViewById(R.id.cancel_button);

        if(!CommonUtil.isNullString(""+mStrSelectedBatchId)){
            callApiToLoadbatchWiseStudents(mStrSelectedBatchId);
        }

        chkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mAlstudentsbatchwise!=null && mAlstudentsbatchwise.size()>0){
                    if(b){
                        for(int i = 0 ; i < mAlstudentsbatchwise.size() ; i++){
                            mAlstudentsbatchwise.get(i).setSelected(true);
                            assignHomwWorkStudentsAdapter.notifyDataSetChanged();
                        }
                    }else{
                        for(int i = 0 ; i < mAlstudentsbatchwise.size() ; i++){
                            mAlstudentsbatchwise.get(i).setSelected(false);
                            assignHomwWorkStudentsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        mBtnassignhw.setOnClickListener(this);

        mBtnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog.isShowing() && dialog!=null){
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void callApiToLoadbatchWiseStudents(String mStrSelectedBatchId) {

        if(!CommonUtil.isInternetAvailable(AddHomeWorkActivity.this)){
            return;
        }

        new OkHttpRequest(AddHomeWorkActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_DETAIL,
                RequestParam.batchDetails(""+mStrSelectedBatchId),
                RequestParam.getNull(),
                CODE_BATCH_DETAILS,
                false,
                this);
    }

    private void showCustomDialog() {
        String[] dialogOptions = ResourceUtils.getStringArray(R.array.options_for_upload_file);
        AlertDialog.Builder builder = new AlertDialog.Builder(AddHomeWorkActivity.this);

        builder.setTitle("" + AppController.getInstance().getResources()
                .getString(R.string.upload_file_options));
        builder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        // Set attachment type
                        ATTACHMENT_TYPE = ATTACHMENT_IMAGE;
                        String[] necessaryPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
                        requestPermissionsCompat(necessaryPermissions, CHOOSER_PERMISSIONS_REQUEST_CODE);
                        break;
                    case 1:
                         // Set attachment type
                        ATTACHMENT_TYPE = ATTACHMENT_PDF;
                        askForPermission();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CHOOSER_PERMISSIONS_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openChooserWithGallery(AddHomeWorkActivity.this,"Choose Photo from",0);
        } else if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openCamera(AddHomeWorkActivity.this,0);
        }else if (requestCode == GALLERY_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            easyImage.openGallery(AddHomeWorkActivity.this,0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case REQUEST_PDF:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();

                    mFileSelectedCoursePdf=myFile;
                    LogUtil.e("FILE >>>",""+myFile.toString());
                    LogUtil.e("PATH >>>",""+path);
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = AddHomeWorkActivity.this.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }

                    ivHoweworkpdf.setVisibility(View.VISIBLE);
                    tvPdfFileName.setVisibility(View.VISIBLE);

                    tvPdfFileName.setText("" + displayName);
                }
                break;
        }

        easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                onPhotoReturned(imageFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(AddHomeWorkActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });
    }

    private void onPhotoReturned(List<File> imageFiles) {
        photos.addAll(imageFiles);
        mFileimage = photos.get(photos.size()-1);
        imagesAdapter.notifyDataSetChanged();
        mRvimages.scrollToPosition(photos.size() - 1);
    }

    private void requestPermissionsCompat(String[] necessaryPermissions, int chooserPermissionsRequestCode) {
        ActivityCompat.requestPermissions(AddHomeWorkActivity.this, necessaryPermissions, chooserPermissionsRequestCode);
    }

    private void askForPermission() {
        if (PermissionManager.hasPermissions(AddHomeWorkActivity.this, Permissions.STORAGE_PERMISSIONS)) {
            showChooser();
        } else {
            PermissionManager.requestPermissions(AddHomeWorkActivity.this, Constants.CODE_RUNTIME_STORAGE_PERMISSION,
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
        PhotoPicker.configuration(AddHomeWorkActivity.this)
                .setImagesFolderName(Constants.DIRECTORY_GIT_MEDIA);

        PhotoPicker.openChooserWithGallery(AddHomeWorkActivity.this, "" + ResourceUtils.getString(R.string.select_picture_from), 0);
    }

    private void showChooser() {
        if (ATTACHMENT_TYPE.equals(ATTACHMENT_PDF)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, REQUEST_PDF);
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

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        if(response==null){
            return;
        }

        switch (requestId){
            case CODE_BATCH_LIST:
                Log.e("students batches",""+response);
                final Gson batchGson = new Gson();
                try{
                    modelBatchList = batchGson
                            .fromJson(response, BatchListModel.class);

                    if (modelBatchList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListBatches=new ArrayList<>();

                        for(int i = 0 ; i  < modelBatchList.getBatches().size() ; i++){
                            if(modelBatchList.getBatches().get(i).getStatus().equalsIgnoreCase("Running")){
                                mArrListBatches.add(modelBatchList.getBatches().get(i));
                            }
                        }

                        if (mArrListBatches != null && mArrListBatches.size() > 0) {
                            setadapterForBatchList();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_BATCH_DETAILS:
                Log.e("students",""+response);
                final Gson studentsGson = new Gson();
                try{
                    batchDetailsModel = studentsGson.fromJson(response,BatchDetailsModel.class);
                    if(batchDetailsModel.getStatus().equals(Constants.SUCCESS_CODE)){
                        if(batchDetailsModel.getBatchDetail().getStudents()!=null &&
                                batchDetailsModel.getBatchDetail().getStudents().size()>0){

                            mAlstudentsbatchwise = batchDetailsModel.getBatchDetail().getStudents();

                            if(mArrlistassignedstudents!=null && mArrlistassignedstudents.size()>0){

                                for(int i = 0 ; i < mArrlistassignedstudents.size() ; i++){
                                    for(int j = 0 ; j < mAlstudentsbatchwise.size() ; j++){
                                        if(mArrlistassignedstudents.get(i).getId().toString().equalsIgnoreCase(""+mAlstudentsbatchwise.get(j).getId().toString())){
                                            mAlstudentsbatchwise.get(j).isSelected = true;
                                        }
                                    }
                                }

                                setStudentwiseAdapter(mAlstudentsbatchwise);

                            }else{

                                // first of check whether students selected or not
                                if(mAlselectedStudentsIds!=null && mAlselectedStudentsIds.size()>0){
                                    for(int i = 0 ; i < mAlstudentsbatchwise.size() ; i++){
                                        for(int j = 0 ; j < mAlselectedStudentsIds.size() ; j++){
                                            if(mAlselectedStudentsIds.get(j).equalsIgnoreCase(""+mAlstudentsbatchwise.get(i).getId().toString())){
                                                mAlstudentsbatchwise.get(i).setSelected(true);
                                                assignHomwWorkStudentsAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }

                                setStudentwiseAdapter(mAlstudentsbatchwise);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_ADD_HOMEWORK:
                Log.e("homework response",""+response);
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.has("status")) {
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_HOMEWORK_DETAILS:
                Log.e("homework details",""+response);
                final Gson homeworkdetailsgson = new Gson();
                try{
                    homeWorkDetailsModelClass = homeworkdetailsgson.fromJson(response,HomeWorkDetailsModelClass.class);
                    if(homeWorkDetailsModelClass.getStatus().equals(Constants.SUCCESS_CODE)){
                        setHomeWorkDetails(homeWorkDetailsModelClass);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setHomeWorkDetails(HomeWorkDetailsModelClass homeWorkDetailsModelClass) {
        getSupportActionBar().setTitle("Edit HomeWork");

        mEdthwtitle.setText(""+homeWorkDetailsModelClass.getBatchDetail().getTitle());
        mEdthwdescription.setText(""+homeWorkDetailsModelClass.getBatchDetail().getDescription());

        /*set assigned homework students*/
        if(mAlstudentsbatchwise!=null && mAlstudentsbatchwise.size()>0){
            mAlstudentsbatchwise.clear();
        }

        if(!CommonUtil.isNullString(homeWorkDetailsModelClass.getBatchDetail().getBatchName())){
            spinnerBatch.setVisibility(View.GONE);
            mTxtSelectBatchTitle.setVisibility(View.GONE);
        }else{
            spinnerBatch.setVisibility(View.VISIBLE);
            mTxtSelectBatchTitle.setVisibility(View.VISIBLE);
        }

        mTxtshowstudents.setVisibility(View.VISIBLE);
        mArrlistassignedstudents = homeWorkDetailsModelClass.getBatchDetail().getStudents();
        strBatchIdfromHomework = ""+homeWorkDetailsModelClass.getBatchDetail().getBatchId();
        mBtnaddhomework.setText("Update HomeWork");

    }

    private void setStudentwiseAdapter(ArrayList<BatchDetailsModel.BatchDetail.Student> mAlstudentsbatchwise) {
        assignHomwWorkStudentsAdapter =
                new AssignHomwWorkStudentsAdapter(AddHomeWorkActivity.this,mAlstudentsbatchwise,this);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        mRvstudents.setLayoutManager(manager);
        mRvstudents.setAdapter(assignHomwWorkStudentsAdapter);
        assignHomwWorkStudentsAdapter.notifyDataSetChanged();
    }

    private void setadapterForBatchList() {
        mAlbatches = new ArrayList<>();

        if(mArrListBatches.size()>0 && mArrListBatches!=null){
            for(int i = 0 ; i < mArrListBatches.size() ; i++){
                mAlbatches.add(i,""+mArrListBatches.get(i).getName());
            }
        }

        mAlbatches.add(0,"Select Batch");
        ArrayAdapter<String> partnerAdapter = new ArrayAdapter<>(AddHomeWorkActivity.this,android.R.layout.simple_list_item_1,mAlbatches);
        spinnerBatch.setAdapter(partnerAdapter);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_hw:
                validateFirstData();
                break;
            case R.id.tv_show_students:

                if(!CommonUtil.isNullString(""+mStrHomeWorkId)){
                    showDialogForAssignStudents(""+strBatchIdfromHomework);
                }else{
                    showDialogForAssignStudents(""+mStrSelectedBatchId);
                }
                break;
            case R.id.assign_students_btn:

                /*Selecting students and store id in to string and send them while calling SERVICE */

                mAlselectedStudentsIds = new ArrayList<>();

                for(int i = 0 ; i < mAlstudentsbatchwise.size() ; i++){
                    if(mAlstudentsbatchwise.get(i).isSelected()==true){
                        mAlselectedStudentsIds.add(""+mAlstudentsbatchwise.get(i).getId());
                    }else if(mAlstudentsbatchwise.get(i).isSelected()==false){
                        mAlselectedStudentsIds.remove(""+mAlstudentsbatchwise.get(i).getId());
                    }
                }

                if(dialog.isShowing() && dialog!=null){
                    dialog.dismiss();
                }

                break;
        }
    }

    @SuppressLint("NewApi")
    private void validateFirstData() {

        if(!CommonUtil.isInternetAvailable(AddHomeWorkActivity.this)){
            playSoundForAttendance("Internet connectivity is not available",AddHomeWorkActivity.this);
            Toast.makeText(this, "Internet connectivity is not available", Toast.LENGTH_SHORT).show();
        }

        StringBuilder stringBuilder = new StringBuilder("");
        for(int i = 0 ; i < mAlselectedStudentsIds.size() ; i++){
            stringBuilder.append(""+mAlselectedStudentsIds.get(i)).append(",");
        }

        mStrSelectedStudentsIds = stringBuilder.toString();
        if(mStrSelectedStudentsIds.endsWith(",")){
            mStrSelectedStudentsIds = mStrSelectedStudentsIds.substring(0,mStrSelectedStudentsIds.length()-1);
        }

        if(CommonUtil.isNullString(mEdthwtitle.getText().toString().trim())){
            playSoundForAttendance("Please Enter Homework Title",AddHomeWorkActivity.this);
            Toast.makeText(this, "Please Enter Homework Title", Toast.LENGTH_SHORT).show();
        }else if(CommonUtil.isNullString(mEdthwdescription.getText().toString().trim())) {
            playSoundForAttendance("Please Enter Description", AddHomeWorkActivity.this);
            Toast.makeText(this, "Please Enter Description", Toast.LENGTH_SHORT).show();
        } else if(CommonUtil.isNullString(""+mStrSelectedStudentsIds)){
            playSoundForAttendance("Please Select Students before Assigning homework",AddHomeWorkActivity.this);
            Toast.makeText(this, "Please Select Students before Assigning homework", Toast.LENGTH_SHORT).show();
        } else if(CommonUtil.isNullString(""+spinnerBatch.getSelectedItem().toString().trim())){
            playSoundForAttendance("Please Select Proper Batch",AddHomeWorkActivity.this);
            Toast.makeText(this, "Please Select Proper Batch", Toast.LENGTH_SHORT).show();
        } else if(photos.size()==0 || photos==null){
            playSoundForAttendance("Please Attach Photo Before Assigning Homework",AddHomeWorkActivity.this);
            Toast.makeText(this, "Please Attacg Photo Before Assigning Homework", Toast.LENGTH_SHORT).show();
        } else{

            if(!CommonUtil.isNullString(""+mStrSelectedStudentsIds)){
                callApiToAddHomeWork();
            }

        }

    }

    private void callApiToAddHomeWork() {

        if(!CommonUtil.isInternetAvailable(AddHomeWorkActivity.this)){
            return;
        }

        if(!CommonUtil.isNullString(""+mStrHomeWorkId)){
            mStrSelectedBatchId = ""+strBatchIdfromHomework;
        }

            /* update the homework */

        new OkHttpRequest(AddHomeWorkActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_ADD_HOMEWORK,
                RequestParam.addHomework(""+CommonUtil.getSharedPrefrencesInstance(AddHomeWorkActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                        ""+mStrSelectedBatchId,
                        ""+CommonUtil.getSharedPrefrencesInstance(AddHomeWorkActivity.this).getString(KEY_LOGGEDIN_EMPLOYEE_ID,""),
                        ""+mEdthwtitle.getText().toString().trim(),
                        ""+mEdthwdescription.getText().toString().trim(),
                        ""+mStrSelectedStudentsIds,
                        "0",
                        ""+mStrHomeWorkId),
                RequestParam.getNull(),
                RequestParam.homeworkimage(photos),
                CODE_ADD_HOMEWORK,
                true,this);

    }

    @Override
    public void AddStudent(int position, boolean value) {
        mAlstudentsbatchwise.get(position).setSelected(value);
        assignHomwWorkStudentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onImageClick(int position) {

    }
}
