package com.notifyrapp.www.notifyr.Business;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.notifyrapp.www.notifyr.AppStartActivity;
import com.notifyrapp.www.notifyr.Data.RepositoryBuilder;
import com.notifyrapp.www.notifyr.MainActivity;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Data.WebApi;
import com.notifyrapp.www.notifyr.Data.Repository;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.Model.UserSetting;

import net.danlew.android.joda.JodaTimeAndroid;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class Business {

    /* The View Context */
    Context context;

    /* Data comes from the Notifyr Web Api */
    WebApi webApi;

    /* Data comes from local SQL Lite */
    Repository repo;

    /* Builds Databases and Tables */
    RepositoryBuilder repoBuilder;

    public Business(Context context)
    {
        JodaTimeAndroid.init(context);
        this.context = context;
    }


    //region Data Access
    public Boolean createNotifyrDatabase(String userId)
    {
        return new RepositoryBuilder(context,userId).createNotifyrDatabase();
    }
    //endregion

    //region Items
    public void getAllItems(CallbackInterface callback)
    {
        new WebApi(context).getAllItems(callback);
    }

    public void getPopularItems(int skip,int take,CallbackInterface callback)
    {
        new WebApi(context).getPopularItems(skip,take,callback);
    }

    public List<Item> getUserItemsFromLocal()
    {
        return new Repository(context).getUserItems();
    }

    public void getUserItemsFromServer(CallbackInterface callback)
    {
        new WebApi(context).getUserItems(callback);
    }

    public Boolean saveUserItemLocal(Item userItem)
    {
        return new Repository(context).saveUserItemLocal(userItem);
    }
    //endregion

    //region Articles
    public List<Article> getArticles(int skip, int take,String sortBy,int itemTypeId) {
        return webApi.GetArticles(skip,take,sortBy,itemTypeId);
    }

    public void getItemArticles(long itemId,int skip,int take,String sortBy, CallbackInterface callback){
        new WebApi(context).getItemArticles(itemId,skip,take,sortBy,callback);
    }

    public void  getUserArticlesFromServer(int skip, int take, String sortBy, int itemTypeId, final CallbackInterface callback)    {
        new WebApi(context).getUserArticles(skip,take,sortBy,itemTypeId,new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {
                Business b = new Business(context);
                Log.d("CALLBACK_CHECK","GOT USER ARTICLES FROM SERVER... SAVING TO LOCAL NOW....");
                ArrayList<Article> articles = (ArrayList<Article>) data;
                for (Article currentArticle: articles) {
                    b.saveArticleLocal(currentArticle);
                }
                if (callback != null) {
                    callback.onCompleted(data);
                }
            }
        });
    }

    public List<Article>  getUserArticlesFromLocal(int skip, int take,String sortBy, int itemTypeId)    {
        return new Repository(context).getUserArticles(skip,take,sortBy,itemTypeId);
    }

    public Boolean saveArticleLocal(Article article)
    {
        return new Repository(context).saveArticle(article);
    }
    //endregion

    //region User Accounts
    public void registerAccount(String userName, String password, CallbackInterface callback){
        new WebApi(context).registerUserProfile(userName,password,callback);
    }

    public void updateToken(CallbackInterface callback)
    {
        new WebApi(context).getAccessToken(callback);
    }

    public UserSetting getUserSettings()
    {
        return new Repository(context).getUserSettings();
    }
    public boolean saveUserSettingsLocal(UserSetting userSetting)
    {
        return new Repository(context).saveUserSettings(userSetting);
    }
    public void saveUserSettingsServer(UserSetting userSetting,CallbackInterface callback)
    {
        new WebApi(context).saveUserSettings(userSetting,callback);
    }
    public boolean checkIfDatabaseExists()
    {
        return new Repository(context).checkIfDatabaseExists();
    }

    public void syncUserSettingWithServer(UserSetting userSetting){
        //new WebApi(context).registerUserProfile(userName,password,callback);
    }

    //endregion

    //region Notifications
    public void getUserNotifications(String fromDate, final CallbackInterface callback)
    {
        new WebApi(context).getUserNotifications(fromDate,new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {

                org.joda.time.DateTime now = new org.joda.time.DateTime(); // Default time zone.
                org.joda.time.DateTime zulu = now.toDateTime( org.joda.time.DateTimeZone.UTC );
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("LastUpdateUserNotifiedArticles", zulu.toString()).commit();
                Log.d("PRINTTIME:",zulu.toString());
                if (callback != null) {
                    callback.onCompleted(data);
                }
            }
        });
    }

    public Boolean saveUserNotificationLocal(Article article)
    {
        return new Repository(context).saveUserNotification(article);
    }

    public List<Article> getUserNotificationsLocal() /* TODO ADD PARAMS FOR PAGING) */
    {
        return new Repository(context).getUserNotifications();
    }
    //endregions



}
