package com.example.david.twob;

/**
 * Created by david on 2/25/16.
 */
public class Candidate {
    public String mName;
    public String mParty;
    public int mPhotoId;

    public Candidate(String name, String party, int photo) {
        mName = name;
        mParty = party;
        mPhotoId = photo;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getParty() {
        return mParty;
    }

    public void setParty(String party) {
        mParty = party;
    }

    public int getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(int photoId) {
        mPhotoId = photoId;
    }

}
