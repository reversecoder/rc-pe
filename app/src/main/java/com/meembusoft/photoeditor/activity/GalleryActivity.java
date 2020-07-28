package com.meembusoft.photoeditor.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alexvasilkov.gestures.adapter.EndlessRecyclerAdapter;
import com.alexvasilkov.gestures.adapter.PhotoListAdapter;
import com.alexvasilkov.gestures.adapter.PhotoPagerAdapter;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.meembusoft.photoeditor.R;
import com.meembusoft.photoeditor.base.BaseActivity;

public class GalleryActivity extends BaseActivity {

    private static final String TAG = GalleryActivity.class.getSimpleName();
    private static final int PAGE_SIZE = 30;
    private static final int NO_POSITION = -1;
    private PhotoListAdapter gridAdapter;
    private PhotoPagerAdapter pagerAdapter;
    private RecyclerView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initGrid();
    }

    private void initGrid() {
        grid = findViewById(R.id.recyclerView);
        grid.setLayoutManager(new GridLayoutManager(this, 3));

        gridAdapter = new PhotoListAdapter(new PhotoListAdapter.OnPhotoListener() {
            @Override
            public void onPhotoClick(int position) {

            }
        });
        gridAdapter.setLoadingOffset(PAGE_SIZE / 2);
        gridAdapter.setCallbacks(new EndlessRecyclerAdapter.LoaderCallbacks() {
            @Override
            public boolean canLoadNextItems() {
                return gridAdapter.canLoadNext();
            }

            @Override
            public void loadNextItems() {
                // We should either load all items that were loaded before state save / restore,
                // or next page if we already loaded all previously shown items
//                int count = Math.max(savedPhotoCount, gridAdapter.getCount() + PAGE_SIZE);
//                Events.create(FlickrApi.LOAD_IMAGES_EVENT).param(count).post();
            }
        });
        grid.setAdapter(gridAdapter);
    }
}