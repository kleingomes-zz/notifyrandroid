package com.notifyrapp.www.notifyr.UI;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by K on 12/28/2016.
 */

public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
    private int bufferItemCount = 2;
    private int currentPage = 0;
    private int itemCount = 0;
    private boolean isLoading = true;
    private int oldTop =0;
    private int oldFirstVisibleItem =0;
    private int last = 0;
    private boolean control = true;
    private AbsListView.OnScrollListener onScrollListener;
    public abstract void loadMore(int page, int totalItemsCount);
    public abstract void onUpScrolling();
    public abstract void onDownScrolling();

    public InfiniteScrollListener(int bufferItemCount) {
        this.bufferItemCount = bufferItemCount;
        this.currentPage = 0;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Do Nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (totalItemCount < itemCount) {
            this.itemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.isLoading = true;
            }
        }

        if (isLoading && (totalItemCount > itemCount)) {
            isLoading = false;
            itemCount = totalItemCount;
            currentPage++;
        }

        if (!isLoading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + bufferItemCount)) {
            loadMore(currentPage + 1, totalItemCount);
            isLoading = true;
        }

        int top = (view == null) ? 0 : view.getTop();

        if (firstVisibleItem == oldFirstVisibleItem) {
            if (top > oldTop) {
                onUpScrolling();
            } else if (top < oldTop) {
                onDownScrolling();
            }
        } else {
            if (firstVisibleItem < oldFirstVisibleItem) {
                onUpScrolling();
            } else {
                onDownScrolling();
            }
        }

        oldTop = top;
        oldFirstVisibleItem = firstVisibleItem;
    }

    public void setCurrentPage(int page)
    {
        this.currentPage = page;
    }



}

