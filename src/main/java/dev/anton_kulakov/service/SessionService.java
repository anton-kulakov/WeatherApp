package dev.anton_kulakov.service;

import dev.anton_kulakov.dao.SessionDao;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionDao sessionDao;

    @Autowired
    public SessionService(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void persist(User user, UUID uuid) {
        int userId = user.getId();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        UserSession userSession = new UserSession(uuid.toString(), userId, expiresAt);

        sessionDao.persist(userSession);
    }

    public void deleteExpiredSessions() {
        sessionDao.deleteExpiredSessions();
    }

    public Optional<UserSession> get(Cookie cookie) {
        String uuid = cookie.getValue();
        return sessionDao.getById(uuid);
    }

    public void deleteByID(Cookie cookie) {
        String uuid = cookie.getValue();
        sessionDao.delete(uuid);
    }
}
