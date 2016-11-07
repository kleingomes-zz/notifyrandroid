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

import org.json.JSONException;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AppStartActivity extends Activity {

    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        Business biz = new Business(this);
        String userId = "";
        ctx = this;
        /* CHECK IF USER EXISTS  */
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("userid", "").commit();
        userId = PreferenceManager.getDefaultSharedPreferences(this).getString("userid", "");
        if(userId.equals(""))
        {
            Log.d("ACCOUNT_CHECK","No Account Found... Contacting Server to create one " + userId);
            /* CREATE ACCOUNT */
            try {
                biz.RegisterAccount("","",new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // Running callback
                        Log.d("CALLBACK_CHECK","REACHED CALL BACK");
                        /******* CREATE LOCAL DB HERE *******/
                        String user = PreferenceManager.getDefaultSharedPreferences(ctx).getString("userid", "");
                        Business business = new Business(ctx);
                        business.CreateNotifyrDatabase(user);
                        startActivity(new Intent(AppStartActivity.this, ArticleActivity.class));
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("ACCOUNT_CHECK","Account Created: " + userId);
        }
        else
        {
            /* ACCOUNT EXISTS */
            Log.d("ACCOUNT_CHECK","Account Exists... Logging In As: " + userId);
            startActivity(new Intent(AppStartActivity.this, ArticleActivity.class));
        }

        /* REGISTER FOR REMOTE NOTIFICATIONS */

    }

    private AsyncTask<Object, Void, String> createOrUpdateDatabase = new AsyncTask<Object, Void, String>() {

       //  if(createOrUpdateDatabase.getStatus().equals(AsyncTask.Status.PENDING)) {
            //createOrUpdateDatabase.execute(this,user);
        //        }

        @Override
        protected String doInBackground(Object... params) {

            /******* CREATE LOCAL DB HERE *******/
            Context context = (Context)params[0];
            String userId =  (String)params[1];
            Business business = new Business(context);
            business.CreateNotifyrDatabase(userId);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get sharedPreferences here
        }

        @Override
        protected void onPostExecute(String s) {
            startActivity(new Intent(AppStartActivity.this, ArticleActivity.class));
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
