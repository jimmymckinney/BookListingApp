package com.example.android.booklistingapp;

public class Book {

    private String mTitle;
    private String mAuthors;
    private String mThumbnail;
    private String mUrl;

    public Book (String title, String authors, String thumbnail, String url) {
        mTitle = title;
        mAuthors = authors;
        mUrl = url;
        mThumbnail = thumbnail;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public String getUrl(){return mUrl;}
}
