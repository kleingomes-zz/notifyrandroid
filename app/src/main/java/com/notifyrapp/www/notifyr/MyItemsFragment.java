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
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Button;
import android.widget.Toast;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.UI.InfiniteScrollListener;
import com.notifyrapp.www.notifyr.UI.ItemAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.DownloadImageTask;
import com.notifyrapp.www.notifyr.Model.Item;

import java.util.ArrayList;
import java.util.List;
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

    private Context ctx;
    private Button btnFrequency;
    private OnFragmentInteractionListener mListener;
    private TableLayout itemTable;
    private ListView mListView;
    private List<Item> userItemsList;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> list_items = new ArrayList<>();
    private int count = 0;
    public Button btnEditDoneDelete;
    private Button btnTrashcanDelete;
    private CheckBox checkBoxDelete;
    public MainActivity act;
    public int counterforDeleteButton = 0;
    public int visibility = 0;
    public static MyItemsFragment fragment;
    private int position;


//    private OnFragmentInteractionListener mListener;

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
        userItemsList = new ArrayList<>();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        act = (MainActivity) getActivity();
        act.abTitle.setText("My Interests");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_tab_1);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_items, container, false);
        this.ctx = view.getContext();
        btnEditDoneDelete = (Button) act.findViewById(R.id.btnEditDone);
        btnTrashcanDelete = (Button) act.findViewById(R.id.btnTrashCanDelete);
        mListView = (ListView) view.findViewById(R.id.items_list_view);
        //  checkBoxDelete = (CheckBox) mListView.findViewById(R.id.checkboxDelete);
        //Get the batch of items
        //final Business biz = new Business(view.getContext());
        itemAdapter = new ItemAdapter(ctx, userItemsList);
        mListView.setAdapter(itemAdapter);
        getUserItems();





                btnEditDoneDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        counterforDeleteButton++;
                        if (counterforDeleteButton % 2 != 0) {
                            btnEditDoneDelete.setText("Done");
                            btnTrashcanDelete.setVisibility(View.VISIBLE);
//                     itemAdapter.checkBoxDelete.setVisibility(View.VISIBLE);
                    for (int i = 0; i< itemAdapter.numberofItems-2; i++) //still need to get all of the items
                    {
                        checkBoxDelete = (CheckBox) view.findViewWithTag(String.valueOf(i));
                        checkBoxDelete.setVisibility(View.VISIBLE);
                    }

                            Toast.makeText(getActivity(), "Please select the items you wish to delete",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i< itemAdapter.numberofItems-2; i++) //still need to get all of the items
                            {
                                checkBoxDelete = (CheckBox) view.findViewWithTag(String.valueOf(i));
                                checkBoxDelete.setVisibility(View.GONE);
                            }
                            btnEditDoneDelete.setText("Edit");
                            btnTrashcanDelete.setVisibility(View.GONE);

//             checkboxDelete.setVisibility(View.GONE);

                        }

                    }

                });

        btnTrashcanDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //now we are actually going to remove the items from the listview when we hit the trashcan
                for (int i  = 0; i < itemAdapter.numberofItems; i++)
                {
                    checkBoxDelete = (CheckBox) view.findViewWithTag(String.valueOf(i));
                    if (checkBoxDelete.isChecked())
                    {
                        itemAdapter.remove(i);
                    }
                }
//                for (int i = 0; i < itemAdapter.selectedItemPositions.size(); i++)
//                {
//                    position = itemAdapter.selectedItemPositions.get(i);
//                    userItemsList.remove(position);
//                    itemAdapter.notifyDataSetChanged();

//                }
//                for (int i = 0; i < itemAdapter.selectedItemPositions.size(); i++)
//                {
//                     if (btnEditDoneDelete.getTag())
//                     if (itemAdapter.selectedItemPositions.contains((Integer)itemAdapter.checkBoxDelete.getTag()))
//                     {
//                         userItemsList.remove()
//                     }

//                }
                Toast.makeText(getActivity(), "Items have been deleted",
                        Toast.LENGTH_SHORT).show();
            }

        });


        //add the onclick listener to open the list of articles
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "Clicked Item: " + ItemAdapter.item.getName(),
                // Toast.LENGTH_SHORT).show();
                Fragment newFragment = new ArticleListFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack("myitems_frag");
                transaction.commit();
            }
        });


        return view;

    }

    public void getUserItems() {
        final Business business = new Business(ctx);
        List<Item> localItems = business.getUserItemsFromLocal();
        userItemsList.addAll(localItems);
        itemAdapter.notifyDataSetChanged();
        itemAdapter.notifyDataSetChanged();
        itemAdapter.notifyDataSetChanged();


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
