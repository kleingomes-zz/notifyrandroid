package com.notifyrapp.www.notifyr.Business;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.notifyrapp.www.notifyr.Data.RepositoryBuilder;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Data.WebApi;
import com.notifyrapp.www.notifyr.Data.Repository;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.Model.UserSetting;

import java.util.List;


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

    public void getUserItemsFromLocal(CallbackInterface callback)
    {
        new WebApi(context).getUserItems(callback);
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
    public List<Article> GetArticles(int skip, int take,String sortBy,int itemTypeId) {
        return webApi.GetArticles(skip,take,sortBy,itemTypeId);
    }

    public void getItemArticles(long itemId,int skip,int take,String sortBy, CallbackInterface callback){
        new WebApi(context).getItemArticles(itemId,skip,take,sortBy,callback);
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

    public boolean checkIfDatabaseExists() {
        return new Repository(context).checkIfDatabaseExists();
    }

    public void syncUserSettingWithServer(UserSetting userSetting){
        //new WebApi(context).registerUserProfile(userName,password,callback);
    }

    //endregion



}
