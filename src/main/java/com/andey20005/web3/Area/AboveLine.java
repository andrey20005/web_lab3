package com.andey20005.web3.Area;


import com.andey20005.web3.Point;

public record AboveLine(double centerX, double centerY, double normX, double normY) implements Area {
    @Override
    public boolean hit(Point point) {
        return (point.getX()/point.getR() - centerX)*normX + (point.getY()/point.getR() - centerY)*normY >= 0;
    }
}
