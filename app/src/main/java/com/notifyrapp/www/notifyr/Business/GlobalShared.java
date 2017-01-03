package com.notifyrapp.www.notifyr.Business;

/**
 * Created by K on 1/3/2017.
 */

public final class GlobalShared {

    private GlobalShared() {
        GlobalShared.isInEditMode = false;
    }

    private static Boolean isInEditMode;
    private static int articleDisplayType;


    public static int getArticleDisplayType() {
        return articleDisplayType;
    }

    public static void setArticleDisplayType(int articleDisplayType) {
        GlobalShared.articleDisplayType = articleDisplayType;
    }


    public static Boolean getIsInEditMode() {
        return isInEditMode;
    }

    public static void setIsEditMode(Boolean isEditMode) {
        GlobalShared.isInEditMode = isEditMode;
    }
}

