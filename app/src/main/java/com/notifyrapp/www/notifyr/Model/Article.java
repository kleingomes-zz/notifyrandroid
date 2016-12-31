/**
 * Created by K on 11/4/2016.
 */
package com.notifyrapp.www.notifyr.Model;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class Article implements Serializable {

    /* Private Fields */
    private long id;
    private int score;
    private String source;
    private String title;
    private String author;
    private String description;
    private String url;
    private String iurl;
    private DateTime articleNotifiedDate;
    private DateTime publishDate;
    private Boolean isFavourite;
    private String shortLinkUrl;
    private String relatedInterests;
    private String timeAgo;
    private String notifiedTimeAgo;
    private String relatedInterestsURL;


    /* Getters and Setters */
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIurl() {
        if (iurl.contains("notifyr"))
        {
            return iurl.replace("http","https");
        }
        return iurl;
    }

    public void setIurl(String iurl) {
        this.iurl = iurl;
    }

    public DateTime getArticleNotifiedDate() {
        return articleNotifiedDate;
    }

    public void setArticleNotifiedDate(DateTime articleNotifiedDate) {
        this.articleNotifiedDate = articleNotifiedDate;
    }

    public DateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(DateTime publishDate) {
        this.publishDate = publishDate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite = favourite;
    }

    public String getShortLinkUrl() {
        return shortLinkUrl;
    }

    public void setShortLinkUrl(String shortLinkUrl) {
        this.shortLinkUrl = shortLinkUrl;
    }

    public String getRelatedInterests() {
        return relatedInterests;
    }

    public void setRelatedInterests(String relatedInterests) {
        this.relatedInterests = relatedInterests;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getNotifiedTimeAgo() {
        return notifiedTimeAgo;
    }

    public void setNotifiedTimeAgo(String notifiedTimeAgo) {
        this.notifiedTimeAgo = notifiedTimeAgo;
    }

    public String getRelatedInterestsURL() {
        return relatedInterestsURL;
    }

    public void setRelatedInterestsURL(String relatedInterestsURL) {
        this.relatedInterestsURL = relatedInterestsURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
