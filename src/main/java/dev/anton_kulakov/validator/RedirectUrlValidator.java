package dev.anton_kulakov.validator;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedirectUrlValidator {
    private static final Set<String> ALLOWED_REDIRECTS = Set.of("/sign-in", "/sign-up", "/index", "/search");

    public String getValidRedirectToUrl(String redirectToUrl) {
        return isRedirectUrlValid(redirectToUrl) ? redirectToUrl : "/index";
    }

    private boolean isRedirectUrlValid(String url) {
        return url != null && ALLOWED_REDIRECTS.contains(url);
    }
}
