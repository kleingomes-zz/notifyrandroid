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
    public RepositoryBuilder(Context context)
    {
        this.context = context;
    }

    //region  Functions

    public Boolean checkIfDatabaseExists()
    {
        return false;
    }

    public Boolean createNotifyrDatabase(String userId)
    {
        this.userId = userId;
        this.notifyrDB= null;
        String TableName = "UserProfile";
        String Data="";

        try {
            /* Create Database */
            this.context.deleteDatabase(dbName);
            this.notifyrDB = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

            /* Create Tables */
            CreateUserProfileTable();
            CreateArticleTable();
            CreateItemTable();
            CreateUserSettingsTable();
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

    private void CreateUserProfileTable()
    {
        String TableName = "UserProfile";
        String PrintToConsole="";

        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName
                    + " (Id VARCHAR, Email VARCHAR, AccountType VARCHAR);");

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

    private void CreateArticleTable()
    {

    }

    private void CreateItemTable()
    {

    }

    private void CreateUserSettingsTable()
    {
        String TableName = "UserSetting";
        this.notifyrDB.execSQL("CREATE TABLE IF NOT EXISTS "
                + TableName
                + " (Id VARCHAR, MaxNotifications INT, ArticleDisplayType INT,ArticleReaderMode BIT);");
    }


    //endregion

    //region test code

    //endregion
}
