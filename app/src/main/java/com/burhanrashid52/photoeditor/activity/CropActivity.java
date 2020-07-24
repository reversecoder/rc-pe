package com.burhanrashid52.photoeditor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.burhanrashid52.photoeditor.R;
import com.burhanrashid52.photoeditor.base.BaseActivity;
import com.steelkiwi.cropiwa.CropIwaView;

import static com.burhanrashid52.photoeditor.activity.EditImageActivity.IMAGE_URI;

public class CropActivity extends BaseActivity {

    private static final String TAG = CropActivity.class.getSimpleName();
    private CropIwaView cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_crop);

        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.label_crop);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cropView = findViewById(R.id.crop_view);
        Uri imageUri = getIntent().getParcelableExtra(IMAGE_URI);
        cropView.setImageUri(imageUri);

        cropView.configureOverlay().setDynamicCrop(true).apply();
        cropView.configureImage().setImageTranslationEnabled(true).apply();
        cropView.configureImage().setImageScaleEnabled(true).apply();
//        need to add image format
//        cropView.configureOverlay().setBorderColor(value).apply();
//        cropView.configureOverlay().setCornerColor(value).apply();
//        cropView.configureOverlay().setOverlayColor(value).apply();
//        cropView.configureOverlay().setGridColor(value).apply();
//        need to add dashed grid
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
//            cropView.crop(configurator.getSelectedSaveConfig());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}