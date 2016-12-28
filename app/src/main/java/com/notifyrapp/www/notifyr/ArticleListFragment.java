package com.notifyrapp.www.notifyr;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.ProgressBar;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.UI.ArticleAdapter;
import com.notifyrapp.www.notifyr.UI.InfiniteScrollListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArticleListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArticleListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArticleListFragment extends Fragment {

    private int mParam1;

    private Context ctx;
    private Activity act;
    private ListView mListView;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeContainer;
    private WebViewFragment mWebViewFragment;
    private List<Article> articleList;
    private int currentPage = 0;
    private final int pageSize = 20;
    private String sortBy = "Score";

    public ArticleListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @return A new instance of fragment ArticleListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArticleListFragment newInstance(int position) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        fragment.setArguments(args);

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt("pos");
        }
        if(mParam1 == 0)
        {
            this.sortBy = "PublishDate";
        }
        else if(mParam1 == 1)
        {
            this.sortBy = "Score";
        }
        else
        {
            this.sortBy = "Favourite";
        }
        // Init the Widgets
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.menu_tab_1);
        articleList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity act = (MainActivity)getActivity();
        this.act = act;

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        this.ctx = view.getContext();

        // Lookup the swipe container view
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mListView = (ListView) view.findViewById(R.id.article_list_view);

        // Get the first batch of articles
        getArticles(0,pageSize,sortBy);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call mSwipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                articleList.clear();
                getArticles(0,pageSize,sortBy);
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        // Add the scroll listener to know when we hit the bottom
        mListView.setOnScrollListener(new InfiniteScrollListener(10) {
            @Override
            public void loadMore(int page, int totalItemsCount) {
                getArticles(page*pageSize,pageSize,sortBy);
            }
        });

        // Add the onclick listener to open the web view
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
                appBar.setVisibility(View.INVISIBLE);
                String articleURL = articleList.get(position).getUrl();
                mWebViewFragment = new WebViewFragment().newInstance(articleURL);
                fragmentTransaction.add(R.id.fragment_container, mWebViewFragment, "webview_frag");
                fragmentTransaction.addToBackStack("articlelist_frag");
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void getArticles(int skip, int take, final String sortBy)
    {
        final Business business = new Business(ctx);
        business.getUserArticlesFromServer(skip,pageSize,"Score",-1, new CallbackInterface() {

            @Override
            public void onCompleted(Object data) {
                List<Article> articles = (List<Article>) data;
                List<Article> localArticles = business.getUserArticlesFromLocal(0,pageSize,sortBy,-1);
                articleList.addAll(localArticles);
                ArticleAdapter adapter = new ArticleAdapter(ctx, articleList);
                adapter.notifyDataSetChanged();
                if(articleList.size() == pageSize) {
                    mListView.setAdapter(adapter);
                }

                mSwipeContainer.setRefreshing(false);
            }
        });
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
