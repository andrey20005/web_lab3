package com.andey20005.web3;

import com.andey20005.web3.Area.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;

@Named
@RequestScoped
public class PointBean implements Serializable {
    private double x = 0;
    private double y = 0;
    private double r = 3;
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

    @Inject
    private Table table;

    public void submit() {
        table.addPoint(new Point(x, y, r, area));
        System.out.println("произошла запись x=" + x + " y=" + y + " r=" + r);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
}
