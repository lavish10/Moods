package com.moods_final.moods.feed;

/**
 * Created by lavish on 29/11/16.
 */
public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String source;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl,String source) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.source=source;
    }

    public String getText() {
        return text;
    }
    public String getSource(){  return  source;}

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
