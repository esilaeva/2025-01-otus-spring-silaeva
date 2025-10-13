package ru.otus.hw.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.model.ApiResponse;

import java.util.concurrent.TimeUnit;

// https://www.baeldung.com/java-caching-caffeine
@Configuration
public class AddressCacheConfiguration {

    @Bean
    public Cache<String, ApiResponse> addressCache() {
        return Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }
}
