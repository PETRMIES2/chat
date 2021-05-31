package com.sopeapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

public class ChatLayoutMessageOut extends RelativeLayout {


    public static final int USER_CHAT_ELEMENT_COUNT = 2;
    public static final float DENSITY_INDEPENDENT_PIXEL_ADJUST_FACTOR = 10F;
    public static final int MESSAGE_TOP_MARGIN = 10;

    public ChatLayoutMessageOut(Context context) {
        super(context);
    }

    public ChatLayoutMessageOut(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public ChatLayoutMessageOut(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        if (getChildCount() < USER_CHAT_ELEMENT_COUNT)
            return;

        int messageHeight = getChildAt(0).getMeasuredHeight();
        int messageWidth = getChildAt(0).getMeasuredWidth();

        DisplayMetrics size = getContext().getResources().getDisplayMetrics();
        int layoutWidth = 0;
        if (messageWidth > size.widthPixels) {
            layoutWidth = size.widthPixels - 100;
        } else {
            layoutWidth = (int) (layoutWidth + messageWidth + convertDpToPixel(DENSITY_INDEPENDENT_PIXEL_ADJUST_FACTOR, getContext()));
        }


        setMeasuredDimension(layoutWidth, messageHeight);
    }


    public static float convertDpToPixel(float densityIndependentPixel, Context context) {
        return densityIndependentPixel * context.getResources().getDisplayMetrics().density;
    }

}
