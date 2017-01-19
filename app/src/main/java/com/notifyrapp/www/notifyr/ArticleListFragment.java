package com.notifyrapp.www.notifyr;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

    private int selectedIndex;
    private int itemId;
    private int itemTypeId;
    private Boolean isItemMode;
    private String itemName;
    private Context ctx;
    private Activity act;
    private ListView mListView;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeContainer;
    private WebViewFragment mWebViewFragment;
    private int currentPage = 0;
    private final int pageSize = 20;
    private Business.SortBy sortBy = Business.SortBy.Newest;
    private FloatingActionButton upFab;
    private ArticleAdapter adapter;
    private ProgressBar pbFooter;
    private InfiniteScrollListener mInfiniteScrollListener;
    private RadioGroup radioGroupArticleSort;
    private RadioButton radioButtonNewest;
    private RadioButton radioButtonPopular;
    private RadioButton radioButtonBookmark;
    private View sortViewHeader;
    private Boolean showFooterLoader = true;
    // The actual articles display on the screen
    private List<Article> articleListOnScreen;
    private RelativeLayout nothingFoundView;
    // Temp buffers to hold contents of sort types
    // These will be swapped with articleListOnScreen
    // if selected index of the checkbox is active
    private List<Article> articleListNewestBuffer;
    private List<Article> articleListPopularBuffer;
    private List<Article> articleListBookmarkBuffer;

    public TextView abTitle;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    public static ArticleListFragment newInstance(int position,int itemTypeId, String itemName) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putInt("selectedIndex", position);
        args.putInt("itemTypeId", itemTypeId);
        args.putString("itemName", itemName);
        args.putBoolean("isItemMode", false);

        fragment.setArguments(args);

        return fragment;
    }

    public static ArticleListFragment newInstance(int itemId,String itemName) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putInt("itemId", itemId);
        args.putString("itemName", itemName);
        args.putBoolean("isItemMode", true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            isItemMode = getArguments().getBoolean("isItemMode");
            if(isItemMode) {
                itemId = getArguments().getInt("itemId");
                itemName = getArguments().getString("itemName");
                itemTypeId = getArguments().getInt("itemTypeId");
            } else {
                selectedIndex = getArguments().getInt("selectedIndex");
                itemName = getArguments().getString("itemName");
                itemTypeId = getArguments().getInt("itemTypeId");
            }

        }


        // Init the Widgets
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);

        articleListOnScreen = new ArrayList<>();
        articleListNewestBuffer = new ArrayList<>();
        articleListBookmarkBuffer = new ArrayList<>();
        articleListPopularBuffer = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity act = (MainActivity)getActivity();

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_article_list, container, false);
        sortViewHeader = View.inflate(getActivity(), R.layout.article_sort, null);
        this.act = act;
        this.ctx = view.getContext();
        Button btnEditDoneDelete = (Button) act.findViewById(R.id.btnEditDone);
        Button btnTrashcanDelete = (Button) act.findViewById(R.id.btnTrashCanDelete);
        btnEditDoneDelete.setVisibility(View.GONE);
        btnTrashcanDelete.setVisibility(View.GONE);

        abTitle =  (TextView)act.findViewById(R.id.abTitle);
        nothingFoundView = (RelativeLayout) view.findViewById(R.id.article_not_found);

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
        radioGroupArticleSort =  (RadioGroup) sortViewHeader.findViewById(R.id.radioGroupArticleSort);
        radioButtonNewest = (RadioButton) sortViewHeader.findViewById(R.id.radioButtonNewest);
        radioButtonPopular = (RadioButton) sortViewHeader.findViewById(R.id.radioButtonPopular);
        radioButtonBookmark = (RadioButton) sortViewHeader.findViewById(R.id.radioButtonBookmarks);
        if(radioButtonNewest.isChecked())  {
            this.sortBy = Business.SortBy.Newest;
        }
        else if(radioButtonPopular.isChecked())  {
            this.sortBy = Business.SortBy.Popular;
        }
        else  {
            this.sortBy = Business.SortBy.Bookmark;
        }

        if(isItemMode) {
            radioButtonBookmark.setVisibility(View.GONE);
        }

        mListView = (ListView) view.findViewById(R.id.article_list_view);
        mListView.setFooterDividersEnabled(false);
        mListView.setHeaderDividersEnabled(false);
        mListView.addHeaderView(sortViewHeader);

        // Config adapter and get the first batch of articles
        adapter = new ArticleAdapter(ctx, articleListOnScreen);
        mListView.setAdapter(adapter);

        // Only get articles if the list is empty
        // This is to prevent api calls when selecting
        // the home tab.
        if(articleListOnScreen.size() == 0)
        {
            getArticles(0,pageSize,sortBy.toString());
        }

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showFooterLoader = false;
                mListView.setAdapter(adapter);
                articleListOnScreen.clear();
                mInfiniteScrollListener.setCurrentPage(0);
                currentPage = 0;
                getArticles(0,pageSize,sortBy.toString());
                showFooterLoader = true;
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
                    getArticles((currentPage) * pageSize, pageSize, sortBy.toString());
                }
            }

            @Override
            public void onUpScrolling() {
            }

            @Override
            public void onDownScrolling() {
            }

        };

        mListView.setOnScrollListener(mInfiniteScrollListener);
        mInfiniteScrollListener.setCurrentPage(0);

        View progressView = inflater.inflate(R.layout.progress_circle, null);
        pbFooter = (ProgressBar) progressView.findViewById(R.id.pb_main);
        pbFooter.setVisibility(View.VISIBLE);
        mListView.addFooterView(progressView);
        // Add the onclick listener to open the web view
        mListView.setClickable(true);
     /*   mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AppBarLayout appBar = (AppBarLayout) getActivity().findViewById(R.id.appbar);
                appBar.setVisibility(View.INVISIBLE);
                Article article = articleListOnScreen.get(position-1);
                mWebViewFragment = new WebViewFragment().newInstance(article);
                fragmentTransaction.add(R.id.fragment_container, mWebViewFragment, "webview_frag");
                fragmentTransaction.addToBackStack("articlelist_frag");
                fragmentTransaction.commit();
            }
        });*/
        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View v,
                                           final int pos, long id) {
                final Business business = new Business(ctx);
                final int position  = pos -1;
                final Article article = articleListOnScreen.get(position);
                // bookmark buttons
                if(sortBy == Business.SortBy.Bookmark)
                {
                    BubbleActions.on(v)
                            .addAction("Remove", R.drawable.bubble_delete, new Callback() {
                                @Override
                                public void doAction() {
                                    Boolean isSuccess = business.deleteBookmark(article);
                                    if(isSuccess) {
                                        articleListOnScreen.remove(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(v.getContext(), "Removed Bookmark", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(v.getContext(), "Error Removing Bookmark", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(v.getContext(), "Bookmarked", Toast.LENGTH_SHORT).show();
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

        // Handle Sort Order and it's Changes
        radioGroupArticleSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                articleListOnScreen.clear();
                if (radioButtonNewest.isChecked())
                {
                    if(articleListNewestBuffer.size() == 0)
                    {
                        getArticles(0, pageSize, Business.SortBy.Newest.toString());
                    }
                    else
                    {
                        articleListOnScreen.addAll(articleListNewestBuffer);
                        adapter.notifyDataSetChanged();
                    }
                    sortBy =  Business.SortBy.Newest;
                }
                else if (radioButtonPopular.isChecked())
                {
                    if(articleListPopularBuffer.size() == 0)
                    {
                        getArticles(0, pageSize, Business.SortBy.Popular.toString());
                    }
                    else
                    {
                        articleListOnScreen.addAll(articleListPopularBuffer);
                        adapter.notifyDataSetChanged();
                    }
                    sortBy =  Business.SortBy.Popular;
                }
                else if (radioButtonBookmark.isChecked())
                {
                    getArticles(0, pageSize, Business.SortBy.Bookmark.toString());
                    sortBy =  Business.SortBy.Bookmark;
                }
            }
        });


        return view;
    }


    public void getArticles(final int skip, final int take, final String sortBy)
    {
        final Business business = new Business(ctx);
        // Set the Loader
        if (pbFooter != null && showFooterLoader) pbFooter.setVisibility(View.VISIBLE);
        if (pbFooter != null && !showFooterLoader)  pbFooter.setVisibility(View.GONE);

        if(sortBy == Business.SortBy.Bookmark.toString()) {
            List<Article> bookmarks = business.getBookmarks(skip, take);
            if(bookmarks != null && bookmarks.size() > 0) {
                nothingFoundView.setVisibility(View.GONE);
                articleListOnScreen.addAll(bookmarks);
                adapter.notifyDataSetChanged();
            }
            else
            {
                nothingFoundView.setVisibility(View.VISIBLE);
            }
            if (pbFooter != null && articleListOnScreen.size() < pageSize) {
                pbFooter.setVisibility(View.GONE);
            } else {
                pbFooter.setVisibility(View.VISIBLE);
            }
            mSwipeContainer.setRefreshing(false);
        }
        else {
            business.getArticlesFromServer(skip, pageSize, sortBy, itemTypeId,itemId,isItemMode, new CallbackInterface() {

                @Override
                public void onCompleted(Object data) {
                    List<Article> articles = (List<Article>) data;
                    // At this point we know that the data was saved into the DB
                    //  List<Article> localArticles = new ArrayList<Article>();

                    if (sortBy == Business.SortBy.Newest.toString()) {
                        //articleListNewestBuffer = business.getUserArticlesFromLocal(skip, take, Business.SortBy.Popular.toString(), -1);
                        articleListNewestBuffer = (List<Article>) data;
                        if(pbFooter != null && articles.size() > 0) articleListOnScreen.addAll(articleListNewestBuffer);
                    } else if (sortBy == Business.SortBy.Popular.toString()) {
                        ///articleListPopularBuffer = business.getUserArticlesFromLocal(skip, take, Business.SortBy.Newest.toString(), -1);
                        articleListPopularBuffer = (List<Article>) data;
                        if(pbFooter != null && articles.size() > 0) articleListOnScreen.addAll(articleListPopularBuffer);
                    }

                    if (pbFooter != null && articleListOnScreen.size() <  pageSize) {
                        pbFooter.setVisibility(View.GONE);
                    } else {
                        pbFooter.setVisibility(View.VISIBLE);
                    }
                    if(pbFooter != null && articles.size() > 0) {
                        adapter.notifyDataSetChanged();
                        currentPage++;
                    }

                    /// Show or hide the nothing found view
                    if(articleListOnScreen.size() == 0) {
                        nothingFoundView.setVisibility(View.VISIBLE);
                    }
                    else {
                        nothingFoundView.setVisibility(View.GONE);
                    }

                    mSwipeContainer.setRefreshing(false);
                }
            });
        }
        mListView.removeHeaderView(sortViewHeader);
        mListView.addHeaderView(sortViewHeader);
    }

    private void clearArticleBuffers()
    {
        articleListPopularBuffer.clear();
        articleListBookmarkBuffer.clear();
        articleListNewestBuffer.clear();
        articleListOnScreen.clear();
        currentPage = 0;
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
