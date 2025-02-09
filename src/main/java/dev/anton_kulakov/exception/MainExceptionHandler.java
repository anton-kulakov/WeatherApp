package dev.anton_kulakov.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class MainExceptionHandler {
    @ExceptionHandler({
            UserNotFoundException.class,
            UserAlreadyExistsException.class,
            WeatherApiException.class,
            LocationAlreadyExistsException.class
    })
    public ModelAndView handleCustomAppExceptions(MainAppException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", e.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleDefaultExceptions(Exception e) {
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("errorMessage", e.getMessage());

            return modelAndView;
    }
}
