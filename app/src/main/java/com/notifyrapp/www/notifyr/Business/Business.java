package com.notifyrapp.www.notifyr.Business;

import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Data.WebApi;
import com.notifyrapp.www.notifyr.Data.Repository;
import java.util.List;

/**
 * Created by K on 11/4/2016.
 */


public class Business {

    /* Data comes from the Notifyr Web Api */
    WebApi webApi = new WebApi();

    /* Data comes from local SQL Lite */
    Repository repo = new Repository();

    //region Article

    /* Get All Articles for All Items */
    public List<Article> GetArticles(int skip, int take,String sortBy,int itemTypeId) {
        return webApi.GetArticles(skip,take,sortBy,itemTypeId);
    }

    //endregion

    //region Item

    //endregion
}
