package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.UI.BottomNavigationViewHelper;


public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
        MyItemsFragment.OnFragmentInteractionListener,
        WebViewFragment.OnFragmentInteractionListener,
        ArticleListFragment.OnFragmentInteractionListener,
        MyNotificationsFragment.OnFragmentInteractionListener

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
    private SettingsFragment settingsFragment;
    private MyItemsFragment myItemsFragment;
    private MyNotificationsFragment myNotificationsFragment;
    private ArticleListFragment bookmarksFrag;
    private ArticleListFragment newestFrag;
    private ArticleListFragment popularFrag;
    public TextView abTitle;
    public Business.MenuTab currentMenu = Business.MenuTab.Home;
    public boolean isBookmarkDirty;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        int id = item.getItemId();
        switch (item.getItemId()) {

            case android.R.id.home:
                FragmentManager fm = this.getSupportFragmentManager();
                if(currentMenu == Business.MenuTab.Notifications) {
                    fm.popBackStack("notificationlist_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                else if (currentMenu == Business.MenuTab.Home){
                    fm.popBackStack("articlelist_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportActionBar().hide();
                    setAppBarVisibility(false);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // INIT
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();

        this.ctx = this;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorNotifyrLightBlue)));
        getSupportActionBar().hide();

        abTitle =  (TextView)findViewById(R.id.abTitle);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // without this listener the tabs would still get updated when fragments are swiped, but ....  (read the next comment)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(isBookmarkDirty && tab.getPosition() == 2 && bookmarksFrag != null) {
                    bookmarksFrag.getArticles(0, 20, "");
                }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });



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

                        // Check which fragment is active, inactive ones return null
                        Fragment pos0 = getSupportFragmentManager().findFragmentByTag("home_frag");
                        Fragment pos1 = getSupportFragmentManager().findFragmentByTag("myitems_frag");
                        Fragment pos2 = getSupportFragmentManager().findFragmentByTag("discover_frag");
                        Fragment pos3 = getSupportFragmentManager().findFragmentByTag("notifications_frag");
                        Fragment pos4 = getSupportFragmentManager().findFragmentByTag("settings_frag");

                        // Remove the fragment that is not null from the manager
                        if(pos0 != null) {  getSupportFragmentManager().beginTransaction().remove(pos0).commit(); }
                        if(pos1 != null) {  getSupportFragmentManager().beginTransaction().remove(pos1).commit(); }
                        if(pos2 != null) {  getSupportFragmentManager().beginTransaction().remove(pos2).commit(); }
                        if(pos3 != null) {  getSupportFragmentManager().beginTransaction().remove(pos3).commit(); }
                        if(pos4 != null) {  getSupportFragmentManager().beginTransaction().remove(pos4).commit(); }

                        // SHOW/HIDE the app bar depending on which menu tab you're on
                        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                        if(position == 0)
                        {
                            currentMenu = Business.MenuTab.Home;
                            getSupportActionBar().hide();
                            setAppBarVisibility(false);
                            abTitle.setText(R.string.empty);
                            viewPager.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            getSupportActionBar().show();
                            setAppBarVisibility(true);
                            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            viewPager.setVisibility(View.GONE);
                        }

                        // LOAD THE NEW FRAGMENT
                        if(position == 1)
                        {
                            abTitle.setText(R.string.menu_tab_1);
                            currentMenu = Business.MenuTab.Interests;
                            myItemsFragment = new MyItemsFragment();
                            fragmentTransaction.add(R.id.fragment_container, myItemsFragment,"myitems_frag");
                            fragmentTransaction.commit();
                        }
                        else if(position == 3) {   abTitle.setText(R.string.menu_tab_3);
                            currentMenu = Business.MenuTab.Notifications;
                            myNotificationsFragment = new MyNotificationsFragment();
                            fragmentTransaction.add(R.id.fragment_container, myNotificationsFragment, "notifications_frag");
                            fragmentTransaction.commit();
                        }
                        else if(position == 2) {
                            currentMenu = Business.MenuTab.Discover;
                            abTitle.setText(R.string.menu_tab_2);
                        }
                        else if(position == 4)
                        {
                            currentMenu = Business.MenuTab.Settings;
                            abTitle.setText(R.string.menu_tab_4);
                            settingsFragment = new SettingsFragment();
                            fragmentTransaction.add(R.id.fragment_container, settingsFragment,"settings_frag");
                            fragmentTransaction.commit();
                        }
                        return false;
                    }
                });

    }

    private void setAppBarVisibility(Boolean isHidden)
    {
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
        if(isHidden) {
            appBar.setVisibility(View.INVISIBLE);
        }
        else{
            appBar.setVisibility(View.VISIBLE);
        }
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
            abTitle =  (TextView)findViewById(R.id.abTitle);
            abTitle.setText(R.string.empty);

            if(position == 0)
            {
                newestFrag = ArticleListFragment.newInstance(position);
                return newestFrag;
            }
            else if (position == 1)
            {
                popularFrag = ArticleListFragment.newInstance(position);
                return popularFrag;
            }
            else
            {
                bookmarksFrag = ArticleListFragment.newInstance(position);
                return bookmarksFrag;
            }
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
