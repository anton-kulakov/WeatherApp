package dev.anton_kulakov.dao;

import dev.anton_kulakov.exception.UserAlreadyExistsException;
import dev.anton_kulakov.model.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Slf4j
public class UserDao {
    private final SessionFactory sessionFactory;
    private static final String COUNT_HQL = "SELECT COUNT(u) FROM User u WHERE u.login = :login";
    private static final String GET_HQL = "FROM User u WHERE u.login = :login";

    @Autowired
    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void persist(User user) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(COUNT_HQL, Long.class);
        query.setParameter("login", user.getLogin());
        Long count = query.uniqueResult();

        if (count != null && count > 0) {
            log.error("User with id {} and login {} already exists", user.getId(), user.getLogin());
            throw new UserAlreadyExistsException("User with login %s already exists".formatted(user.getLogin()));
        } else {
            session.persist(user);
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> getByLogin(String login) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery(GET_HQL, User.class);
        query.setParameter("login", login);

        return query.uniqueResultOptional();
    }
}
