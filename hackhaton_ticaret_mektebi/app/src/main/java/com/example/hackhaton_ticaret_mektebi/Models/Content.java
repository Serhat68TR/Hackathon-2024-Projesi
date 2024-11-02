package com.example.hackhaton_ticaret_mektebi.Models;

public class Content {
    //private String contentID;
    private String contentName;
    private String contentProvider;
    private String contentSharedDate;
    private String contentDepartment;
    private String contentSize;
    private String contentURL;

    public Content() {
        // Default constructor required for calls to DataSnapshot.getValue(Content.class)
    }

    public Content(String contentDepartment, String contentName, String contentProvider, String contentSharedDate, String contentSize, String contentURL) {
        //this.contentID = contentID;
        this.contentDepartment = contentDepartment;
        this.contentName = contentName;
        this.contentProvider = contentProvider;
        this.contentSharedDate = contentSharedDate;
        this.contentSize = contentSize;
        this.contentURL = contentURL;
    }
    /*
    public String getContentID() {
        return contentID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    */
    public String getContentURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getContentProvider() {
        return contentProvider;
    }

    public void setContentProvider(String contentProvider) {
        this.contentProvider = contentProvider;
    }

    public String getContentSharedDate() {
        return contentSharedDate;
    }

    public void setContentSharedDate(String contentSharedDate) {
        this.contentSharedDate = contentSharedDate;
    }

    public String getContentDepartment() {
        return contentDepartment;
    }

    public void setContentDepartment(String contentDepartment) {
        this.contentDepartment = contentDepartment;
    }

    public String getContentSize() {
        return contentSize;
    }

    public void setContentSize(String contentSize) {
        this.contentSize = contentSize;
    }
// Getters and Setters
}

