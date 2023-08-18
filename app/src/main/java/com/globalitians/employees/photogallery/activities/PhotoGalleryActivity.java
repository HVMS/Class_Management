package com.globalitians.employees.photogallery.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.globalitians.employees.R;

import static com.globalitians.employees.utility.CommonUtil.playSoundForAttendance;
import static com.globalitians.employees.utility.CommonUtil.setFullScreenMode;

public class PhotoGalleryActivity extends AppCompatActivity {
    private RecyclerView mRvPhotoGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode(PhotoGalleryActivity.this);
        setContentView(R.layout.activity_photo_gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViews();
        playSoundForAttendance("Here You can see the photos of GIT",PhotoGalleryActivity.this);
    }

    private void findViews() {
        mRvPhotoGallery=findViewById(R.id.rv_photo_gallery);

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

}
