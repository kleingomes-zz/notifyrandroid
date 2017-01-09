package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
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
import com.notifyrapp.www.notifyr.Model.ItemType;
import com.notifyrapp.www.notifyr.UI.BottomNavigationViewHelper;

import java.util.List;


public class MainActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
        MyItemsFragment.OnFragmentInteractionListener,
        WebViewFragment.OnFragmentInteractionListener,
        ArticleListFragment.OnFragmentInteractionListener,
        MyNotificationsFragment.OnFragmentInteractionListener,
        DiscoverFragment.OnFragmentInteractionListener
{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Context ctx;
    private SettingsFragment settingsFragment;
    private DiscoverFragment discoverFragment;
    private MyItemsFragment myItemsFragment;
    private MyNotificationsFragment myNotificationsFragment;
    public TextView abTitle;
    public Business.MenuTab currentMenu = Business.MenuTab.Home;
    public boolean isBookmarkDirty;
    private Button btnEditDone;
    private Button btnTrashCanDelete;
    private int currentMenuPage = 0;

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

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // INIT
        this.ctx = this;
        setContentView(R.layout.activity_main);
        getSupportActionBar().setShowHideAnimationEnabled(false);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorNotifyrLightBlue)));
        getSupportActionBar().hide();

        Fabric.with(this, new Answers());
        Fabric.with(this, new Crashlytics());


        //String PACKAGE_NAME = getApplicationContext().getPackageName();

        abTitle =  (TextView)findViewById(R.id.abTitle);
        btnEditDone = (Button)findViewById(R.id.btnEditDone);
        btnTrashCanDelete = (Button)findViewById(R.id.btnTrashCanDelete);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
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
                        if(pos0 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos0).commit(); }
                        if(pos1 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos1).commit(); }
                        if(pos2 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos2).commit(); }
                        if(pos3 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos3).commit(); }
                        if(pos4 != null && currentMenuPage != position) {  getSupportFragmentManager().beginTransaction().remove(pos4).commit(); }

                        // SHOW/HIDE the app bar depending on which menu tab you're on
                        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
                        if(position == 0)
                        {
                            if(currentMenuPage != 0) {
                                getSupportActionBar().setShowHideAnimationEnabled(false);
                                currentMenu = Business.MenuTab.Home;
                                getSupportActionBar().hide();
                                setAppBarVisibility(false);
                                abTitle.setText(R.string.empty);
                                // NEED TO REDRAW THE APP BAR (in case the user added categories)
                                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                                mViewPager.setAdapter(mSectionsPagerAdapter);
                                viewPager.setVisibility(View.VISIBLE);
                                btnEditDone.setVisibility(View.GONE);
                                btnTrashCanDelete.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            getSupportActionBar().setShowHideAnimationEnabled(false);
                            getSupportActionBar().show();
                            setAppBarVisibility(true);
                            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            viewPager.setVisibility(View.GONE);
                        }

                        // LOAD THE NEW FRAGMENT (BUT NOT IF IT'S ALREADY LOADED!
                        switch(position)
                        {
                            case 1 :
                                if(currentMenuPage != 1) {
                                    abTitle.setText(R.string.menu_tab_1);
                                    btnEditDone.setVisibility(View.VISIBLE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    currentMenu = Business.MenuTab.Interests;
                                    myItemsFragment = myItemsFragment == null ? new MyItemsFragment() : myItemsFragment;
                                    fragmentTransaction.add(R.id.fragment_container, myItemsFragment, "myitems_frag");
                                    fragmentTransaction.commit();
                                }
                                    break;
                            case 2 :
                                if(currentMenuPage != 2) {
                                    btnEditDone.setVisibility(View.GONE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    currentMenu = Business.MenuTab.Discover;
                                    abTitle.setText(R.string.menu_tab_2);
                                    discoverFragment = discoverFragment == null ? new DiscoverFragment() : discoverFragment;
                                    fragmentTransaction.add(R.id.fragment_container, discoverFragment, "discover_frag");
                                    fragmentTransaction.commit();
                                }
                                break;
                            case 3 :
                                if(currentMenuPage != 3) {
                                    btnEditDone.setVisibility(View.GONE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    abTitle.setText(R.string.menu_tab_3);
                                    currentMenu = Business.MenuTab.Notifications;
                                    myNotificationsFragment = myNotificationsFragment == null ? new MyNotificationsFragment() : myNotificationsFragment;
                                    fragmentTransaction.add(R.id.fragment_container, myNotificationsFragment, "notifications_frag");
                                    fragmentTransaction.commit();
                                }
                                break;
                            case 4 :
                                if(currentMenuPage != 4) {
                                    btnEditDone.setVisibility(View.GONE);
                                    btnTrashCanDelete.setVisibility(View.GONE);
                                    currentMenu = Business.MenuTab.Settings;
                                    abTitle.setText(R.string.menu_tab_4);
                                    settingsFragment = settingsFragment == null ? new SettingsFragment() : settingsFragment;
                                    fragmentTransaction.add(R.id.fragment_container, settingsFragment, "settings_frag");
                                    fragmentTransaction.commit();
                                }
                                break;
                        }
                        currentMenuPage = position;
                        return false;
                    }
                });

    }

    @Override
    public void onBackPressed() {
        //final Myfragment fragment = (Myfragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);

        //if (fragment.allowBackPressed()) { // and then you define a method allowBackPressed with the logic to allow back pressed or not
       //     super.onBackPressed();
       // }
        // FRAGMENT MANAGER
        /*FragmentManager fragmentManager = getSupportFragmentManager();
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
        if(pos0 != null ) {  getSupportFragmentManager().beginTransaction().remove(pos0).commit(); }
        if(pos1 != null ) {  getSupportFragmentManager().beginTransaction().remove(pos1).commit(); }
        if(pos2 != null ) {  getSupportFragmentManager().beginTransaction().remove(pos2).commit(); }
        if(pos3 != null ) {  getSupportFragmentManager().beginTransaction().remove(pos3).commit(); }
        if(pos4 != null ) {  getSupportFragmentManager().beginTransaction().remove(pos4).commit(); }

        // SHOW/HIDE the app bar depending on which menu tab you're on
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);

            if(currentMenuPage != 0) {
                getSupportActionBar().setShowHideAnimationEnabled(false);
                currentMenu = Business.MenuTab.Home;
                getSupportActionBar().hide();
                setAppBarVisibility(false);
                abTitle.setText(R.string.empty);
                // NEED TO REDRAW THE APP BAR (in case the user added categories)
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager.setAdapter(mSectionsPagerAdapter);
                viewPager.setVisibility(View.VISIBLE);
                btnEditDone.setVisibility(View.GONE);
                btnTrashCanDelete.setVisibility(View.GONE);
            }
*/
    }

    private void commitFragment(String fragmentName,String title,Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        abTitle.setText(title);
        settingsFragment = new SettingsFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment,fragmentName);
        fragmentTransaction.commit();
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

        private Business business;
        private int categoryCount = 1;
        List<ItemType> itemCategories;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            business = new Business(ctx);
            itemCategories = business.getItemCategories();
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
