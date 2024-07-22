package com.investment.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class BucketService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        return Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(10)
                                  .refillIntervally(10, Duration.ofMinutes(1))
                                  .build())
                .addLimit(Bandwidth.builder().capacity(5)
                                  .refillIntervally(5, Duration.ofSeconds(20))
                                  .build())
                .build();
    }
}
