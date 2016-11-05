package com.notifyrapp.www.notifyr.Data;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.notifyrapp.www.notifyr.Model.UserProfile;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by K on 11/5/2016.
 */

public class Repository {

    private String dbName = "NotifyrLocal";

    //region Get Data

    public UserProfile GetUserProfile()
    {
        return new UserProfile();
    }

    //endregion

    //region Save Data
    public Boolean SaveUserProfile()
    {
        return true;
    }
    //endregion



    //region Helper Functions

    public Boolean CheckIfDatabaseExists()
    {
        return false;
    }

    public Boolean OpenOrCreateDatabase()
    {
        SQLiteDatabase myDB= null;
        String TableName = "UserProfile";

        String Data="";

  /* Create a Database. */
        try {
            myDB = myDB.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

   /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + TableName
                    + " (Id VARCHAR, Email VARCHAR);");

   /* Insert data to a Table*/
            myDB.execSQL("INSERT INTO "
                    + TableName
                    + " (Field1, Field2)"
                    + " VALUES ('23123123213123', kleingomes@gmail.com);");

   /*retrieve data from database */
            Cursor c = myDB.rawQuery("SELECT * FROM " + TableName , null);

            int Column1 = c.getColumnIndex("Field1");
            int Column2 = c.getColumnIndex("Field2");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    String Name = c.getString(Column1);
                    int Age = c.getInt(Column2);
                    Data =Data +Name+"/"+Age+"\n";
                }while(c.moveToNext());
            }
        }
        catch(Exception e) {
        } finally {
            if (myDB != null)
                myDB.close();
        }
        return true;
    }
    //endregion

}
