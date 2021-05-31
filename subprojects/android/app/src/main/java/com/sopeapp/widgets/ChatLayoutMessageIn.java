package com.sopeapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ChatLayoutMessageIn extends RelativeLayout {


    public static final int USER_CHAT_ELEMENT_COUNT = 3;
    public static final float DENSITY_INDEPENDENT_PIXEL_ADJUST_FACTOR = 10F;
    public static final int PADDING_TOP_PIXELS = 10;

    public ChatLayoutMessageIn(Context context) {
        super(context);
    }

    public ChatLayoutMessageIn(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ChatLayoutMessageIn(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (getChildCount() < USER_CHAT_ELEMENT_COUNT)
            return;

        int messageHeight = getChildAt(1).getMeasuredHeight();
        int messageWidth = getChildAt(0).getMeasuredWidth()  + getChildAt(1).getMeasuredWidth();
        int layoutWidth = (int) (messageWidth + convertDpToPixel(DENSITY_INDEPENDENT_PIXEL_ADJUST_FACTOR, getContext()));


        setMeasuredDimension(layoutWidth, messageHeight + PADDING_TOP_PIXELS);
    }


    public static float convertDpToPixel(float densityIndependentPixel, Context context) {
        return densityIndependentPixel * context.getResources().getDisplayMetrics().density;
    }

}
