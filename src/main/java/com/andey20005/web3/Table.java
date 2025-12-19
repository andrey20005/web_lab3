package com.andey20005.web3;

import jakarta.ejb.Stateless;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class Table implements Serializable {
    private final List<Point> points = new ArrayList<>();

    public void addPoint(Point point) {
        point.time = LocalDateTime.now();
        points.add(point);
    }

    public List<Point> getPoints() {
        return points;
    }
}
