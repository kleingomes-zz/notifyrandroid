package com.notifyrapp.www.notifyr.Data;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.Item;
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
    public Repository(Context context)
    {
        this.context = context;
    }

    //region Items
    public Boolean saveUserItemLocal(Item userItem){

        String tableName = "UserItem";
        SQLiteDatabase db = null;
        boolean isSuccess = true;
        try {
            File path = context.getDatabasePath(dbName);
            db = SQLiteDatabase.openDatabase(String.valueOf(path), null, 0);
            Boolean exists = checkIsDataAlreadyInDBorNot(tableName,"ItemId",String.valueOf(userItem.getId()));
            if(!exists) {
                // Insert the useritem
                db.execSQL("INSERT INTO "
                        + tableName
                        + " (ItemId, Name,IUrl,ItemTypeId,ItemTypeName,UserItemId,Priority)"
                        + " VALUES ("
                        + userItem.getId() + ","
                        + "'" + userItem.getName() + "'" + ","
                        + "'" + userItem.getIurl() + "'" + ","
                        + userItem.getItemTypeId() + ","
                        + "'" +userItem.getItemTypeName() + "'"  + ","
                        + userItem.getUserItemId() + ","
                        + userItem.getPriority() + ");");
            }
        }
        catch(Exception e) {
            isSuccess = false;
            Log.e("exception", e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return isSuccess;
    }

    public List<Item> getUserItems()
    {
        String TableName = "UserItem";
        String Data="";
        String PrintToConsole="";
        SQLiteDatabase db = null;
        ArrayList<Item> items = new ArrayList<Item>();



        try {
            File path = context.getDatabasePath(dbName);
            db = SQLiteDatabase.openDatabase(String.valueOf(path), null, 0);
            Cursor c =  db.rawQuery("SELECT * FROM " + TableName + " ORDER BY NAME", null);

            int col_itemid = c.getColumnIndex("ItemId");
            int col_name = c.getColumnIndex("Name");
            int col_iurl = c.getColumnIndex("IUrl");
            int col_itemtypeid = c.getColumnIndex("ItemTypeId");
            int col_itemtypename = c.getColumnIndex("ItemTypeName");
            int col_useritemid = c.getColumnIndex("UserItemId");
            int col_priority = c.getColumnIndex("Priority");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    Item item = new Item();
                    item.setId(c.getInt(col_itemid));
                    item.setName(c.getString(col_name));
                    item.setIurl(c.getString(col_iurl));
                    item.setItemTypeId(c.getInt(col_itemtypeid));
                    item.setItemTypeName(c.getString(col_itemtypename));
                    item.setUserItemId(c.getInt(col_useritemid));
                    item.setPriority(c.getInt(col_priority));
                    items.add(item);
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
        return items;
    }

    //endregion

    //region Article
    public Boolean saveArticle(Article article)
    {
        return true;
    }

    public List<Article> getArticles(int skip, int take, String sortBy)
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
    //endregion

    //region User

    public Boolean saveUserProfile(UserProfile userProfile)
    {
        String tableName = "UserProfile";
        SQLiteDatabase notifyrDB;
        notifyrDB = this.context.openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        Boolean accountExists = checkIfTableHasRows(tableName);

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

    public UserProfile getUserProfile()
    {
        return new UserProfile();
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
                    //Log.d("getUserSettings:",String.valueOf(c.getInt(col_maxNotifications)));
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

    public Boolean saveUserSettings(UserSetting userSetting)
    {
        String tableName = "UserSetting";
        SQLiteDatabase notifyrDB;
        SQLiteDatabase db = null;
        boolean isSuccess = true;
        int isArticleReaderMode = (userSetting.isArticleReaderMode()) ? 1:0;
        try {
            File path = context.getDatabasePath(dbName);
            db = SQLiteDatabase.openDatabase(String.valueOf(path), null, 0);
            // Insert the profile
            db.execSQL("UPDATE " //update
                    + tableName
                   // + " (MaxNotifications , ArticleDisplayType, ArticleReaderMode)"
                    + " SET MaxNotifications = "+String.valueOf(userSetting.getMaxNotificaitons())+", ArticleDisplayType = "+String.valueOf(userSetting.getArticleDisplayType())+", ArticleReaderMode = "+String.valueOf(isArticleReaderMode)+";");

        }
        catch(Exception e) {
            isSuccess = false;
            Log.e("exception", e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }


        return isSuccess;
    }
    //endregion

    //region Notifications
    public Boolean saveUserNotification(Article article){

        String tableName = "UserNotification";
        SQLiteDatabase db = null;
        boolean isSuccess = true;
        try {
            File path = context.getDatabasePath(dbName);
            db = SQLiteDatabase.openDatabase(String.valueOf(path), null, 0);
            Boolean exists = checkIsDataAlreadyInDBorNot(tableName,"ArticleId",String.valueOf(article.getId()));
            if(!exists) {
                // Insert the UserNotification
                db.execSQL("INSERT INTO "
                        + tableName
                        + " (ArticleId, Source,Score,Title,Author,Description,URL,IURL,ArticleNotifiedDate,PublishDate,IsFavourite,ShortLinkURL,RelatedInterests,TimeAgo,NotifiedTimeAgo,RelatedInterestsURL)"
                        + " VALUES ("
                        + article.getId() + ","
                        + "'" + article.getSource() + "'" + ","
                        + article.getScore() + ","
                        +  "'" + article.getTitle() + "'" + ","
                        + "'" + article.getAuthor() + "'" + ","
                        +  "'" + article.getDescription() + "'" + ","
                        +  "'" + article.getUrl() + "'" + ","
                        +  "'" + article.getIurl() + "'" + ","
                        +  "'" + article.getArticleNotifiedDate() + "'" + ","
                        +  "'" + article.getPublishDate() + "'" + ","
                        +  "'" + article.getFavourite() + "'" + ","
                        +  "'" + article.getShortLinkUrl() + "'" + ","
                        +  "'" + article.getRelatedInterests() + "'" + ","
                        +  "'" + article.getTimeAgo() + "'" + ","
                        +  "'" + article.getNotifiedTimeAgo() + "'" + ","
                        +  "'" + article.getRelatedInterestsURL() + "'"
                        + ");");
            }
        }
        catch(Exception e) {
            isSuccess = false;
            Log.e("exception", e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return isSuccess;
    }

    public List<Article> getUserNotifications() /* TODO ADD PARAMS FOR PAGING) */
    {
        String TableName = "UserNotification";
        String Data="";
        SQLiteDatabase db = null;
        ArrayList<Article> articles = new ArrayList<Article>();
        try {
            File path = context.getDatabasePath(dbName);
            db = SQLiteDatabase.openDatabase(String.valueOf(path), null, 0);
            Cursor c =  db.rawQuery("SELECT * FROM " + TableName + " ORDER BY ArticleNotifiedDate DESC", null);

            int col_articleid = c.getColumnIndex("ArticleId");
            int col_title = c.getColumnIndex("Title");
            // UPDATE THIS FOR NOTIFICATION
            /*
            int col_name = c.getColumnIndex("Name");
            int col_iurl = c.getColumnIndex("IUrl");
            int col_itemtypeid = c.getColumnIndex("ItemTypeId");
            int col_itemtypename = c.getColumnIndex("ItemTypeName");
            int col_useritemid = c.getColumnIndex("UserItemId");
            int col_priority = c.getColumnIndex("Priority");
            */

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    Article article = new Article();
                    article.setId(c.getInt(col_articleid));
                    article.setTitle(c.getString(col_title));
                    /* FINISH THIS FOR REST OF ATTRIBUTES */

                    articles.add(article);
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
        return articles;
    }

    //endregion

    //region Helpers
    private boolean checkIsDataAlreadyInDBorNot(String TableName,
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

    private boolean checkIfTableHasRows(String TableName) {
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
    //endregion
}
