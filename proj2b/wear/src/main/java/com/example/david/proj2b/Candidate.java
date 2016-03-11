package com.example.david.proj2b;

/**
 * Created by david on 2/25/16.
 */
public class Candidate {
    public String mName;
    public String mParty;
    public String mState;

    public Candidate(String name, String party, String state) {
        mName = name;
        mParty = party;
        mState = state;
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

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

}
