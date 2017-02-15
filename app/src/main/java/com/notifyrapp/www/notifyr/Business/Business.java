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

import java.util.ArrayList;
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

    public void saveAllAvailableItems(CallbackInterface callback)
    {
        new WebApi(context).getAllItems(new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {
                List<Item> itemsList = (List<Item>) data;
                for (Item item: itemsList)
                {
                    saveItem(item);
                }
            }
        });
    }

    public List<Item> getLocalItemsByQuery(String query)
    {
        return new Repository(context).getItemsByQuery(query);
    }

    public List<Item>  getItemsLocal()
    {
        return new Repository(context).getItems();
    }

    public List<Item> getUserItemsFromLocal()
    {
        return new Repository(context).getUserItems();
    }

    public void getUserItemsFromServer(CallbackInterface callback)
    {
        new WebApi(context).getUserItems(callback);
    }

    public Boolean saveItem(Item userItem)
    {
        return new Repository(context).saveItem(userItem);
    }

    public Boolean deleteAllItems()
    {
        return new Repository(context).deleteAllItems();
    }

    public Boolean saveUserItemLocal(Item userItem)
    {
        return new Repository(context).saveUserItem(userItem);
    }

    public Boolean deleteUserItemLocal(Item userItem)
    {
        return new Repository(context).deleteUserItem(userItem);
    }

    public void deleteUserItemFromServer(int itemId,CallbackInterface callback)
    {
        new WebApi(context).deleteUserItem(itemId,callback);
    }

    public void saveUserItemToServer(Item item,CallbackInterface callback)
    {
        new WebApi(context).saveUserItem(item,callback);
    }

    public void updateUserItemPriorityFromServer(int itemId,int priorityId,CallbackInterface callback)
    {
        new WebApi(context).updateUserItemPriority(itemId,priorityId,callback);
    }

    public void getPopularItemsFromServer(int skip,int take,CallbackInterface callback)
    {
        new WebApi(context).getPopularItems(skip,take,callback);
    }

    public void getPopularItemsByItemTypeIdFromServer(int skip,int take,int itemTypeId,CallbackInterface callback)
    {
        new WebApi(context).getPopularItemsByItemTypeId(skip,take,itemTypeId,callback);
    }

    public void getItemsByQuery(String query,CallbackInterface callback)
    {
        new WebApi(context).getItemsByQuery(query,callback);
    }

    public Boolean isUserFollowingItem(Item userItem)
    {
        return new Repository(context).isUserFollowingItem(userItem);
    }

    public Boolean isUserItemInList(List<Item>userItems,Item userItem)
    {
        for (Item currentItem : userItems) {
            if(currentItem.getId() == userItem.getId())
            {
                return true;
            }
        }
        return false;
    }

    public void syncUserItems()
    {
        // 1. get the local user items
        final List<Item> userItemsLocal = getUserItemsFromLocal();

        // 2. get the server user items
        getUserItemsFromServer(new CallbackInterface() {
            @Override
            public void onCompleted(Object data) {

                boolean isFound = false;
                List<Item> userItemsServer = (List<Item>)data;

                // Check if the item is found locally but not on the server
                // If true then save the local item to the server
                for (Item currentLocalItem : userItemsLocal)
                {
                    for (Item currentServerItem : userItemsServer)
                    {
                        if(currentLocalItem.getId() == currentServerItem.getId())
                        {
                            isFound = true;
                        }
                    }
                    if(!isFound)
                    {
                        saveUserItemToServer(currentLocalItem, new CallbackInterface() {
                            @Override
                            public void onCompleted(Object data) {
                            }
                        });
                    }
                }

                // Check if the item is found on the server but not locally
                // If true delete the item from the server
                isFound = false;
                for (Item currentServerItem : userItemsServer)
                {
                    for (Item currentLocalItem : userItemsLocal)
                    {
                        if(currentLocalItem.getId() == currentServerItem.getId())
                        {
                            isFound = true;
                        }
                    }
                    if(!isFound)
                    {
                        deleteUserItemFromServer(currentServerItem.getId(), new CallbackInterface() {
                            @Override
                            public void onCompleted(Object data) {
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Get the item type categories (i.e. company, product) that the user
     * follows.
     */
    public List<ItemType> getItemCategories()
    {
        return new Repository(context).getItemCategories();
    }

    public Boolean hasItemCategoriesChanged(List<ItemType> oldCategories)
    {
        List<ItemType> newCategories = getItemCategories();

        if(newCategories.size() != oldCategories.size())
        {
            return true;
        }

        for(int i = 0; i < oldCategories.size(); i++)
        {
            if(oldCategories.get(i).getId() != newCategories.get(i).getId())
            {
                return true;
            }
        }
        return false;
    }

    //endregion

    //region Articles
     public void getItemArticles(long itemId,int skip,int take,String sortBy, CallbackInterface callback){
        new WebApi(context).getItemArticles(itemId,skip,take,sortBy,callback);
    }

    public void getArticlesFromServer(int skip,int take, String sortBy,int itemTypeId,int itemId,Boolean isItem, CallbackInterface callback)
    {
        if(isItem)
        {
            getUserItemArticlesFromServer(skip,take,sortBy,itemId,callback);
        }
        else
        {
            getUserArticlesFromServer(skip,take,sortBy,itemTypeId,callback);
        }

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

    public void  getUserItemArticlesFromServer(int skip, int take, String sortBy,  int itemId, CallbackInterface callback) {
        new WebApi(context).getUserItemArticles(skip,take,sortBy,itemId,callback);
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

    public void registerDevice(String token, CallbackInterface callback){
        new WebApi(context).registerDevice(token,callback);
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
        Boolean result = new Repository(context).saveUserSettings(userSetting);
        return result;
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
                org.joda.time.DateTime now = new org.joda.time.DateTime().plusHours(1); // Default time zone.
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

    public void deleteUserNotificationsServer(CallbackInterface callback)
    {
        new WebApi(context).deleteUserNotificationsServer(callback);
    }

    public Boolean deleteUserNotificationsLocal()
    {
        return  new Repository(context).deleteUserNotifications();
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

    //region MISC
    public void getNetworkStatus(CallbackInterface callback)
    {
        new WebApi(context).getNetworkStatus(callback);
    }

    public void sendTestNotification(CallbackInterface callback)
    {
        new WebApi(context).sendTestNotification(callback);
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
