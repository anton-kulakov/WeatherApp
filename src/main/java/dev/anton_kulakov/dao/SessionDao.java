package dev.anton_kulakov.dao;

import dev.anton_kulakov.model.UserSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SessionDao {
    private final SessionFactory sessionFactory;
    private static final String COUNT_BY_ID_HQL = "SELECT COUNT(us) FROM UserSession us WHERE us.id = :value";
    private static final String DELETE_EXPIRED_SESSIONS_HQL = "DELETE FROM UserSession us WHERE us.expiresAt < NOW()";

    @Autowired
    public SessionDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void persist(UserSession userSession) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.persist(userSession);
    }

    @Transactional
    public void deleteExpiredSessions() {
        Session session = sessionFactory.getCurrentSession();
        MutationQuery query = session.createMutationQuery(DELETE_EXPIRED_SESSIONS_HQL);
        query.executeUpdate();
    }

    @Transactional(readOnly = true)
    public Long countById(String uuid) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(COUNT_BY_ID_HQL, Long.class);
        query.setParameter("value", uuid);

        return query.uniqueResult();
    }
}
