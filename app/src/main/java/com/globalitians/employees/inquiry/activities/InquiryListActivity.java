package com.globalitians.employees.inquiry.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.globalitians.employees.R;
import com.globalitians.employees.inquiry.adapter.InquiryListAdapter;
import com.globalitians.employees.inquiry.model.ModelInquiry;
import com.globalitians.employees.utility.CommonUtil;
import com.globalitians.employees.utility.Constants;
import com.globalitians.employees.utility.network.OkHttpInterface;
import com.globalitians.employees.utility.network.OkHttpRequest;
import com.globalitians.employees.utility.network.RequestParam;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.globalitians.employees.utility.CommonUtil.getSharedPrefrencesInstance;
import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;
import static com.globalitians.employees.utility.Constants.CODE_DELETE_INQUIRIES;
import static com.globalitians.employees.utility.Constants.CODE_INQUIRIES;
import static com.globalitians.employees.utility.Constants.KEY_EMPLOYEE_BRANCH_ID;
import static com.globalitians.employees.utility.Constants.KEY_INQUIRY_DATE;
import static com.globalitians.employees.utility.Constants.KEY_INTENT_INQUIRY_ID;
import static com.globalitians.employees.utility.Constants.REQUEST_PHONE_CALL;

public class InquiryListActivity extends AppCompatActivity implements OkHttpInterface, View.OnClickListener, InquiryListAdapter.OnInquiryListListener {

    private ShimmerFrameLayout mShimmerContainer;
    private ListView mLvCourses;
    private TextView mTvAddNewInquiry;
    private SwipeRefreshLayout mSwipeToRefresh;

    private SearchView mSerachViewInquiry;
    private View mListFooter;

    private ArrayList<ModelInquiry.Inquiry> mArrListInquiry = null;
    private ArrayList<ModelInquiry.Inquiry> mArrListInquirySearch = null;
    private InquiryListAdapter mAdapterInquiryList = null;
    private int callPosition;
    private int deletePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(InquiryListActivity.this);
        setContentView(R.layout.activity_inquiry_list);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        findViews();
        callApiToLoadInquiryList();
    }

    private void init() {
        if (mArrListInquiry != null && mArrListInquiry.size() > 0) {
            mArrListInquiry.clear();
        }
        mArrListInquiry = new ArrayList<>();
        mArrListInquirySearch = new ArrayList<>();
        mListFooter = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_list_footer, null);
    }

    private void callApiToLoadInquiryList() {
        if (!CommonUtil.isInternetAvailable(InquiryListActivity.this)) {
            return;
        }

        mShimmerContainer.setVisibility(View.VISIBLE);
        mShimmerContainer.startShimmerAnimation();
        new OkHttpRequest(InquiryListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_INQUIRIES,
                RequestParam.inquiriesList(
                        ""+getSharedPrefrencesInstance(InquiryListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_INQUIRIES,
                false, this);
    }

    private void findViews() {
        mSerachViewInquiry = (SearchView) findViewById(R.id.searchViewInquiry);
        setSearchViewListener();
        mShimmerContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerContainer.setVisibility(View.GONE);
        mLvCourses = findViewById(R.id.lv_inquiries);
        mTvAddNewInquiry = findViewById(R.id.tv_add_new_inquiry);
        mTvAddNewInquiry.setOnClickListener(this);
        mSwipeToRefresh = findViewById(R.id.swipeToRefreshInquiryList);
        mSwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mArrListInquiry != null && mArrListInquiry.size() > 0) {
                    mArrListInquiry.clear();
                }
                mArrListInquiry = new ArrayList<>();
                callApiToLoadInquiryList();
            }
        });
    }

    private void setSearchViewListener() {
        mSerachViewInquiry.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryString) {
                String query=queryString.toLowerCase();
                mArrListInquirySearch.clear();
                if (mArrListInquiry != null && mArrListInquiry.size() > 0) {
                    for (int i = 0; i < mArrListInquiry.size(); i++) {
                        if (mArrListInquiry.get(i).getFname().toLowerCase().contains(query) ||
                                mArrListInquiry.get(i).getLname().toLowerCase().contains(query) ||
                                mArrListInquiry.get(i).getInquiryDate().toLowerCase().contains(query) ||
                                mArrListInquiry.get(i).getContact().toLowerCase().contains(query) ||
                                mArrListInquiry.get(i).getCourses().get(0).getName().contains(query)) {
                            if (mArrListInquirySearch != null && mArrListInquirySearch.size() > 0) {
                                for (int j = 0; j < mArrListInquirySearch.size(); j++) {
                                    if (mArrListInquirySearch.get(j).getId().equals(mArrListInquiry.get(i).getId())) {
                                        //nothing
                                    } else {
                                        mArrListInquirySearch.add(mArrListInquiry.get(i));
                                    }
                                }
                            } else {
                                mArrListInquirySearch.add(mArrListInquiry.get(i));
                            }
                        }
                    }
                }
                setSearchInquiryListAdapter();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                    if(mArrListInquirySearch!=null && mArrListInquirySearch.size()>0){
                        mArrListInquirySearch.clear();
                    }

                    if (mArrListInquiry != null && mArrListInquiry.size() > 0) {
                        for (int i = 0; i < mArrListInquiry.size(); i++) {
                            if (mArrListInquiry.get(i).getFname().toLowerCase().contains(query) ||
                                    mArrListInquiry.get(i).getLname().toLowerCase().contains(query) ||
                                    mArrListInquiry.get(i).getInquiryDate().toLowerCase().contains(query) ||
                                    mArrListInquiry.get(i).getContact().toLowerCase().contains(query)) {

                                /*if(mArrListInquiry.get(i).getCourses()!=null){
                                    for(int count=0;count<mArrListInquiry.get(i).getCourses().size();count++){
                                        if(mArrListInquiry.get(i).getCourses().get(count).getName().contains(query)) {
                                            if (mArrListInquirySearch != null && mArrListInquirySearch.size() > 0) {
                                                for (int j = 0; j < mArrListInquirySearch.size(); j++) {
                                                    if (mArrListInquirySearch.get(j).getId().equals(mArrListInquiry.get(i).getId())) {
                                                        //nothing
                                                    } else {
                                                        mArrListInquirySearch.add(mArrListInquiry.get(i));
                                                    }
                                                }
                                            } else {
                                                mArrListInquirySearch.add(mArrListInquiry.get(i));
                                            }
                                        }
                                    }
                                }*/

                                if (mArrListInquirySearch != null && mArrListInquirySearch.size() > 0) {
                                    for (int j = 0; j < mArrListInquirySearch.size(); j++) {
                                        if (mArrListInquirySearch.get(j).getId().equals(mArrListInquiry.get(i).getId())) {
                                            //nothing
                                        } else {
                                            mArrListInquirySearch.add(mArrListInquiry.get(i));
                                        }
                                    }
                                } else {
                                    mArrListInquirySearch.add(mArrListInquiry.get(i));
                                }
                            }
                        }
                    }
                    setSearchInquiryListAdapter();
                    return false;
                }
        });

        mSerachViewInquiry.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSerachViewInquiry.setQueryHint("");
                setInquiryListAdapter();
                return false;
            }
        });
    }

    @Override
    public void onOkHttpStart(int requestId) {

    }

    @Override
    public void onOkHttpSuccess(int requestId, int statusCode, String response) {
        Log.e(">>>> response:",""+response);
        if (response == null) {
            response = "null";
        }

        switch (requestId) {
            case CODE_INQUIRIES:
                final Gson inquiryListGson = new Gson();
                try {
                    ModelInquiry modelInquiryList = inquiryListGson
                            .fromJson(response, ModelInquiry.class);

                    if (modelInquiryList.getStatus().equals(Constants.SUCCESS_CODE)) {
                        mArrListInquiry = modelInquiryList.getInquiries();
                        if (mArrListInquiry != null && mArrListInquiry.size() > 0) {
                            setInquiryListAdapter();
                        }
                    }
                    if (mSwipeToRefresh.isRefreshing()) {
                        mSwipeToRefresh.setRefreshing(false);
                    }
                    mShimmerContainer.stopShimmerAnimation();
                    mShimmerContainer.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CODE_DELETE_INQUIRIES:
                try {
                    JSONObject jsonDeleteInquiry = new JSONObject(response);
                    if (jsonDeleteInquiry.has("status")) {
                        if (jsonDeleteInquiry.getString("status").equalsIgnoreCase("success")) {
                            playSoundForAttendance("" + jsonDeleteInquiry.getString("message"),InquiryListActivity.this);
                            Toast.makeText(this, "" + jsonDeleteInquiry.getString("message"), Toast.LENGTH_SHORT).show();
                            mArrListInquiry.remove(deletePosition);
                            mAdapterInquiryList.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void setInquiryListAdapter() {
        mAdapterInquiryList = new InquiryListAdapter(InquiryListActivity.this, mArrListInquiry, this);
        mLvCourses.setAdapter(mAdapterInquiryList);
        if (mLvCourses.getFooterViewsCount() == 0) {
            mLvCourses.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
    }

    private void setSearchInquiryListAdapter() {
        mAdapterInquiryList=null;
        mAdapterInquiryList = new InquiryListAdapter(InquiryListActivity.this, mArrListInquirySearch, this);
        mLvCourses.setAdapter(mAdapterInquiryList);
        if (mLvCourses.getFooterViewsCount() == 0) {
            mLvCourses.addFooterView(mListFooter);
        }
        mListFooter.setVisibility(View.GONE);
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
            case R.id.tv_add_new_inquiry:
                startActivity(new Intent(InquiryListActivity.this, AddInquiryActivity.class));
                break;
        }
    }

    @Override
    public void onInquiryRawClick(int position) {
        startActivity(new Intent(InquiryListActivity.this, AddInquiryActivity.class)
                .putExtra(KEY_INTENT_INQUIRY_ID, "" + mArrListInquiry.get(position).getId()
                )
        .putExtra(KEY_INQUIRY_DATE,""+mArrListInquiry.get(position).getInquiryDate()));
    }

    @Override
    public void onCallClick(int position) {
        callPosition = position;
        checkAndRequestCallPermission(position);
    }

    @Override
    public void onDeleteInquiry(int position) {
        deletePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(InquiryListActivity.this);
        builder.setMessage("Are you Sure ?");
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callApiToDeleteInquiry();
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //nothing
            }
            // Create the AlertDialog object and return it
        });

        builder.show();
    }

    private void callApiToDeleteInquiry() {
        if (!CommonUtil.isInternetAvailable(InquiryListActivity.this)) {
            return;
        }

        new OkHttpRequest(InquiryListActivity.this,
                OkHttpRequest.Method.POST,
                Constants.WS_DELETE_INQUIRIES,
                RequestParam.deleteInquiries(mArrListInquiry.get(deletePosition).getSlug(),
                        ""+getSharedPrefrencesInstance(InquiryListActivity.this).getString(KEY_EMPLOYEE_BRANCH_ID,"")),
                RequestParam.getNull(),
                CODE_DELETE_INQUIRIES,
                true, this);
    }

    private void checkAndRequestCallPermission(int position) {
        if (ContextCompat.checkSelfPermission(InquiryListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InquiryListActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        } else {
            callNow("" + mArrListInquiry.get(position).getContact());
        }
    }

    public void callNow(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            playSoundForAttendance("Please grant call permission from settings.",InquiryListActivity.this);
            Toast.makeText(this, "Please grant call permission from settings.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            startActivity(intent);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callNow("" + mArrListInquiry.get(callPosition).getContact());
            } else {
                playSoundForAttendance("Call Permission not granted.",InquiryListActivity.this);
                Toast.makeText(this, "Call Permission not granted.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
