package dev.anton_kulakov.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.anton_kulakov.controller.interceptor.IdentifiedUserInterceptor;
import dev.anton_kulakov.controller.interceptor.SearchPageInterceptor;
import dev.anton_kulakov.controller.interceptor.UnidentifiedUserInterceptor;
import dev.anton_kulakov.dao.UserDao;
import dev.anton_kulakov.service.CookieService;
import dev.anton_kulakov.service.SessionService;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import javax.sql.DataSource;
import java.net.http.HttpClient;
import java.util.Properties;

@Profile("dev")
@Configuration
@ComponentScan("dev.anton_kulakov")
@PropertySource({"classpath:hibernate.properties", "classpath:application.properties"})
@EnableTransactionManagement
@EnableWebMvc
public class SpringConfig implements WebMvcConfigurer {
    private final ApplicationContext applicationContext;
    private final Environment env;

    @Autowired
    public SpringConfig(ApplicationContext applicationContext, Environment env) {
        this.applicationContext = applicationContext;
        this.env = env;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        registry.viewResolver(resolver);
    }

    @Bean
    public UnidentifiedUserInterceptor unidentifiedUserInterceptor(CookieService cookieService, SessionService sessionService) {
        return new UnidentifiedUserInterceptor(cookieService, sessionService);
    }

    @Bean
    public SearchPageInterceptor searchPageInterceptor() {
        return new SearchPageInterceptor();
    }

    @Bean
    public IdentifiedUserInterceptor identifiedUserInterceptor(CookieService cookieService, SessionService sessionService, UserDao userDao) {
        return new IdentifiedUserInterceptor(cookieService, sessionService, userDao);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        UnidentifiedUserInterceptor unidentifiedUserInterceptor = applicationContext.getBean(UnidentifiedUserInterceptor.class);
        SearchPageInterceptor searchPageInterceptor = applicationContext.getBean(SearchPageInterceptor.class);
        IdentifiedUserInterceptor identifiedUserInterceptor = applicationContext.getBean(IdentifiedUserInterceptor.class);

        registry.addInterceptor(unidentifiedUserInterceptor)
                .addPathPatterns("/sign-in/**", "/sign-up/**")
                .excludePathPatterns("/resources/**", "/index/**", "/search/**", "/sign-out");

        registry.addInterceptor(searchPageInterceptor)
                .addPathPatterns("/search")
                .excludePathPatterns("/resources/**", "/index/**", "/sign-in", "/sign-up", "/sign-out");

        registry.addInterceptor(identifiedUserInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/resources/**", "/sign-in", "/sign-up", "/sign-out");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("resources/");
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(env.getRequiredProperty("hibernate.driver_class"));
        dataSource.setUrl(env.getRequiredProperty("hibernate.connection.url"));
        dataSource.setUsername(env.getRequiredProperty("hibernate.connection.username"));
        dataSource.setPassword(env.getRequiredProperty("hibernate.connection.password"));

        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));

        return properties;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("dev.anton_kulakov.model");
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());

        return transactionManager;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Flyway flyway() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource())
                .locations("classpath:db/migration")
                .schemas("database")
                .load();

        flyway.migrate();

        return flyway;
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
