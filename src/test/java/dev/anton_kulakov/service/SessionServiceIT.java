package dev.anton_kulakov.service;

import dev.anton_kulakov.config.TestConfig;
import dev.anton_kulakov.dao.SessionDao;
import dev.anton_kulakov.dto.UserRegistrationDto;
import dev.anton_kulakov.model.User;
import dev.anton_kulakov.model.UserSession;
import lombok.SneakyThrows;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
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
    private final Flyway flyway;

    @Autowired
    public SessionServiceIT(UserService userService, SessionService sessionService, SessionDao sessionDao, Flyway flyway) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.sessionDao = sessionDao;
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
        createTestUserInDB();
        User user = getTestUser();
        UUID uuid = UUID.randomUUID();
        sessionService.persist(user, uuid);

        Assertions.assertEquals(1, (long) sessionDao.countById(uuid.toString()));
    }

    @SneakyThrows
    @Test
    @DisplayName("Expired sessions should be removed from the database")
    void shouldDeleteExpiredSessions() {
        createTestUserInDB();
        User user = getTestUser();
        UUID uuid = UUID.randomUUID();

        LocalDateTime expiresAt = LocalDateTime.now();
        UserSession userSession = new UserSession(uuid.toString(), user.getId(), expiresAt);

        sessionDao.persist(userSession);

        Thread.sleep(2000);
        sessionService.deleteExpiredSessions();

        Assertions.assertEquals(0, (long) sessionDao.countById(uuid.toString()));
    }

    private static User getTestUser() {
        User user = new User();
        user.setId(1);
        return user;
    }

    private void createTestUserInDB() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setLogin("test-login");
        userRegistrationDto.setPassword("test-password");
        userService.persist(userRegistrationDto);
    }
}
