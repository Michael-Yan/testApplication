package com.zzz.monitor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.zzz.monitor.bean.Snippet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class Timeline extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mStart = 0;
    private int mEnd = 0;
    private int mCurrent;
    private int mPlay;
    private int mDuration = 0;
    private int mLeft = 0;
    private int mRight = 0;
    private int mMiddle = 0;
    private int mOffset = 0;
    private int mLineWidth = dip2px(1);
    private ArrayList<Integer> mAvailable = new ArrayList<>();

    private Scroller mScroller;
    private int mXPosition;
    private int mXPositionLast;
    private Timer mTimer = null;

    private List<Snippet> mSnippets = new ArrayList<>();
    private OnTouchCallBackListener mOnTouchCallBackListener;

    public Timeline(Context context) {
        this(context, null);
    }

    public Timeline(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Timeline(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);

        initView();
    }

    public int getCurrent() {
        return mCurrent;
    }

    public void setCurrent(long mimillis) {
        if (mPlay + mimillis / 1000 - mCurrent > 60) {
            int index = mAvailable.indexOf(mCurrent);

            if (index + 1 < mAvailable.size()) {
                mCurrent = mAvailable.get(index + 1);
                mOffset = (mCurrent - mStart) / 60 * dip2px(1);
                invalidate();
            }
        }
    }

    public void setVodConfig(int start, int end, List<Snippet> snippets) {
        mStart = start / 60 * 60 - 60 * 60;
        mEnd = end / 60 * 60 + 60 * 60;
        mSnippets = snippets;
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;

        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            width = getMeasuredWidth();
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = getMeasuredHeight();
        }

        mHeight = height;
        mWidth = width;
        mMiddle = width >> 1;
        mDuration = width / mLineWidth;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBar(canvas);
    }

    private void drawBar(Canvas canvas) {
        mLeft = mStart - mMiddle / mLineWidth * 60 + mOffset / mLineWidth * 60;
        mCurrent = mLeft + mMiddle / mLineWidth * 60;
        mRight = mLeft + mWidth / mLineWidth * 60;

        RectF rectF = new RectF(0, mHeight >> 1, mWidth, mHeight);
        mPaint.setColor(Color.parseColor("#CFD8DC"));
        canvas.drawRect(rectF, mPaint);

        for (Snippet snippet : mSnippets) {
            int snippetStart = snippet.getStart() / 60 * 60;
            int snippetEnd = snippet.getEnd() / 60 * 60 + 60;

            if (snippetEnd < mLeft - 600) {
                continue;
            }

            if (snippetEnd > mRight + 600) {
                break;
            }

            int gapStart = (snippetStart - mLeft) / 60;
            int gapEnd = (snippetEnd - mLeft) / 60;

            rectF = new RectF(gapStart * mLineWidth, mHeight >> 1, gapEnd * mLineWidth, mHeight);
            mPaint.setColor(Color.parseColor("#00B0FF"));
            canvas.drawRect(rectF, mPaint);

            for (int i = snippetStart; i < snippetEnd; i += 60) {
                if (!mAvailable.contains(i)) {
                    mAvailable.add(i);
                }
            }
        }

        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setTextSize(dip2px(16));
        mPaint.setColor(Color.parseColor("#ffffff"));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);

        for (int i = 0; i < mDuration; i++) {
            int position = i * mLineWidth;
            if (0 == (mLeft + i * 60) % 3600) {
                Date date = new Date((long) (mLeft + i * 60) * 1000);
                String text = sdf.format(date);
                canvas.drawLine(position, mHeight >> 1, position, (mHeight >> 1) + dip2px(25), mPaint);
                canvas.drawText(text, 0, text.length(), position - dip2px(20), (mHeight >> 1) + dip2px(45), mPaint);
            } else if (0 == (mLeft + i * 60) % 1800) {
                canvas.drawLine(position, mHeight >> 1, position, (mHeight >> 1) + dip2px(25), mPaint);
            } else if (0 == (mLeft + i * 60) % 300) {
                canvas.drawLine(position, mHeight >> 1, position, (mHeight >> 1) + dip2px(15), mPaint);
            }
        }

        mPaint.setColor(Color.parseColor("#ff0000"));
        canvas.drawLine(mMiddle, mHeight >> 1, mMiddle, (mHeight >> 1) + dip2px(mHeight), mPaint);

        mPaint.setColor(Color.parseColor("#000000"));

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date date = new Date((long) mCurrent * 1000);
        String text = sdf.format(date);
        canvas.drawText(text, 0, text.length(), mMiddle - ((int) mPaint.measureText(text) >> 1), mHeight / (float) 3, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        mXPosition = (int) event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mTimer != null) {
                    mTimer.cancel();
                }
                mScroller.forceFinished(true);
                mXPositionLast = mXPosition;
                break;
            case MotionEvent.ACTION_MOVE:
                mOffset += mXPositionLast - mXPosition;
                mXPositionLast = mXPosition;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mAvailable.contains(mCurrent)) {
                            mPlay = mCurrent;
                            mOnTouchCallBackListener.onUp(mCurrent);
                        } else {
                            mOnTouchCallBackListener.onStop();
                        }
                    }
                }, 500);

                return false;
            default:
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    private int dip2px(float dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public void setCallBackListener(OnTouchCallBackListener onTouchCallBackListener) {
        mOnTouchCallBackListener = onTouchCallBackListener;
    }

    public interface OnTouchCallBackListener {
        void onUp(long current);

        void onStop();
    }
}
