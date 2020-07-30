package com.meembusoft.photoeditor.adapter;

import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.meembusoft.photoeditor.Photo;
import com.meembusoft.photoeditor.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends RecyclePagerAdapter<PagerAdapter.ViewHolder> {

    private final ViewPager viewPager;
    private List<Photo> photos = new ArrayList<>();

    public PagerAdapter(ViewPager pager) {
        this.viewPager = pager;
    }

    public void setPhotos(List<Photo> photoList){
        photos = photoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup container) {
        ViewHolder holder = new ViewHolder(container);
        holder.image.getController().enableScrollInViewPager(viewPager);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.image.getController().getSettings()
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setRotationEnabled(true)
                .setRestrictRotation(false)
                .setOverscrollDistance(holder.image.getContext(), 32f, 32f)
                .setOverzoomFactor(2f)
                .setFillViewport(true)
                .setFitMethod(Settings.Fit.INSIDE)
                .setBoundsType(Settings.Bounds.NORMAL)
                .setGravity(Gravity.CENTER)
                .setAnimationsDuration(Settings.ANIMATIONS_DURATION);

        Photo photo = photos.get(position);
        AppUtil.loadImage(holder.itemView.getContext(), holder.image, photo.getImageUri(), false, false, true);
    }

    public GestureImageView getImageView(RecyclePagerAdapter.ViewHolder holder) {
        return ((ViewHolder) holder).image;
    }

    static class ViewHolder extends RecyclePagerAdapter.ViewHolder {
        final GestureImageView image;

        ViewHolder(ViewGroup container) {
            super(new GestureImageView(container.getContext()));
            image = (GestureImageView) itemView;
        }
    }
}