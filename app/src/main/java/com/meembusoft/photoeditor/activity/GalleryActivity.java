package com.meembusoft.photoeditor.activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.transition.GestureTransitions;
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.alexvasilkov.gestures.transition.tracker.SimpleTracker;
import com.meembusoft.photoeditor.Photo;
import com.meembusoft.photoeditor.R;
import com.meembusoft.photoeditor.adapter.PagerAdapter;
import com.meembusoft.photoeditor.adapter.RecyclerAdapter;
import com.meembusoft.photoeditor.base.BaseActivity;
import com.meembusoft.photoeditor.util.AppUtil;

import java.io.File;
import java.util.List;

public class GalleryActivity extends BaseActivity {
//
//    private static final String TAG = GalleryActivity.class.getSimpleName();
//    private static final int PAGE_SIZE = 30;
//    private static final int NO_POSITION = -1;
//    private PhotoListAdapter gridAdapter;
//    private PhotoPagerAdapter pagerAdapter;
//    private RecyclerView grid;

    private RecyclerView list;
    private ViewPager pager;
    private View background;

    private RecyclerAdapter recyclerAdapter;
    private PagerAdapter pagerAdapter;
    private ViewsTransitionAnimator<Integer> animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Setting new photos list
        String folderPath = Environment.getExternalStorageDirectory() + File.separator + AppUtil.getApplicationName(GalleryActivity.this);
        List<Photo> photos = AppUtil.getAllPhotos(folderPath);

        // Initializing ListView
        list = findViewById(R.id.recycler_list);
        recyclerAdapter = new RecyclerAdapter(new RecyclerAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(int position) {
                // Animating image transition from given list position into pager
                animator.enter(position, true);
            }
        });
        list.setLayoutManager(new GridLayoutManager(GalleryActivity.this, 3));
        list.setAdapter(recyclerAdapter);
        recyclerAdapter.setPhotos(photos);

        // Initializing ViewPager
        pager = findViewById(R.id.recycler_pager);
        pagerAdapter = new PagerAdapter(pager);
        pager.setAdapter(pagerAdapter);
        pager.setPageMargin(12);
        pagerAdapter.setPhotos(photos);

        // Initializing images animator. It requires us to provide FromTracker and IntoTracker items
        // that are used to find images views for particular item IDs in the list and in the pager
        // to keep them in sync.
        // In this example we will use SimpleTracker which will track images by their positions,
        // if you have a more complicated case see further examples.
        final SimpleTracker listTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int position) {
                RecyclerView.ViewHolder holder = list.findViewHolderForLayoutPosition(position);
                return holder == null ? null : RecyclerAdapter.getImageView(holder);
            }
        };

        final SimpleTracker pagerTracker = new SimpleTracker() {
            @Override
            public View getViewAt(int position) {
                RecyclePagerAdapter.ViewHolder holder = pagerAdapter.getViewHolder(position);
                return holder == null ? null : pagerAdapter.getImageView(holder);
            }
        };

        animator = GestureTransitions.from(list, listTracker).into(pager, pagerTracker);

        // Setting up background animation during image transition
        background = findViewById(R.id.recycler_full_background);
        animator.addPositionUpdateListener(new ViewPositionAnimator.PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                applyImageAnimationState(position);
            }
        });
    }

    private void applyImageAnimationState(float position) {
        background.setVisibility(position == 0f ? View.INVISIBLE : View.VISIBLE);
        background.setAlpha(position);
    }

    @Override
    public void onBackPressed() {
        // We should leave full image mode instead of closing the screen
        if (!animator.isLeaving()) {
            animator.exit(true);
        } else {
            super.onBackPressed();
        }
    }

//    private void initGrid() {
//        grid = findViewById(R.id.recycler_list);
//        grid.setLayoutManager(new GridLayoutManager(this, 3));
//
//        gridAdapter = new PhotoListAdapter(new PhotoListAdapter.OnPhotoListener() {
//            @Override
//            public void onPhotoClick(int position) {
//
//            }
//        });
//        gridAdapter.setLoadingOffset(PAGE_SIZE / 2);
//        gridAdapter.setCallbacks(new EndlessRecyclerAdapter.LoaderCallbacks() {
//            @Override
//            public boolean canLoadNextItems() {
//                return gridAdapter.canLoadNext();
//            }
//
//            @Override
//            public void loadNextItems() {
//                // We should either load all items that were loaded before state save / restore,
//                // or next page if we already loaded all previously shown items
////                int count = Math.max(savedPhotoCount, gridAdapter.getCount() + PAGE_SIZE);
////                Events.create(FlickrApi.LOAD_IMAGES_EVENT).param(count).post();
//            }
//        });
//        grid.setAdapter(gridAdapter);
//
//        // Setting new photos list
//        String rootPath = Environment.getExternalStorageDirectory() + File.separator + AppUtil.getApplicationName(GalleryActivity.this);
//        // image naming and path  to include sd card  appending name you choose for file
//        String filePath = rootPath + File.separator + "PhotoEditor_29-Jul-2020, Wednesday, 08:17:50 AM.png";
//        List<Photo> photos = new ArrayList<Photo>(){{
//            add(new Photo("PhotoEditor_29-Jul-2020, Wednesday, 08:17:50 AM.png", Uri.fromFile(new File(filePath))));
//        }};
//        gridAdapter.setPhotos(photos, false);
//        gridAdapter.onNextItemsLoaded();
//    }
}