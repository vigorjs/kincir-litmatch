package com.example.kincir.config.security.advisers;

import com.example.kincir.config.security.advisers.exception.ValidateException;
import com.example.kincir.utils.responseWrapper.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.webjars.NotFoundException;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@CrossOrigin
public class AppWideExceptionHandler {

    /*
     * Authentication Handler Exception
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        return Response.error("Error: Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidateException.class)
    public ResponseEntity<?> handleValidationException(ValidateException e) {
        return Response.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return Response.error("Invalid login credentials", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return Response.error("Internal Server Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        return Response.error(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Bad Input Handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handlePropertyValueException(RuntimeException e) {
        String errorMessage = e.getCause() != null ? e.getCause().getLocalizedMessage() : e.getMessage();
        if (e.getMessage().contains("\"email\"")) {
            errorMessage = "Email field cannot be empty";
        }
        if (e.getMessage().contains("Password")) {
            errorMessage = "Password field cannot be empty";
        }
        if (e.getMessage().contains("\"username\"")) {
            errorMessage = "Username field cannot be empty";
        }
        if (e.getMessage().contains("\"users_email_key\"")) {
            errorMessage = "Email has been registered on our system, please login instead";
        }
        if (e.getMessage().contains("\"serviceStocks_user_id_key\"")) {
            errorMessage = "This user already had a ServiceStockRepository";
        }
        return Response.error("ERROR: " + errorMessage.split("Detail:")[0], HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return Response.error(errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .toList(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<?> handleInternalServerError(HttpServerErrorException.InternalServerError ex) {
        return Response.error("500: Unknown Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
