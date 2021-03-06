package com.base.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cqts.kxg.R;

/**
 * 圆角矩形布局<br>
 * 所有view/布局-都可以用
 */
public class RoundImageView extends ImageView {
    private float roundLayoutRadius = 0f;
    private int bgColor;
    Context context;
    AttributeSet attrs;
    public  Path roundPath;
    private int measuredHeight;

    public RoundImageView(Context context) {
        this(context, null);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (roundLayoutRadius == 0) {
            getAttrs(context, attrs);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (roundPath == null && roundLayoutRadius > 0 ) {
            setWillNotDraw(false);//如果你继承的是ViewGroup,注意此行,否则draw方法是不会回调的;
            roundPath = new Path();
            RectF rectF = new RectF();
            measuredHeight = getMeasuredHeight();
            rectF.set(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
            //添加一个圆角矩形到path中, 如果要实现任意形状的View, 只需要手动添加path就行
            roundPath.addRoundRect(rectF, roundLayoutRadius, roundLayoutRadius, Path.Direction.CW);
        }

        if (roundPath != null&& measuredHeight!= getMeasuredHeight()){
            setWillNotDraw(false);//如果你继承的是ViewGroup,注意此行,否则draw方法是不会回调的;
            roundPath = new Path();
            RectF rectF = new RectF();
            measuredHeight = getMeasuredHeight();
            rectF.set(0f, 0f, getMeasuredWidth(), getMeasuredHeight());
            //添加一个圆角矩形到path中, 如果要实现任意形状的View, 只需要手动添加path就行
            roundPath.addRoundRect(rectF, roundLayoutRadius, roundLayoutRadius, Path.Direction.CW);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (roundPath != null) {
            canvas.drawColor(bgColor);
            canvas.clipPath(roundPath);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG|Paint.ANTI_ALIAS_FLAG));
        }
        super.draw(canvas);
    }

    //获得参数
    void getAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundImageView);
        //圆角弧度
        roundLayoutRadius = typedArray.getDimensionPixelSize(R.styleable
                .RoundImageView_mylayoutradius1, 0);
        // 背景参数
        bgColor = typedArray.getColor(R.styleable.RoundImageView_mylayoutbg1, Color.WHITE);
        typedArray.recycle();
    }
}