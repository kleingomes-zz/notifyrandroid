package com.notifyrapp.www.notifyr.UI;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.notifyrapp.www.notifyr.Business.ImageCacheManager;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
/**
 * Created by K on 1/2/2017.
 */

public class DiscoverRecyclerAdapter extends RecyclerView
        .Adapter<DiscoverRecyclerAdapter.DataObjectHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Item> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        ImageView itemImage;
        TextView itemName;
        Context ctx;

        public DataObjectHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemName = (TextView) itemView.findViewById(R.id.itemName);
            ctx = itemView.getContext();
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public DiscoverRecyclerAdapter(ArrayList<Item> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_discover, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {
        //holder.itemImage.setText(mDataset.get(position).getItemTypeName());
        final int id = mDataset.get(position).getId();
        String iurl = mDataset.get(position).getIurl();

        // Check if the image is in cache
        Bitmap image = ImageCacheManager.getImageFromMemoryCache("item_"+String.valueOf(id));
        if(image != null)
        {
            holder.itemImage.setImageBitmap(image);
        }
        else
        {
            Picasso.with(holder.ctx).load(iurl).into( holder.itemImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Bitmap image=((BitmapDrawable)holder.itemImage.getDrawable()).getBitmap();
                    ImageCacheManager.saveImageToMemoryCache("item_"+String.valueOf(id),image);
                }

                @Override
                public void onError() {
                    Log.d("FAILED","Fail");
                }
            });
        }
        holder.itemName.setText(mDataset.get(position).getName());
    }

    public void addItem(Item dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
