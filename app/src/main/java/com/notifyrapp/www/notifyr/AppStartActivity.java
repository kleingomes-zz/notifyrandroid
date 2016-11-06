package com.notifyrapp.www.notifyr;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Model.UserProfile;

import static java.lang.Thread.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AppStartActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        Business biz = new Business(this);
        String userId = "";

        /* CHECK IF USER EXISTS  */
        userId = PreferenceManager.getDefaultSharedPreferences(this).getString("userId", "");
        if(userId.equals(""))
        {
            /* CREATE ACCOUNT */
            biz.RegisterAccount("","",new Runnable()
            {
                @Override
                public void run()
                {
                    // Running callback
                }
            });
            Log.d("ACCOUNT_CHECK","No Account Found" + userId);
        }
        else
        {
            /* ACCOUNT EXISTS */
            Log.d("ACCOUNT_CHECK","Account Exists" + userId);
        }

        /* REGISTER FOR REMOTE NOTIFICATIONS */

        if(xTask.getStatus().equals(AsyncTask.Status.PENDING)) xTask.execute(this);

    }

    private AsyncTask<Context, Void, String> xTask = new AsyncTask<Context, Void, String>() {
        @Override
        protected String doInBackground(Context... params) {
            try {
                /******* CHECK/CREATE LOCAL DB HERE *******/
                Boolean localDbExists = false;

                if(localDbExists) {

                    Context context = params[0];
                    Business business = new Business(context);

                    business.CreateNotifyrDatabase("91315557-b9fa-4884-8ec4-cd372065c456");

                    // Use it
                    SharedPreferences prefs =
                            context.getSharedPreferences("UserSettings",
                                    Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("userId", "51545423");
                    editor.commit();
                }
                else {
                    // Create a new account and contact the database
                }
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(AppStartActivity.this, ArticleActivity.class));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get sharedPreferences here
        }

        @Override
        protected void onPostExecute(String s) {
            //do something as the execution completed, you can launch your real activity.
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    protected void RegsiterGuest()
    {

    }
    protected void RegisterDevice()
    {

    }

}
