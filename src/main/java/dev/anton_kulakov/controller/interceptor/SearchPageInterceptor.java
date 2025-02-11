package dev.anton_kulakov.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Pattern;

public class SearchPageInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {

        String rawQuery = request.getParameter("query");

        if (rawQuery == null || rawQuery.isBlank()) {
            rawQuery = "/";
        }

        String query = removeMultipleSpaces(rawQuery);

        if (!isQueryValid(query)) {
            query = " ";
        }

        request.setAttribute("queryAttribute", query);
        return true;
    }

    private String removeMultipleSpaces(String rawQuery) {
        return rawQuery.replaceAll("\\s{2,}", " ").trim();
    }

    private boolean isQueryValid(String query) {
        String regex = "^[a-zA-Z\\u0410-\\u044F0-9'-]+( [a-zA-Z\\u0410-\\u044F0-9'-]+)*$";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
        return pattern.matcher(query).matches();
    }
}
