

package com.globalitians.employees.utility.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static okhttp3.MultipartBody.FORM;


public class OkHttpRequest {

    private static final String TAG = "OkHttpRequest";
    private String mUrl = "";
    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private RequestBody mRequestBody;
    private Map<String, String> mBody = new HashMap<String, String>();
    private Map<String, String> mHeaders = new HashMap<String, String>();
    private Map<String, File> mFile = new HashMap<String, File>();
    private boolean mIsShowProgressDialog;
    private int mRequestId;
    private Method mRequestType;
    private OkHttpInterface mObjInterface;


    public OkHttpRequest() {

    }

    // Get all requested parameter and initiate API call without File
    public OkHttpRequest(Activity activity,
                         Method requestType,
                         String url, Map<String, String> params,
                         Map<String, String> headers,
                         int type,
                         boolean isShowDialog,
                         OkHttpInterface objInterface) {
        this.mActivity = activity;
        this.mRequestType = requestType;
        this.mBody = params;
        this.mHeaders = headers;
        this.mFile = null;
        this.mIsShowProgressDialog = isShowDialog;
        this.mRequestId = type;
        this.mObjInterface = objInterface;
        this.mRequestBody = null;
        this.mUrl = url;

        // Call API Request
        callWebApiRequest();
    }

    // Get all requested parameter and initiate API call with File
    public OkHttpRequest(Activity activity,
                         Method requestType,
                         String url,
                         Map<String, String> params,
                         Map<String, String> headers,
                         Map<String, File> files,
                         int type,
                         boolean isShowDialog,
                         OkHttpInterface objInterface) {
        this.mActivity = activity;
        this.mRequestType = requestType;
        this.mBody = params;
        this.mHeaders = headers;
        this.mFile = files;
        this.mIsShowProgressDialog = isShowDialog;
        this.mRequestId = type;
        this.mObjInterface = objInterface;
        this.mRequestBody = null;
        this.mUrl = url;

        // Call API Request
        callWebApiRequest();
    }

    public static void cancelOkHttpRequest(String url) {
        try {
            HTTPUtils.cancelRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void callWebApiRequest() {
        // Request Method
        switch (mRequestType) {
            case GET:
                mUrl = generateGetUrl();
                new networkAsyncCall().execute();
                break;
            case POST:
                mRequestBody = generatePostUrl();

                if (mRequestBody != null) {
                    new networkAsyncCall().execute();
                } else {
                }
                break;
            case DELETE:
//                LoopjApiInitializer.delete(mUrl, requestParams, mHeaders, asyncHttpResponseHandler);
                break;
            default:
                break;
        }
    }

    // Show progress
    private void showProgress() {
        if (mIsShowProgressDialog) {
            mProgressDialog = getProgressDialog(mActivity);
        }
    }

    // Dismiss progress
    private void dismissProgress() {
        if (mIsShowProgressDialog) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    private ProgressDialog getProgressDialog(Context c) {
        ProgressDialog pDialog = new ProgressDialog(c);
        pDialog.setMessage("Please wait..");
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    // Create Get url using HashMap
    private String generateGetUrl() {
        boolean isFirstParam = true;
        for (String key : mBody.keySet()) {
            if (isFirstParam) {
                mUrl = mUrl + "?" + key + "=" + mBody.get(key);
                isFirstParam = false;
            } else {
                mUrl = mUrl + "&" + key + "=" + mBody.get(key);
            }
        }
        return mUrl;
    }

    // Create RequestBody using HashMap
    private RequestBody generatePostUrl() {
        if (mBody != null && mBody.size() < 1) {
            return null;
        }
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(FORM);
        for (String key : mBody.keySet()) {
            multipartBuilder.addFormDataPart("" + key, "" + mBody.get(key));
        }

        if (mFile != null) {
            for (String key : mFile.keySet()) {
                String strValue = mFile.get("" + key).toString();
                if (strValue != null && key.contains("profileimage")) {
                    Log.e(">>> file",""+mFile.get("" + key));
                    multipartBuilder.addFormDataPart("profileimage", "" + mFile.get("" + key).getName(), RequestBody.create(MediaType.parse("image/*"), mFile.get("" + key)));
                }
                if (strValue != null && key.contains("picfile")) {
                    Log.e(">>> file",""+mFile.get("" + key));
                    multipartBuilder.addFormDataPart("picfile", "" + mFile.get("" + key).getName(), RequestBody.create(MediaType.parse("image/*"), mFile.get("" + key)));
                }
                if (strValue != null && key.contains("syllabusfile")) {
                    Log.e(">>> file",""+mFile.get("" + key));
                    multipartBuilder.addFormDataPart("syllabusfile", "" + mFile.get("" + key).getName(), RequestBody.create(MediaType.parse("application/pdf"), mFile.get("" + key)));
                }
                if (strValue != null && key.contains("register_user_photo")) {
                    Log.e(">>> register",""+mFile.get("" + key));
                    multipartBuilder.addFormDataPart("register_user_photo", "" + mFile.get("" + key).getName(), RequestBody.create(MediaType.parse("image/*"), mFile.get("" + key)));
                }
                if(strValue!=null && key.contains("files"))
                {
                    Log.e(">>> register",""+mFile.get("" + key));
                    multipartBuilder.addFormDataPart(key, "" + mFile.get("" + key).getName(), RequestBody.create(MediaType.parse("image/*"), mFile.get("" + key)));
                }
            }
        }

        RequestBody formBody = multipartBuilder.build();
        return formBody;
    }

    // Request method
    public enum Method {
        POST, GET, DELETE
    }

    // Call request in background thread
    public class networkAsyncCall extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            showProgress();
            mObjInterface.onOkHttpStart(mRequestId);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response = "" + null;
            try {
                if (mRequestType == Method.POST) {
                    response = HTTPUtils.postRun(mUrl, mRequestBody, mHeaders);
                }
                if (mRequestType == Method.GET) {
                    response = HTTPUtils.getRun(mUrl, mHeaders);
                }

            } catch (final IOException e) {
                final String finalResponse = response;
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mObjInterface.onOkHttpFailure(mRequestId, 0, finalResponse, e);
                        }
                    });
                }
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                mObjInterface.onOkHttpSuccess(mRequestId, 1, response);
            }
            dismissProgress();
            mObjInterface.onOkHttpFinish(mRequestId);
        }
    }
}
