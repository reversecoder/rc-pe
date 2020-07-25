package com.meembusoft.photoeditor.filters;

import ja.burhanrashid52.photoeditor.enumeration.PhotoFilter;

public interface FilterListener {
    void onFilterSelected(PhotoFilter photoFilter);
}