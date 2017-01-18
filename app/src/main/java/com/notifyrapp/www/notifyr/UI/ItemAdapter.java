
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
        import android.widget.RelativeLayout;
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

/**
 * Created by dchi on 12/29/16.
 */

public class ItemAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Item> mDataSource;
    private Map<Integer, Item> mDeleteQueue;
    private MainActivity act;
    private ItemAdapter adapter;
    private Business mBusiness;
    private RelativeLayout mNothingFoundView;

    public CheckBox checkBoxDelete;
    public View rowView;




    public ItemAdapter(Context context, List<Item> items, Map<Integer, Item> deleteItemsQueue, MainActivity activity, RelativeLayout nothingFoundView) {
        mContext = context;
        mDataSource = items;
        mDeleteQueue = deleteItemsQueue;
        act = activity;
        adapter = this;
        mBusiness = new Business(context);
        mNothingFoundView = nothingFoundView;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource == null ? 0 : mDataSource.size();
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        rowView = mInflater.inflate(R.layout.list_item, parent, false);
        rowView.setClickable(true);
        final MyItemsFragment itemsFragment = new MyItemsFragment();

        //set the checkbox visibility to gone
        checkBoxDelete = (CheckBox) rowView.findViewById(R.id.checkboxDelete);

        checkBoxDelete.setTag(position);
        Boolean isInEditMode = GlobalShared.getIsInEditMode();

        if(isInEditMode)
        {
            checkBoxDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            // Not in edit mode restore to all unchecked
            checkBoxDelete.setChecked(false);
            mDeleteQueue.clear();
            checkBoxDelete.setVisibility(View.GONE);
        }

        checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // get checkbox position
                Integer position = (Integer) buttonView.getTag();
                // get item
                Item deleteItem = mDataSource.get(position);
                deleteItem.setItemRowId(position);

                if(isChecked) {
                    // item is checked so we need to queue a delete
                    // check if its already in the collection
                    if(!mDeleteQueue.containsKey(position))
                    {
                        // it doesn't so insert it
                        mDeleteQueue.put(position,deleteItem);
                    }
                }
                else
                {
                    // item is NOT checked make sure its out of the delete queue
                    // we don't want to delete an item that got unchecked
                    if(mDeleteQueue.containsKey(position))
                    {
                        // uhoh the item is in the delete queue even though
                        // the user unchecked - remove it . This can happen if
                        // the user checks and unchecks an item
                        mDeleteQueue.remove(position);
                    }
                }
            }
        });

        // We need to check if the current checkbox is in the delete queue to recheck it
        // this happens because android redraws the view on listview scroll
        Integer currentButtonPosition = (Integer) checkBoxDelete.getTag();
        if(mDeleteQueue.containsKey(currentButtonPosition))
        {
            checkBoxDelete.setChecked(true);
        }



        // Get the name of the item
        final TextView itemTextView = (TextView) rowView.findViewById(R.id.item_name);

        // get frequency
        final TextView frequencyTextView = ((TextView) rowView.findViewById(R.id.item_frequency));


        // Get image of the item
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image_view);

        //Get the item
        final Item item = (Item) getItem(position);

        // Load the Elements with data
        itemTextView.setText(item.getName());
        frequencyTextView.setText("Frequency: "+item.getPriorityString());

        //for the frequency button
        Button btnFrequency = (Button) rowView.findViewById(R.id.btnFrequency);
        btnFrequency.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(v.getContext())
                        .title("Set Frequency")
                        .content("How often would you like to receive notifications about "+ item.getName() +"?")
                        .positiveText("Save")
                        .negativeText("Cancel")
                        .items(R.array.freq_options)
                        .itemsCallbackSingleChoice(3-item.getPriority(), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                int selectPriority = 3-which;
                                Business business = new Business(mContext);
                                item.setPriority(selectPriority);
                                business.saveUserItemLocal(item);
                                business.updateUserItemPriorityFromServer(item.getId(), selectPriority, new CallbackInterface() {
                                    @Override
                                    public void onCompleted(Object data) {
                                        // TODO: Check for a fail here!!
                                    }
                                });
                                adapter.notifyDataSetChanged();
                                Log.d("SELECTED RADIO BUTTON:",text + ":" + which);
                                Toast.makeText(act,"Saved", Toast.LENGTH_SHORT).show();
                                return true;
                            }

                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();

            }
        });

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

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new ArticleListFragment().newInstance(item.getId(),item.getName());
                android.support.v4.app.FragmentTransaction transaction =  act.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack("myitems_frag");
                final Drawable upArrow = act.getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                upArrow.setColorFilter(act.getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
                TextView abTitle =  (TextView)act.findViewById(R.id.abTitle);
                abTitle.setText("");
                abTitle.setPadding(0,0,150,0);
                transaction.commit();
            }
        });

        rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                BubbleActions.on(v)
                        .addAction("Remove", R.drawable.bubble_delete, new Callback() {
                            @Override
                            public void doAction() {
                                Boolean isSuccess = mBusiness.deleteUserItemLocal(item);
                                if(isSuccess) {
                                    mDataSource.remove(position);
                                    // Now delete from Server
                                    mBusiness.deleteUserItemFromServer(item.getId(), new CallbackInterface() {
                                        @Override
                                        public void onCompleted(Object data) {
                                            // Maybe log something here later
                                        }
                                    });


                                    if(mDataSource.size() == 0)
                                    {
                                        mNothingFoundView.setVisibility(View.VISIBLE);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(v.getContext(), "Removed " + item.getName(), Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(v.getContext(), "Error Removing " + item.getName(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addAction("Frequency", R.drawable.bubble_more, new Callback() {
                            @Override
                            public void doAction() {

                                new MaterialDialog.Builder(v.getContext())
                                        .title("Set Frequency")
                                        .content("How often would you like to receive notifications about "+ item.getName() +"?")
                                        .positiveText("Save")
                                        .negativeText("Cancel")
                                        .items(R.array.freq_options)
                                        .itemsCallbackSingleChoice(3-item.getPriority(), new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                                int selectPriority = 3-which;
                                                Business business = new Business(mContext);
                                                item.setPriority(selectPriority);
                                                business.saveUserItemLocal(item);
                                                business.updateUserItemPriorityFromServer(item.getId(), selectPriority, new CallbackInterface() {
                                                    @Override
                                                    public void onCompleted(Object data) {
                                                        // TODO: Check for a fail here!!
                                                    }
                                                });
                                                adapter.notifyDataSetChanged();
                                                Log.d("SELECTED RADIO BUTTON:",text + ":" + which);
                                                Toast.makeText(act,"Saved", Toast.LENGTH_SHORT).show();
                                                return true;
                                            }

                                        })
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                            }
                                        })
                                        .show();
                            }
                        })
                        .addAction("Close", R.drawable.bubble_hide, new Callback() {
                            @Override
                            public void doAction() {

                            }
                        })
                        .show();
                return false;
            }
        });

        return rowView;
    }


}

