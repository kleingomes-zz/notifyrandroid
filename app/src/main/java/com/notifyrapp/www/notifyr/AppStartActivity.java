package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;

import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;

import static java.lang.Thread.sleep;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */



public class AppStartActivity extends AppCompatActivity{

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_start);
        Business biz = new Business(this);
        String userId = "";
        ctx = this;
        /* CHECK IF USER EXISTS  */
    //  PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("userid", "").commit();
        userId = PreferenceManager.getDefaultSharedPreferences(this).getString("userid", "");
        if(userId.equals(""))
        {
            Log.d("ACCOUNT_CHECK","No Account Found... Contacting Server to create one " + userId);
            //* CREATE ACCOUNT *//*
            try {
                biz.registerAccount("","",new CallbackInterface()
                {
                    @Override
                    public void onCompleted(Object data) {
                        // Running callback
                        Log.d("CALLBACK_CHECK","REACHED CALL BACK");
                        String userId = (String) data;
                        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("userid", userId).commit();
                        //******* CREATE LOCAL DB HERE *******//*
                        Business business = new Business(ctx);
                        //business.createNotifyrDatabase(user);
                        business.updateToken(new CallbackInterface() {
                            @Override
                            public void onCompleted(Object data) {
                                startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                            }
                        });

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("ACCOUNT_CHECK","Account Created: " + userId);
        }
        else
        {
            //* ACCOUNT EXISTS *//*
            Log.d("ACCOUNT_CHECK","Account Exists... Logging In As: " + userId);
            new Business(ctx).updateToken(new CallbackInterface() {
                @Override
                public void onCompleted(Object data) {
                     startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                }
            });
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
            business.createNotifyrDatabase(userId);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //get sharedPreferences here
        }

        @Override
        protected void onPostExecute(String s) {
           // startActivity(new Intent(AppStartActivity.this, ArticleActivity.class));
            //do something as the execution completed, you can launch your real activity.
        }
    };


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
