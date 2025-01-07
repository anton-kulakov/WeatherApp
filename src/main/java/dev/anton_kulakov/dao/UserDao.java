package dev.anton_kulakov.dao;

import dev.anton_kulakov.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class UserDao {
    private final SessionFactory sessionFactory;
    private static final String COUNT_BY_LOGIN_HQL = "SELECT COUNT(u) FROM User u WHERE u.login = :value";
    private static final String GET_BY_LOGIN_HQL = "FROM User u WHERE u.login = :value";

    @Autowired
    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void persist(User user) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(COUNT_BY_LOGIN_HQL, Long.class);
        query.setParameter("value", user.getLogin());
        Long count = query.uniqueResult();

        if (count != null && count > 0) {
            throw new IllegalArgumentException("User with this login already exists");
        } else {
            session.persist(user);
        }
    }

    @Transactional
    public Optional<User> getByLogin(String login) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(GET_BY_LOGIN_HQL, User.class);
        query.setParameter("value", login);

        return query.uniqueResultOptional();
    }

}
