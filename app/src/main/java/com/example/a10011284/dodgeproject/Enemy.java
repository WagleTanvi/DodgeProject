package com.example.a10011284.dodgeproject;

import android.graphics.Rect;

/**
 * Created by 10011284 on 3/24/2017.
 */

public class Enemy {
    int xPos;
    int yPos;
    public Enemy(int x, int y){
        xPos = x;
        yPos = y;
    }
    public int getX(){
        return xPos;
    }
    public int getY(){
        return yPos;
    }
    public void move(int value){
        yPos = yPos+value;
    }
    public Rect getRectangle() {
        Rect r = new Rect(xPos,yPos,xPos+50,yPos+37);
        return r;
    }
}
