package com.develop.management.aspect;

import com.develop.management.exception.AccessDeniedException;
import com.develop.management.exception.AuthenticationException;
import com.develop.management.exception.EntityNotFoundException;
import com.develop.management.exception.InvalidSpaceshipIdException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterThrowing(pointcut = "execution(* com.develop.management.service.SpaceshipServiceImpl.getSpaceshipById(..)) && args(id)", throwing = "ex")
    public void logBeforeIfNegative(Long id, Exception ex) {
        if (id < 0) {
            logger.warn("Attempt to fetch spaceship with negative ID: {}. Exception: {}", id, ex.getMessage());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.develop.management.exception.GlobalExceptionHandler.handleEntityNotFoundException(..))", throwing = "ex")
    public void logEntityNotFoundException(EntityNotFoundException ex) {
        logger.info("EntityNotFoundException handled: {}", ex.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.develop.management.exception.GlobalExceptionHandler.handleAccessDeniedException(..))", throwing = "ex")
    public void logAccessDeniedException(AccessDeniedException ex) {
        logger.info("AccessDeniedException handled: {}", ex.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.develop.management.exception.GlobalExceptionHandler.handleAuthorizationDeniedException(..))", throwing = "ex")
    public void logAuthorizationDeniedException(AuthorizationDeniedException ex) {
        logger.info("AuthorizationDeniedException handled: {}", ex.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.develop.management.exception.GlobalExceptionHandler.handleGlobalException(..))", throwing = "ex")
    public void logGlobalException(Exception ex) {
        logger.info("Exception handled in GlobalExceptionHandler: {}", ex.getMessage(), ex);
    }

    @AfterThrowing(pointcut = "execution(* com.develop.management.exception.GlobalExceptionHandler.handleInvalidSpaceshipIdException(..))", throwing = "ex")
    public void logInvalidSpaceshipIdException(InvalidSpaceshipIdException ex) {
        logger.info("InvalidSpaceshipIdException handled: {}", ex.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.develop.management.exception.GlobalExceptionHandler.handleValidationException(..))", throwing = "ex")
    public void logValidationException(MethodArgumentNotValidException ex) {
        logger.info("Validation failed with errors: {}", ex.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.develop.management.exception.GlobalExceptionHandler.handleAuthenticationException(..))", throwing = "ex")
    public void logAuthenticationException(AuthenticationException ex) {
        logger.info("AuthenticationException handled: {}", ex.getMessage());
    }

}
