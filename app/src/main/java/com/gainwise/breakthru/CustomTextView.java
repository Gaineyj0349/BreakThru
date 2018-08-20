package com.gainwise.breakthru;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
        setFont();
    }
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "FjallaOne-Regular.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}
