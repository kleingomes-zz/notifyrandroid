package com.notifyrapp.www.notifyr.UI;

import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by K on 12/28/2016.
 */

public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {
    private int bufferItemCount = 10;
    private int currentPage = 0;
    private int itemCount = 0;
    private boolean isLoading = true;
    private int last = 0;
    private boolean control = true;



    public InfiniteScrollListener(int bufferItemCount) {
        this.bufferItemCount = bufferItemCount;
        this.currentPage = 0;
    }

    public abstract void loadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Do Nothing2
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

    }

    public void setCurrentPage(int page)
    {
        this.currentPage = page;
    }
}