package com.investment.controller.filter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.investment.controller.exceptionhandling.ApiProblemType;
import com.investment.service.BucketService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final BucketService bucketService;
    private final ObjectMapper mapper;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        final String ipAddress = httpRequest.getRemoteAddr();
        final Bucket bucket = bucketService.resolveBucket(ipAddress);
        final ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            chain.doFilter(httpRequest, httpResponse);
        } else {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            ApiProblemType tooManyRequests = ApiProblemType.TOO_MANY_REQUESTS;
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(tooManyRequests.getStatus(), tooManyRequests.getMessage());
            problemDetail.setType(tooManyRequests.getUri());
            problemDetail.setTitle(tooManyRequests.getTitle());
            String problemDetailsJson = mapper.writeValueAsString(problemDetail);
            httpResponse.getWriter().write(problemDetailsJson);
        }
    }
}
