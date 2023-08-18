package com.globalitians.employees.Homework.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.globalitians.employees.Homework.adapter.HomeWorkBatchListAdapter;
import com.globalitians.employees.Homework.adapter.HomeworkListAdapter;
import com.globalitians.employees.Homework.model.HomeworkListModelClass;
import com.globalitians.employees.batches.model.BatchListModel;
import com.globalitians.employees.customviews.CustomTextView;
import com.globalitians.employees.R;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_BATCH_LIST;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_HOMEWORK;
import static com.globalitians.employees.utility.Constants.CODE_HOMEWORK_LIST;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;

public class HomeworkListActivity extends AppCompatActivity
    implements OkHttpInterface,
        HomeworkListAdapter.HomeWorkListner,
        HomeWorkBatchListAdapter.OnHomeWorkBatchClick{

    private CustomTextView mTvNoData;
    private RecyclerView mRvhwlist;
    private RecyclerView mRvbatches;
    private ArrayList<BatchListModel.Batch> mAlbatches;
    private HomeWorkBatchListAdapter homeWorkBatchListAdapter;
    private SwipeRefreshLayout mSRHomeworkList;

    private HomeworkListModelClass homeworkListModelClass;
    private ArrayList<HomeworkListModelClass.Homework> mAlhwlist;
    private HomeworkListAdapter homeworkListAdapter;
    private BatchListModel batchListModel;
    private String mStrBatchId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(HomeworkListActivity.this);
        setContentView(R.layout.activity_homework_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Homework List");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        findViews();
        init();
        callApiToLoadHomeWorkList();
        callApiToLoadBatchList();
    }

    private void callApiToLoadBatchList() {
        if(!CommonUtil.isInternetAvailable(HomeworkListActivity.this)){
            return;
        }

        new OkHttpRequest(HomeworkListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_BATCH_LIST,
                RequestParam.batchlist(""+CommonUtil.getSharedPrefrencesInstance(HomeworkListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_BATCH_LIST,
                false,
                this);
    }

    private void callApiToLoadHomeWorkList() {

        if(!CommonUtil.isInternetAvailable(HomeworkListActivity.this)){
            return;
        }

        new OkHttpRequest(HomeworkListActivity.this,
                OkHttpRequest.Method.GET,
                Constants.WS_HOMEWORK_LIST,
                RequestParam.loadGetRequestsData(),
                RequestParam.getNull(),
                CODE_HOMEWORK_LIST,
                false,this);
    }

    private void init() {
        mAlhwlist = new ArrayList<>();
        mAlbatches = new ArrayList<>();
    }

    private void findViews() {
        mRvhwlist = findViewById(R.id.rv_homework_list);
        mRvbatches = findViewById(R.id.rv_batch_list);
        mTvNoData = findViewById(R.id.tv_extra);

        mSRHomeworkList = findViewById(R.id.swipeToRefreshFacultyList);
        mSRHomeworkList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mSRHomeworkList.isRefreshing() && mSRHomeworkList!=null){
                    mSRHomeworkList.setRefreshing(false);
                }
            }
        });
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
            case CODE_HOMEWORK_LIST:
                Log.e("homework list",""+response);
                final Gson hwGson = new Gson();
                try{
                    mAlhwlist = new ArrayList<>();
                    homeworkListModelClass = hwGson.fromJson(response,HomeworkListModelClass.class);
                    if(homeworkListModelClass.getStatus().equals(Constants.SUCCESS_CODE)){
                        if(homeworkListModelClass.getHomeworks()!=null && homeworkListModelClass.getHomeworks().size()>0){

                            mAlhwlist = homeworkListModelClass.getHomeworks();
                            mTvNoData.setVisibility(View.GONE);
                            mRvhwlist.setVisibility(View.VISIBLE);
                            mSRHomeworkList.setVisibility(View.VISIBLE);
                            setHomeworkAdapter(mAlhwlist);
                        }else{
                            mTvNoData.setVisibility(View.VISIBLE);
                            homeworkListAdapter.notifyDataSetChanged();
                            mRvhwlist.setVisibility(View.GONE);
                            mSRHomeworkList.setVisibility(View.GONE);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_DELETE_HOMEWORK:
                Log.e("remove homework",""+response);
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse != null && jsonResponse.has("status") && jsonResponse.has("message")) {
                        Toast.makeText(this, "" + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        CommonUtil.playSoundForAttendance("" + jsonResponse.getString("message"), HomeworkListActivity.this);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case CODE_BATCH_LIST:
                Log.e("batch list",""+response);
                final Gson gson = new Gson();
                try{
                    batchListModel = gson.fromJson(response,BatchListModel.class);
                    if(batchListModel.getStatus().equals(Constants.SUCCESS_CODE)){
                        if(batchListModel.getBatches()!=null && batchListModel.getBatches().size()>0){
                            mAlbatches = new ArrayList<>();

                            /* now show only running batch list stuff here */

                            for(int i = 0 ; i < batchListModel.getBatches().size() ; i++){
                                if(batchListModel.getBatches().get(i).getStatus().equalsIgnoreCase("Running")){
                                    mAlbatches.add(batchListModel.getBatches().get(i));
                                }
                            }

                            setBatchListAdapter(mAlbatches);

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }

    }

    private void setBatchListAdapter(ArrayList<BatchListModel.Batch> mAlbatches) {
        BatchListModel.Batch batch = new BatchListModel.Batch();
        batch.setId(121);
        batch.setAlias("All About");
        batch.setBranch("Vastral");
        batch.setName("All");
        batch.setStatus("Running");

        mAlbatches.add(0,batch);

        homeWorkBatchListAdapter = new HomeWorkBatchListAdapter(HomeworkListActivity.this,mAlbatches,this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.HORIZONTAL,false);
        mRvbatches.setLayoutManager(gridLayoutManager);
        mRvbatches.setAdapter(homeWorkBatchListAdapter);
        homeWorkBatchListAdapter.notifyDataSetChanged();
    }

    private void setHomeworkAdapter(ArrayList<HomeworkListModelClass.Homework> mAlhwlist) {
        homeworkListAdapter =
                new HomeworkListAdapter(HomeworkListActivity.this,mAlhwlist,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRvhwlist.setLayoutManager(linearLayoutManager);
        mRvhwlist.setAdapter(homeworkListAdapter);
        homeworkListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOkHttpFailure(int requestId, int statusCode, String response, Throwable error) {

    }

    @Override
    public void onOkHttpFinish(int requestId) {

    }

    @Override
    public void onHomeWorkItemClicked(int position) {
        Intent intent = new Intent(HomeworkListActivity.this,HomeWorkDetailsActivity.class);
        intent.putExtra("hw_id",""+mAlhwlist.get(position).getId());
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void onMoreOptionsClick(final int position, ImageView mIvremovehw) {
        PopupMenu popupMenu = new PopupMenu(HomeworkListActivity.this,mIvremovehw);
        popupMenu.setGravity(Gravity.END);
        popupMenu.inflate(R.menu.homework_remove);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.remove_homework:
                        callFirstOfAllSureDialog(position);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void callFirstOfAllSureDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeworkListActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callApiToDeleteHomework(""+mAlhwlist.get(position).getId());
                mAlhwlist.remove(position);
                homeworkListAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }

    private void callApiToDeleteHomework(String Id) {
        if(!CommonUtil.isInternetAvailable(HomeworkListActivity.this)){
            return;
        }

        new OkHttpRequest(HomeworkListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_DELETE_HOMEWORK,
                RequestParam.removeHomeWork(""+Id),
                RequestParam.getNull(),
                CODE_DELETE_HOMEWORK,
                true,this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBatchClick(int position) {

        if(mAlbatches.get(position).getName().equalsIgnoreCase("All")){
            /*callApiToLoadHomeWorkList();*/
            // do nothing
        }else{
            // call api for homework as per batches
            mStrBatchId = ""+mAlbatches.get(position).getId();
            callApiToLoadHomeWorkListBatchWise();
        }
        homeworkListAdapter.notifyDataSetChanged();
    }

    private void callApiToLoadHomeWorkListBatchWise() {

        if(!CommonUtil.isInternetAvailable(HomeworkListActivity.this)) {
            return;
        }

        new OkHttpRequest(HomeworkListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_HOMEWORK_LIST,
                RequestParam.getHomeWorkListBatchWise(""+getSharedPrefrencesInstance(HomeworkListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,""),
                                                        ""+mStrBatchId),
                RequestParam.getNull(),
                CODE_HOMEWORK_LIST,
                false,this);
    }
}
