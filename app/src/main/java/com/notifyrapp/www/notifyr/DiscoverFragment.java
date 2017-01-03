package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.UI.DiscoverRecyclerAdapter;
import com.notifyrapp.www.notifyr.UI.DividerItemDecoration;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "RecyclerViewActivity";
    private TextView topResultsTextView;
    private Context ctx;
    private List<Item> itemsList;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipe;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_discover, container, false);
        final Business business = new Business(getContext());
        this.ctx = getContext();
        //Define your Searchview
        final SearchView searchView = (SearchView) view.findViewById(R.id.search_view);
        topResultsTextView = (TextView) view.findViewById(R.id.txtSettings);

        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutDiscoverRecyclerview);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPopularItems(true);
            }
        });
        swipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(searchView.isIconified())
                {
                    ((MainActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);
                    ((MainActivity)getActivity()).getSupportActionBar().hide();
                }
                searchView.setIconified(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(!searchView.isIconified())
                {
                    ((MainActivity)getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);
                    ((MainActivity)getActivity()).getSupportActionBar().show();
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                if(newText.equals("")) {

                    topResultsTextView.setText(R.string.header_you_might_like);
                    itemsList.clear();
                    getPopularItems(false);
                }
                else {
                    topResultsTextView.setText("Results...");
                    itemsList.clear();
                    if(newText.length() > 2) {
                        business.getItemsByQuery(newText, new CallbackInterface() {
                            @Override
                            public void onCompleted(Object data) {
                                List<Item> results = (List<Item>) data;
                                itemsList.addAll(results);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                //topResultsTextView.setText(R.string.header_you_might_like);
                return true;
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.discover_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);//new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        itemsList = new ArrayList<Item>();
        mAdapter = new DiscoverRecyclerAdapter(itemsList);
        mRecyclerView.setAdapter(mAdapter);
        getPopularItems(false);


        return view;
    }

    private void getPopularItems(Boolean forceServerLoad)
    {
        // First check if popular items is cached else get it from server
        List<Item> cachedItems = (List<Item>)CacheManager.getObjectFromMemoryCache("popular_items");
        if(cachedItems != null && forceServerLoad == false)
        {
            itemsList.addAll(cachedItems);
            mAdapter.notifyDataSetChanged();
        }
        else
        {
            new Business(ctx).getPopularItems(0, 20, new CallbackInterface() {
                @Override
                public void onCompleted(Object data) {
                    List<Item> downloadedItems = (List<Item>) data;
                    CacheManager.saveObjectToMemoryCache("popular_items", data);
                    itemsList.addAll(downloadedItems);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
        swipe.setRefreshing(false);
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
