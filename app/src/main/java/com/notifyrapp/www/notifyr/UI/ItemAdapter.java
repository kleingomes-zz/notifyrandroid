
package com.notifyrapp.www.notifyr.UI;


        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.drawable.BitmapDrawable;
        import android.support.annotation.NonNull;
        import android.support.v4.app.FragmentManager;
        import android.util.Log;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v4.app.Fragment;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
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
        import com.notifyrapp.www.notifyr.Business.CacheManager;
        import com.notifyrapp.www.notifyr.Business.CallbackInterface;
        import com.notifyrapp.www.notifyr.Business.DownloadImageTask;
        import com.notifyrapp.www.notifyr.Business.CacheManager;
        import com.notifyrapp.www.notifyr.MainActivity;
        import com.notifyrapp.www.notifyr.Model.Article;
        import com.notifyrapp.www.notifyr.Model.Item;
        import com.notifyrapp.www.notifyr.MyItemsFragment;
        import com.notifyrapp.www.notifyr.R;
        import com.squareup.picasso.Picasso;


        import java.util.ArrayList;
        import java.util.List;
/**
 * Created by dchi on 12/29/16.
 */

public class ItemAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Item> mDataSource;
    public List<Integer> selectedItemPositions = new ArrayList<>();

    public int numberofItems;
    private ProgressBar mProgressBar;
    private int counter;
    private int temporaryListPosition;
    private int i;
    public CheckBox checkBoxDelete;
    public View rowView;




    public ItemAdapter(Context context, List<Item> items) {
        mContext = context;
        mDataSource = items;
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

        // MainActivity act = (MainActivity) getActivity();
        // Get view for row item

         rowView = mInflater.inflate(R.layout.list_item, parent, false);
        rowView.setClickable(true);
        final MyItemsFragment itemsFragment = new MyItemsFragment();
//        itemsFragment = MyItemsFragment.fragment;
//        MainActivity activity = (MainActivity) MyItemsFragment.fragment.getActivity();
//        MainActivity activity = (MainActivity) .getActivity();
//        MainActivity activity =  itemsFragment.act;
//        Button btnEditDoneDelete = (Button) activity.findViewById(R.id.btnEditDone);
//        Button btnTrashcanDelete = (Button) activity.findViewById(R.id.btnTrashCanDelete);
        //set the checkbox visibility to gone
        checkBoxDelete = (CheckBox) rowView.findViewById(R.id.checkboxDelete);

//        checkBoxDelete.setTag("hello");

       numberofItems++;
        checkBoxDelete.setTag(String.valueOf(position));

//        selectedItemPositions.add((Integer) checkBoxDelete.getTag());



        //store an array full of the checkboxes to be deleted
//        numberofItems = mDataSource.size();
//        .containts
//        .size();
        //for(final int currentinteger: selectedItemsPosition)
//        Arrays.fill(itemListArray, -1);
//        checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (isChecked)
//                {
//                    counter = 3;
//                    selectedItemPositions.add(position);
//                }
//                else
//                {
//                    if (selectedItemPositions.contains(position))
//                    {
//                        selectedItemPositions.remove(position);
//                    }
//                    counter = 2;
//                }
//            }
//        });

//            @Override
//            public void onClick(View view) {
//                if (checkBoxDelete.isChecked() == true)
//                {
//                    counter = 3;
//                    selectedItemPositions.add(position);
//                    itemListArray[i++] = position; //stores the position in the array
//                }
//                else if (checkBoxDelete.isChecked() == false)
//                {
//                    counter = 2;
//                    if (selectedItemPositions.contains(position))
//                    {
//                        selectedItemPositions.remove(position);
//                    }
//                }
//
//
//            }
//        });

//        itemsFragment.btnEditDoneDelete.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkBoxDelete.setVisibility(View.VISIBLE);
//
//            }
//        });
/*
        if (itemsFragment.counterforDeleteButton%2 != 0)
        {
            checkBoxDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            checkBoxDelete.setVisibility(View.VISIBLE);
        }

*/
        // for the buttons in the action bar
        //   btnEditDone = (Button) rowView.findViewById(R.id.btnEditDone);
//        MainActivity.clickEvent(rowView)
//        if(btnEditDone.getText() == "Done")
//        {
//            checkBoxDelete.setVisibility(View.VISIBLE);
//        }
        // Get the name of the item
        TextView itemTextView = (TextView) rowView.findViewById(R.id.item_name);

        // get frequency
        TextView frequencyTextView = ((TextView) rowView.findViewById(R.id.item_frequency));


        // Get image of the item
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image_view);

        //Get the item
        final Item item = (Item) getItem(position);

        // Load the Elements with data
        itemTextView.setText(item.getName());
//       itemTextView.setText(Integer.toString(position));
        frequencyTextView.setText("Frequency: "+item.getPriorityString());

        //for the frequency button
        Button btnFrequency = (Button) rowView.findViewById(R.id.btnFrequency);
        btnFrequency.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(v.getContext())
                        .title("Set Frequency")
                        .content("How often would you like to receive notifications?")
                        .positiveText("Save")
                        .negativeText("Cancel")
                        .items(R.array.freq_options)
                        .itemsCallbackSingleChoice(3-item.getPriority(), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /**
                                 * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected radio button to actually be selected.
                                 **/
                                Log.d("SELECTED RADIO BUTTON:",text + ":" + which);
                                //Toast.makeText(getActivity(), text + ", ID = " + which, Toast.LENGTH_SHORT).show();
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

        return rowView;
    }
}
