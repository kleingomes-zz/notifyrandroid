
package com.notifyrapp.www.notifyr.UI;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import java.util.Arrays;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import android.view.View.OnClickListener;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.notifyrapp.www.notifyr.ArticleListFragment;
import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Business.DownloadImageTask;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Business.GlobalShared;
import com.notifyrapp.www.notifyr.MainActivity;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.MyItemsFragment;
import com.notifyrapp.www.notifyr.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.samthompson.bubbleactions.BubbleActions;
import me.samthompson.bubbleactions.Callback;

import static com.notifyrapp.www.notifyr.R.id.itemIsChecked;

/**
 * Created by dchi on 12/29/16.
 */

public class PopularItemAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Item> mDataSource;
    private List<Item> userItems;
    private Map<Integer, Item> mDeleteQueue;
    private MainActivity act;
    private PopularItemAdapter adapter;
    private Business mBusiness;


    public CheckBox checkBoxDelete;
    public View rowView;




    public PopularItemAdapter(Context context, List<Item> items) {
        mContext = context;
        mDataSource = items;
        mBusiness = new Business(context);
        userItems = mBusiness.getUserItemsFromLocal();
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

    public void remove(int position)
    {
        mDataSource.remove(position);
        this.notifyDataSetChanged();
    }

    //4
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        rowView = mInflater.inflate(R.layout.list_item_popular, parent, false);
        rowView.setClickable(true);

        // Get the name of the item
        TextView itemTextView = (TextView) rowView.findViewById(R.id.item_name);

        // get check image
        final ImageView imageAdd = ((ImageView) rowView.findViewById(R.id.imageAdd));

        // Get image of the item
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image_view);

        //Get the item
        final Item item = (Item) getItem(position);

        // Load the Elements with data
        itemTextView.setText(item.getName());

        // Check if the image is in cache
        Bitmap image = CacheManager.getImageFromMemoryCache("item_" + String.valueOf(item.getName()));
        if(image != null)
        {
            imageView.setImageBitmap(image);
        }
        else
        {
            Picasso.with(mContext).load(item.getIurl()).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Bitmap image=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    CacheManager.saveImageToMemoryCache("item_" + String.valueOf(item.getName()), image);
                }

                @Override
                public void onError() {
                    Log.d("FAILED",item.getName() + " " + item.getIurl());
                }
            });
        }

        // set the check box value if the user already follows it
        Boolean userHasItem = mBusiness.isUserItemInList(userItems,item);
        if (userHasItem) {
            imageAdd.setImageResource(R.mipmap.ic_check_circle_black_24dp);
            imageAdd.setTag("checked");
        } else {
            imageAdd.setImageResource(R.mipmap.ic_add_circle_outline_black_24dp);
            imageAdd.setTag("unchecked");
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setPriority(2);
                Boolean isChecked = imageAdd.getTag() == "checked" ? true : false;
                if (!isChecked) {
                    Boolean isSuccess = mBusiness.saveUserItemLocal(item);
                    if (isSuccess) {
                        mBusiness.saveUserItemToServer(item, new CallbackInterface() {
                            @Override
                            public void onCompleted(Object data) {

                            }
                        });
                        imageAdd.setImageResource(R.mipmap.ic_check_circle_black_24dp);
                        imageAdd.setTag("checked");
                    }
                } else {
                    Boolean isSuccess = mBusiness.deleteUserItemLocal(item);
                    if (isSuccess) {
                        mBusiness.deleteUserItemFromServer(item.getId(), new CallbackInterface() {
                            @Override
                            public void onCompleted(Object data) {

                            }
                        });
                        imageAdd.setImageResource(R.mipmap.ic_add_circle_outline_black_24dp);
                        imageAdd.setTag("unchecked");
                    }
                }
            }
        });
        return rowView;
    }


}
