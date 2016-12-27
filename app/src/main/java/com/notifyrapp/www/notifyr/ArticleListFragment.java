package com.notifyrapp.www.notifyr;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.UI.ArticleAdapter;

import java.util.ArrayList;


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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.lightGray), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MainActivity act = (MainActivity)getActivity();
        this.act = act;
        act.abTitle.setText("");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_article_list, container, false);

       // MainActivity act = (MainActivity)getActivity();
        act.abTitle.setText("My Interests");
        // Init the Widgets
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.menu_tab_1);

        this.ctx = view.getContext();
        mListView = (ListView) view.findViewById(R.id.article_list_view);
        Business business = new Business(ctx);
        business.getUserArticlesFromServer(0,100,"Score",-1, new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {
                ArrayList<Article> articles = (ArrayList<Article>) data;
                ArticleAdapter adapter = new ArticleAdapter(ctx, articles);
                mListView.setAdapter(adapter);
            }
        });

        return view;
    }

   /* public class AsyncTest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            final Business business = new Business(ctx);
            business.getUserArticlesFromServer(0, 100, "Score", -1, new CallbackInterface() {
                @Override
                public void onCompleted(Object data) {

                    ArrayList<Article> articles = (ArrayList<Article>) data;
                    List<Article> articleList = business.getUserArticlesFromLocal(0,20,"Score",-1);

                    TableLayout itemTable =  (TableLayout) view.findViewById(R.id.article_list_table);
                    for (final Article currentArticle: articleList) {
                        TableRow row = (TableRow)inflater.inflate(R.layout.article_image_row, null,false);//(TableRow) view.findViewById(R.id.item_row);
                        row.setClickable(true);

                        row.setBackgroundResource(R.drawable.row_border);

                        ((TextView)row.findViewById(R.id.txtImageArticleTitle)).setText(currentArticle.getTitle());

                        ImageView image = (ImageView) row.findViewById(R.id.imgArticle);

                        new DownloadImageTask(image).execute(currentArticle.getIurl());

                        itemTable.addView(row);
                    }

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Business biz = new Business(ctx);

        }
    } */


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
