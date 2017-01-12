package com.notifyrapp.www.notifyr;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.notifyrapp.www.notifyr.Business.Business;
import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Business.CacheManager;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.Model.UserSetting;

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
        getSupportActionBar().hide();

        userId = PreferenceManager.getDefaultSharedPreferences(this).getString("userid", "");
        // TODO: Remove this line (Hardcoded Klein account)
        //PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("userid", "6e43f43b-b86d-4eca-ae98-78938fa239af").commit();
        //PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("userid", "77dbbcbf-ef0d-42b6-91c8-5d830b6b004b").commit();

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
                Toast.makeText(ctx,"Unable To Connect To Server!", Toast.LENGTH_SHORT).show();
            }
            Log.d("ACCOUNT_CHECK","Account Created: " + userId);
        }
        else
        {
            // TODO: remove this
            //this.deleteDatabase("NotifyrLocal.db");
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

                    /* SYNC USER ITEMS AND SETTINGS WITH SERVER */
                    new Business(ctx).syncUserItems();
                   /* business.getUserItemsFromServer(new CallbackInterface()
                    {
                        @Override
                        public void onCompleted(Object data) {
                            // Running callback
                            Log.d("CALLBACK_CHECK","SAVED ITEMS TO LOCAL STORE");
                            List<Item> items = (List<Item>) data;
                            for (Item currentItem: items) {
                                business.saveUserItemLocal(currentItem);
                            }
                        }
                    });*/
                    // TODO: remove this
                    PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("LastUpdateUserNotifiedArticles", "").commit();
                    String lastUpdate = PreferenceManager.getDefaultSharedPreferences(ctx).getString("LastUpdateUserNotifiedArticles", "");
                    business.getUserNotificationsFromServer(lastUpdate,new CallbackInterface()
                    {
                        @Override
                        public void onCompleted(Object data) {
                            List<Article> articles = (List<Article>) data;
                            business.saveUserNotificationLocalAsync(articles);
                        }
                    });

                    startActivity(new Intent(AppStartActivity.this, MainActivity.class));
                }
            });
        }

        /* HOUSE KEEPING  */
        // MAKE SURE CACHE IS CLEAR
        //CacheManager.clearCacheAsync(ctx);

        /* BACKGROUND SERVICE **/
        // START THE BACKGROUND SERVICE TO GET ARTICLES
        // use this to start and trigger a service
        // Intent i = new Intent(ctx, BackgroundService.class);
        // ctx.startService(i);


        /* REGISTER FOR REMOTE NOTIFICATIONS */
        Thread registerNotificationsThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    InstanceID instanceID = InstanceID.getInstance(ctx);

                    final String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    new  Business(ctx).registerDevice(token, new CallbackInterface() {
                        @Override
                        public void onCompleted(Object data) {
                            PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("deviceToken", token).commit();
                            Log.d("DEVICE_REGISTER_TOKEN", "Registered Device with server" );
                        }
                    });

                    Log.d("DEVICE_REGISTER_TOKEN", "GCM Registration Token: " + token);

                }catch (Exception e) {
                    Log.d("DEVICE_REGISTER_TOKEN", "Failed to complete token refresh", e);
                }

            }
        });

        registerNotificationsThread.start();
    }
}
