package dev.anton_kulakov.dao;

import dev.anton_kulakov.model.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class LocationDao {
    private final SessionFactory sessionFactory;
    private static final String GET_BY_USER_ID_HQL = "FROM Location l WHERE l.userID = :value";

    @Autowired
    public LocationDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    public List<Location> getByUserId(int userId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Location> query = session.createQuery(GET_BY_USER_ID_HQL, Location.class);
        query.setParameter("value", userId);

        return query.list();
    }
}
