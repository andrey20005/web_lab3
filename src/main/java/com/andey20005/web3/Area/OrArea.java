package com.andey20005.web3.Area;

import com.andey20005.web3.Point;

import java.util.Collection;

public record OrArea(Collection<Area> areas) implements Area {
    public void addArea(Area area) {
        areas.add(area);
    }

    @Override
    public boolean hit(Point point) {
        return areas.stream().anyMatch(area -> area.hit(point));
    }
}
