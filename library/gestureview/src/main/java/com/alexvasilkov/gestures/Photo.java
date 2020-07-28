package com.alexvasilkov.gestures;

import android.net.Uri;

public class Photo {

    private String name;
    private Uri uri;

    public Photo(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}