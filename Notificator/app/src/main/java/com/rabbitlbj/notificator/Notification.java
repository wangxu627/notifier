package com.rabbitlbj.notificator;

public class Notification {

    private String name;
    private int imageID;

    public Notification(String name, int imageID) {
        this.name = name;
        this.imageID = imageID;
    }

    public String getName() {
        return name;
    }

    public int getImageID() {
        return imageID;
    }
}
