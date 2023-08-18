package com.globalitians.employees.courses.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.globalitians.employees.R;
import com.globalitians.employees.courses.model.ModelCourseCategoriesList;
import com.globalitians.employees.courses.model.ModelCourseDurationList;
import com.globalitians.employees.utility.AppController;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_IMAGE;
import static com.globalitians.employees.utility.Constants.ATTACHMENT_PDF;
import static com.globalitians.employees.utility.Constants.CODE_ADD_NEW_COURSE;
import static com.globalitians.employees.utility.Constants.CODE_GET_COURSE_CATEGORIES;
import static com.globalitians.employees.utility.Constants.CODE_GET_COURSE_DURATIONS;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class AddCourseActivity extends AppCompatActivity implements View.OnClickListener, OkHttpInterface {
    private static final int REQUEST_PDF = 1212;
    private ScrollView scrollAddCourseContainer;
    private EditText edtCourseName;
    private EditText edtCourseFees;
    private Spinner spinnerCourseDuration;
    private TextView tvDecreaseFees;
    private TextView tvIncreaseFees;
    private Spinner spinnerCourseCategory;
    private ImageView ivCourseImagePick;
    private ImageView ivCourseImage;
    private ImageView ivCourseFilePick;
    private TextView tvCourseFileName;
    private TextView tvAddCourse;

    private ModelCourseCategoriesList modelCourseCategories = null;
    private ModelCourseDurationList modelCourseDurationList = null;
    private String strUploadType = "photo";
    private ArrayList<File> mArrListFilesToSend = null;

    private static String ATTACHMENT_TYPE = ""; // IMAGE, PDF
    private File mFileSelectedCourseImage = null;
    private File mFileSelectedCoursePdf = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setFullScreenMode(AddCourseActivity.this);
            setContentView(R.layout.activity_add_course);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            findViews();
            setListeners();
            CommonUtil.hideSoftKeyboard(AddCourseActivity.this);
            callApiToLoadCourseCategories();
        }catch(Exception e){
            e.printStackTrace();
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

    private void findViews() {

        if (mArrListFilesToSend != null) {
            mArrListFilesToSend.clear();
        }
        mArrListFilesToSend = new ArrayList<>();
        scrollAddCourseContainer = (ScrollView) findViewById(R.id.scroll_Add_course_container);
        scrollAddCourseContainer.setVisibility(View.GONE);
        edtCourseName = (EditText) findViewById(R.id.edt_course_name);
        edtCourseFees = (EditText) findViewById(R.id.edtCoursefees);
        spinnerCourseDuration = (Spinner) findViewById(R.id.spinner_course_duration);
        tvDecreaseFees = (TextView) findViewById(R.id.tv_decrease_fees);
        tvIncreaseFees = (TextView) findViewById(R.id.tv_increase_fees);
        spinnerCourseCategory = (Spinner) findViewById(R.id.spinner_course_category);
        ivCourseImagePick = (ImageView) findViewById(R.id.iv_course_image_pick);
        ivCourseImage = (ImageView) findViewById(R.id.iv_course_image);
        ivCourseFilePick = (ImageView) findViewById(R.id.iv_course_file_pick);
        tvCourseFileName = (TextView) findViewById(R.id.tv_course_file_name);
        tvAddCourse = (TextView) findViewById(R.id.tv_add_course);
    }

    private void setListeners() {
        tvAddCourse.setOnClickListener(this);
        tvIncreaseFees.setOnClickListener(this);
        tvDecreaseFees.setOnClickListener(this);
        ivCourseImagePick.setOnClickListener(this);
        ivCourseFilePick.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_course:
                validateAndCallServiceToAddNewCourse();
                break;
            case R.id.tv_increase_fees:

                if (CommonUtil.isNullString(edtCourseFees.getText().toString().trim())) {
                    edtCourseFees.setText("0");
                }

                int fees = Integer.parseInt(edtCourseFees.getText().toString().trim());

                if (fees != 99999) {
                    fees += 500;
                    edtCourseFees.setText("" + fees);
                }
                break;
            case R.id.tv_decrease_fees:
                if (CommonUtil.isNullString(edtCourseFees.getText().toString().trim())) {
                    edtCourseFees.setText("00");
                }

                int fees2 = Integer.parseInt(edtCourseFees.getText().toString().trim());
                if (fees2 > 499) {
                    fees2 -= 500;
                    edtCourseFees.setText("" + fees2);
                }
                break;
            case R.id.iv_course_image_pick:
                showDialogToSelectUploadOption();
                break;
            case R.id.iv_course_file_pick:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, REQUEST_PDF);
                break;
        }
    }

    private void askForPermission() {
        if (PermissionManager.hasPermissions(AddCourseActivity.this, Permissions.STORAGE_PERMISSIONS)) {
            showChooser();
        } else {
            PermissionManager.requestPermissions(AddCourseActivity.this, Constants.CODE_RUNTIME_STORAGE_PERMISSION,
                    permissionListener, "", Permissions.STORAGE_PERMISSIONS);
        }
    }

    public void showChooser() {
        if (ATTACHMENT_TYPE.equals(ATTACHMENT_IMAGE)) { // User selected image option
            // Move to specific folder
            PhotoPicker.configuration(AddCourseActivity.this).setImagesFolderName(Constants.DIRECTORY_GIT_MEDIA);
            PhotoPicker.openChooserWithGallery(AddCourseActivity.this, ResourceUtils.getString(R.string.select_picture_from), PhotoPicker.SELECT_PHOTO);
        } else if (ATTACHMENT_TYPE.equals(ATTACHMENT_PDF)) {
            CommonUtil.playSoundForAttendance("In Progress. Please check letter.",AddCourseActivity.this);
        }
    }

    private void updateImage() {
        // Configure image chooser option
        PhotoPicker.configuration(AddCourseActivity.this)
                .setImagesFolderName(Constants.DIRECTORY_GIT_MEDIA);

        PhotoPicker.openChooserWithGallery(AddCourseActivity.this, "" + ResourceUtils.getString(R.string.select_picture_from), 0);
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

    private void showDialogToSelectUploadOption() {
        String[] dialogOptions = ResourceUtils.getStringArray(R.array.options_for_upload_file);
        /*String[] dialogOptions = {
                "Upload Photo",
                "Upload PDF",
                "Cancel"
        };*/
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCourseActivity.this);

        builder.setTitle("" + AppController.getInstance().getResources()
                .getString(R.string.upload_file_options));
        builder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        // Set attachment type
                        ATTACHMENT_TYPE = ATTACHMENT_IMAGE;
                        askForPermission();
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

    private void validateAndCallServiceToAddNewCourse() {
        if (CommonUtil.isNullString(edtCourseName.getText().toString().trim())) {
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_coursename), Toast.LENGTH_SHORT).show();
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_coursename),AddCourseActivity.this);
            return;
        } else if (spinnerCourseDuration.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_courseduration), Toast.LENGTH_SHORT).show();
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_courseduration),AddCourseActivity.this);
            return;
        } else if (CommonUtil.isNullString(edtCourseFees.getText().toString().trim())) {
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_course_fees), Toast.LENGTH_SHORT).show();
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_course_fees),AddCourseActivity.this);
            return;
        } else if (spinnerCourseCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "" + getResources().getString(R.string.toast_invalid_course_category), Toast.LENGTH_SHORT).show();
            CommonUtil.playSoundForAttendance("" + getResources().getString(R.string.toast_invalid_course_category),AddCourseActivity.this);
            return;
        } else {
            callApiToAddNewCourse("" + edtCourseName.getText().toString(),
                    "" + modelCourseCategories.getAlCategories().get(spinnerCourseCategory.getSelectedItemPosition()).getId(),
                    "" + modelCourseDurationList.getAlDuration().get(spinnerCourseDuration.getSelectedItemPosition()).getDuration(),
                    "" + edtCourseFees.getText().toString());
        }
    }

    private void callApiToAddNewCourse(String name,
                                       String cat_id,
                                       String duration,
                                       String fees) {

        if (!CommonUtil.isInternetAvailable(AddCourseActivity.this)) {
            return;
        }

        if (mFileSelectedCourseImage != null || mFileSelectedCoursePdf!=null) {
            //Adding New Course with media file...
            new OkHttpRequest(AddCourseActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_COURSE,
                    RequestParam.addNewCourse(
                            "" + name,
                            "" + cat_id,
                            "" + duration,
                            "" + fees,
                            ""+getSharedPrefrencesInstance(AddCourseActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                    RequestParam.getNull(),
                    RequestParam.addNewCourseImageParam(mFileSelectedCourseImage,mFileSelectedCoursePdf),
                    CODE_ADD_NEW_COURSE,
                    true,
                    this);
        } else {
            new OkHttpRequest(AddCourseActivity.this,
                    OkHttpRequest.Method.POST,
                    Constants.WS_ADD_COURSE,
                    RequestParam.addNewCourse(
                            "" + name,
                            "" + cat_id,
                            "" + duration,
                            "" + fees,
                            ""+getSharedPrefrencesInstance(AddCourseActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                    RequestParam.getNull(),
                    CODE_ADD_NEW_COURSE,
                    true,
                    this);
        }

    }

    private void callApiToLoadCourseDurations() {
        if (!CommonUtil.isInternetAvailable(AddCourseActivity.this)) {
            return;
        }

        new OkHttpRequest(AddCourseActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_COURSE_DURATIONS,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_GET_COURSE_DURATIONS,
                true, this);
    }

    private void callApiToLoadCourseCategories() {
        if (!CommonUtil.isInternetAvailable(AddCourseActivity.this)) {
            return;
        }

        new OkHttpRequest(AddCourseActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_COURSE_CATEGORIES,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_GET_COURSE_CATEGORIES,
                true, this);
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
            case CODE_GET_COURSE_DURATIONS:
                Log.e("categories >> ", "" + response);
                final Gson courseDurationList = new Gson();
                try {
                    modelCourseDurationList = courseDurationList
                            .fromJson(response, ModelCourseDurationList.class);

                    if (modelCourseDurationList.getAlDuration() != null && modelCourseDurationList.getAlDuration().size() > 0) {
                        setCourseDurationListAdapter(modelCourseDurationList.getAlDuration());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_GET_COURSE_CATEGORIES:
                Log.e("durations >> ", "" + response);
                final Gson courseCategories = new Gson();
                try {
                    modelCourseCategories = courseCategories
                            .fromJson(response, ModelCourseCategoriesList.class);

                    if (modelCourseCategories.getStatus().equals("success")) {
                        if (modelCourseCategories.getAlCategories() != null && modelCourseCategories.getAlCategories().size() > 0) {
                            setCourseCategoriesAdapter(modelCourseCategories.getAlCategories());
                        }
                    } else {
                        Toast.makeText(this, "" + modelCourseCategories.getMessage(), Toast.LENGTH_SHORT).show();
                        CommonUtil.playSoundForAttendance(""+modelCourseCategories.getMessage(),AddCourseActivity.this);
                    }

                    callApiToLoadCourseDurations();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case CODE_ADD_NEW_COURSE:
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse != null && jsonResponse.has("status") && jsonResponse.has("message")) {
                        Toast.makeText(this, "" + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        CommonUtil.playSoundForAttendance("" + jsonResponse.getString("message"),AddCourseActivity.this);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        scrollAddCourseContainer.setVisibility(View.VISIBLE);
    }

    private void setCourseCategoriesAdapter(ArrayList<ModelCourseCategoriesList.Categories> alCategories) {
        ArrayList<String> alCourseCategories = new ArrayList<>();
        if (alCategories != null && alCategories.size() > 0) {
            for (int i = 0; i < alCategories.size(); i++) {
                alCourseCategories.add("" + alCategories.get(i).getName());
            }
        }

        alCourseCategories.add(0, "--Select Course Category--");
        ArrayAdapter<String> adptCourseCategories = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_list_item_1, alCourseCategories);
        spinnerCourseCategory.setAdapter(adptCourseCategories);
    }

    private void setCourseDurationListAdapter(ArrayList<ModelCourseDurationList.Duration> mArrListCoursDurations) {
        ArrayList<String> alDurations = new ArrayList<>();
        if (mArrListCoursDurations != null && mArrListCoursDurations.size() > 0) {
            for (int i = 0; i < mArrListCoursDurations.size(); i++) {
                alDurations.add("" + mArrListCoursDurations.get(i).getDuration());
            }
        }

        alDurations.add(0, "--Select Course Duration--");
        ArrayAdapter<String> adptCourseDurations = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_list_item_1, alDurations);
        spinnerCourseDuration.setAdapter(adptCourseDurations);
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_PDF:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String uriString = uri.toString().replace("file:","");
                    File myFile = new File(uriString);

                    mFileSelectedCoursePdf = myFile;

                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }

                    tvCourseFileName.setText("" + displayName);
                }
                break;
        }


        PhotoPicker.handleActivityResult(requestCode, resultCode, data, AddCourseActivity.this, new PhotoPickerCallback() {
            @Override
            public void onImagePickerError(Exception e, PhotoPicker.ImageSource source, int type) {
                //Some error handling
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
            // If photo format is not valid as well as file is corrupted.
            /*if (CommonUtil.isPhotoCorrupted(returnedFile)) {
                Toast.makeText(this, "" + ResourceUtils.getString(R.string.toast_attachment_photo_size_null), Toast.LENGTH_SHORT).show();
                CommonUtil.playSoundForAttendance("" + ResourceUtils.getString(R.string.toast_attachment_photo_size_null),AddCourseActivity.this);
                return;
            }*/
            // Reduce the image bitmap to prevent from out of memory error.
            //returnedFile = CommonUtil.compressImage(returnedFile.getAbsolutePath());
            mFileSelectedCourseImage = returnedFile;

            //Bitmap myBitmap = BitmapFactory.decodeFile(mFileSelectedCourseImage.getAbsolutePath());
            ivCourseImage.setImageURI(Uri.fromFile(mFileSelectedCourseImage));
            //ivCourseImage.setImageBitmap(myBitmap);
            //ivCourseImagePick.setImageBitmap(myBitmap);
        }
        System.gc();
    }
}
