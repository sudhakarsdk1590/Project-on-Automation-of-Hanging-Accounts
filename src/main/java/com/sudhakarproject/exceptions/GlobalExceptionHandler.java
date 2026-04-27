package com.sudhakarproject.exceptions;

import com.sudhakarproject.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.JobExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CannotGetJdbcConnectionException.class,
            SQLException.class,
            OracleDatabaseDownException.class
    })
    public ResponseEntity<ApiErrorResponse> handleOracleDown(Exception ex){
        log.error("Oracle DB Failure : {}",ex.getMessage(),ex);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(503)
                        .error("Oracle Database Unavailable")
                        .message("Unable to connect Oracle DB. Please try again later")
                        .path("/api/jobs/run")
                        .build());
    }


    /*=====================================================
        POSTGRES FAILURE
    =====================================================*/
    @ExceptionHandler(PostgresDatabaseException.class)
    public ResponseEntity<ApiErrorResponse> handlePostgres(PostgresDatabaseException ex) {

        log.error("Postgres Failure : {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(500)
                        .error("PostgreSQL Error")
                        .message(ex.getMessage())
                        .path("/api/jobs/run")
                        .build());
    }



    /*=====================================================
        BATCH JOB FAILURE
    =====================================================*/
    @ExceptionHandler({
            JobExecutionException.class,
            BatchJobFailedException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBatch(Exception ex) {

        log.error("Batch Job Failed : {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(500)
                        .error("Batch Job Failed")
                        .message("Batch execution failed.")
                        .path("/api/jobs/run")
                        .build());
    }



    /*=====================================================
        FILE GENERATION FAILURE
    =====================================================*/
    @ExceptionHandler(FileGenerationException.class)
    public ResponseEntity<ApiErrorResponse> handleFile(FileGenerationException ex) {

        log.error("File Generation Failed : {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(500)
                        .error("File Generation Failed")
                        .message(ex.getMessage())
                        .path("/api/jobs/run")
                        .build());
    }



    /*=====================================================
        VALIDATION ERRORS
    =====================================================*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError field : ex.getBindingResult().getFieldErrors()) {
            errors.put(field.getField(), field.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }



    /*=====================================================
        INVALID REQUEST
    =====================================================*/
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalid(
            InvalidRequestException ex) {

        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(400)
                        .error("Bad Request")
                        .message(ex.getMessage())
                        .path("/api/jobs/run")
                        .build());
    }



    /*=====================================================
        UNKNOWN ERROR
    =====================================================*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnknown(Exception ex) {

        log.error("Unexpected Error : {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(500)
                        .error("Internal Server Error")
                        .message("Something went wrong. Contact support.")
                        .path("/api/jobs/run")
                        .build());
    }
}
