package com.notifyrapp.www.notifyr.Business;

import android.content.Context;

import com.notifyrapp.www.notifyr.Data.RepositoryBuilder;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Data.WebApi;
import com.notifyrapp.www.notifyr.Data.Repository;
import java.util.List;

/**
 * Created by K on 11/4/2016.
 */


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
        repo = new Repository(context);
        repoBuilder = new RepositoryBuilder(context);
        webApi = new WebApi();
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
    public Boolean CheckIfDatabaseExists()
    {
        return repoBuilder.CheckIfDatabaseExists();
    }
    public Boolean CreateNotifyrDatabase(String userId)
    {
        return repoBuilder.CreateNotifyrDatabase(userId);
    }

    //endregion
}
