package com.meembusoft.photoeditor.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.alexvasilkov.android.commons.ui.Views;
import com.meembusoft.photoeditor.Photo;
import com.meembusoft.photoeditor.R;
import com.meembusoft.photoeditor.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Photo> photos = new ArrayList<>();
    private final OnPhotoClickListener listener;

    public RecyclerAdapter(OnPhotoClickListener photoClickListener) {
        listener = photoClickListener;
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(parent);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag(R.id.tag_item);
                listener.onPhotoClick(pos);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo photo = photos.get(position);

        // Storing item position for click handler
        holder.itemView.setTag(R.id.tag_item, position);
        AppUtil.loadImage(holder.itemView.getContext(), holder.image, photo.getImageUri(), false, false, true);
    }

    public static ImageView getImageView(RecyclerView.ViewHolder holder) {
        return ((ViewHolder) holder).image;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;

        ViewHolder(ViewGroup parent) {
            super(Views.inflate(parent, R.layout.list_image_item));
            image = itemView.findViewById(R.id.list_image);
        }
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(int position);
    }
}