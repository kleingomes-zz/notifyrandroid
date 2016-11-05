/**
 * Created by K on 11/4/2016.
 */
package com.notifyrapp.www.notifyr.Model;

import java.util.Date;

public class Article {

    /* Private Fields */
    private long id;
    private String source;
    private String title;
    private String author;
    private String url;
    private String iurl;
    private Date ArticleNotifiedDate;
    private Date PublishDate;
    private int Score;
    private Boolean IsFavourite;
    private String ShortLinkUrl;
    private String RelatedInterests;
    private String TimeAgo;
    private String NotifiedTimeAgo;
    private String RelatedInterestsURL;


    /* Getters and Setters */
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

}
