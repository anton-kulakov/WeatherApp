package dev.anton_kulakov.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Pattern;

public class SearchPageInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String rawQuery = request.getParameter("query");

        if (rawQuery == null || rawQuery.isBlank()) {
            rawQuery = "/";
        }

        String query = rawQuery.replaceAll("\\s{2,}", " ").trim();

        String regex = "^[a-zA-Z\\u0410-\\u044F0-9'-]+( [a-zA-Z\\u0410-\\u044F0-9'-]+)*$";
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
        boolean isQueryValid = pattern.matcher(query).matches();

        if (!isQueryValid) {
            query = "/";
        }

        request.setAttribute("queryAttribute", query);
        return true;
    }
}
