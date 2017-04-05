package com.moods_final.moods.moods;

import android.gesture.Gesture;

public class GestureHolder {
    private Gesture gesture;
    private String gestureName;

    public GestureHolder(Gesture gesture, String name){
        this.gesture = gesture;
        this.gestureName = name;
    }

    public Gesture getGesture(){
        return gesture;
    }

    public void setGesture(Gesture gesture){
        this.gesture = gesture;
    }

    public String getName(){
        return gestureName;
    }

    public void setName(String name){
        this.gestureName = name;
    }

}
