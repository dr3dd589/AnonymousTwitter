package com.iitr.saurabh.anonymoustwitter;

/**
 * Created by Saurabh on 04-01-2018.
 */

public class Send {

    Send(){}

    public Send(String imageUrl, String text) {
        ImageUrl = imageUrl;
        this.text = text;
    }

    private String ImageUrl;
    private String text;

    public long getNoOfLikes() {
        return NoOfLikes;
    }

    public Send(long noOfLikes) {
        NoOfLikes = noOfLikes;
    }

    private long NoOfLikes;



    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }





}
