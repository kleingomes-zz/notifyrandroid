package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.notifyrapp.www.notifyr.Business.BottomNavigationViewHelper;
import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.Item;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
        MyItemsFragment.OnFragmentInteractionListener,
        WebViewFragment.OnFragmentInteractionListener,
        ArticleListFragment.OnFragmentInteractionListener
{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Context ctx;
    SettingsFragment settingsFragment;
    MyItemsFragment myItemsFragment;
    TextView abTitle;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {


            case android.R.id.home:
                FragmentManager fm = this.getSupportFragmentManager();
                fm.popBackStack ("myitems_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.ctx = this;

        // INIT
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        abTitle =  (TextView)findViewById(R.id.abTitle);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorNotifyrLightBlue)));
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

     /*   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        int position = item.getItemId();
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                position = 0;
                                break;
                            case R.id.action_interests:
                                position = 1;
                                break;
                            case R.id.action_discover:
                                position = 2;
                                break;
                            case R.id.action_notifications:
                                position = 3;
                                break;
                            case R.id.action_settings:
                                position = 4;
                                break;
                        }
                        // FRAGMENT MANAGER
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        // DROP THE CURRENT FRAGMENT
                        Fragment fragment = null;

                        Fragment pos0 = getSupportFragmentManager().findFragmentByTag("home_frag");
                        Fragment pos1 = getSupportFragmentManager().findFragmentByTag("myitems_frag");
                        Fragment pos2 = getSupportFragmentManager().findFragmentByTag("discover_frag");
                        Fragment pos3 = getSupportFragmentManager().findFragmentByTag("notifications_frag");
                        Fragment pos4 = getSupportFragmentManager().findFragmentByTag("settings_frag");

                        if(pos0 != null) {  getSupportFragmentManager().beginTransaction().remove(pos0).commit(); }
                        if(pos1 != null) {  getSupportFragmentManager().beginTransaction().remove(pos1).commit(); }
                        if(pos2 != null) {  getSupportFragmentManager().beginTransaction().remove(pos2).commit(); }
                        if(pos3 != null) {  getSupportFragmentManager().beginTransaction().remove(pos3).commit(); }
                        if(pos4 != null) {  getSupportFragmentManager().beginTransaction().remove(pos4).commit(); }

                        // LOAD THE NEW FRAGMENT
                        if(position == 0)
                        {
                            AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
                            appBar.setVisibility(View.VISIBLE);
                            abTitle.setText(R.string.menu_tab_0);
                        }
                        else
                        {
                            AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
                            appBar.setVisibility(View.INVISIBLE);
                        }

                        if(position == 1)
                        {
                            abTitle.setText(R.string.menu_tab_1);
                            myItemsFragment = new MyItemsFragment();
                            fragmentTransaction.add(R.id.fragment_container, myItemsFragment,"myitems_frag");
                            fragmentTransaction.commit();
                        }
                        else if(position == 3) {   abTitle.setText(R.string.menu_tab_3); }
                        else if(position == 2) {   abTitle.setText(R.string.menu_tab_2); }
                        else if(position == 4)
                        {
                            abTitle.setText(R.string.menu_tab_4);
                            settingsFragment = new SettingsFragment();
                            fragmentTransaction.add(R.id.fragment_container, settingsFragment,"settings_frag");
                            fragmentTransaction.commit();
                        }
                        return false;
                    }
                });

       /* BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.ic_home_black_24dp, R.string.menu_tab_0))
                .addItem(new BottomNavigationItem(R.mipmap.ic_star_border_black_24dp, R.string.menu_tab_1))
                .addItem(new BottomNavigationItem(R.mipmap.ic_explore_black_24dp, R.string.menu_tab_2))
                .addItem(new BottomNavigationItem(R.mipmap.ic_notifications_none_black_24dp, R.string.menu_tab_3))
                .addItem(new BottomNavigationItem(R.mipmap.ic_settings_black_24dp, R.string.menu_tab_4))
                .setActiveColor(R.color.colorNotifyrLightBlue)
                .setInActiveColor("#95a5a6")
                .setBarBackgroundColor("#ECECEC")
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {

                // FRAGMENT MANAGER
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // DROP THE CURRENT FRAGMENT
                Fragment fragment = null;

                Fragment pos0 = getSupportFragmentManager().findFragmentByTag("home_frag");
                Fragment pos1 = getSupportFragmentManager().findFragmentByTag("myitems_frag");
                Fragment pos2 = getSupportFragmentManager().findFragmentByTag("discover_frag");
                Fragment pos3 = getSupportFragmentManager().findFragmentByTag("notifications_frag");
                Fragment pos4 = getSupportFragmentManager().findFragmentByTag("settings_frag");

                if(pos0 != null) {  getSupportFragmentManager().beginTransaction().remove(pos0).commit(); }
                if(pos1 != null) {  getSupportFragmentManager().beginTransaction().remove(pos1).commit(); }
                if(pos2 != null) {  getSupportFragmentManager().beginTransaction().remove(pos2).commit(); }
                if(pos3 != null) {  getSupportFragmentManager().beginTransaction().remove(pos3).commit(); }
                if(pos4 != null) {  getSupportFragmentManager().beginTransaction().remove(pos4).commit(); }

                // LOAD THE NEW FRAGMENT
                if(position == 0)
                {
                    AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
                    appBar.setVisibility(View.VISIBLE);
                    abTitle.setText(R.string.menu_tab_0);
                }
                else
                {
                    AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
                    appBar.setVisibility(View.INVISIBLE);
                }

                if(position == 1)
                {
                    abTitle.setText(R.string.menu_tab_1);
                    myItemsFragment = new MyItemsFragment();
                    fragmentTransaction.add(R.id.fragment_container, myItemsFragment,"myitems_frag");
                    fragmentTransaction.commit();
                }
                else if(position == 3) {   abTitle.setText(R.string.menu_tab_3); }
                else if(position == 2) {   abTitle.setText(R.string.menu_tab_2); }
                else if(position == 4)
                {
                    abTitle.setText(R.string.menu_tab_4);
                    settingsFragment = new SettingsFragment();
                    fragmentTransaction.add(R.id.fragment_container, settingsFragment,"settings_frag");
                    fragmentTransaction.commit();
                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });*/

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /** @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
    return true;
    }

    return super.onOptionsItemSelected(item);
    }*/
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Newest";
                case 1:
                    return "Popular";
                case 2:
                    return "Bookmarks";

            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_article, container, false);
            //  TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //   textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    //region My Interests



    //endregion
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
