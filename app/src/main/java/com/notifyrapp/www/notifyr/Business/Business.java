package com.notifyrapp.www.notifyr.Business;

import android.content.Context;

import com.notifyrapp.www.notifyr.Data.RepositoryBuilder;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Data.WebApi;
import com.notifyrapp.www.notifyr.Data.Repository;

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



    //region Article

    /* Get All Articles for All Items */
    public List<Article> GetArticles(int skip, int take,String sortBy,int itemTypeId) {
        return webApi.GetArticles(skip,take,sortBy,itemTypeId);
    }

    //endregion

    //region Item

    //endregion

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

    public void getUserItems(CallbackInterface callback)
    {
        new WebApi(context).getUserItems(callback);
    }

    //endregion

    //region Articles
    public void getItemArticles(long itemId,int skip,int take,String sortBy, CallbackInterface callback){
        new WebApi(context).getItemArticles(itemId,skip,take,sortBy,callback);
    }
    //endregion

    //region User Accounts
    public void registerAccount(String userName, String password, CallbackInterface callback){
        new WebApi(context).registerUserProfile(userName,password,callback);
    }


    public void updateToken(Runnable callback)
    {
     //   new WebApi(context).getAccessToken(callback);
    }

    public void updateToken(CallbackInterface callback)
    {
        new WebApi(context).getAccessToken(callback);
    }

    //endregion



}
