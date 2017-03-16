package com.example.dy.group_demo6.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dy on 17-3-8.
 */

public class ToggleButton extends View implements View.OnClickListener{
    private OnToggleChanged listener;
    private int onColor = Color.parseColor("#4ebb7f");
    private int offColor = Color.parseColor("#dadbda");
    private boolean toggleOn = true;
    private int BTN_WIDTH = 40;// 宽度
    private int BTN_HEIGHT = 30;// 高度
    private int CIRCLE_RADIUS = 8;// 圆的半径
    private int LINE_WIDTH = 2;// 直线高度
    private int circleX;// 圆心X轴坐标
    private boolean changeCompelete = true;
    private Resources r;
    private TimerTask task;
    private Timer timer;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1111) {
                invalidate();
            }
        };
    };

    public ToggleButton(Context context) {
        this(context, null, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        doInit();
    }

    public int getOnColor() {
        return onColor;
    }
    public void setOnColor(int onColor) {
        this.onColor = onColor;
        invalidate();
    }
    public int getOffColor() {
        return offColor;
    }
    public void setOffColor(int offColor) {
        this.offColor = offColor;
        invalidate();
    }
    public boolean isToggleOn() {
        return toggleOn;
    }
    public void setToggleOn(boolean toggleOn) {
        this.toggleOn = toggleOn;
        if (toggleOn) {
            circleX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_WIDTH - CIRCLE_RADIUS, r.getDisplayMetrics());
        }else {
            circleX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS, r.getDisplayMetrics());
        }
        invalidate();
    }
    private void doInit() {
        r = Resources.getSystem();
        setOnClickListener(this);
        circleX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_WIDTH - CIRCLE_RADIUS, r.getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (toggleOn) {
            Paint linePaint = new Paint();
            linePaint.setColor(onColor);
            linePaint.setAntiAlias(true);
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LINE_WIDTH, r.getDisplayMetrics()));
            canvas.drawRect(0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (BTN_HEIGHT - LINE_WIDTH) / 2, r.getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_WIDTH, r.getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (BTN_HEIGHT + LINE_WIDTH) / 2, r.getDisplayMetrics()), linePaint);
            Paint circlePaint = new Paint();
            circlePaint.setColor(onColor);
            circlePaint.setAntiAlias(true);
            canvas.drawCircle(circleX, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_HEIGHT / 2, r.getDisplayMetrics()),TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS, r.getDisplayMetrics()), circlePaint);
        } else {
            Paint circlePaint = new Paint();
            circlePaint.setColor(offColor);
            circlePaint.setAntiAlias(true);
            canvas.drawCircle(circleX,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_HEIGHT / 2, r.getDisplayMetrics()),TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS, r.getDisplayMetrics()), circlePaint);
            Paint linePaint = new Paint();
            linePaint.setColor(offColor);
            linePaint.setAntiAlias(true);
            linePaint.setStyle(Paint.Style.FILL);
            linePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LINE_WIDTH, r.getDisplayMetrics()));
            canvas.drawRect(0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (BTN_HEIGHT - LINE_WIDTH) / 2, r.getDisplayMetrics()),TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_WIDTH, r.getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (BTN_HEIGHT + LINE_WIDTH) / 2, r.getDisplayMetrics()), linePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST){
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_WIDTH, r.getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_HEIGHT,r.getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        if (changeCompelete) {
            if (toggleOn) {
                toggleOn = false;
            }else {
                toggleOn = true;
            }
            changeCompelete = false;
            task = new TimerTask() {
                @Override
                public void run() {
                    if (toggleOn) {
                        circleX++;
                    }else {
                        circleX--;
                    }
                    handler.sendEmptyMessage(1111);
                    if (circleX == TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BTN_WIDTH - CIRCLE_RADIUS, r.getDisplayMetrics()) || circleX ==TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS, r.getDisplayMetrics())) {
                        changeCompelete = true;
                        timer.cancel();
                    }
                }
            };
            timer = new Timer();
            timer.schedule(task, 0, 2);
            listener.onToggle(toggleOn);
        }
    }

    public interface OnToggleChanged {
        public void onToggle(boolean on);
    }

    public void setOnToggleChanged(OnToggleChanged onToggleChanged) {
        listener = onToggleChanged;
    }
}
