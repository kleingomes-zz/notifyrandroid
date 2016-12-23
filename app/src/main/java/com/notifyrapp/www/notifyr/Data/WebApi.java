package com.notifyrapp.www.notifyr.Data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.notifyrapp.www.notifyr.Business.CallbackInterface;
import com.notifyrapp.www.notifyr.Model.AccessToken;
import com.notifyrapp.www.notifyr.Model.Article;
import com.notifyrapp.www.notifyr.Model.Item;
import com.notifyrapp.www.notifyr.Model.UserProfile;
import com.notifyrapp.www.notifyr.Model.UserSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.notifyrapp.www.notifyr.Data.WebApi.NotifyrObjects.Item;
import static java.lang.Thread.sleep;

public class WebApi {

    /* Private Fields */
    private String apiBaseUrl = "http://www.notifyr.ca/dev/service/api/";
    private String tokenUrl = "http://www.notifyr.ca/dev/service/token";

    private String defaultPassword = "2014$NotifyrPassword$2014";
    //private String apiBaseUrlDev = "http://www.notifyr.ca/dev/service/api/";
    private Context context;
    private String accessToken = "";
    private String userId = "";

    /* Constructor */
    public WebApi(Context context)
    {
        this.context = context;
        this.userId = PreferenceManager.getDefaultSharedPreferences(context).getString("userid", "");
        this.accessToken = PreferenceManager.getDefaultSharedPreferences(context).getString("accesstoken", "");

   /*     if(this.accessToken.equals("") && !this.userId.equals(""))
        {
            getAccessToken(null);
        }*/
    }
    //region Security

    public void getAccessToken(CallbackInterface callback)
    {
        String url = tokenUrl;

        if(postJSONObjectFromURL.getStatus().equals(AsyncTask.Status.PENDING)) {
            postJSONObjectFromURL.execute(url,context,callback,NotifyrObjects.AccessToken);
        }
    }


    //endregion

    //region User Profile
    public void registerUserProfile(String userName, String password, CallbackInterface callback){
        String urlPath = "Account/RegisterGuest";
        String url = apiBaseUrl + urlPath;

        if(postJSONObjectFromURL.getStatus().equals(AsyncTask.Status.PENDING)) {
            postJSONObjectFromURL.execute(url,context,callback,NotifyrObjects.UserProfile);
        }
    }

    public void saveUserSettings(UserSetting userSetting, CallbackInterface callback){
        String urlPath = "User/SaveUserSettings";

        String url = apiBaseUrl + urlPath;
        Log.d("saveUserSettings:",url);
        if(postJSONObjectFromURL.getStatus().equals(AsyncTask.Status.PENDING)) {
            postJSONObjectFromURL.execute(url,context,callback,NotifyrObjects.UserSetting,userSetting);
        }
    }
    //endregion

    //region Items
    public void getItemArticles(long itemId, int skip, int take, String sortBy, CallbackInterface callback){
        String urlPath = "Item/getItemArticles?itemId="+itemId+"&skip="+skip+"&take="+take;
        String url = apiBaseUrl + urlPath;
        if(postJSONObjectFromURL.getStatus().equals(AsyncTask.Status.PENDING)) {
            postJSONObjectFromURL.execute(url,context,callback, Item);
        }
    }

    public void getPopularItems(int skip, int take, CallbackInterface callback){
        String urlPath = "Item/getPopularItems?skip="+skip+"&take="+take;
        String url = apiBaseUrl + urlPath;
        if(postJSONObjectFromURL.getStatus().equals(AsyncTask.Status.PENDING)) {
            postJSONObjectFromURL.execute(url,context,callback, Item);
        }
    }

    public void getAllItems(CallbackInterface callback){
        String urlPath = "Item/getAllItems";
        String url = apiBaseUrl + urlPath;
        if(postJSONObjectFromURL.getStatus().equals(AsyncTask.Status.PENDING)) {
            postJSONObjectFromURL.execute(url,context,callback, Item );
        }
    }

    public void getUserItems(CallbackInterface callback){
        String urlPath = "Item/GetUserItems";
        String url = apiBaseUrl + urlPath;
        if(postJSONObjectFromURL.getStatus().equals(AsyncTask.Status.PENDING)) {
            postJSONObjectFromURL.execute(url,context,callback, Item );
        }
    }

    //endregion

    //region Articles
    public List<Article> GetArticles(int skip, int take,String sortBy, int itemTypeId )
    {
     //   Map<String,Object> map = new ObjectMapper().readValue(new URL("http://dot.com/api/?customerId=1234").openStream(), Map.class);
        List<Article> articles = new ArrayList<Article>();
        Article a = new Article();
        a.setId(2321);
        a.setTitle("Apple Releases new iPhone");
        a.setSource("CNN");
        articles.add(a);
        return articles;
    }
    //endregion

    //region Network Requests
    private AsyncTask<Object, Void, List<Object>> postJSONObjectFromURL = new AsyncTask<Object, Void, List<Object>>() {

        @Override
        protected List<Object> doInBackground(Object... params) {
            HttpURLConnection httpcon;
            String urlString = (String) params[0];
            Context parentContext = (Context) params[1];
            CallbackInterface callback = (CallbackInterface) params[2];
            NotifyrObjects notifyrObjectType = (NotifyrObjects) params[3];

            String result = "";

            try {

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                /* Set Request Type */
                if(notifyrObjectType == NotifyrObjects.AccessToken || notifyrObjectType == NotifyrObjects.UserProfile || notifyrObjectType == NotifyrObjects.UserSetting) {
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                }
                else if(notifyrObjectType == Item || notifyrObjectType == NotifyrObjects.Article )
                {
                    String bearer = "Bearer " + accessToken;
                    conn.setRequestProperty("Authorization", bearer);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                }

                /* Set Request Body */
                // Access Token
                if(notifyrObjectType == NotifyrObjects.AccessToken)
                {
                    String userName =  userId + "@notifyr.ca";
                    String str =  "grant_type=password&username="+ userName + "&password="+defaultPassword;
                    byte[] outputInBytes = str.getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();
                }
                // User Settings
                else if(notifyrObjectType == NotifyrObjects.UserSetting)
                {
                    UserSetting userSetting = (UserSetting) params[4];
                    String bearer = "Bearer " + accessToken;
                    conn.setRequestProperty("Authorization", bearer);
                    String param = "maxNotifications=" + userSetting.getMaxNotificaitons() + "&"
                            + "articleDisplayType=" + userSetting.getArticleDisplayType() + "&"
                            + "articleReaderMode=" + userSetting.isArticleReaderMode();
                    byte[] outputInBytes = param.getBytes("UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write( outputInBytes );
                    os.close();
                }

                conn.connect();
                int statusCode = conn.getResponseCode();

                InputStream is = null;

                if (statusCode >= 200 && statusCode < 400) {
                    // Create an InputStream in order to extract the response object
                    is = conn.getInputStream();
                }
                else {
                    is = conn.getErrorStream();
                }

                InputStream stream = conn.getInputStream();
                result = streamToString(stream);
                System.out.println("JSON: " + result);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                List<Object> returnObj = new ArrayList<>();
                if(!result.equals("")) {
                    if(notifyrObjectType == NotifyrObjects.Article || notifyrObjectType == NotifyrObjects.Item)
                    {
                        returnObj.add(new JSONArray(result));
                    }
                    else
                    {
                        returnObj.add(new JSONObject(result));
                    }

                    returnObj.add(callback);
                    returnObj.add(notifyrObjectType);
                    returnObj.add(context);
                }
                return returnObj;//new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Object> returnObjects) {
            // Save to Local SettingsActivity and Database

            if(returnObjects != null && returnObjects.size() == 4) {

                CallbackInterface callback = (CallbackInterface) returnObjects.get(1);
                NotifyrObjects notifyrType = (NotifyrObjects) returnObjects.get(2);
                JSONObject jsonObject = null;
                JSONArray jsonArray = null;

                if(notifyrType == NotifyrObjects.Article || notifyrType == NotifyrObjects.Item)
                {
                    jsonArray = (JSONArray) returnObjects.get(0);
                }
                else
                {
                    jsonObject = (JSONObject) returnObjects.get(0);
                }

                try {

                    if (notifyrType == NotifyrObjects.UserProfile) {
                        //Repository repo = new Repository(context);
                        //UserProfile userProfile = new UserProfile();
                        //userProfile.UserId = jsonObject.getString("UserId");
                        //userProfile.Email = jsonObject.getString("Email");
                        //repo.saveUserProfile(userProfile);
                        //PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userid", userProfile.UserId).commit();
                        callback.onCompleted(jsonObject.getString("UserId"));
                    }
                    else if (notifyrType == NotifyrObjects.Article) {



                    }
                    else if (notifyrType == Item) {
                        ArrayList<Item> items = new ArrayList<Item>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Item item = new Item();
                            JSONObject jsonItem = jsonArray.getJSONObject(i);
                            item.setId((!jsonItem.isNull( "Id" ) ?  jsonItem.getInt("Id") : -1));
                            item.setName(!jsonItem.isNull( "Name" ) ?  jsonItem.getString("Name") : "");
                            item.setIurl(!jsonItem.isNull( "IUrl" ) ?  jsonItem.getString("IUrl") : "");
                            item.setItemTypeId((!jsonItem.isNull( "ItemTypeId" ) ?  jsonItem.getInt("ItemTypeId") : -1));
                            item.setItemTypeName((!jsonItem.isNull( "ItemTypeName" ) ?  jsonItem.getString("ItemTypeName") : ""));
                            items.add(item);
                        }
                        callback.onCompleted(items);
                    }
                    else if (notifyrType == NotifyrObjects.AccessToken) {
                        AccessToken acesssTokenObj = new AccessToken();
                        acesssTokenObj.setTokenValue(jsonObject.getString("access_token"));
                        accessToken = acesssTokenObj.getTokenValue();
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("accesstoken",accessToken).commit();
                        callback.onCompleted(null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            super.onPostExecute(returnObjects);
        }
    };
    //endregion

    //region Helpers
    public enum NotifyrObjects {
        Article, Item, UserProfile, AccessToken, UserSetting
    }

    private String streamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    //endregion
}
