package com.notifyrapp.www.notifyr.Data;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.UserProfile;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class Repository {

    private Context context;
    private String dbName = "NotifyrLocal.db";

    //region Get Data
    public Repository(Context context)
    {
        this.context = context;
    }

    public UserProfile GetUserProfile()
    {
        return new UserProfile();
    }

    public List<Article> GetArticles(int skip, int take,String sortBy)
    {
        return new ArrayList<Article>();
    }

    public Article GetArticleById(long id)
    {
        return new Article();
    }

    //endregion

    //region Save Data

    public Boolean SaveUserProfile(UserProfile userProfile)
    {
        return true;
    }

    public Boolean SaveArticle(Article article)
    {
        return true;
    }

    //endregion




}
