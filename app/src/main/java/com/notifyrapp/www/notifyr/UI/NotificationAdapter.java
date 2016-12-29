package com.notifyrapp.www.notifyr.UI;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Business.DownloadImageTask;
import com.notifyrapp.www.notifyr.Business.ImageCacheManager;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dchi on 12/28/16.
 */

public class NotificationAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Article> mDataSource;
    private ProgressBar mProgressBar;

    public NotificationAdapter(Context context, List<Article> articles) {
        mContext = context;
        mDataSource = articles;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
        //return mDataSource.get(position).getId();
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_notification, parent, false);

        // Get the name of the article
        TextView titleTextView = (TextView) rowView.findViewById(R.id.txtNotificationArticle);

        // get the name of the source
        TextView sourceTextView = (TextView) rowView.findViewById(R.id.txtNotificationSource);

        //get the name of the item article is associated with
        TextView itemTextView = (TextView) rowView.findViewById(R.id.txtNotificationItem);

        //get the notified time ago
        TextView timeAgoTextView = (TextView) rowView.findViewById(R.id.txtNotificationTime);

        // Get image of the item
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgNotification);

        // Get Article
        final Article article = (Article) getItem(position);

        // Get Progress Bar
        mProgressBar =  (ProgressBar) rowView.findViewById(R.id.pbHeaderProgress);

        // Load the Elements with data
        titleTextView.setText(article.getTitle());
        sourceTextView.setText(article.getSource()); // + " - " + article.getTimeAgo());
        itemTextView.setText(article.getRelatedInterests());
        timeAgoTextView.setText(article.getNotifiedTimeAgo());

        // Check if the image is in cache
        String imageUrl = article.getRelatedInterestsURL();
        Bitmap image = ImageCacheManager.getImage("notification_"+String.valueOf(article.getId()), mContext);
        if(image != null)
        {
            imageView.setImageBitmap(image);
        }
        else
        {
            if(mProgressBar != null) mProgressBar.setVisibility(View.VISIBLE);
            if (imageUrl != null && !imageUrl.isEmpty()) {
                new DownloadImageTask(imageView,mProgressBar, new CallbackInterface() {
                    @Override
                    public void onCompleted(Object data) {
                        if(data != null) {
                            Bitmap articleImage = (Bitmap) data;
                            ImageCacheManager.saveImage("notification_"+String.valueOf(article.getId()), articleImage,mContext);
                        }
                        if(mProgressBar != null) mProgressBar.setVisibility(View.GONE);
                    }
                }).execute(imageUrl);
            }
        }

        return rowView;
    }

}


