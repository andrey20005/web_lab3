package com.andey20005.web3;

import com.andey20005.web3.Area.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.ArrayList;

@Named
@ApplicationScoped
public class AreaBean {
    private Area area;
    {
        AndArea quarterCircle = new AndArea(new ArrayList<>());
        quarterCircle.addArea(new AboveLine(0, 0, 1, 0));
        quarterCircle.addArea(new AboveLine(0, 0, 0, 1));
        quarterCircle.addArea(new Circle(1, 0, 0));
        AndArea lowerTriangle = new AndArea(new ArrayList<>());
        lowerTriangle.addArea(new AboveLine(0, 0, 1, 0));
        lowerTriangle.addArea(new AboveLine(0, 0, 0, -1));
        lowerTriangle.addArea(new AboveLine(0, -0.5, -1, 1));
        AndArea rectangle = new AndArea(new ArrayList<>());
        rectangle.addArea(new AboveLine(0, 0, -1, 0));
        rectangle.addArea(new AboveLine(0, 0, 0, -1));
        rectangle.addArea(new AboveLine(-1, -1, 1, 0));
        rectangle.addArea(new AboveLine(-1, -1, 0, 1));
        OrArea orArea = new OrArea(new ArrayList<>());
        orArea.addArea(quarterCircle);
        orArea.addArea(lowerTriangle);
        orArea.addArea(rectangle);
        area = orArea;
    }


    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
