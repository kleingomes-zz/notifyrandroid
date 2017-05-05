package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Button;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Business.GlobalShared;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.UI.ItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    private ItemAdapter newItemAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private ArrayList<Item> list_items = new ArrayList<>();
    private int count = 0;
    private int searchFilterCounter = 0;
    public Button btnEditDoneDelete;
    private Button btnTrashcanDelete;
    private CheckBox checkBoxDelete;
    public MainActivity act;
    public int counterforDeleteButton = 0;
    public int visibility = 0;
    private Map<Integer, Item> itemsToDelete;
    public static MyItemsFragment fragment;
    private RelativeLayout nothingFoundView;
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
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
        userItemsList = new ArrayList<>();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GlobalShared.setIsEditMode(false);
        itemsToDelete = new HashMap<>();
        act = (MainActivity) getActivity();
        act.abTitle.setText("My Interests");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.menu_tab_1);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_items, container, false);
        nothingFoundView = (RelativeLayout) view.findViewById(R.id.nothing_found);
        this.ctx = view.getContext();
        btnEditDoneDelete = (Button) act.findViewById(R.id.btnEditDone);
        btnEditDoneDelete.setVisibility(View.VISIBLE);
        btnTrashcanDelete = (Button) act.findViewById(R.id.btnTrashCanDelete);
        mListView = (ListView) view.findViewById(R.id.items_list_view);
        itemAdapter = new ItemAdapter(ctx, userItemsList, itemsToDelete, (MainActivity) getActivity(), nothingFoundView);
        mListView.setAdapter(itemAdapter);
        View emptyFooter = inflater.inflate(R.layout.empty_table_footer, null);
        View filterHeader = inflater.inflate(R.layout.list_item_filter_search_header, null);
        mListView.setHeaderDividersEnabled(true);
        mListView.addHeaderView(filterHeader);
        mListView.setFooterDividersEnabled(false);
        mListView.addFooterView(emptyFooter);
        btnEditDoneDelete.setText("Edit");

        mListView.setClickable(true);


        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerItems);
        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!GlobalShared.getIsInEditMode()) {
                    mListView.setAdapter(itemAdapter);
                    userItemsList.clear();
                    getUserItems();
                }
                mSwipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getUserItems();

        //Define the SearchView
        final SearchView searchView = (SearchView) view.findViewById(R.id.filter_search);
        final ArrayList<Item> tempList = new ArrayList<>();

        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchView.setIconified(false);//makes entire search bar clickable
                //save the current list of user items into tempList
                if (userItemsList.size() > 0) {
                    for (Item element : userItemsList) {
                        tempList.add(element);
                    }
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 2) {
                    userItemsList.clear(); //clear the userItemsList
                    for (Item temp : tempList) { //iterate through temp list
                        if (temp.getName().toLowerCase().contains(newText.toLowerCase())) //if the item name contains the searched name
                        {
                            userItemsList.add(temp);
                        }
                    }
                }
                itemAdapter.notifyDataSetChanged();

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //mListView.setAdapter(newItemAdapter);
                return true;
            }
        });
        //when the user leaves the search bar return the original list view
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //copy the contents of templist back to userItemsList
                userItemsList.clear();
                for (Item tempListElement : tempList) {
                    userItemsList.add(tempListElement);
                }
                itemAdapter.notifyDataSetChanged();
                tempList.clear();
                return false;
            }
        });


        btnEditDoneDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterforDeleteButton++;
                if (counterforDeleteButton % 2 != 0) {
                    btnEditDoneDelete.setText("Done");
                    btnTrashcanDelete.setVisibility(View.VISIBLE);
                    GlobalShared.setIsEditMode(true);
                    mSwipeContainer.setEnabled(false);
                    itemAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Please select the items you wish to delete", Toast.LENGTH_SHORT).show();
                } else {
                    btnEditDoneDelete.setText("Edit");
                    btnTrashcanDelete.setVisibility(View.GONE);
                    GlobalShared.setIsEditMode(false);
                    mSwipeContainer.setEnabled(true);
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });

        btnTrashcanDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Need an offset to decrease target the new row
                // when the previous item gets deleted in the userItemsList
                Integer offset = 0;
                Boolean hasErrors = false;
                Business business = new Business(ctx);
                // Need to sort the value of rows so when we remove a row
                // we know the higher row will be decremented by one (the offset)
                List<Integer> sortedRowPositions = new ArrayList<>(itemsToDelete.keySet());
                Collections.sort(sortedRowPositions);

                for (Integer row : sortedRowPositions) {
                    // Get the item to delete
                    Item item = userItemsList.get(row - offset);

                    // Delete it from the local SQL DB first
                    // If that's a success then remove it from the listview adapter
                    Boolean deleteSuccess = business.deleteUserItemLocal(item);
                    // Now delete from Server
                    business.deleteUserItemFromServer(item.getId(), new CallbackInterface() {
                        @Override
                        public void onCompleted(Object data) {
                            // Maybe log something here later
                        }
                    });

                    if (deleteSuccess) {
                        Log.d("DELETING_ITEM", item.getName() + " ROW: " + item.getItemRowId());
                        userItemsList.remove(row - offset);
                        offset++;
                    } else {
                        hasErrors = true;
                    }
                }

                itemsToDelete.clear();
                itemAdapter.notifyDataSetChanged();
                if (userItemsList.size() == 0) {
                    nothingFoundView.setVisibility(View.VISIBLE);
                }
                if (hasErrors) {
                    Toast.makeText(getActivity(), "An unknown error occurred while deleting an item.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Item(s) deleted successfully!", Toast.LENGTH_SHORT).show();
                }

            }

        });

        return view;
    }

    public void getUserItems() {
        final Business business = new Business(ctx);
        List<Item> localItems = business.getUserItemsFromLocal();
        if (localItems.size() == 0) {
            nothingFoundView.setVisibility(View.VISIBLE);
        } else {
            nothingFoundView.setVisibility(View.GONE);
            userItemsList.clear();
            userItemsList.addAll(localItems);
            itemAdapter.notifyDataSetChanged();
        }
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
