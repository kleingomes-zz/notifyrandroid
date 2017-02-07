package com.notifyrapp.www.notifyr;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.os.Handler;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Business.DownloadImageTask;
import com.notifyrapp.www.notifyr.Model.Item;
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
    private int currentPage = 0;
    private NotificationAdapter adapter;
    private RelativeLayout nothingFoundView;
    private Button clearAllBtn;
    private Button btnEditDone;
    private Button btnTrashCanDelete;

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
    public void onDestroyView() {
        clearAllBtn.setVisibility(View.GONE);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_notification_list, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        this.ctx = view.getContext();
        MainActivity act = (MainActivity) getActivity();
        act.abTitle.setText("Notifications");
        act.btnEditDone.setVisibility(View.GONE);
        act.btnTrashCanDelete.setVisibility(View.GONE);


        // Lookup the swipe container view
        mSwipeNotificationContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeNotificationContainer);
        mListView = (ListView)  view.findViewById(R.id.notification_list_view);
        clearAllBtn = (Button) ((MainActivity) getActivity()).findViewById(R.id.btnClearAll);
        clearAllBtn.setVisibility(View.VISIBLE);
        nothingFoundView = (RelativeLayout) view.findViewById(R.id.notify_not_found);
        adapter = new NotificationAdapter(ctx, notificationList);
        View emptyFooter = inflater.inflate(R.layout.empty_table_footer, null);
        mListView.addFooterView(emptyFooter);
        mListView.setAdapter(adapter);
        mListView.setFooterDividersEnabled(false);
        getNotifications(0, pageSize);


        //Setup refresh listener which triggers new data loading
        mSwipeNotificationContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationList.clear();
                currentPage = 0;
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
                if(totalItemsCount > 10) {
                    getNotifications(currentPage * pageSize, pageSize);
                }

            }

            @Override
            public void onUpScrolling() {

            }

            @Override
            public void onDownScrolling() {

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

        // Handle clear all button click
        clearAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(ctx)
                        .title("Confirmation")
                        .content("Are you sure?")
                        .contentColor(ContextCompat.getColor(ctx,R.color.notifyrBlue))
                        .positiveText("Yes")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new Business(ctx).deleteUserNotificationsLocal();
                            }
                        })
                        .show();
            }
        });


        return view;
    }

    public void getNotifications(final int skip, int take) {
        final Business business = new Business(ctx);
        List<Article> localNotifications = business.getUserNotificationsLocal(skip, take);
        nothingFoundView.setVisibility(View.GONE);
        notificationList.addAll(localNotifications);
        adapter.notifyDataSetChanged();

        if (localNotifications.size() == 0 && skip == 0) {
            nothingFoundView.setVisibility(View.VISIBLE);
        }
        else
        {
            nothingFoundView.setVisibility(View.GONE);
        }
        if(localNotifications.size() > 0) {
            currentPage++;
        }

        mSwipeNotificationContainer.setRefreshing(false);

     /*   List<Article> cachedItems = (List<Article>) CacheManager.getObjectFromMemoryCache("user_notifications");

        if(cachedItems != null && skip == 0)
        {
            notificationList.addAll(cachedItems);
            adapter.notifyDataSetChanged();
        }
        else {
            business.getUserNotificationsFromServer(skip, take, new CallbackInterface() {
                @Override
                public void onCompleted(Object data) {
                    if (localNotifications.size() == 0 && skip == 0) {
                        nothingFoundView.setVisibility(View.VISIBLE);
                    } else {
                        if(skip == 0) {
                            CacheManager.deleteObjectFromCache("user_notifications");
                            CacheManager.saveObjectToMemoryCache("user_notifications", data);
                        }
                        nothingFoundView.setVisibility(View.GONE);
                        notificationList.addAll(localNotifications);
                        adapter.notifyDataSetChanged();
                        currentPage++;
                    }
                    pbFooter.setVisibility(View.GONE);
                    mSwipeNotificationContainer.setRefreshing(false);
                }
            });
        }
*/
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
