package com.example.ywx.scrollselectorview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/21.
 */

public class ScrollSelectorView extends View {
    private final static int seeView=5;//可见数目
    private float x;
    private float anArea;//当前屏幕每个元素所占有的大小
    private Paint selectorPaint,textPaint;
    private int currentPosition;//当前选中的元素下表
    private List<String> dataList;
    private float offset=0;//偏移量
    private int width,height;//空间的宽高
    private float selectoredTextSize,commonTextSize;

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    public float getSelectoredTextSize() {
        return selectoredTextSize;
    }

    public void setSelectoredTextSize(float selectoredTextSize) {
        this.selectoredTextSize = selectoredTextSize;
    }

    public float getCommonTextSize() {
        return commonTextSize;
    }

    public void setCommonTextSize(float commonTextSize) {
        this.commonTextSize = commonTextSize;
    }

    public int getSelectoredColor() {
        return selectoredColor;
    }

    public void setSelectoredColor(int selectoredColor) {
        this.selectoredColor = selectoredColor;
    }

    public int getCommonColor() {
        return commonColor;
    }

    public void setCommonColor(int commonColor) {
        this.commonColor = commonColor;
    }
    public void nextPosition()
    {
        if(currentPosition<dataList.size()-1)
        {
            currentPosition++;
            invalidate();
        }
    }
    public void perPosition()
    {
        if(currentPosition>0)
        {
            currentPosition--;
            invalidate();
        }
    }
    public String getCurrentString()
    {
        return dataList.get(currentPosition);
    }
    private int selectoredColor,commonColor;
    public ScrollSelectorView(Context context) {
        this(context,null);
    }

    public ScrollSelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScrollSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta=context.obtainStyledAttributes(attrs,R.styleable.ScrollSelectorView);
        commonTextSize=ta.getFloat(R.styleable.ScrollSelectorView_commonTextSize,40);
        commonColor=ta.getInt(R.styleable.ScrollSelectorView_commonColor,Color.GRAY);
        selectoredTextSize=ta.getFloat(R.styleable.ScrollSelectorView_selectoredTextSize,60);
        selectoredColor=ta.getInt(R.styleable.ScrollSelectorView_selectoredColor,Color.BLACK);
        ta.recycle();
        init();
    }

    private void init() {
        dataList=new ArrayList<>();
        dataList.add("1000");
        dataList.add("2000");
        dataList.add("3000");
        dataList.add("4000");
        dataList.add("5000");
        dataList.add("6000");
        dataList.add("7000");
        selectorPaint=new Paint();
        selectorPaint.setAntiAlias(true);
        selectorPaint.setColor(selectoredColor);
        selectorPaint.setTextSize(selectoredTextSize);
        textPaint=new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(commonColor);
        textPaint.setTextSize(commonTextSize);
        currentPosition=dataList.size()/2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
        anArea=w/seeView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float textWidth=0;
        float textHeight;
        if(currentPosition>=0&&currentPosition<dataList.size())
        {
            textWidth=selectorPaint.measureText(dataList.get(currentPosition));
            textHeight=selectorPaint.descent()-selectorPaint.ascent();
            canvas.drawText(dataList.get(currentPosition),width/2-textWidth/2+offset,height/2+textHeight/2,selectorPaint);
        }
        for(int i=0;i<dataList.size();i++)
        {
            textHeight=textPaint.descent()-textPaint.ascent();
            if(currentPosition>0&&currentPosition<dataList.size()-1)
            {
                textWidth=(textPaint.measureText(dataList.get(currentPosition+1))+textPaint.measureText(dataList.get(currentPosition-1)))/2;
            }
            if(i!=currentPosition)
            {
                canvas.drawText(dataList.get(i),(i-currentPosition)*anArea+width/2+offset-textWidth/2,height/2+textHeight,textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x=event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float movex=(event.getX()-x);
                if(currentPosition!=0&&currentPosition!=dataList.size()-1)
                {
                    offset=movex;
                }
                else{
                    offset=movex/2.5f;
                }
                if(movex>0)
                {
                    if(Math.abs(movex)>=anArea) {
                        if (currentPosition > 0) {
                            offset=0;
                            currentPosition--;
                            x = event.getX();
                        }
                    }
                } else
                {
                    if(Math.abs(movex)>=anArea) {
                        if (currentPosition < dataList.size() - 1) {
                            offset=0;
                            currentPosition++;
                            x = event.getX();
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                final ValueAnimator value=ValueAnimator.ofFloat(offset,0);
                value.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        offset= (float) value.getAnimatedValue();
                        invalidate();
                    }
                });
                value.setDuration(300);
                value.start();
                break;
        }
        return true;
    }
}
