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
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.Model.UserSetting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


        userId = PreferenceManager.getDefaultSharedPreferences(this).getString("userid", "");
        // TODO: Remove this line (Hardcoded Klein account)
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("userid", "6e43f43b-b86d-4eca-ae98-78938fa239af").commit();
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
                        business.createNotifyrDatabase(userId);
                        UserSetting settings = business.getUserSettings();
                        Log.d("SETTINGS", String.valueOf(settings.getMaxNotificaitons()));
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
                Log.d("ACCOUNT_CHECK","Account Failed: " + e.getMessage());
            }
            Log.d("ACCOUNT_CHECK","Account Created: " + userId);
        }
        else
        {
            // TODO: remove this
            this.deleteDatabase("NotifyrLocal.db");
            final Business business = new Business(ctx);
            if(!business.checkIfDatabaseExists()) {
                business.createNotifyrDatabase(userId);
                Log.d("DATABASE_CHECK","Account Exists... But Not Database: " + userId);
            }
            else {
                Log.d("DATABASE_CHECK", "Database Exists...: " + userId);
            }

            //* ACCOUNT EXISTS *//*
            Log.d("ACCOUNT_CHECK","Account Exists... Logging In As: " + userId);
            UserSetting settings = business.getUserSettings();

            business.updateToken(new CallbackInterface() {
                @Override
                public void onCompleted(Object data) {
                    business.getUserItemsFromServer(new CallbackInterface()
                    {
                        @Override
                        public void onCompleted(Object data) {
                            // Running callback
                            Log.d("CALLBACK_CHECK","SAVED ITEMS TO LOCAL STORE");
                            ArrayList<Item> items = (ArrayList<Item>) data;
                            for (Item currentItem: items) {
                                business.saveUserItemLocal(currentItem);
                            }
                        }
                    });
                    // TODO: remove this
                    PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("LastUpdateUserNotifiedArticles", "").commit();
                    String lastUpdate = PreferenceManager.getDefaultSharedPreferences(ctx).getString("LastUpdateUserNotifiedArticles", "");
                    business.getUserNotifications(lastUpdate,new CallbackInterface()
                    {
                        @Override
                        public void onCompleted(Object data) {
                            // Running callback
                            Log.d("CALLBACK_CHECK","SAVED NOTIFIED ARTICLES TO LOCAL STORE");
                            ArrayList<Article> articles = (ArrayList<Article>) data;
                            for (Article currentArticle: articles) {
                                business.saveUserNotificationLocal(currentArticle);
                            }
                        }
                    });

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
