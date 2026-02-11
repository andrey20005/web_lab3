package com.andey20005.web3;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class Table implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "pointsPU")
    private EntityManager em;

    @Transactional
    public void addPoint(Point point) {
        em.persist(point);
    }

    public List<Point> getPoints() {
        TypedQuery<Point> query = em.createQuery(
                "SELECT p FROM Point p ORDER BY p.createdAt DESC",
                Point.class
        );
        return query.getResultList();
    }
}
