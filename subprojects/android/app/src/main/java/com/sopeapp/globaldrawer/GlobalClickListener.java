package com.sopeapp.globaldrawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class GlobalClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener channelClickListener;
    GestureDetector channelGestureDetector;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public GlobalClickListener(Context context, OnItemClickListener listener) {
        channelClickListener = listener;
        channelGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && channelClickListener != null && channelGestureDetector.onTouchEvent(e)) {
            channelClickListener.onItemClick(childView, view. getChildAdapterPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
