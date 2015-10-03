package ru.pomerantsevp.udacity.popularmovies.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by pavel on 10/4/15.
 */
public class StaticHeightListView extends ListView {
    public StaticHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Used some ideas
        // from http://blog.lovelyhq.com/setting-listview-height-depending-on-the-items/
        ListAdapter listAdapter = getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, this);
                item.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = getDividerHeight() * (numberOfItems - 1);

            // Looks like I'm doing something wrong with children, so super.onMeasure() is called
            // to set children's measurements right again.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Nevertheless, the list view's measurements were calculated correctly,
            // and we want to set them.
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                    totalItemsHeight + totalDividersHeight);

        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
