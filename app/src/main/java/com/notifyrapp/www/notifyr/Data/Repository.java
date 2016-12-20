package com.notifyrapp.www.notifyr.Data;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.UserProfile;
import com.notifyrapp.www.notifyr.Model.UserSetting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class Repository {

    private Context context;
    private String dbName = "NotifyrLocal.db";
    private String userId;
    private SQLiteDatabase notifyrDB;

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

        String TableName = "Article";
        String Data="";
        String PrintToConsole="";
        ArrayList<Article> articles = new ArrayList<Article>();
        this.notifyrDB = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        try {

            Cursor c =  this.notifyrDB.rawQuery("SELECT * FROM " + TableName , null);

            int col_userId = c.getColumnIndex("ArticleId");
            int col_email = c.getColumnIndex("Email");
            int col_accountType = c.getColumnIndex("AccountType");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    String Id = c.getString(col_userId);
                    String Name = c.getString(col_email);
                    PrintToConsole = PrintToConsole + Id +"/"+Name+"\n";
                    Log.e("CreateUserProfileTable", PrintToConsole);
                }while(c.moveToNext());
            }

        }
        catch(Exception e) {
            Log.e("exception", e.getMessage());
        } finally {
            if (notifyrDB != null) {
                notifyrDB.close();
            }
        }



        return new ArrayList<Article>();
    }

    public Article GetArticleById(long id)
    {
        return new Article();
    }

    public UserSetting getUserSettings() {
        String TableName = "UserSetting";
        String Data="";
        String PrintToConsole="";
        UserSetting userSetting = new UserSetting();
        SQLiteDatabase db = null;

        try {

            File path = context.getDatabasePath(dbName);
            db = SQLiteDatabase.openDatabase(String.valueOf(path), null, 0);

            Cursor c =  db.rawQuery("SELECT * FROM " + TableName , null);

            int col_maxNotifications = c.getColumnIndex("MaxNotifications");
            int col_displayType = c.getColumnIndex("ArticleDisplayType");
            int col_readerMode = c.getColumnIndex("ArticleReaderMode");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    userSetting.setMaxNotificaitons(c.getInt(col_maxNotifications));
                    userSetting.setArticleDisplayType(c.getInt(col_displayType));
                    userSetting.setArticleReaderMode(c.getInt(col_readerMode) > 0);
                }while(c.moveToNext());
            }
        }
        catch(Exception e) {
            Log.e("exception", e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return userSetting;
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

    public boolean checkIfDatabaseExists() {

        SQLiteDatabase checkDB = null;
        try {
            File path = context.getDatabasePath(dbName);
            checkDB = SQLiteDatabase.openDatabase(String.valueOf(path), null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }

}
