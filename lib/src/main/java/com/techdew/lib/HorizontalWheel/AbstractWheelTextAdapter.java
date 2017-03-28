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
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techdew.lib.R;

/**
 * Abstract spinnerwheel adapter provides common functionality for adapters.
 */
public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

    /** Text view resource. Used as a default view for adapter. */
    public static final int TEXT_VIEW_ITEM_RESOURCE = -1;

    /** No resource constant. */
    protected static final int NO_RESOURCE = 0;

    /** Default text color */
    public static final int DEFAULT_TEXT_COLOR = 0xFF101010;

    /** Default text color */
    public static final int LABEL_COLOR = 0xFF700070;

    /** Default text size */
    public static final int DEFAULT_TEXT_SIZE = 24;

    /// Custom text typeface
    private Typeface textTypeface;

    // Text settings
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textSize = DEFAULT_TEXT_SIZE;

    // Current context
    protected Context context;
    // Layout inflater
    protected LayoutInflater inflater;

    // Items resources
    protected int itemResourceId;
    protected int itemTextResourceId;

    // Empty items resources
    protected int emptyItemResourceId;


    protected AbstractWheelTextAdapter(Context context) {
        this(context, TEXT_VIEW_ITEM_RESOURCE);
    }

    protected AbstractWheelTextAdapter(Context context, int itemResource) {
        this(context, itemResource, NO_RESOURCE);
    }

    protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource) {
        this.context = context;
        itemResourceId = itemResource;
        itemTextResourceId = itemTextResource;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public int getTextColor() {
        return textColor;
    }


    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }


    public void setTextTypeface(Typeface typeface) {
        this.textTypeface = typeface;
    }

    /**
     * Gets text size
     * @return the text size
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Sets text size
     * @param textSize the text size to set
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    /**
     * Gets resource Id for items views
     * @return the item resource Id
     */
    public int getItemResource() {
        return itemResourceId;
    }

    /**
     * Sets resource Id for items views
     * @param itemResourceId the resource Id to set
     */
    public void setItemResource(int itemResourceId) {
        this.itemResourceId = itemResourceId;
    }

    /**
     * Gets resource Id for text view in item layout
     * @return the item text resource Id
     */
    public int getItemTextResource() {
        return itemTextResourceId;
    }

    /**
     * Sets resource Id for text view in item layout
     * @param itemTextResourceId the item text resource Id to set
     */
    public void setItemTextResource(int itemTextResourceId) {
        this.itemTextResourceId = itemTextResourceId;
    }

    /**
     * Gets resource Id for empty items views
     * @return the empty item resource Id
     */
    public int getEmptyItemResource() {
        return emptyItemResourceId;
    }

    /**
     * Sets resource Id for empty items views
     * @param emptyItemResourceId the empty item resource Id to set
     */
    public void setEmptyItemResource(int emptyItemResourceId) {
        this.emptyItemResourceId = emptyItemResourceId;
    }

    /**
     * Returns text for specified item
     * @param index the item index
     * @return the text of specified items
     */
    protected abstract CharSequence getItemText(int index);

    @Override
    public View getItem(int index, View convertView, ViewGroup parent, int currentItemIdx) {
        if (index >= 0 && index < getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResourceId, parent);
            }
            TextView textView = getTextView(convertView, itemTextResourceId);
            if (textView != null) {
                CharSequence text = getItemText(index);
                if (text == null) {
                    text = "";
                }
                textView.setText(text);
                configureTextView(textView, index == currentItemIdx);
            }
            return convertView;
        }
        return null;
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getView(emptyItemResourceId, parent);
        }
        if (convertView instanceof TextView) {
            configureTextView((TextView)convertView, false);
        }

        return convertView;
    }

    /**
     * Configures text view. Is called for the TEXT_VIEW_ITEM_RESOURCE views.
     * @param textView the text view to be configured
     * @param isSelectedItem
     */
    protected void configureTextView(TextView textView, boolean isSelectedItem) {
        Boolean tag = (Boolean) textView.getTag(R.id.wheel_text_view_configured_state);
        if(tag == null || tag != isSelectedItem) {
            textView.setTag(R.id.wheel_text_view_configured_state, isSelectedItem);
            onConfigureTextView(textView, isSelectedItem);
        }
    }

    protected void onConfigureTextView(TextView textView, boolean isSelectedItem) {
        if (itemResourceId == TEXT_VIEW_ITEM_RESOURCE) {
            textView.setTextColor(textColor);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(textSize);
            textView.setLines(1);
        }
        if (textTypeface != null) {
            textView.setTypeface(textTypeface);
        } else {
            textView.setTypeface(Typeface.SANS_SERIF, getDefaultTextStyle());
        }
    }

    protected int getDefaultTextStyle() {return Typeface.BOLD;}

    /**
     * Loads a text view from view
     * @param view the text view or layout containing it
     * @param textResource the text resource Id in layout
     * @return the loaded text view
     */
    private TextView getTextView(View view, int textResource) {
        TextView text = null;
        try {
            if (textResource == NO_RESOURCE && view instanceof TextView) {
                text = (TextView) view;
            } else if (textResource != NO_RESOURCE) {
                text = (TextView) view.getTag(textResource);
                if(text == null) {
                    text = (TextView) view.findViewById(textResource);
                    view.setTag(textResource, text);
                }
            }
        } catch (ClassCastException e) {
//            Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "AbstractWheelAdapter requires the resource ID to be a TextView", e);
        }
        
        return text;
    }
    
    /**
     * Loads view from resources
     * @param resource the resource Id
     * @return the loaded view or null if resource is not set
     */
    private View getView(int resource, ViewGroup parent) {
        switch (resource) {
        case NO_RESOURCE:
            return null;
        case TEXT_VIEW_ITEM_RESOURCE:
            return new TextView(context);
        default:
            return inflater.inflate(resource, parent, false);
        }
    }
}
