package com.forudesigns.foru.tools;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;


/**
 * Created by zuoyun on 2016/6/24.
 */
public class MyWebView extends WebView {
    private OnWebScrollChangeListener onWebScrollChangeListener;

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MyWebView(Context context) {
        super(context);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(onWebScrollChangeListener!=null)
            onWebScrollChangeListener.onWebScroll(t,oldt);
    }

    public void setOnWebScrollChangeListener(OnWebScrollChangeListener listener) {
        this.onWebScrollChangeListener = listener;
    }
    public static interface OnWebScrollChangeListener{
        public void onWebScroll(int t,int oldt);
    }
}
