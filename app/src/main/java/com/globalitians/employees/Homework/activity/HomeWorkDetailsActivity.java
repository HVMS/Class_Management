package com.globalitians.employees.Homework.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalitians.employees.Homework.adapter.AssignedHomeWorkStudentAdapter;
import com.globalitians.employees.Homework.adapter.HomeworkImagesAdapter;
import com.globalitians.employees.Homework.model.HomeWorkDetailsModelClass;
import com.globalitians.employees.R;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.TouchImageView;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_HOMEWORK;
import static com.globalitians.employees.utility.Constants.CODE_HOMEWORK_DETAILS;
import static com.globalitians.employees.utility.Constants.CODE_REMOVE_STUDENTS_HOMEWORK;

public class HomeWorkDetailsActivity extends AppCompatActivity
        implements OkHttpInterface,
        AssignedHomeWorkStudentAdapter.OnHomeWorkStudentRemove,
        HomeworkImagesAdapter.OnImageClickListner{

    private CustomTextView mTxthwtitle;
    private CustomTextView mTxthwdescription;
    private CustomTextView mTxtbatchname;
    private CustomTextView mTxtpdfname;
    private CustomTextView mTxtAttachment;
    private ImageView mIvpdf;
    private RecyclerView mRvhwstudents;
    private RecyclerView mRvhwimages;

    private HomeWorkDetailsModelClass homeWorkDetailsModelClass;
    private AssignedHomeWorkStudentAdapter assignedHomeWorkStudentAdapter;
    private HomeworkImagesAdapter homeworkImagesAdapter;
    private ArrayList<String> mAlattachments;
    private ArrayList<HomeWorkDetailsModelClass.BatchDetail.Student> mAlstudents;
    private String mStrhwid="";

    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(HomeWorkDetailsActivity.this);
        setContentView(R.layout.activity_home_work_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("HomeWork Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        getHomeWorkId();
    }

    private void getHomeWorkId() {
        Intent intent = getIntent();
        if(intent!=null){
            mStrhwid = ""+intent.getStringExtra("hw_id");
            if(!CommonUtil.isNullString(""+mStrhwid)){
                callApiToLoadHomeWorkDetails(mStrhwid);
            }
        }
    }

    private void callApiToLoadHomeWorkDetails(String mStrhwid) {
        if(!CommonUtil.isInternetAvailable(HomeWorkDetailsActivity.this)){
            return;
        }

        new OkHttpRequest(HomeWorkDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_HOMEWORK_DETAILS,
                RequestParam.getHomeWorkDetails(""+mStrhwid),
                RequestParam.getNull(),
                CODE_HOMEWORK_DETAILS,
                false,this);
    }

    private void init() {
        mAlstudents = new ArrayList<>();
        mAlattachments = new ArrayList<>();
    }

    private void findViews() {
        mTxthwtitle = findViewById(R.id.tv_actual_homework_title);
        mTxtbatchname = findViewById(R.id.tv_actual_batch_title);
        mTxthwdescription = findViewById(R.id.tv_actual_description);
        mIvpdf = findViewById(R.id.iv_course_file_pick);
        mTxtpdfname = findViewById(R.id.tv_pdf_file_name);
        mRvhwstudents = findViewById(R.id.rv_hw_students);
        mRvhwimages = findViewById(R.id.rv_images);
        mTxtAttachment = findViewById(R.id.tv_assignments);
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

        if(id==R.id.action_edit) {
            Intent intent = new Intent(HomeWorkDetailsActivity.this,AddHomeWorkActivity.class);
            intent.putExtra("homework_id",""+mStrhwid);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_employee, menu);
        MenuItem itemPdf = menu.findItem(R.id.action_pdf);
        itemPdf.setVisible(false);
        MenuItem itemExcel = menu.findItem(R.id.action_excel);
        itemExcel.setVisible(false);
        MenuItem itemAdd = menu.findItem(R.id.action_add);
        itemAdd.setVisible(false);
        MenuItem itemSetting = menu.findItem(R.id.action_settings);
        itemSetting.setVisible(false);
        MenuItem itemEdit = menu.findItem(R.id.action_edit);
        itemEdit.setVisible(true);
        return true;
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
            case CODE_HOMEWORK_DETAILS:
                Log.e("homework details",""+response);
                final Gson gson = new Gson();
                try{
                    homeWorkDetailsModelClass = gson.fromJson(response,HomeWorkDetailsModelClass.class);
                    if(homeWorkDetailsModelClass.getStatus().equals(Constants.SUCCESS_CODE)){
                        sethomedetails(homeWorkDetailsModelClass);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_DELETE_HOMEWORK:
                Log.e("remove_student_hw",""+response);
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse != null && jsonResponse.has("status") && jsonResponse.has("message")) {
                        Toast.makeText(this, "" + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        CommonUtil.playSoundForAttendance("" + jsonResponse.getString("message"), HomeWorkDetailsActivity.this);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void sethomedetails(HomeWorkDetailsModelClass homeWorkDetailsModelClass) {

        mTxtbatchname.setText(""+homeWorkDetailsModelClass.getBatchDetail().getBatchName());
        mTxthwtitle.setText(""+homeWorkDetailsModelClass.getBatchDetail().getTitle());
        mTxthwdescription.setText(""+homeWorkDetailsModelClass.getBatchDetail().getDescription());

        if(homeWorkDetailsModelClass.getBatchDetail().getStudents()!=null
                && homeWorkDetailsModelClass.getBatchDetail().getStudents().size()>0){
            mAlstudents = homeWorkDetailsModelClass.getBatchDetail().getStudents();
            setStudentsAdapter(mAlstudents);
        }

        for(int i = 0 ; i < homeWorkDetailsModelClass.getBatchDetail().getAttachments().size() ; i++){
            String string = ""+homeWorkDetailsModelClass.getBatchDetail().getAttachments().get(i).getImage();
            if(string.endsWith(".png") || string.endsWith(".jpg") || string.endsWith(".PNG")){
                mIvpdf.setVisibility(View.GONE);
                mTxtpdfname.setVisibility(View.GONE);
                mAlattachments.add(homeWorkDetailsModelClass.getBatchDetail().getAttachments().get(i).getImage());
            }else{
                mIvpdf.setVisibility(View.VISIBLE);
                mTxtpdfname.setVisibility(View.VISIBLE);
                mTxtpdfname.setText(""+homeWorkDetailsModelClass.getBatchDetail().getAttachments().get(i).getImage());
            }
        }

        homeworkImagesAdapter =
                new HomeworkImagesAdapter(HomeWorkDetailsActivity.this,mAlattachments,this);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        mRvhwimages.setLayoutManager(linearLayoutManager);
        mRvhwimages.setAdapter(homeworkImagesAdapter);
        homeworkImagesAdapter.notifyDataSetChanged();

    }

    private void setStudentsAdapter(ArrayList<HomeWorkDetailsModelClass.BatchDetail.Student> mAlstudents) {
        assignedHomeWorkStudentAdapter =
                new AssignedHomeWorkStudentAdapter(HomeWorkDetailsActivity.this,mAlstudents,this);
        GridLayoutManager manager1 = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRvhwstudents.setLayoutManager(manager1);
        mRvhwstudents.setAdapter(assignedHomeWorkStudentAdapter);
        assignedHomeWorkStudentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onRemoveStudent(int position) {
        callApiToRemoveStudentFromHomework(""+mStrhwid,""+mAlstudents.get(position).getId());
        mAlstudents.remove(position);
        setStudentsAdapter(mAlstudents);
    }

    private void callApiToRemoveStudentFromHomework(String mStrhwid, String mStrstudentId) {

        if(!CommonUtil.isInternetAvailable(HomeWorkDetailsActivity.this)){
            return;
        }

        new OkHttpRequest(HomeWorkDetailsActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_REMOVE_STUDENTS_HOMEWORK,
                RequestParam.deleteStudentHomework(""+mStrhwid,""+mStrstudentId),
                RequestParam.getNull(),
                CODE_REMOVE_STUDENTS_HOMEWORK,
                true,this);
    }

    @Override
    public void onImageClick(int position) {

        dialog = new Dialog(HomeWorkDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_to_load_full_image);
        dialog.setCancelable(false);

        final TouchImageView touchImageView = dialog.findViewById(R.id.iv_hw_image);

        if(!CommonUtil.isNullString(""+mAlattachments.get(position))){
            CommonUtil.setImageToGlide(HomeWorkDetailsActivity.this,touchImageView,""+mAlattachments.get(position));
        }

        final ImageView mIvClose = dialog.findViewById(R.id.iv_close);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }
}
