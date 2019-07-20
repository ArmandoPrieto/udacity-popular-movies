package com.udacity.popularMovies.model;

public class Review {

    private String id;
    private String author;
    private String content;
    private String url;
    public static final String ID = "id";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String URL = "url";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
