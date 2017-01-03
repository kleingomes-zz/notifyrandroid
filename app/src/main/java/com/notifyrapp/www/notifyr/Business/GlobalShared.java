package com.notifyrapp.www.notifyr.Business;

/**
 * Created by K on 1/3/2017.
 */

public final class GlobalShared {

    private GlobalShared() {}

    private static int articleDisplayType;


    public static int getArticleDisplayType() {
        return articleDisplayType;
    }

    public static void setArticleDisplayType(int articleDisplayType) {
        GlobalShared.articleDisplayType = articleDisplayType;
    }


}

