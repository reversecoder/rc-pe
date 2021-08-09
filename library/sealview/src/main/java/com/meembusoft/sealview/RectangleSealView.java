package com.meembusoft.sealview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatTextView;

public class RectangleSealView extends AppCompatTextView {
    private int mLeftSize = 24;
    private int mRightSize = 50;
    private String mRightString;
    private String mLeftString;
    private float mR = -20f;

    public RectangleSealView(Context context) {
        super(context);
    }

    public RectangleSealView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.sealstyle);
        setGravity(Gravity.CENTER);
        setTextColor(getResources().getColor(android.R.color.holo_red_light));
        setBackground(getResources().getDrawable(R.drawable.rectangle_border_tansparent_bg));
//        setRotationSeal(mR);
        setRotation(mR);

        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int index = typedArray.getIndex(i);
            if (index == R.styleable.sealstyle_left_text) {
                mLeftString = typedArray.getString(index);
            } else if (index == R.styleable.sealstyle_right_text) {

                mRightString = typedArray.getString(index);
            } else if (index == R.styleable.sealstyle_left_text_size) {
                mLeftSize = typedArray.getDimensionPixelSize(index, 12);
            } else if (index == R.styleable.sealstyle_right_text_size) {
                mRightSize = typedArray.getDimensionPixelSize(index, 15);
            }
        }
        typedArray.recycle();
        reDraw();
    }

    private void setRotationSeal(float r) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(this, "rotation", 0f, r);
        rotation.setDuration(0);
        rotation.start();
    }

    private void reDraw() {
        StringBuilder sb = new StringBuilder();

        int leftIndex = 0;
        if (!TextUtils.isEmpty(mLeftString)) {
            leftIndex = mLeftString.length();
            sb.append(mLeftString);
        }

        int rightIndex = 0;
        if (!TextUtils.isEmpty(mRightString)) {
            rightIndex = mRightString.length();
            sb.append(mRightString);
        }
        setText(sb);
        Spannable span = new SpannableString(getText());
        span.setSpan(new AbsoluteSizeSpan(mRightSize), leftIndex, getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new AbsoluteSizeSpan(mLeftSize), 0, leftIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(span);
    }

    public void setRightString(String rightString) {
        mRightString = rightString;
        reDraw();
    }

    public void setLeftString(String leftString) {
        mLeftString = leftString;
        reDraw();
    }

    public void setRightSize(int rightSize) {
        mRightSize = rightSize;
        reDraw();
    }

    public void setLeftSize(int leftSize) {
        mLeftSize = leftSize;
        reDraw();
    }

    public void setSealInfo(String leftString, int leftSize, String rightString, int rightSize, float rotation) {
        mLeftString = leftString;
        mLeftSize = leftSize;
        mRightString = rightString;
        mRightSize = rightSize;
        mR = rotation;
        reDraw();
    }
}