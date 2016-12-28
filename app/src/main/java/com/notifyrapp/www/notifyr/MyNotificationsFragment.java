package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.os.Handler;


import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Business.DownloadImageTask;


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

    private OnFragmentInteractionListener mListener;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_notifications, container, false);
        //set the swipeView
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                swipeView.setRefreshing(true);
                //Log.d("Swipe", "Refreshing Number");
                (new Handler()).postDelayed (new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);

                    }

                },1000);


            }

        });

        Business biz = new Business(view.getContext());
        List<Article> userNotificationsList = biz.getUserNotificationsLocal();

        TableLayout notificationTable = (TableLayout) view.findViewById(R.id.my_notifications_table);
        for (final Article currentArticle: userNotificationsList) {

            TableRow row = (TableRow) inflater.inflate (R.layout.notification_row, null, false);
            row.setClickable(true);
            /*
            row.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Fragment newFragment = new ArticleListFragment()
                }
            }
            */
            //set the border on the backgroud
            row.setBackgroundResource(R.drawable.row_border);
            //title of the notification article
            ((TextView)row.findViewById(R.id.txtNotificationArticle)).setText(currentArticle.getTitle());
            //name of the source
            ((TextView)row.findViewById(R.id.txtNotificationSource)).setText(currentArticle.getSource());
            //name of the item the notification is associated with
            ((TextView)row.findViewById(R.id.txtNotificationItem)).setText(currentArticle.getRelatedInterests());
            //time ago
            ((TextView)row.findViewById(R.id.txtNotificationTime)).setText(currentArticle.getNotifiedTimeAgo());//or gettimeago
            //image icon of the item the notification is associated with
            ImageView image = (ImageView) row.findViewById(R.id.imgNotification);
            new DownloadImageTask(image).execute(currentArticle.getRelatedInterestsURL());
            notificationTable.addView(row);

        }
        return view;

        //make an array list of articles, and make two or three of them.
        //for loop through each one of the three.
        //make an array of Articles from the article class
        //paging
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
