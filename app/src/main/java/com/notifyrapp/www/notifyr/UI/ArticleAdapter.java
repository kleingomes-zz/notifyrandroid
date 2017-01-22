package com.notifyrapp.www.notifyr.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.UserSetting;
import com.notifyrapp.www.notifyr.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by K on 12/27/2016.
 */

public class ArticleAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Article> mDataSource;
    private ConnectivityManager mConnManager;
    private NetworkInfo mWifi;
    private UserSetting mUserSettings;
    private int viewMode = 1;

    public ArticleAdapter(Context context, List<Article> articles) {
        mContext = context;
        mDataSource = articles;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mConnManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifi = mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    @Override
    public void notifyDataSetChanged() {
        mUserSettings = new Business(mContext).getUserSettings();
        int userMode = mUserSettings.getArticleDisplayType();

        // User wants WiFi only AND HAS WiFi ON
        if(userMode == 1 && mWifi.isConnected())
        {
            viewMode = 1; // Image Mode
        }
        // User wants WiFi only AND has WIFI off OR NEVER wants images
        else if((userMode == 1 && !mWifi.isConnected()) || userMode == 0)
        {
            viewMode = 0; // Text Mode
        }
        // User always wants images
        else if(userMode == 2)
        {
            viewMode = 1; // Image Mode
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mDataSource != null || mDataSource.size()>0)
            return mDataSource.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
        //return mDataSource.get(position).getId();
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        View rowView;

        // Image Mode
        if(viewMode == 1) {
            // Get view for row item
            rowView = mInflater.inflate(R.layout.list_item_article_image, parent, false);

            // Get Title element
            TextView titleTextView = (TextView) rowView.findViewById(R.id.txtImageArticleTitle);

            // Get Subtitle element
            TextView subtitleTextView = (TextView) rowView.findViewById(R.id.txtImageArticleSubTitle);

            // Get image
            final ImageView imageView = (ImageView) rowView.findViewById(R.id.imgArticle);

            // Get Article
            final Article article = (Article) getItem(position);

            // Get Progress Bar
            final ProgressBar mProgressBar = (ProgressBar) rowView.findViewById(R.id.pbHeaderProgress);

            // Load the Elements with data
            titleTextView.setText(article.getTitle());
            subtitleTextView.setText(article.getSource() + " - " + article.getTimeAgo());

            // Load the image element
            String imageUrl = article.getIurl();

            // Check if the image is in cache
            Bitmap image = CacheManager.getArticleImageFromMemoryCache("article_" + String.valueOf(article.getId()));
            if (image != null) {
                imageView.setImageBitmap(image);
            }
            else {
                mProgressBar.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(article.getIurl())
                        .error(ContextCompat.getDrawable(mContext, R.drawable.large_logo))
                        .into(imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        CacheManager.saveArticleImageToMemoryCache("article_" + String.valueOf(article.getId()), image);
                    }

                    @Override
                    public void onError() {
                        Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.large_icon);
                        mProgressBar.setVisibility(View.GONE);
                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                        imageView.setImageBitmap(largeIcon);
                        Log.d("IMAGE_DOWNLOAD_FAILED", article.getTitle() + " " + article.getIurl());
                        CacheManager.saveArticleImageToMemoryCache("article_" + String.valueOf(article.getId()), largeIcon);
                    }
                });
            }
        }
        // Text Mode
        else
        {
            // Get view for row item
            rowView = mInflater.inflate(R.layout.list_item_article_noimage, parent, false);

            // Get Title element
            TextView titleTextView = (TextView) rowView.findViewById(R.id.txtNoImageArticleTitle);

            // Get Subtitle element
            TextView subtitleTextView = (TextView) rowView.findViewById(R.id.txtNoImageArticleSubTitle);

            // Get Article
            final Article article = (Article) getItem(position);

            // Load the Elements with data
            titleTextView.setText(article.getTitle());

            subtitleTextView.setText(article.getSource() + " - " + article.getTimeAgo());
        }

        return rowView;
    }

}
