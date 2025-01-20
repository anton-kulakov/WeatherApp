package dev.anton_kulakov.service;

import dev.anton_kulakov.dao.SessionDao;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public boolean isUserSessionExist(Cookie[] cookies) {
        if (cookies == null) {
            return false;
        }

        for (Cookie cookie : cookies) {
            if (!("uuid".equals(cookie.getName()))) {
                return false;
            }
        }

        for (Cookie cookie : cookies) {
            String uuid = cookie.getName();

            if (sessionDao.countById(uuid) == 0) {
                return false;
            }
        }

        return true;
    }

    public void deleteExpiredSessions() {
        sessionDao.deleteExpiredSessions();
    }

    public Cookie createCookie(UUID uuid) {
        Cookie cookie = new Cookie("uuid", uuid.toString());
        int secondsInADay = 24 * 60 * 60;
        cookie.setMaxAge(secondsInADay);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
