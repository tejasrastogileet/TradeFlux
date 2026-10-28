package com.tradeflux.portfolio_service.advices;


import com.tradeflux.portfolio_service.exceptions.BadRequestException;
import com.tradeflux.portfolio_service.exceptions.ConflictException;
import com.tradeflux.portfolio_service.exceptions.ForbiddenException;
import com.tradeflux.portfolio_service.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceNotFoundException.class
    )
    public ResponseEntity<ErrorResponse>
    handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {

        log.error(
                "Resource not found: {}",
                ex.getMessage()
        );
        return ResponseEntity.status(
                HttpStatus.NOT_FOUND
        ).body(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .status(404)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(
            BadRequestException.class
    )
    public ResponseEntity<ErrorResponse>
    handleBadRequestException(
            BadRequestException ex
    ) {

        log.error(
                "Bad request: {}",
                ex.getMessage()
        );

        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST
        ).body(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .status(400)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(
            ForbiddenException.class
    )
    public ResponseEntity<ErrorResponse>
    handleForbiddenException(
            ForbiddenException ex
    ) {

        log.error(
                "Forbidden: {}",
                ex.getMessage()
        );

        return ResponseEntity.status(
                HttpStatus.FORBIDDEN
        ).body(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .status(403)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(
            ConflictException.class
    )
    public ResponseEntity<ErrorResponse>
    handleConflictException(
            ConflictException ex
    ) {

        log.error(
                "Conflict: {}",
                ex.getMessage()
        );

        return ResponseEntity.status(
                HttpStatus.CONFLICT
        ).body(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .status(409)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        String message =
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage();

        return ResponseEntity.status(
                HttpStatus.BAD_REQUEST
        ).body(
                ErrorResponse.builder()
                        .message(message)
                        .status(400)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(
            Exception.class
    )
    public ResponseEntity<ErrorResponse>
    handleGenericException(
            Exception ex
    ) {

        log.error(
                "Unexpected exception",
                ex
        );

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(
                ErrorResponse.builder()
                        .message("Internal server error")
                        .status(500)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}