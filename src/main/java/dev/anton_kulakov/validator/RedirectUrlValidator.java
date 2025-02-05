package dev.anton_kulakov.validator;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedirectUrlValidator {
    private static final Set<String> ALLOWED_REDIRECTS = Set.of("/sign-in", "/sign-up", "/index",  "/search");

    public boolean isRedirectUrlValid(String url) {
        return ALLOWED_REDIRECTS.contains(url);
    }
}
