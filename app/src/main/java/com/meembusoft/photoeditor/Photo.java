package com.meembusoft.photoeditor;

import android.net.Uri;

public class Photo {

    Uri imageUri;

    public Photo(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}