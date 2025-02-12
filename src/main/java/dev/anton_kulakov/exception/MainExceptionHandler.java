package dev.anton_kulakov.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
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
        log.error("Unexpected error: ", e);
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "We're sorry, but an unexpected error has occurred. Please try again later.");

        return modelAndView;
    }
}
