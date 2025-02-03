package dev.anton_kulakov.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

public class SearchPageInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        String query = request.getParameter("query");

        if (query == null || query.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/index");
            return false;
        }

        return true;
    }
}
