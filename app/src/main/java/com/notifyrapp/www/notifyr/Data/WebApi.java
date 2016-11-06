package com.notifyrapp.www.notifyr.Data;

import android.content.Context;
import android.preference.PreferenceManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.UserProfile;

public class WebApi {

    /* Private Fields */
    private String apiBaseUrl = "http://www.notifyr.ca/service/api/";
    private String apiBaseUrlDev = "http://www.notifyr.ca/dev/service/api/";
    private Context context;

    /* Constructor */
    public WebApi(Context context)
    {
        this.context = context;
    }

    //region User Profile
    public UserProfile RegisterUserProfile(String userName, String password, Runnable callback)
    {


        /* Callback should only run on success */
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("userId", "test").commit();
        callback.run();
        return new UserProfile();
    }
    //endregion

    //region Articles
    public List<Article> GetArticles(int skip, int take,String sortBy, int itemTypeId )
    {
     //   Map<String,Object> map = new ObjectMapper().readValue(new URL("http://dot.com/api/?customerId=1234").openStream(), Map.class);
        List<Article> articles = new ArrayList<Article>();
        Article a = new Article();
        a.setId(2321);
        a.setTitle("Apple Releases new iPhone");
        a.setSource("CNN");
        articles.add(a);
        return articles;
    }
    //endregion
}
