package com.andey20005.web3;

import com.andey20005.web3.Area.Area;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Point implements Serializable {
    public double x;
    public double y;
    public double r;
    public boolean hit;
    public LocalDateTime time;

    public Point(double x, double y, double r, Area area) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = area.hit(this);
    }

    public Point(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public Point(double x, double y, double r, boolean hit) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getR() {
        return r;
    }

    public boolean getHit() {
        return hit;
    }
}
