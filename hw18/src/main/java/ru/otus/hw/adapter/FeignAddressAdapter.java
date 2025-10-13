package ru.otus.hw.adapter;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.hw.client.AddressApiClient;
import ru.otus.hw.model.ApiResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignAddressAdapter {

    private final AddressApiClient addressApiClient;

    private final Cache<String, ApiResponse> addressCache;

    // https://habr.com/ru/articles/793550/
    @Retry(name = "addressRetry", fallbackMethod = "fallbackMethod")
    @RateLimiter(name = "addressRL", fallbackMethod = "fallbackMethod")
    @CircuitBreaker(name = "addressCB", fallbackMethod = "fallbackMethod")
    public ApiResponse getAddressFromRemoteServer() {
        ApiResponse apiResponse = addressApiClient.getAddresses(1);
        addressCache.put("api-response", apiResponse);
        return apiResponse;
    }

    // The fallback method can apply various business logic
    // to produce a best-effort response. For example, a fallback method can return data from a
    // local cache or simply return an immediate error message.
    // The method must follow the signature of the method the circuit breaker is applied for and also
    // have an extra last argument used for passing the exception that triggered the circuit breaker
    private ApiResponse fallbackMethod(Exception exception) throws Exception {
        log.warn("Fallback method was occurred. Exception message: {}", exception.getMessage());
        ApiResponse cached = addressCache.getIfPresent("api-response");
        if (cached != null) {
            return cached;
        } else {
            throw exception;
        }
    }
}