package dev.anton_kulakov.service;

import dev.anton_kulakov.dao.SessionDao;
import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {
    private final UserDao userDao;
    private final SessionDao sessionDao;

    @Autowired
    public SessionService(UserDao userDao, SessionDao sessionDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
    }

    @Scheduled(fixedRate = 21600000)
    public void deleteExpiredSessions() {
        sessionDao.deleteExpiredSessions();
    }

    public Optional<UserSession> get(Cookie cookie) {
        String uuid = cookie.getValue();
        return sessionDao.getById(uuid);
    }

    public void deleteById(Cookie cookie) {
        String uuid = cookie.getValue();
        sessionDao.delete(uuid);
    }

    public UUID create(String login) {
        Optional<User> optionalUser = userDao.getByLogin(login);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException();
        }

        User user = optionalUser.get();
        UUID uuid = UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        UserSession userSession = new UserSession(uuid.toString(), user, expiresAt);

        sessionDao.persist(userSession);

        return uuid;
    }
}
