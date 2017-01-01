package com.notifyrapp.www.notifyr.Business;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.notifyrapp.www.notifyr.Data.DatabaseBuilder;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Data.WebApi;
import com.notifyrapp.www.notifyr.Data.Repository;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.Model.ItemType;
import com.notifyrapp.www.notifyr.Model.UserSetting;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.List;
import java.util.Map;


public class Business {

    /* The View Context */
    Context context;

    /* Data comes from the Notifyr Web Api */
    WebApi webApi;

    /* Data comes from local SQL Lite */
    Repository repo;

    /* Builds Databases and Tables */
    DatabaseBuilder repoBuilder;

    public Business(Context context)
    {
        JodaTimeAndroid.init(context);
        this.context = context;
    }


    //region Data Access
    public Boolean createNotifyrDatabase(String userId)
    {
        return new DatabaseBuilder(context,userId).createNotifyrDatabase();
    }
    //endregion

    //region Items
    public void getAllItems(CallbackInterface callback)
    {
        new WebApi(context).getAllItems(callback);
    }

    public void getPopularItems(int skip,int take,CallbackInterface callback)
    {
        new WebApi(context).getPopularItems(skip,take,callback);
    }

    public List<Item> getUserItemsFromLocal()
    {
        return new Repository(context).getUserItems();
    }

    public void getUserItemsFromServer(CallbackInterface callback)
    {
        new WebApi(context).getUserItems(callback);
    }

    public Boolean saveUserItemLocal(Item userItem)
    {
        return new Repository(context).saveUserItemLocal(userItem);
    }

    public void syncUserItems()
    {

    }

    /**
     * Get the item type categories (i.e. company, product) that the user
     * follows.
     */
    public List<ItemType> getItemCategories()
    {
        return new Repository(context).getItemCategories();
    }
    //endregion

    //region Articles
     public void getItemArticles(long itemId,int skip,int take,String sortBy, CallbackInterface callback){
        new WebApi(context).getItemArticles(itemId,skip,take,sortBy,callback);
    }

    public void  getUserArticlesFromServer(int skip, int take, String sortBy, int itemTypeId, final CallbackInterface callback)    {
        new WebApi(context).getUserArticles(skip,take,sortBy,itemTypeId,new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {

              /**  Business b = new Business(context);
                Log.d("CALLBACK_CHECK","GOT USER ARTICLES FROM SERVER... SAVING TO LOCAL NOW....");
                final long startTime = System.currentTimeMillis();
                List<Article> articles = (List<Article>) data;
                b.saveArticlesLocal(articles);
                final long endTime = System.currentTimeMillis();
                Log.d("OPERATION_TIME","Article Save execution time: " + (endTime - startTime));
               */
                if (callback != null) {
                    callback.onCompleted(data);
                }
            }
        });
    }

    public List<Article>  getUserArticlesFromLocal(int skip, int take,String sortBy, int itemTypeId)    {
        return new Repository(context).getUserArticles(skip,take,sortBy,itemTypeId);
    }

    public Boolean saveArticlesLocal(List<Article> articles)
    {
        return new Repository(context).saveArticles(articles);
    }

    public void saveArticlesLocalAsync(List<Article> articles)
    {
        if(saveUserNotificationLocalAsync.getStatus().equals(AsyncTask.Status.PENDING)) {
            saveUserNotificationLocalAsync.execute(articles);
        }
    }

    private AsyncTask<Object, Void, List<Object>> saveArticlesLocalAsync = new AsyncTask<Object, Void, List<Object>>() {

        @Override
        protected List<Object> doInBackground(Object... params) {
            List<Article> articles = (List<Article>) params[0];
            new Repository(context).saveArticles(articles);
            return null;
        }
    };
    //endregion

    //region User Accounts
    public void registerAccount(String userName, String password, CallbackInterface callback){
        new WebApi(context).registerUserProfile(userName,password,callback);
    }

    public void updateToken(CallbackInterface callback)
    {
        new WebApi(context).getAccessToken(callback);
    }

    public UserSetting getUserSettings()
    {
        return new Repository(context).getUserSettings();
    }
    public boolean saveUserSettingsLocal(UserSetting userSetting)
    {
        return new Repository(context).saveUserSettings(userSetting);
    }
    public void saveUserSettingsServer(UserSetting userSetting,CallbackInterface callback)
    {
        new WebApi(context).saveUserSettings(userSetting,callback);
    }
    public boolean checkIfDatabaseExists()
    {
        return new Repository(context).checkIfDatabaseExists();
    }

    public void syncUserSettingWithServer(UserSetting userSetting){
        //new WebApi(context).registerUserProfile(userName,password,callback);
    }

    //endregion

    //region Notifications
    public void getUserNotificationsFromServer(int skip,int take, final CallbackInterface callback)
    {
        new WebApi(context).getUserNotifications(skip,take,new CallbackInterface() {
          @Override
          public void onCompleted(Object data) {
          if (callback != null) {
                    callback.onCompleted(data);
                }
            }
        });
    }

    public void getUserNotificationsFromServer(String fromDate, final CallbackInterface callback)
    {
        new WebApi(context).getUserNotifications(fromDate,new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {
                org.joda.time.DateTime now = new org.joda.time.DateTime(); // Default time zone.
                org.joda.time.DateTime zulu = now.toDateTime( org.joda.time.DateTimeZone.UTC );
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("LastUpdateUserNotifiedArticles", zulu.toString()).commit();
                if (callback != null) {
                    callback.onCompleted(data);
                }
            }
        });
    }

    public Boolean saveUserNotificationLocal(List<Article> articles)
    {
        return new Repository(context).saveUserNotifications(articles);
    }

    public void saveUserNotificationLocalAsync(List<Article> articles)
    {
        if(saveUserNotificationLocalAsync.getStatus().equals(AsyncTask.Status.PENDING)) {
            saveUserNotificationLocalAsync.execute(articles);
        }
    }

    private AsyncTask<Object, Void, List<Object>> saveUserNotificationLocalAsync = new AsyncTask<Object, Void, List<Object>>() {

        @Override
        protected List<Object> doInBackground(Object... params) {
            List<Article> articles = (List<Article>) params[0];
            new Repository(context).saveUserNotifications(articles);
            return null;
        }
    };

    public List<Article> getUserNotificationsLocal(int skip, int take) /* TODO ADD PARAMS FOR PAGING) */
    {
        return new Repository(context).getUserNotifications(skip, take);
    }
    //endregion

    //region Bookmark
    public Boolean saveBookmark(Article article)
    {
        return new Repository(context).saveBookmark(article);
    }
    public Boolean deleteBookmark(Article article)
    {
        return new Repository(context).deleteBookmark(article);
    }
    public List<Article> getBookmarks(int skip, int take)
    {
        return new Repository(context).getBookmarks(skip,take);
    }

    //endregion

    //region ENUM
    public enum MenuTab
    {
        Home,
        Interests,
        Discover,
        Notifications,
        Settings
    }

    public enum SortBy
    {
        Newest("PublishDate"),
        Popular("Score"),
        Bookmark("Bookmark");

        private final String text;

        private SortBy(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    //endregion

}
