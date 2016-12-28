package com.notifyrapp.www.notifyr.Business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.notifyrapp.www.notifyr.R;

import java.io.InputStream;

/**
 * Created by K on 11/20/2016.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    CallbackInterface mCallback;
    ProgressBar mProgressBar;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    public DownloadImageTask(ImageView bmImage,CallbackInterface callback) {
        this.bmImage = bmImage;
        this.mCallback = callback;
    }

    public DownloadImageTask(ImageView bmImage, ProgressBar progressBar) {
        this.bmImage = bmImage;
        this.mProgressBar = progressBar;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(result!= null) {
            bmImage.setImageBitmap(result);
        }
        else
        {
            bmImage.setBackgroundResource(R.mipmap.ic_launcher);
            bmImage.setScaleType(ImageView.ScaleType.MATRIX);

        }
        if(mCallback != null)
        {
            mCallback.onCompleted(result);
        }
        if(mProgressBar != null)
        {
            mProgressBar.setVisibility(View.GONE);
        }
    }
}