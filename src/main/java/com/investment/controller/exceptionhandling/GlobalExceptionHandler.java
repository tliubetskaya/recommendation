package com.investment.controller.exceptionhandling;

import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import com.investment.service.exeption.ApplicationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ProblemDetail handle(@NotNull final ApplicationException exception) {
        final ApiProblemType apiProblemType = getApiProblemType(exception.getType());
        return getProblemDetail(apiProblemType, exception.getMessage());
    }

    private ProblemDetail getProblemDetail(final ApiProblemType apiProblemType, final String message) {
        final String errorMessage = StringUtils.isBlank(message) ? apiProblemType.getMessage() : message;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(apiProblemType.getStatus(), errorMessage);
        problemDetail.setType(apiProblemType.getUri());
        problemDetail.setTitle(apiProblemType.getTitle());
        return problemDetail;
    }

    private ApiProblemType getApiProblemType(final String errorCode) {
        return Arrays.stream(ApiProblemType.values())
                .filter(apiProblemType -> apiProblemType.name().equals(errorCode))
                .findFirst()
                .orElse(ApiProblemType.INTERNAL_SERVER_ERROR);
    }
}
