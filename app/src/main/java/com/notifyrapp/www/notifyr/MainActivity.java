package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import android.widget.Button;
import android.widget.TextView;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.ItemType;
import com.notifyrapp.www.notifyr.UI.BottomNavigationViewHelper;

import java.util.List;

import static android.support.v4.view.PagerAdapter.POSITION_NONE;


public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
        MyItemsFragment.OnFragmentInteractionListener,
        WebViewFragment.OnFragmentInteractionListener,
        ArticleListFragment.OnFragmentInteractionListener,
        MyNotificationsFragment.OnFragmentInteractionListener,
        DiscoverFragment.OnFragmentInteractionListener
{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private FragmentStatePagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Context ctx;
    private SettingsFragment settingsFragment;
    private DiscoverFragment discoverFragment;
    private MyItemsFragment myItemsFragment;
    private MyNotificationsFragment myNotificationsFragment;
    private BottomNavigationView bottomNavigationView;
    public TextView abTitle;
    public Business.MenuTab currentMenu = Business.MenuTab.Home;
    public boolean isBookmarkDirty;
    public Button btnEditDone;
    public Button btnTrashCanDelete;
    private int currentMenuPage = 0;
    private boolean isFirstTime = false;
    private String notificationUrl;
    private boolean isIncomingNotification = false;
    private List<ItemType> oldCategories;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()) {
            // case android.R.id.context_menu:
            //      mMenuDialogFragment.show(fragmentManager, "ContextMenuDialogFragment");
            //     break;
            case android.R.id.home:
                FragmentManager fm = this.getSupportFragmentManager();
                if(currentMenu == Business.MenuTab.Notifications) {
                    fm.popBackStack("notificationlist_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    abTitle.setText(R.string.menu_tab_3);
                }
                else if (currentMenu == Business.MenuTab.Home){
                    fm.popBackStack("articlelist_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportActionBar().hide();
                    setAppBarVisibility(false);
                }
                else if (currentMenu == Business.MenuTab.Interests)
                {
                    fm.popBackStack("myitems_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    abTitle.setText(R.string.menu_tab_2);
                }
                else if (currentMenu == Business.MenuTab.Settings)
                {
                    fm.popBackStack("settings_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    abTitle.setText(R.string.menu_tab_4);
                }
                abTitle.setPadding(0,0,0,0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // INIT Fabric
        Fabric.with(this,  new Answers(),new Crashlytics());
        // Get INCOMING Notification DATA
        notificationUrl = getIntent().getStringExtra("articleUrl");
        isIncomingNotification = getIntent().getBooleanExtra("isIncomingNotification",false);
        // INIT
        this.ctx = this;
        setContentView(R.layout.activity_main);
        getSupportActionBar().setShowHideAnimationEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorNotifyrLightBlue)));
        getSupportActionBar().hide();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        abTitle = (TextView) findViewById(R.id.abTitle);
        abTitle.setPadding(0,0,0,0);
        btnEditDone = (Button) findViewById(R.id.btnEditDone);
        btnTrashCanDelete = (Button) findViewById(R.id.btnTrashCanDelete);


        // Check if it's the first time the user opens app to default discover page
        isFirstTime = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isFirstTime", false);
        // Now set it to false since the app has been opened
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean("isFirstTime",false).commit();

        if (isFirstTime) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            View view = bottomNavigationView.findViewById(R.id.action_discover);
            view.performClick();
            Fragment pos2 = getSupportFragmentManager().findFragmentByTag("discover_frag");
            if(pos2 != null) {  getSupportFragmentManager().beginTransaction().remove(pos2).commit(); }
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setShowHideAnimationEnabled(false);
            getSupportActionBar().hide();
            setAppBarVisibility(true);
            ViewPager viewPager = (ViewPager) findViewById(R.id.container);
            viewPager.setVisibility(View.GONE);
            btnEditDone.setVisibility(View.GONE);
            btnTrashCanDelete.setVisibility(View.GONE);
            currentMenu = Business.MenuTab.Discover;
            abTitle.setText(R.string.menu_tab_2);
            discoverFragment = discoverFragment == null ? new DiscoverFragment() : discoverFragment;
            fragmentTransaction.add(R.id.fragment_container, discoverFragment, "discover_frag");
            fragmentTransaction.commit();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if(isIncomingNotification)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar);
            appBar.setVisibility(View.INVISIBLE);
            Article article = new Article();
            article.setUrl(notificationUrl);
            WebViewFragment mWebViewFragment = new WebViewFragment().newInstance(article);
            fragmentTransaction.add(R.id.fragment_container, mWebViewFragment, "webview_frag");
            fragmentTransaction.addToBackStack("articlelist_frag");
            fragmentTransaction.commit();
        }


        mSectionsPagerAdapter = new AppBarAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.canScrollHorizontally(2);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // without this listener the tabs would still get updated when fragments are swiped, but ....  (read the next comment)
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // if(isBookmarkDirty && tab.getPosition() == 2 && bookmarksFrag != null) {
                //     bookmarksFrag.getArticles(0, 20, "");
                // }
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });



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
                        Fragment webFrag = getSupportFragmentManager().findFragmentByTag("webview_frag");
                        // Remove the fragment that is not null from the manager
                        if(pos0 != null && currentMenuPage != position) {
                            getSupportFragmentManager().beginTransaction().remove(pos0).commit();
                        }
                        if(pos1 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos1).commit(); }
                        if(pos2 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos2).commit(); }
                        if(pos3 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos3).commit(); }
                        if(pos4 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos4).commit(); }
                        int backStackCount = fragmentManager.getBackStackEntryCount();
                        for(int entry = 0; entry < backStackCount; entry++){
                            getSupportFragmentManager().popBackStack();
                        }

                        // SHOW/HIDE the app bar depending on which menu tab you're on
                        if(position == 0)
                        {
                            if(isFirstTime)
                            {
                                   clearFragments();
                            }
                            if(currentMenuPage != 0 || isFirstTime) {

                                if(pos1 != null ) getSupportFragmentManager().beginTransaction().remove(pos1).commit();
                                if(webFrag != null ) getSupportFragmentManager().beginTransaction().remove(webFrag).commit();

                                getSupportActionBar().setShowHideAnimationEnabled(false);
                                currentMenu = Business.MenuTab.Home;
                                getSupportActionBar().hide();
                                setAppBarVisibility(false);
                                abTitle.setText(R.string.empty);
                                // NEED TO REDRAW THE APP BAR (in case the user added categories)
                                // We only redraw if the item categories change since this is
                                // an expensive operation
                                if(new Business(ctx).hasItemCategoriesChanged(oldCategories)) {
                                    mSectionsPagerAdapter = new AppBarAdapter(getSupportFragmentManager());
                                    mViewPager.setAdapter(mSectionsPagerAdapter);
                                    mSectionsPagerAdapter.notifyDataSetChanged();
                                    mViewPager.destroyDrawingCache();
                                }
                                mViewPager.setVisibility(View.VISIBLE);
                                btnEditDone.setVisibility(View.INVISIBLE);
                                btnTrashCanDelete.setVisibility(View.INVISIBLE);
                            }

                        }
                        else
                        {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            getSupportActionBar().setShowHideAnimationEnabled(false);
                            if(position == 2) {
                                getSupportActionBar().hide();
                            } else {
                                getSupportActionBar().show();
                            }
                            setAppBarVisibility(true);
                            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            mViewPager.setVisibility(View.GONE);
                        }

                        // LOAD THE NEW FRAGMENT (BUT NOT IF IT'S ALREADY LOADED!)
                        switch(position)
                        {
                            case 1 :
                                if(currentMenuPage != 1) {
                                    abTitle.setText(R.string.menu_tab_1);
                                    btnEditDone.setVisibility(View.VISIBLE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    currentMenu = Business.MenuTab.Interests;
                                    myItemsFragment = myItemsFragment == null ? new MyItemsFragment() : myItemsFragment;
                                    if(!myItemsFragment.isAdded()) {
                                        clearFragments();
                                        fragmentTransaction.add(R.id.fragment_container, myItemsFragment, "myitems_frag");
                                        fragmentTransaction.commit();
                                    }
                                }
                                break;
                            case 2 :
                                if(currentMenuPage != 2) {
                                    btnEditDone.setVisibility(View.GONE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    currentMenu = Business.MenuTab.Discover;
                                    abTitle.setText(R.string.menu_tab_2);
                                    discoverFragment = discoverFragment == null ? new DiscoverFragment() : discoverFragment;
                                    if(!discoverFragment.isAdded()) {
                                        clearFragments();
                                        fragmentTransaction.add(R.id.fragment_container, discoverFragment, "discover_frag");
                                        fragmentTransaction.commit();
                                    }
                                }
                                break;
                            case 3 :
                                if(currentMenuPage != 3) {
                                    btnEditDone.setVisibility(View.GONE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    abTitle.setText(R.string.menu_tab_3);
                                    currentMenu = Business.MenuTab.Notifications;
                                    myNotificationsFragment = myNotificationsFragment == null ? new MyNotificationsFragment() : myNotificationsFragment;
                                    if(!myNotificationsFragment.isAdded()) {
                                        clearFragments();
                                        fragmentTransaction.add(R.id.fragment_container, myNotificationsFragment, "notifications_frag");
                                        fragmentTransaction.commit();
                                    }
                                }
                                break;
                            case 4 :
                                if(currentMenuPage != 4) {
                                    btnEditDone.setVisibility(View.GONE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    currentMenu = Business.MenuTab.Settings;
                                    abTitle.setText(R.string.menu_tab_4);
                                    settingsFragment = settingsFragment == null ? new SettingsFragment() : settingsFragment;
                                    if(!settingsFragment.isAdded()) {
                                        clearFragments();
                                        fragmentTransaction.add(R.id.fragment_container, settingsFragment, "settings_frag");
                                        fragmentTransaction.commit();
                                    }
                                }
                                break;
                        }
                        currentMenuPage = position;
                        abTitle.setPadding(0,0,0,0);
                        return false;
                    }
                });


    }

    private void clearFragments()
    {
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        if(frags != null && frags.size() > 0) {
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
            if (pos0 != null) {
                getSupportFragmentManager().beginTransaction().remove(pos0).commit();
            }
            if (pos1 != null) {
                getSupportFragmentManager().beginTransaction().remove(pos1).commit();
            }
            if (pos2 != null) {
                getSupportFragmentManager().beginTransaction().remove(pos2).commit();
            }
            if (pos3 != null) {
                getSupportFragmentManager().beginTransaction().remove(pos3).commit();
            }
            if (pos4 != null) {
                getSupportFragmentManager().beginTransaction().remove(pos4).commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // FRAGMENT MANAGER
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        // DROP THE CURRENT FRAGMENT
        Fragment fragment = null;

        // Check which fragment is active, inactive ones return null
        //Fragment pos0 = getSupportFragmentManager().findFragmentByTag("home_frag");
        Fragment pos1 = getSupportFragmentManager().findFragmentByTag("myitems_frag");

        Fragment webFrag = getSupportFragmentManager().findFragmentByTag("webview_frag");

        // Remove the fragment that is not null from the manager
        //if(pos0 != null ) {  getSupportFragmentManager().beginTransaction().remove(pos0).commit(); }
        if(pos1 != null && currentMenu != Business.MenuTab.Interests) {  getSupportFragmentManager().beginTransaction().remove(pos1).commit(); }

        if(webFrag != null ) {  getSupportFragmentManager().beginTransaction().remove(webFrag).commit(); }

        if(pos1 != null && webFrag != null)
        {
            abTitle.setPadding(0,0,0,0);
            fm.popBackStack("myitems_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            abTitle.setText(R.string.menu_tab_1);
        }
        else if(pos1 != null)
        {
            abTitle.setPadding(0,0,0,0);
            fm.popBackStack("myitems_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            abTitle.setText(R.string.menu_tab_1);
        }
        else if (webFrag != null  ) {
            // SHOW/HIDE the app bar depending on which menu tab you're on
            abTitle.setPadding(0,0,0,0);
            if (currentMenu == Business.MenuTab.Notifications) {
                fm.popBackStack("notificationlist_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                abTitle.setText(R.string.menu_tab_3);
            } else if (currentMenu == Business.MenuTab.Home) {
                fm.popBackStack("articlelist_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportActionBar().hide();
                setAppBarVisibility(false);
            } else if (currentMenu == Business.MenuTab.Interests) {
                fm.popBackStack("myitems_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                abTitle.setText(R.string.menu_tab_1);
            } else if (currentMenu == Business.MenuTab.Settings) {
                fm.popBackStack("settings_frag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                abTitle.setText(R.string.menu_tab_4);
            }
        }

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
    public class AppBarAdapter extends FragmentStatePagerAdapter {

        private Business business;
        private int categoryCount = 1;
        List<ItemType> itemCategories;

        public AppBarAdapter(FragmentManager fm) {
            super(fm);
            business = new Business(ctx);
            itemCategories = business.getItemCategories();
            oldCategories = itemCategories;
            categoryCount = itemCategories != null && itemCategories.size() > 0 ? itemCategories.size() : 1;

        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            abTitle =  (TextView)findViewById(R.id.abTitle);
            abTitle.setText(R.string.empty);
            int itemTypeId = -1;
            if(position ==0) {
                itemTypeId = -1;
            } else {
                itemTypeId = itemCategories.get(position-1).getId();
            }

            return ArticleListFragment.newInstance(position,itemTypeId,"");
        }

        @Override
        public int getCount() {
            // Show All Categories PLUS the ALL category
            if (itemCategories != null && itemCategories.size() > 1) {
                return categoryCount+1;
            }
            else{
                return 1;
            }
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (itemCategories != null && itemCategories.size() > 1)
            {
                if(position ==0) {
                    return "All";
                }
                return itemCategories.get(position-1).getItemTypeName();
            }
            else
            {
                return "All";
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheManager.clearCache(ctx);
        CacheManager.clearImageMemoryCache();
    }

  /*  @Override
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
    }*/
}