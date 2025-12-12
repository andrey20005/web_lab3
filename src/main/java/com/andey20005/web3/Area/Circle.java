package com.andey20005.web3.Area;

import com.andey20005.web3.Point;

public record Circle(double radius, double x, double y) implements Area {

    @Override
    public boolean hit(Point point) {
        return Math.pow(point.x/point.r - x, 2) + Math.pow(point.x/point.r - y, 2) <= Math.pow(radius, 2);
    }
}
