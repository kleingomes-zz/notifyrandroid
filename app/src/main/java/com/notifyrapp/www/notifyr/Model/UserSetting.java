package com.notifyrapp.www.notifyr.Model;

/**
 * Created by K on 11/20/2016.
 */

public class UserSetting {

    private int maxNotificaitons;
    private ArticleDisplayType articleDisplayType;
    private boolean articleReaderMode;
    private String networkStatus;
    private String version;


    public int getMaxNotificaitons() {
        return maxNotificaitons;
    }

    public void setMaxNotificaitons(int maxNotificaitons) {
        maxNotificaitons = maxNotificaitons;
    }

    public ArticleDisplayType getArticleDisplayType() {
        return articleDisplayType;
    }

    public void setArticleDisplayType(ArticleDisplayType articleDisplayType) {
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
}

enum ArticleDisplayType
{
    Never,
    WifiOnly,
    Always
}
