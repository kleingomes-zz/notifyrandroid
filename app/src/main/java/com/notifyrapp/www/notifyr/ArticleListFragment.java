package com.notifyrapp.www.notifyr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.UI.ArticleAdapter;
import com.notifyrapp.www.notifyr.UI.InfiniteScrollListener;

import java.util.ArrayList;
import java.util.List;

import me.samthompson.bubbleactions.BubbleActions;
import me.samthompson.bubbleactions.Callback;


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
    private int itemId;
    private int itemTypeId;
    private String itemName;
    private Context ctx;
    private Activity act;
    private ListView mListView;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeContainer;
    private WebViewFragment mWebViewFragment;
    private List<Article> articleList;
    private int currentPage = 0;
    private final int pageSize = 20;
    private Business.SortBy sortBy = Business.SortBy.Newest;
    private FloatingActionButton upFab;
    private ArticleAdapter adapter;
    private ProgressBar pbFooter;
    private InfiniteScrollListener mInfiniteScrollListener;
    private RadioGroup radioGroup;
    public TextView abTitle;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    public static ArticleListFragment newInstance(int position,int itemTypeId, String itemName) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        args.putInt("itemTypeId", itemTypeId);
        args.putString("itemName", itemName);
        fragment.setArguments(args);

        return fragment;
    }

    public static ArticleListFragment newInstance(int itemId,String itemName) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putInt("itemId", itemId);
        args.putString("itemName", itemName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt("pos");
            itemTypeId = getArguments().getInt("itemTypeId");
            itemId = getArguments().getInt("itemId");
            itemName = getArguments().getString("itemName");
        }
        if(mParam1 == 0)  {
            this.sortBy = Business.SortBy.Newest;
            //this.sortBy = "PublishDate";
        }
        else if(mParam1 == 1)  {
            this.sortBy = Business.SortBy.Popular;
            //this.sortBy = "Score";
        }
        else  {
            this.sortBy = Business.SortBy.Bookmark;
            //this.sortBy = "Favourite";
        }
        // Init the Widgets
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);

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
        Button btnEditDoneDelete = (Button) act.findViewById(R.id.btnEditDone);
        btnEditDoneDelete.setVisibility(View.GONE);
        abTitle =  (TextView)act.findViewById(R.id.abTitle);
        if(itemName!= null && !itemName.equals("")) {
            abTitle.setText(itemName);
         }
        /*upFab = (FloatingActionButton) view.findViewById(R.id.fab);
        upFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setSelectionAfterHeaderView();
            }
        });
        upFab.setVisibility(View.INVISIBLE);*/

        // Lookup the swipe container view
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        radioGroup =  (RadioGroup) view.findViewById(R.id.radioGroupTab);
        mListView = (ListView) view.findViewById(R.id.article_list_view);
        mListView.setFooterDividersEnabled(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.addFooterView(new View(ctx, null, 0));
        // Config adapter and get the first batch of articles
        adapter = new ArticleAdapter(ctx, articleList);
        mListView.setAdapter(adapter);
        getArticles(0,pageSize,sortBy.toString());

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListView.setAdapter(adapter);
                articleList.clear();
                mInfiniteScrollListener.setCurrentPage(0);
                currentPage = 0;
                pbFooter.setVisibility(View.GONE);
                getArticles(0,pageSize,sortBy.toString());
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Add the scroll listener to know when we hit the bottom
        mInfiniteScrollListener = new InfiniteScrollListener(1) {
            @Override
            public void loadMore(int page, int totalItemsCount) {

                if(totalItemsCount > 10) {
                    pbFooter.setVisibility(View.VISIBLE);
                    Log.d("CURRENTPAGE", String.valueOf(currentPage));
                    getArticles((currentPage) * pageSize, pageSize, sortBy.toString());
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

             /*  if(currentPage > 2)
                {
                    upFab.setVisibility(View.VISIBLE);
                }
                else
                {
                    upFab.setVisibility(View.INVISIBLE);
                }
                */
            }
        };

        mListView.setOnScrollListener(mInfiniteScrollListener);
        mInfiniteScrollListener.setCurrentPage(0);

        View progressView = inflater.inflate(R.layout.progress_circle, null);
        pbFooter = (ProgressBar) progressView.findViewById(R.id.pb_main);
        pbFooter.setVisibility(View.GONE);
        mListView.addFooterView(progressView);

        // Add the onclick listener to open the web view
        mListView.setClickable(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
                appBar.setVisibility(View.INVISIBLE);
                Article article = articleList.get(position);
                mWebViewFragment = new WebViewFragment().newInstance(article);
                fragmentTransaction.add(R.id.fragment_container, mWebViewFragment, "webview_frag");
                fragmentTransaction.addToBackStack("articlelist_frag");
                fragmentTransaction.commit();
            }
        });
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View v,
                                           final int pos, long id) {
                final Business business = new Business(ctx);
                final Article article = articleList.get(pos);
                // bookmark buttons
                if(mParam1 == 2)
                {
                    BubbleActions.on(v)
                            .addAction("Remove All", R.drawable.bubble_star, new Callback() {
                                @Override
                                public void doAction() {
                                    Boolean isSuccess = business.deleteBookmark(article);
                                    if(isSuccess){
                                        articleList.clear();
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "Removed All Bookmarks!", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(v.getContext(), "Error Removing Bookmarks!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addAction("Remove", R.drawable.bubble_star, new Callback() {
                                @Override
                                public void doAction() {
                                    Boolean isSuccess = business.deleteBookmark(article);
                                    if(isSuccess) {
                                        articleList.remove(pos);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "Removed Bookmarked!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(v.getContext(), "Error Removing Bookmark!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addAction("Share", R.drawable.bubble_share, new Callback() {
                                @Override
                                public void doAction() {
                                    Intent i=new Intent(android.content.Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, article.getTitle());
                                    i.putExtra(android.content.Intent.EXTRA_TEXT, article.getUrl());
                                    startActivity(Intent.createChooser(i,"Sent From www.notifyrapp.com"));
                                    //Toast.makeText(v.getContext(), "Share pressed!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addAction("Close", R.drawable.bubble_hide, new Callback() {
                                @Override
                                public void doAction() {
                                    Toast.makeText(v.getContext(), "Hide pressed!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
                else
                {
                    BubbleActions.on(v)
                            .addAction("Share", R.drawable.bubble_share, new Callback() {
                                @Override
                                public void doAction() {
                                    Intent i=new Intent(android.content.Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(android.content.Intent.EXTRA_SUBJECT, article.getTitle());
                                    i.putExtra(android.content.Intent.EXTRA_TEXT, article.getUrl());
                                    startActivity(Intent.createChooser(i,"Sent From www.notifyrapp.com"));
                                    //Toast.makeText(v.getContext(), "Share pressed!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addAction("Bookmark", R.drawable.bubble_star, new Callback() {
                                @Override
                                public void doAction() {
                                    Boolean isSuccess = business.saveBookmark(article);
                                    if(isSuccess)                                    {
                                        Toast.makeText(v.getContext(), "Bookmarked!", Toast.LENGTH_SHORT).show();
                                       ((MainActivity) getActivity()).isBookmarkDirty = true;
                                    }
                                    else                                    {
                                        Toast.makeText(v.getContext(), "Error Saving Bookmark!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addAction("Close", R.drawable.bubble_hide, new Callback() {
                                @Override
                                public void doAction() {
                                    Toast.makeText(v.getContext(), "Hide pressed!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                }
                return true;
            }
        });

        return view;
    }


    public void getArticles(final int skip, final int take, final String sortBy)
    {
        final Business business = new Business(ctx);

        if(mParam1 == 2) {
            List<Article> bookmarks = business.getBookmarks(skip, take);
            if(bookmarks.size() > 0) {
                articleList.addAll(bookmarks);
                adapter.notifyDataSetChanged();
            }
            mSwipeContainer.setRefreshing(false);
        }
        else {
            business.getUserArticlesFromServer(skip, pageSize, Business.SortBy.Popular.toString(), itemTypeId, new CallbackInterface() {

                @Override
                public void onCompleted(Object data) {
                    List<Article> articles = (List<Article>) data;
                    // At this point we know that the data was saved into the DB
                    //  List<Article> localArticles = new ArrayList<Article>();
                  /*  if (mParam1 == 2) {
                        localArticles = business.getBookmarks(skip, take);
                        mParam1 = 2;
                    } else if (mParam1 == 1) {
                        //  localArticles = business.getUserArticlesFromLocal(skip, take, Business.SortBy.Popular.toString(), -1);
                        mParam1 = 1;
                    } else if (mParam1 == 0) {
                        //  localArticles = business.getUserArticlesFromLocal(skip, take, Business.SortBy.Newest.toString(), -1);
                        mParam1 = 0;
                    } */
                    //if(skip == 0){
                    //    articleList.clear();
                    // }
                    if (pbFooter != null && articleList.size() == 0) {
                        pbFooter.setVisibility(View.GONE);
                    } else {
                        pbFooter.setVisibility(View.VISIBLE);
                    }
                    if(pbFooter != null && articles.size() > 0) {
                        articleList.addAll(articles);
                        adapter.notifyDataSetChanged();
                        currentPage++;
                    }
                    mSwipeContainer.setRefreshing(false);
                }
            });
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
