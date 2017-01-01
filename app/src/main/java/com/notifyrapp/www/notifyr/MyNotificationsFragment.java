package com.notifyrapp.www.notifyr;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.os.Handler;


import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Business.DownloadImageTask;
import com.notifyrapp.www.notifyr.UI.ArticleAdapter;
import com.notifyrapp.www.notifyr.UI.InfiniteScrollListener;
import com.notifyrapp.www.notifyr.UI.NotificationAdapter;


import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyNotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyNotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyNotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context ctx;
    private Activity act;
    private ListView mListView;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeNotificationContainer;
    private List<Article> notificationList;
    private WebViewFragment mWebViewFragment;
    private final int pageSize = 20;
    private NotificationAdapter adapter;
    // private String sortBy = "ArticleNotifiedDate";


    public MyNotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyNotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyNotificationsFragment newInstance(String param1, String param2) {
        MyNotificationsFragment fragment = new MyNotificationsFragment();
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
        notificationList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.ctx = view.getContext();

        // Lookup the swipe container view
        mSwipeNotificationContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeNotificationContainer);
        mListView = (ListView) view.findViewById(R.id.notification_list_view);

        //Get the first batch of articles
        adapter = new NotificationAdapter(ctx, notificationList);
        mListView.setAdapter(adapter);
        getNotifications(0, pageSize);

        //Setup refresh listener which triggers new data loading
        mSwipeNotificationContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                notificationList.clear();
                adapter.notifyDataSetChanged();
                getNotifications(0, pageSize);
            }
        });

        // Configure the refreshing colors
        mSwipeNotificationContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //add the scroll listener to know when we hit the bottom
        mListView.setOnScrollListener(new InfiniteScrollListener(5) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                getNotifications(page * pageSize, pageSize);
            }
        });

        //Add the onclick listener to open the web view
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
                appBar.setVisibility(View.INVISIBLE);
                Article article = notificationList.get(position);
                mWebViewFragment = new WebViewFragment().newInstance(article);
                fragmentTransaction.add(R.id.fragment_container, mWebViewFragment, "webview_frag");
                fragmentTransaction.addToBackStack("notificationlist_frag");
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void getNotifications(int skip, int take) {
        final Business business = new Business(ctx);
        List<Article> localNotifications = business.getUserNotificationsLocal(skip, take);
        notificationList.addAll(localNotifications);
        adapter.notifyDataSetChanged();
        mSwipeNotificationContainer.setRefreshing(false);
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
