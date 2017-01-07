package com.notifyrapp.www.notifyr.UI;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.R;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by K on 1/2/2017.
 */

public class DiscoverRecyclerAdapter extends RecyclerView
        .Adapter<RecyclerView.ViewHolder> {

    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static List<Item> mDataset;
    private static MyClickListener myClickListener;


    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

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
            final Business business = new Business(ctx);
            Log.i(LOG_TAG, "Adding Listener");
           // itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Item item = mDataset.get(getPosition());
                    item.setPriority(2);
                    business.saveUserItemLocal(item);
                    business.save
                }
            });
        }

        @Override
        public void onClick(View v) {

            getPosition();
           // myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public DiscoverRecyclerAdapter(List<Item> myDataset) {
        mDataset = myDataset;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

        if (viewType == EMPTY_VIEW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_circle_center, parent, false);
            EmptyViewHolder emptyViewHolder = new EmptyViewHolder(v);
            return emptyViewHolder;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_discover, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);


        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        //holder.itemImage.setText(mDataset.get(position).getItemTypeName());
        if (holder instanceof DataObjectHolder) {
            final DataObjectHolder hold = (DataObjectHolder) holder;
            final int id = mDataset.get(position).getId();
            final String itemName = mDataset.get(position).getName();

            String iurl = mDataset.get(position).getIurl();

            // Check if the image is in cache
            Bitmap image = CacheManager.getImageFromMemoryCache("item_" + itemName);
            if (image != null) {
                hold.itemImage.setImageBitmap(image);
            } else {
                Picasso.with(hold.ctx)
                        .load(iurl)
                        .into(hold.itemImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap image = ((BitmapDrawable) hold.itemImage.getDrawable()).getBitmap();
                                CacheManager.saveImageToMemoryCache("item_" + itemName, image);
                            }

                            @Override
                            public void onError() {
                                Log.d("FAILED", "Fail");
                            }
                        });
            }
            hold.itemName.setText(mDataset.get(position).getName());
        }
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
        return mDataset.size() > 0 ? mDataset.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDataset.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    private static final int EMPTY_VIEW = 10;
}
