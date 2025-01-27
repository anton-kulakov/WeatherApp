package dev.anton_kulakov.dao;

import dev.anton_kulakov.model.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class LocationDao {
    private final SessionFactory sessionFactory;
    private static final String GET_BY_USER_ID_HQL = "FROM Location l WHERE l.userID = :value";
    private static final String COUNT_BY_USER_ID_HQL = "SELECT COUNT(l) FROM Location l WHERE l.userID = :userId AND l.latitude = :latitude AND l.longitude = :longitude";
    private static final String DELETE_HQL = "DELETE FROM Location l WHERE l.latitude = :latitude AND l.longitude = :longitude";

    @Autowired
    public LocationDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void persist(Location location) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(location);
    }

    @Transactional
    public void delete(BigDecimal latitude, BigDecimal longitude) {
        Session session = sessionFactory.getCurrentSession();
        MutationQuery query = session.createMutationQuery(DELETE_HQL);
        query.setParameter("latitude", latitude);
        query.setParameter("longitude", longitude);

        query.executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<Location> getByUserId(int userId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Location> query = session.createQuery(GET_BY_USER_ID_HQL, Location.class);
        query.setParameter("value", userId);

        return query.list();
    }

    @Transactional(readOnly = true)
    public Long countByUserId(int userId, BigDecimal latitude, BigDecimal longitude) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(COUNT_BY_USER_ID_HQL, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("latitude", latitude);
        query.setParameter("longitude", longitude);

        return query.uniqueResult();
    }
}
