package com.smarthost.allocationservice.config.validation;

import com.smarthost.allocationservice.config.dto.FieldErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String API_DOC_URL = "http://docs.example.com/problems/validations";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        URI apiUri = URI.create(((ServletWebRequest) request).getRequest().getRequestURI());
        log.error("Validation failed with exception for request : {} with request body: {}", apiUri, exception.getBindingResult().getTarget());
        List<FieldErrorResponse> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new FieldErrorResponse(fieldError.getField()
                        ,fieldError.getCode(),fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(buildProblem(HttpStatus.BAD_REQUEST, exception, problemDetail -> {
            problemDetail.setInstance(apiUri);
            problemDetail.setTitle("Validation Failed");
            problemDetail.setType(URI.create(API_DOC_URL));
            problemDetail.setDetail("One or more request fields are invalid");
            problemDetail.setProperty("errors", exception.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(err -> Map.of(
                            "field", err.getField(),
                            "code", Objects.requireNonNull(err.getCode()),
                            "message", Objects.requireNonNull(err.getDefaultMessage())
                    ))
                    .toList());
        }));
    }

    private ProblemDetail buildProblem(HttpStatus httpStatus, Exception exception, Consumer<ProblemDetail> detailConsumer){
        var problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, exception.getMessage());
        detailConsumer.accept(problemDetail);
        return problemDetail;
    }
}
