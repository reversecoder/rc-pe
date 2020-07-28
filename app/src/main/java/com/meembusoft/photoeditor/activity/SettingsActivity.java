package com.meembusoft.photoeditor.activity;

import android.os.Bundle;

import com.meembusoft.photoeditor.R;
import com.meembusoft.photoeditor.base.BaseActivity;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
    }

    private void initViews() {
    }
}