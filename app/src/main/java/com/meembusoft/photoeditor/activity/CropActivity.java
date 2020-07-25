package com.meembusoft.photoeditor.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.PathEffect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.meembusoft.photoeditor.R;
import com.meembusoft.photoeditor.base.BaseActivity;
import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.shape.CropIwaRectShape;

import java.io.File;

import static com.meembusoft.photoeditor.activity.EditImageActivity.IMAGE_URI;

public class CropActivity extends BaseActivity {

    private static final String TAG = CropActivity.class.getSimpleName();
    private CropIwaView cropView;
    private CropIwaSaveConfig.Builder saveConfigBuilder;

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

        saveConfigBuilder = new CropIwaSaveConfig.Builder(createNewEmptyFile(CropActivity.this));

        cropView = findViewById(R.id.crop_view);
        Uri imageUri = getIntent().getParcelableExtra(IMAGE_URI);
        cropView.setImageUri(imageUri);

        cropView.configureOverlay().setCropShape(new CropIwaRectShape(cropView.configureOverlay())).apply();
        cropView.configureOverlay().setDynamicCrop(true).apply();
        cropView.configureImage().setImageTranslationEnabled(true).apply();
        cropView.configureImage().setImageScaleEnabled(true).apply();
        saveConfigBuilder.setCompressFormat(Bitmap.CompressFormat.PNG);
        cropView.configureOverlay().setBorderColor(Color.YELLOW).apply();
        cropView.configureOverlay().setCornerColor(Color.RED).apply();
        cropView.configureOverlay().setOverlayColor(ContextCompat.getColor(CropActivity.this, R.color.cropiwa_default_overlay_color)).apply();
        cropView.configureOverlay().setGridColor(Color.YELLOW).apply();

        // Dash
        int dashLength = dpToPixels(CropActivity.this, 2);
        int spaceLength = dpToPixels(CropActivity.this, 4);
        float[] intervals = {dashLength, spaceLength};
        PathEffect effect = new DashPathEffect(intervals, 0);
        cropView.configureOverlay().getCropShape().getGridPaint().setPathEffect(effect);
        cropView.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            cropView.crop(saveConfigBuilder.build());
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public Uri createNewEmptyFile(Context context) {
        return Uri.fromFile(new File(
                context.getApplicationContext().getFilesDir(),
                System.currentTimeMillis() + ".png"));
    }

    public int dpToPixels(Context context, int dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return Math.round(dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}