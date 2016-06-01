package com.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.BaseValue;
import com.cqts.kxg.R;

/**
 * Created by Administrator on 2016/5/5.
 */
public class MyTagView extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private int marginTop = BaseValue.dp2px(10);
    private int marginLeft = BaseValue.dp2px(10);
    private MyTagView.OnTagClickListener l;

    public MyTagView(Context context) {
        super(context);
        this.context = context;
    }

    public MyTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setMyTag(String[] texts) {
        removeAllViews();
        for (int i = 0; i < texts.length; i++) {
            TextView tagText = (TextView) LayoutInflater.from(context).inflate(R.layout
                    .view_mytagview, null);
            tagText.setText(texts[i]);
            tagText.setSingleLine(true);
            addView(tagText);
            tagText.setOnClickListener(this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int childCount = getChildCount();
        int l = 0, t = 0, r = getPaddingLeft() - marginLeft, b = 0, h = 0;
        int height =0;
        if (childCount != 0) {
            View childAt = getChildAt(0);
            height = childAt.getMeasuredHeight() + marginTop;
        }

        for (int i = 0; i < childCount; i++) {
            View  childAt = getChildAt(i);
            int   width = childAt.getMeasuredWidth();
            r = r + width + marginLeft;
            if (r > BaseValue.screenwidth - getPaddingLeft()) {
                ++h;
                r = width + getPaddingLeft();
            }
            childAt.layout(r - width, h * height + marginTop, r, h * height + height);
        }

        //自适应大小   注释取消
        layoutParams.height = 4 * height + marginTop;
        setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        l.onTagClick(v);
    }

    public void setOnTagClickListener(MyTagView.OnTagClickListener l) {
        this.l = l;
    }

    public interface OnTagClickListener {
        void onTagClick(View v);
    }
}
