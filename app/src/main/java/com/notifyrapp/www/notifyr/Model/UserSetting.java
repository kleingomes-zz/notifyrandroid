package com.notifyrapp.www.notifyr.Model;

/**
 * Created by K on 11/20/2016.
 */

public class UserSetting {

    private int maxNotifications;
    private int articleDisplayType;
    private boolean articleReaderMode;
    private String networkStatus;
    private String version;


    public int getMaxNotificaitons() {
        return maxNotifications;
    }

    public void setMaxNotificaitons(int maxNotifications) {
        this.maxNotifications = maxNotifications;
    }

    public int getArticleDisplayType() {
        return articleDisplayType;
    }

    public void setArticleDisplayType(int articleDisplayType) {
        this.articleDisplayType = articleDisplayType;
    }

    public boolean isArticleReaderMode() {
        return articleReaderMode;
    }

    public String getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(String networkStatus) {
        this.networkStatus = networkStatus;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setArticleReaderMode(boolean articleReaderMode) {
        this.articleReaderMode = articleReaderMode;
    }
}

enum ArticleDisplayType
{
    Never,
    WifiOnly,
    Always
}
