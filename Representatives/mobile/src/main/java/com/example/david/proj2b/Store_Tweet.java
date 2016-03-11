package com.example.david.proj2b;

/**
 * Created by david on 3/10/16.
 */
public class Store_Tweet {

    String name;
    String tweet;

    public Store_Tweet(){}

    public Store_Tweet(String myName, String myTweet){
        name = myName;
        tweet = myTweet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }
}
