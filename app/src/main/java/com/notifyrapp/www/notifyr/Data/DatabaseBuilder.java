package com.notifyrapp.www.notifyr.Data;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.notifyrapp.www.notifyr.Model.UserProfile;

import static android.content.Context.MODE_PRIVATE;


public class DatabaseBuilder {

    private Context context;
    private String dbName = "NotifyrLocal.db";
    private String userId;
    private SQLiteDatabase notifyrDB;

    /* Constructor */
    public DatabaseBuilder(Context context, String userId)
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
            this.notifyrDB = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            /* Create Tables */
            createUserProfileTable();
            createArticleTable();
            createUserNotificationTable();
            createArticleBookmarkTable();
            createItemTable();
            createUserItemTable();
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
    }

    private void createArticleTable()
    {
        String TableName = "Article";
        this.notifyrDB.execSQL(getCreateArticleTableString(TableName));
    }

    private void createUserNotificationTable()
    {
        String TableName = "UserNotification";
        this.notifyrDB.execSQL(getCreateArticleTableString(TableName));
    }

    private void createArticleBookmarkTable()
    {
        String TableName = "ArticleBookmark";
        this.notifyrDB.execSQL(getCreateArticleTableString(TableName));
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

    private void createUserItemTable()
    {
        String TableName = "UserItem";

        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableName
                + "("
                + "ItemId INTEGER PRIMARY KEY,"
                + "Name VARCHAR,"
                + "IUrl VARCHAR,"
                + "ItemTypeId INTEGER,"
                + "ItemTypeName VARCHAR,"
                + "UserItemId INTEGER,"
                + "Priority INTEGER"
                + ");");
    }

    private void createUserSettingsTable()
    {
        String TableName = "UserSetting";
     //   this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
     //           + TableName
     //           + " (MaxNotifications INTEGER, ArticleDisplayType INTEGER,ArticleReaderMode INTEGER);");

        //this.notifyrDB.execSQL("INSERT INTO "
        //        + TableName
        //        + " (MaxNotifications, ArticleDisplayType,ArticleReaderMode)"
        //        + " VALUES (5,2,0);");

        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableName
                + " (SettingName VARCHAR, SettingValue VARCHAR);");

        this.notifyrDB.execSQL("INSERT INTO "
                + TableName
                + " (SettingName, SettingValue)"
                + " VALUES ('MaxNotifications','5');");

        this.notifyrDB.execSQL("INSERT INTO "
                + TableName
                + " (SettingName, SettingValue)"
                + " VALUES ('ArticleDisplayType','2');");

        this.notifyrDB.execSQL("INSERT INTO "
                + TableName
                + " (SettingName, SettingValue)"
                + " VALUES ('ArticleReaderMode','0');");
    }

    public String getCreateArticleTableString(String tableName)
    {
        return "CREATE TABLE IF NOT EXISTS "
                + tableName
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
                + "RelatedInterestsURL VARCHAR,"
                + "PublishDateUnix INTEGER,"
                + "ArticleNotifiedDateUnix INTEGER"
                + ");";
    }

    //endregion

    //region test code
    private void printTable(String tableName)
    {
        Log.e("Printing Table:", tableName);
        String PrintToConsole = "";
        // Print Code Here
        ///////////////////////// PRINT ROWS ///////////////////////////////
        /* Retrieve data from database */
        Cursor c =  this.notifyrDB.rawQuery("SELECT * FROM " + tableName , null);

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
    //endregion
}
