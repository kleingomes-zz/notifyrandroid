package com.notifyrapp.www.notifyr.Business;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.notifyrapp.www.notifyr.Data.Repository;
import com.notifyrapp.www.notifyr.Model.Article;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by K on 12/29/2016.
 */

public final class CacheManager {

    private CacheManager() {  }
    private static final int maxImageCacheSize = 25;
    private static final int maxArticleImageCacheSize = 25;
    private static final int maxObjectCacheSize = 25;
    private static Map<String, Object> objectCache = new HashMap<String, Object>();
    private static Map<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
    private static Map<String, Bitmap> articleImageCache = new HashMap<String, Bitmap>();

    //region Object

    public static void saveObjectToMemoryCache(String key,Object obj)
    {
        if(!objectCache.containsKey(key))
        {
            // Cache is too big remove an old entry
            // to make room for new entry
            if(objectCache.size() > maxObjectCacheSize)
            {
                List<String> list = new ArrayList<String>(objectCache.keySet());
                objectCache.remove(list.get(0));
            }
            objectCache.put(key,obj);
        }
    }


    public static void deleteObjectFromCache(String key)
    {
        if(objectCache.containsKey(key))
        {
             objectCache.remove(key);
        }
    }

    public static Object getObjectFromMemoryCache(String key)
    {
        if(objectCache.containsKey(key))
        {
            Log.d("CACHE_HIT",key);
            return objectCache.get(key);
        }
        Log.d("CACHE_MISS",key);
        return null;
    }

    //endregion

    //region Image
    public static void saveImageToMemoryCache(String key,Bitmap image)
    {
        if(!imageCache.containsKey(key))
        {
            // Cache is too big remove an old entry
            // to make room for new entry
            if(imageCache.size() < maxImageCacheSize)
            {
               imageCache.put(key,image);
            }
        }
    }

    public static Bitmap getImageFromMemoryCache(String key)
    {
        if(imageCache.containsKey(key))
        {
            Log.d("CACHE_HIT",key);
            return imageCache.get(key);
        }
        Log.d("CACHE_MISS",key);
        return null;
    }

    public static void saveArticleImageToMemoryCache(String key,Bitmap image)
    {
        if(!articleImageCache.containsKey(key))
        {
            // Cache is too big remove an old entry
            // to make room for new entry
            if(articleImageCache.size() < maxArticleImageCacheSize)
            {
                List<String> list = new ArrayList<String>(objectCache.keySet());
                articleImageCache.clear();
            }
            articleImageCache.put(key,image);
        }
    }

    public static Bitmap getArticleImageFromMemoryCache(String key)
    {
        if(articleImageCache.containsKey(key))
        {
            Log.d("CACHE_HIT",key);
            return articleImageCache.get(key);
        }
        Log.d("CACHE_MISS",key);
        return null;
    }

    public static void clearArticleImageMemoryCache()
    {
        articleImageCache.clear();
    }

    public static void clearImageMemoryCache()
    {
        imageCache.clear();
    }

    /**
     * Method inserts an image into the cache
     * @param key Unique key which will be used to access the cache item.
     * @param image The image file.
     * @param ctx The Application Context.
     * @return Nothing.
     * @exception IOException On save error.
     * @see IOException
     */
    public static void saveImageToDiskCache(String key,Bitmap image,Context ctx)
    {
        ContextWrapper cw = new ContextWrapper(ctx.getApplicationContext());

        // Save the file name as the unique key
        String fileName = key;

        // Path to /data/data/notifyr/app_data/cache
        File directory = cw.getDir("cache", Context.MODE_PRIVATE);

        // Create Cache Directory
        File cachePath = new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(cachePath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //return directory.getAbsolutePath();
    }

    /**
     * Method gets an image from the cache
     * @param key Unique key which will be used to access the cache item.
     * @param ctx The Application Context.
     * @return Bitmap image.
     */
    public static Bitmap getImageFromDiskCache(String key,Context ctx)
    {
        Bitmap bMap = null;
        ContextWrapper cw = new ContextWrapper(ctx.getApplicationContext());
        File directory = cw.getDir("cache", Context.MODE_PRIVATE);

        // Get a reference to the file
        File f = new File(directory+"/"+key);

        // Check if file exists (Get the file only if it exists)
        if(f.exists() && !f.isDirectory()) {
            Log.d("CACHE_HIT",key);
           return BitmapFactory.decodeFile(directory+"/"+key);
        }

        // If we reach here we have a cache miss return null
        Log.d("CACHE_MISS",key);
        return bMap;
    }

    //endregion

    /**
     * Method clears the cache
     * @param ctx The Application Context.
     */
    public static void clearCache(Context ctx){
        ContextWrapper cw = new ContextWrapper(ctx.getApplicationContext());
        File dir = cw.getDir("cache", Context.MODE_PRIVATE);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
                Log.d("DELETING_CACHE_ITEM",dir+"/"+children[i]);
            }
        }
        imageCache.clear();
        objectCache.clear();
    }

    public static void clearCacheAsync(Context ctx)
    {
        if(clearCacheAsync.getStatus().equals(AsyncTask.Status.PENDING)) {
            clearCacheAsync.execute(ctx);
        }
    }
    private static AsyncTask<Object, Void, List<Object>> clearCacheAsync = new AsyncTask<Object, Void, List<Object>>() {

        @Override
        protected List<Object> doInBackground(Object... params) {
            Context ctx = (Context) params[0];
            clearCache(ctx);
            return null;
        }
    };
}
