package dev.anton_kulakov.service;

import dev.anton_kulakov.config.TestConfig;
import dev.anton_kulakov.dao.SessionDao;
import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import jakarta.servlet.http.Cookie;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDateTime;
import java.util.UUID;

@ActiveProfiles("test")
@SpringJUnitConfig(classes = TestConfig.class)
@WebAppConfiguration
public class SessionServiceIT {
    private final UserService userService;
    private final SessionService sessionService;
    private final SessionDao sessionDao;
    private final CookieService cookieService;
    private final Flyway flyway;

    @Autowired
    public SessionServiceIT(UserService userService, SessionService sessionService, SessionDao sessionDao, CookieService cookieService, Flyway flyway) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.sessionDao = sessionDao;
        this.cookieService = cookieService;
        this.flyway = flyway;
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @DisplayName("After authentication, the new session should be saved in the database")
    void shouldSaveNewSessionInDatabaseAfterAuthentication() {
        saveTestUserInDb();
        UUID uuid = sessionService.create("test-login");
        Cookie cookie = cookieService.create(uuid);

        Assertions.assertEquals(uuid.toString(), sessionService.get(cookie).get().getId());
    }

    @SneakyThrows
    @Test
    @DisplayName("Expired sessions should be removed from the database")
    void shouldDeleteExpiredSessions() {
        saveTestUserInDb();
        User user = createTestUser();
        UUID uuid = UUID.randomUUID();
        Cookie cookie = cookieService.create(uuid);

        LocalDateTime expiresAt = LocalDateTime.now();
        UserSession userSession = new UserSession(uuid.toString(), user, expiresAt);
        sessionDao.persist(userSession);

        Thread.sleep(2000);
        sessionService.deleteExpiredSessions();

        Assertions.assertTrue(sessionService.get(cookie).isEmpty());
    }

    @Test
    @DisplayName("After applying the delete method, the session should be deleted")
    void shouldDeleteSession() {
        saveTestUserInDb();

        UUID uuid = sessionService.create("test-login");
        Cookie cookie = cookieService.create(uuid);

        sessionService.delete(cookie);

        Assertions.assertTrue(sessionService.get(cookie).isEmpty());
    }

    private static User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setLogin("test-login");
        return user;
    }

    private void saveTestUserInDb() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setLogin("test-login");
        userRegistrationDto.setPassword("test-password");
        userService.persist(userRegistrationDto);
    }
}
