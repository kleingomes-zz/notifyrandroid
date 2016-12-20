package com.notifyrapp.www.notifyr.Data;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.notifyrapp.www.notifyr.Model.UserProfile;

import static android.content.Context.MODE_PRIVATE;


public class RepositoryBuilder {

    private Context context;
    private String dbName = "NotifyrLocal.db";
    private String userId;
    private SQLiteDatabase notifyrDB;

    /* Constructor */
    public RepositoryBuilder(Context context,String userId)
    {
        this.context = context;
        this.userId = userId;
    }

    //region  Functions

    public Boolean createNotifyrDatabase()
    {

        this.notifyrDB= null;
        String TableName = "UserProfile";
        String Data="";

        try {
            /* Create Database */
            this.context.deleteDatabase(dbName);
            this.notifyrDB = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            /* Create Tables */
            createUserProfileTable();
            createArticleTable();
            createItemTable();
            createUserSettingsTable();
        }
        catch(Exception e) {
            Log.e("exception", e.getMessage());
            return false;
        } finally {
            if (notifyrDB != null) {
                notifyrDB.close();
            }
        }
        return true;
    }

    private void createUserProfileTable()
    {
        String TableName = "UserProfile";
        String PrintToConsole="";

        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName
                    + " (Id VARCHAR, Email VARCHAR,Password VARCHAR, AccountType VARCHAR);");

        /* Insert data to a Table*/
        this.notifyrDB.execSQL("INSERT INTO "
                    + TableName
                    + " (Id, Email,Password,AccountType)"
                    + " VALUES ('"+this.userId+"',null, null,null);");

        ///////////////////////// PRINT ROWS ///////////////////////////////
        /* Retrieve data from database */
        Cursor c =  this.notifyrDB.rawQuery("SELECT * FROM " + TableName , null);

        int col_userId = c.getColumnIndex("Id");
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
        ///////////////////////// END PRINT ROWS ///////////////////////////////
    }

    private void createArticleTable()
    {
        String TableName = "Article";

        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableName
                + "("
                + "ArticleId INTEGER PRIMARY KEY,"
                + "Source VARCHAR,"
                + "Score INTEGER,"
                + "Title VARCHAR,"
                + "Author VARCHAR,"
                + "Description VARCHAR,"
                + "URL VARCHAR,"
                + "IURL VARCHAR,"
                + "ArticleNotifiedDate DATETIME,"
                + "PublishDate DATETIME,"
                + "IsFavourite BIT,"
                + "ShortLinkURL VARCHAR,"
                + "RelatedInterests VARCHAR,"
                + "TimeAgo VARCHAR,"
                + "NotifiedTimeAgo VARCHAR,"
                + "RelatedInterestsURL VARCHAR"
                + ");");
    }

    private void createItemTable()
    {
        String TableName = "Item";

        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableName
                + "("
                + "ItemId INTEGER PRIMARY KEY,"
                + "Name VARCHAR,"
                + "IUrl VARCHAR,"
                + "ItemTypeId INTEGER,"
                + "ItemTypeName VARCHAR"
                + ");");
    }

    private void createUserSettingsTable()
    {
        String TableName = "UserSetting";
        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableName
                + " (MaxNotifications INTEGER, ArticleDisplayType INTEGER,ArticleReaderMode INTEGER);");
        this.notifyrDB.execSQL("INSERT INTO "
                + TableName
                + " (MaxNotifications, ArticleDisplayType,ArticleReaderMode)"
                + " VALUES (5,3,0);");
    }


    //endregion

    //region test code
    private void printTable(String tableName)
    {
        Log.e("Printing Table:", tableName);

        // Print Code Here

    }
    //endregion
}
