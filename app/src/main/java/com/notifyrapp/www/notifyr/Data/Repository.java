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
    private String userId;

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
        String tableName = "UserProfile";
        SQLiteDatabase notifyrDB;
        notifyrDB = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        Boolean accountExists = CheckIfTableHasRows(tableName);

        if(accountExists)
        {
            // Clear the Account Row
            notifyrDB.execSQL("DELETE FROM "+tableName);
        }
        // Insert the profile
        notifyrDB.execSQL("INSERT INTO "
                    + tableName
                    + " (Id, Email,AccountType)"
                    + " VALUES ('"+userProfile.UserId+"', null,null);");

        if (notifyrDB != null) {
            notifyrDB.close();
        }

        return true;
    }

    public Boolean SaveArticle(Article article)
    {
        return true;
    }
    //endregion

    //region Helpers
    private boolean CheckIsDataAlreadyInDBorNot(String TableName,
                                                      String dbfield, String fieldValue) {
        SQLiteDatabase sqldb = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + fieldValue;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private boolean CheckIfTableHasRows(String TableName) {
        SQLiteDatabase sqldb = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        String Query = "Select * from " + TableName;
        Cursor cursor = sqldb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }





}
