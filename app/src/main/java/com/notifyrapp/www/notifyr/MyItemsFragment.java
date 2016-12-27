package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.DownloadImageTask;
import com.notifyrapp.www.notifyr.Model.Item;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyItemsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyItemsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /* Widgets */
    EditText searchText;

    public MyItemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyItemsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyItemsFragment newInstance(String param1, String param2) {
        MyItemsFragment fragment = new MyItemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_items, container, false);
        MainActivity act = (MainActivity)getActivity();
        act.abTitle.setText("My Interests");
        // Init the Widgets
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.menu_tab_1);
        Business biz = new Business(view.getContext());
        List<Item> userItemsList = biz.getUserItemsFromLocal();

        TableLayout itemTable =  (TableLayout) view.findViewById(R.id.my_items_table);
        for (final Item currentItem: userItemsList) {
            TableRow row = (TableRow)inflater.inflate(R.layout.item_row, null,false);//(TableRow) view.findViewById(R.id.item_row);
            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Clicked Item: " + currentItem.getName(),
                            Toast.LENGTH_SHORT).show();

                    Fragment newFragment = new ArticleListFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack("myitems_frag");
                    transaction.commit();
                }
            });
            row.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v) {
                    Log.d("Priority:", String.valueOf(currentItem.getPriority()));
                    new MaterialDialog.Builder(v.getContext())
                            .title("Set Frequency")
                            .content("How often would you like to receive notifications?")
                            .positiveText("Save")
                            .negativeText("Cancel")
                            .items(R.array.freq_options)
                            .itemsCallbackSingleChoice(3-currentItem.getPriority(), new MaterialDialog.ListCallbackSingleChoice() {
                                @Override
                                public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    /**
                                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                                     * returning false here won't allow the newly selected radio button to actually be selected.
                                     **/
                                    Log.d("SELECTED RADIO BUTTON:",text + ":" + which);
                                    Toast.makeText(getActivity(), text + ", ID = " + which, Toast.LENGTH_SHORT).show();
                                    return true;
                                }

                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();
                    return false;
                }
            });

            row.setBackgroundResource(R.drawable.row_border);

            ((TextView)row.findViewById(R.id.item_name)).setText(currentItem.getName());

            ((TextView)row.findViewById(R.id.item_frequency)).setText("Frequency: "+currentItem.getPriorityString());

            ImageView image = (ImageView) row.findViewById(R.id.item_image_view);

            new DownloadImageTask(image).execute(currentItem.getIurl());

            itemTable.addView(row);
        }

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
