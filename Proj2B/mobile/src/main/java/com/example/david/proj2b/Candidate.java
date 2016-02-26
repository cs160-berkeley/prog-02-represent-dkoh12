package com.example.david.proj2b;

/**
 * Created by david on 2/25/16.
 */
public class Candidate {
    public String mName;
    public String mEmail;
    public String mWebsite;
    public String mParty;
    public String mTweet;
    public int mPhotoId;

    public Candidate(String name, String party, String email, String website, String tweet, int photo) {
        mName = name;
        mEmail = email;
        mWebsite = website;
        mParty = party;
        mTweet = tweet;
        mPhotoId = photo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public String getParty() {
        return mParty;
    }

    public void setParty(String party) {
        mParty = party;
    }

    public String getTweet() {
        return mTweet;
    }

    public void setTweet(String tweet) {
        mTweet = tweet;
    }

    public int getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(int photoId) {
        mPhotoId = photoId;
    }

}
