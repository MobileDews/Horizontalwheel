/*
 * android-spinnerwheel
 * https://github.com/ai212983/android-spinnerwheel
 *
 * based on
 *
 * Android Wheel Control.
 * https://code.google.com/p/android-wheel/
 *
 * Copyright 2011 Yuri Kanivets
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.techdew.lib.HorizontalWheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.techdew.lib.R;

/**
 * Spinner wheel horizontal view.
 *
 * @author Yuri Kanivets
 * @author Dimitri Fedorov
 */
public class HorizontalView extends AbstractWheelView {

    private static int itemID = -1;

    @SuppressWarnings("unused")
    private final String LOG_TAG = VerticalView.class.getName() + " #" + (++itemID);

    /**
     * The width of the selection divider.
     */
    protected int mSelectionDividerWidth;

    // Item width
    private int itemWidth = 0;

    //--------------------------------------------------------------------------
    //
    //  Constructors
    //
    //--------------------------------------------------------------------------

    /**
     * Create a new wheel horizontal view.
     *
     * @param context The application environment.
     */
    public HorizontalView(Context context) {
        this(context, null);
    }

    /**
     * Create a new wheel horizontal view.
     *
     * @param context The application environment.
     * @param attrs A collection of attributes.
     */
    public HorizontalView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.abstractWheelViewStyle);
    }

    /**
     * Create a new wheel horizontal view.
     *
     * @param context the application environment.
     * @param attrs a collection of attributes.
     * @param defStyle The default style to apply to this view.
     */
    public HorizontalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //--------------------------------------------------------------------------
    //
    //  Initiating assets and setter for selector paint
    //
    //--------------------------------------------------------------------------

    @Override
    protected void initAttributes(AttributeSet attrs, int defStyle) {
        super.initAttributes(attrs, defStyle);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalView, defStyle, 0);
        mSelectionDividerWidth = a.getDimensionPixelSize(R.styleable.HorizontalView_selectionDividerWidth, DEF_SELECTION_DIVIDER_SIZE);
        a.recycle();
    }

    public void setSelectionDividerWidth(int selectionDividerWidth) {
        this.mSelectionDividerWidth = selectionDividerWidth;
    }

    @Override
    public void setSelectorPaintCoeff(float coeff) {
        if (mItemsDimmedAlpha >= 100)
            return;

        LinearGradient shader;

        int w = getMeasuredWidth();
        int iw = getItemDimension();
        float p1 = (1 - iw/(float) w)/2;
        float p2 = (1 + iw/(float) w)/2;
        float z = mItemsDimmedAlpha * (1 - coeff);
        float c1f = z + 255 * coeff;

        if (mVisibleItems == 2) {
            int c1 = Math.round( c1f ) << 24;
            int c2 = Math.round( z ) << 24;
            int[] colors =      {c2, c1, 0xff000000, 0xff000000, c1, c2};
            float[] positions = { 0, p1,     p1,         p2,     p2,  1};
            shader = new LinearGradient(0, 0, w, 0, colors, positions, Shader.TileMode.CLAMP);
        } else {
            float p3 = (1 - iw*3/(float) w)/2;
            float p4 = (1 + iw*3/(float) w)/2;

            float s = 255 * p3/p1;
            float c3f = s * coeff ; // here goes some optimized stuff
            float c2f = z + c3f;

            int c1 = Math.round( c1f ) << 24;
            int c2 = Math.round( c2f ) << 24;
            int c3 = Math.round( c3f ) << 24;

            int[] colors = { c2, c2, c2, c2, 0xff000000, 0xff000000, c2, c2, c2, c2 };
            float[] positions = { 0, p3, p3, p1, p1, p2, p2, p4, p4, 1 };
            shader = new LinearGradient(0, 0, w, 0, colors, positions, Shader.TileMode.CLAMP);
        }
        mSelectorWheelPaint.setShader(shader);
        invalidate();
    }


    //--------------------------------------------------------------------------
    //
    //  Scroller-specific methods
    //
    //--------------------------------------------------------------------------

    @Override
    protected WheelScroller createScroller(WheelScroller.ScrollingListener scrollingListener) {
        return new HorizontalScroller(getContext(), scrollingListener);
    }

    @Override
    protected float getMotionEventPosition(MotionEvent event) {
        return event.getX();
    }


    //--------------------------------------------------------------------------
    //
    //  Base measurements
    //
    //--------------------------------------------------------------------------

    @Override
    protected int getBaseDimension() {
        return getWidth();
    }

    /**
     * Returns height of spinnerwheel item
     * @return the item width
     */
    @Override
    protected int getItemDimension() {
        if (itemWidth != 0) {
            return itemWidth;
        }

        if (mItemsLayout != null && mItemsLayout.getChildAt(0) != null) {
            itemWidth = mItemsLayout.getChildAt(0).getMeasuredWidth();
            return itemWidth;
        }

        return getBaseDimension() / mVisibleItems;
    }

    //--------------------------------------------------------------------------
    //
    //  Debugging stuff
    //
    //--------------------------------------------------------------------------


    @Override
    protected void onScrollTouchedUp() {
        super.onScrollTouchedUp();
        int cnt = mItemsLayout.getChildCount();
        View itm;
        Log.e(LOG_TAG, " ----- layout: " + mItemsLayout.getMeasuredWidth() + mItemsLayout.getMeasuredHeight());
        Log.e(LOG_TAG, " -------- dumping " + cnt + " items");
        for (int i = 0; i < cnt; i++) {
            itm = mItemsLayout.getChildAt(i);
            Log.e(LOG_TAG, " item #" + i + ": " + itm.getWidth() + "x" + itm.getHeight());
            itm.forceLayout(); // forcing layout without re-rendering parent
        }
        Log.e(LOG_TAG, " ---------- dumping finished ");
    }


    //--------------------------------------------------------------------------
    //
    //  Layout creation and measurement operations
    //
    //--------------------------------------------------------------------------

    /**
     * Creates item layouts if necessary
     */
    @Override
    protected void createItemsLayout() {
        if (mItemsLayout == null) {
            mItemsLayout = new LinearLayout(getContext());
            mItemsLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    @Override
    protected void doItemsLayout() {
        mItemsLayout.layout(0, 0, getMeasuredWidth(), getMeasuredHeight() - 2 * mItemsPadding);
    }

    @Override
    protected void measureLayout() {
        mItemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        // XXX: Locating bug
        mItemsLayout.measure(
                View.MeasureSpec.makeMeasureSpec(getWidth() + getItemDimension(), View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.AT_MOST));
    }

    //XXX: Most likely, measurements of mItemsLayout or/and its children are done inconrrectly.
    // Investigate and fix it

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        rebuildItems(); // rebuilding before measuring

        int height = calculateLayoutHeight(heightSize, heightMode);

        int width;
        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = Math.max(
                    getItemDimension() * (mVisibleItems - mItemOffsetPercent / 100),
                    getSuggestedMinimumWidth()
            );

            if (widthMode == View.MeasureSpec.AT_MOST) {
                width = Math.min(width, widthSize);
            }
        }
        setMeasuredDimension(width, height);
    }


    /**
     * Calculates control height and creates text layouts
     * @param heightSize the input layout height
     * @param mode the layout mode
     * @return the calculated control height
     */
    private int calculateLayoutHeight(int heightSize, int mode) {
        mItemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mItemsLayout.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(heightSize, View.MeasureSpec.UNSPECIFIED)
                );
        int height = mItemsLayout.getMeasuredHeight();

        if (mode == View.MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height += 2 * mItemsPadding;

            // Check against our minimum width
            height = Math.max(height, getSuggestedMinimumHeight());

            if (mode == View.MeasureSpec.AT_MOST && heightSize < height) {
                height = heightSize;
            }
        }
        // forcing recalculating
        mItemsLayout.measure(
                // MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height - 2 * mItemsPadding, View.MeasureSpec.EXACTLY)
        );

        return height;
    }


    //--------------------------------------------------------------------------
    //
    //  Drawing items
    //
    //--------------------------------------------------------------------------

    @Override
    protected void drawItems(Canvas canvas) {
        canvas.save();
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int iw = getItemDimension();

        // resetting intermediate bitmap and recreating canvases
        mSpinBitmap.eraseColor(0);
        Canvas c = new Canvas(mSpinBitmap);
        Canvas cSpin = new Canvas(mSpinBitmap);

        int left = (mCurrentItemIdx - mFirstItemIdx) * iw + (iw - getWidth()) / 2;
        c.translate(- left + mScrollingOffset, mItemsPadding);
        mItemsLayout.draw(c);

        mSeparatorsBitmap.eraseColor(0);
        Canvas cSeparators = new Canvas(mSeparatorsBitmap);

        if (mSelectionDivider != null) {
            // draw the top divider
            int leftOfLeftDivider = (getWidth() - iw - mSelectionDividerWidth) / 2;
            int rightOfLeftDivider = leftOfLeftDivider + mSelectionDividerWidth;
            cSeparators.save();
            // On Gingerbread setBounds() is ignored resulting in an ugly visual bug.
            cSeparators.clipRect(leftOfLeftDivider, 0, rightOfLeftDivider, h);
            mSelectionDivider.setBounds(leftOfLeftDivider, 0, rightOfLeftDivider, h);
            mSelectionDivider.draw(cSeparators);
            cSeparators.restore();

            cSeparators.save();
            // draw the bottom divider
            int leftOfRightDivider =  leftOfLeftDivider + iw;
            int rightOfRightDivider = rightOfLeftDivider + iw;
            // On Gingerbread setBounds() is ignored resulting in an ugly visual bug.
            cSeparators.clipRect(leftOfRightDivider, 0, rightOfRightDivider, h);
            mSelectionDivider.setBounds(leftOfRightDivider, 0, rightOfRightDivider, h);
            mSelectionDivider.draw(cSeparators);
            cSeparators.restore();
        }

        cSpin.drawRect(0, 0, w, h, mSelectorWheelPaint);
        cSeparators.drawRect(0, 0, w, h, mSeparatorsPaint);

        canvas.drawBitmap(mSpinBitmap, 0, 0, null);
        canvas.drawBitmap(mSeparatorsBitmap, 0, 0, null);
        canvas.restore();
    }

}
