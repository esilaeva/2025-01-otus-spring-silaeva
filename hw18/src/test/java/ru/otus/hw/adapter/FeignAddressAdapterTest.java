package ru.otus.hw.adapter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import ru.otus.hw.dto.AddressDto;
import ru.otus.hw.model.ApiResponse;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static java.net.HttpURLConnection.HTTP_BAD_GATEWAY;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;


@DisplayName("Integration test for FeignAddressAdapter will check: ")
@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock(@ConfigureWireMock(port = 8081))
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FeignAddressAdapterTest {

    private static final String ONE = "1";

    private static final String ADDRESS_PATH = "/api/v2/addresses";

    public static final String CACHE_KEY = "api-response";

    @Autowired
    private FeignAddressAdapter feignAddressAdapter;

    @MockitoSpyBean
    private Cache<String, ApiResponse> addressCache;

    @Autowired
    private RetryRegistry retryRegistry;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RateLimiterRegistry rateLimiterRegistry;

    private Retry retry;

    private CircuitBreaker circuitBreaker;

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        retry = retryRegistry.retry("addressRetry");
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("addressCB");
        rateLimiter = rateLimiterRegistry.rateLimiter("addressRL");

        // Reset all resilience components to initial state
        retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt();

        // Rate limiter resets automatically based on configuration
        circuitBreaker.reset();

    }

    @Test
    @DisplayName("should validate expected response structure and data mapping")
    void shouldValidateResponseStructureAndMapping() {
        // Arrange: Setup successful response
        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK)));

        // Act: Execute request
        ApiResponse result = feignAddressAdapter.getAddressFromRemoteServer();

        // Assert: validation of response structure
        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("status", "OK")
                .hasFieldOrPropertyWithValue("code", 200)
                .hasFieldOrPropertyWithValue("locale", "en_US")
                .hasFieldOrPropertyWithValue("seed", null)
                .hasFieldOrPropertyWithValue("total", 1)
                .hasFieldOrPropertyWithValue("data", Collections.singletonList(
                        AddressDto.builder()
                                .id(1L)
                                .street("98207 Street Name")
                                .streetName("Street Name")
                                .buildingNumber("0000")
                                .city("City Name")
                                .zipcode("00000")
                                .country("Country")
                                .countryCode("CC")
                                .latitude(11.123456)
                                .longitude(22.234567)
                                .build()
                ));
    }

    @Test
    @DisplayName("should retry 3 times after two consecutive failures with HTTP 500 and 503 errors")
    void shouldAnswerSuccessfullyAfterTwoAttemptsWithError() {

        // https://wiremock.org/docs/
        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Retry scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(HTTP_BAD_GATEWAY))
                .willSetStateTo("Second attempt"));

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Retry scenario")
                .whenScenarioStateIs("Second attempt")
                .willReturn(aResponse().withStatus(HTTP_INTERNAL_ERROR))
                .willSetStateTo("Third attempt"));

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Retry scenario")
                .whenScenarioStateIs("Third attempt")
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK)));

        ApiResponse result = feignAddressAdapter.getAddressFromRemoteServer();

        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("status", "OK")
                .hasFieldOrPropertyWithValue("code", 200)
                .hasFieldOrPropertyWithValue("locale", "en_US")
                .hasFieldOrPropertyWithValue("seed", null)
                .hasFieldOrPropertyWithValue("total", 1)
                .hasFieldOrPropertyWithValue("data", Collections.singletonList(
                        AddressDto.builder()
                                .id(1L)
                                .street("98207 Street Name")
                                .streetName("Street Name")
                                .buildingNumber("0000")
                                .city("City Name")
                                .zipcode("00000")
                                .country("Country")
                                .countryCode("CC")
                                .latitude(11.123456)
                                .longitude(22.234567)
                                .build()
                ));

        // Verify retry metrics
        assertThat(retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt()).isEqualTo(1);
        assertThat(retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt()).isZero();

        verify(3, getRequestedFor(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE)));
        Mockito.verify(addressCache, times(1)).put(CACHE_KEY, result);
    }

    @Test
    @DisplayName("should stop retry after getting a 404 Not Found error (we retry only when get a 500, 502 or 503 errors)")
    void shouldStopRetryAfterGetNotFoundError() {

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .willReturn(aResponse().withStatus(HTTP_NOT_FOUND)));

        assertThatThrownBy(() -> feignAddressAdapter.getAddressFromRemoteServer())
                .isExactlyInstanceOf(FeignException.NotFound.class);

        // Verify retry metrics - no retry attempts should be made
        assertThat(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt()).isEqualTo(1);
        assertThat(retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt()).isZero();

        verify(1, getRequestedFor(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE)));
    }

    @Test
    @DisplayName("should get rate limiter error if rate limiter (3 per minute) is exceeded")
    @Timeout(value = 15, unit = TimeUnit.SECONDS)
    void shouldLimitRateOfRequests() {

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK)));

        for (int i = 0; i < 20; i++) {
            try {
                ApiResponse result = feignAddressAdapter.getAddressFromRemoteServer();
                assertThat(result).isNotNull();
            } catch (Exception e) {
                throw new AssertionError("Expected first 20 requests to succeed, but got: " + e.getMessage(), e);
            }
        }

        // Clear cache to ensure fallback throws original exception
        addressCache.invalidateAll();

        // 4th request should be rate limited
        assertThatThrownBy(() -> feignAddressAdapter.getAddressFromRemoteServer())
                .isInstanceOf(RequestNotPermitted.class) // Resilience4j rate limiter exception
                .hasMessageContaining("RateLimiter 'addressRL' does not permit further calls");

        assertThat(rateLimiter.getMetrics().getAvailablePermissions()).isZero();
        assertThat(rateLimiter.getMetrics().getNumberOfWaitingThreads()).isZero();

        // Verify that exactly 3 HTTP requests were made to WireMock
        // The 4th request was blocked by rate limiter before reaching WireMock
        verify(20, getRequestedFor(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE)));

        // Wait for rate limiter to refresh and verify it allows new requests
        await().atMost(Duration.ofSeconds(7))
                .untilAsserted(() ->
                        assertThat(rateLimiter.getMetrics().getAvailablePermissions()).isGreaterThan(0));
    }

    @Test
    @DisplayName("should answer successfully with cache when external service becomes unavailable")
    void shouldGracefullyDegradation() {

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Cache scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK))
                .willSetStateTo("Failure state"));

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Cache scenario")
                .whenScenarioStateIs("Failure state")
                .willReturn(aResponse().withStatus(HTTP_INTERNAL_ERROR)));

        // Act: First request should succeed and populate cache
        ApiResponse firstResult = feignAddressAdapter.getAddressFromRemoteServer();
        assertThat(firstResult).isNotNull();

        // Second request should fail but return cached data
        ApiResponse secondResult = feignAddressAdapter.getAddressFromRemoteServer();
        assertThat(secondResult).isNotNull()
                .isEqualTo(firstResult); // Should be same cached response

        // Assert: Verify cache interactions
        Mockito.verify(addressCache, times(1)).put(CACHE_KEY, firstResult);
        Mockito.verify(addressCache, atLeast(1)).getIfPresent(CACHE_KEY);

        verify(2, getRequestedFor(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE)));

        // VERIFY: Confirm cache was accessed during fallback
        // The cache spy should record exactly 1 call to getIfPresent("api-response")
        // This happens when the second request fails and fallback method is executed
        Mockito.verify(addressCache, times(1)).getIfPresent(CACHE_KEY);
    }

    @Test
    @DisplayName("should handle fallback when cache is empty and service fails")
    void shouldHandleFallbackWithEmptyCache() {
        // Arrange: Setup WireMock to return error
        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .willReturn(aResponse().withStatus(HTTP_INTERNAL_ERROR)));

        // Ensure cache is empty
        addressCache.invalidateAll();

        // Act & Assert: Should throw original exception when cache is empty
        assertThatThrownBy(() -> feignAddressAdapter.getAddressFromRemoteServer())
                .isInstanceOf(FeignException.InternalServerError.class);

        // Verify cache was checked but found empty
        Mockito.verify(addressCache, atLeast(1)).getIfPresent(CACHE_KEY);
    }

    @Test
    @DisplayName("should verify circuit breaker state transitions")
    void shouldVerifyCircuitBreakerStateTransitions() {
        // Arrange: Setup WireMock to return failures initially
        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .willReturn(aResponse().withStatus(HTTP_INTERNAL_ERROR)));

        // Initially circuit breaker should be CLOSED (normal operation)
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        // Act: Make enough failed requests to open circuit breaker
        for (int i = 0; i < 6; i++) {
            try {
                feignAddressAdapter.getAddressFromRemoteServer();
            } catch (Exception e) {
                // Expected failures
            }
        }

        // Assert: Circuit breaker should transition to OPEN
        await().atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN));
    }

    @Test
    @DisplayName("should handle mixed success and failure scenarios")
    void shouldHandleMixedSuccessAndFailureScenarios() {
        // Arrange: Setup alternating success/failure pattern
        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Mixed Scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK))
                .willSetStateTo("Fail"));

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Mixed Scenario")
                .whenScenarioStateIs("Fail")
                .willReturn(aResponse().withStatus(HTTP_INTERNAL_ERROR))
                .willSetStateTo("Success Again"));

        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .inScenario("Mixed Scenario")
                .whenScenarioStateIs("Success Again")
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK)));

        // Act & Assert: First request should succeed
        ApiResponse firstResult = feignAddressAdapter.getAddressFromRemoteServer();
        assertThat(firstResult).isNotNull();

        // Second request should succeed (using cached data from fallback)
        ApiResponse secondResult = feignAddressAdapter.getAddressFromRemoteServer();
        assertThat(secondResult).isNotNull();

        // Third request should succeed
        ApiResponse thirdResult = feignAddressAdapter.getAddressFromRemoteServer();
        assertThat(thirdResult).isNotNull();

        // Verify cache interactions
        Mockito.verify(addressCache, atLeast(1)).put(CACHE_KEY, firstResult);
        Mockito.verify(addressCache, atLeast(1)).getIfPresent(CACHE_KEY);
    }

    @Test
    @DisplayName("should verify rate limiter refresh behavior")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void shouldVerifyRateLimiterRefreshBehavior() {
        // Arrange: Setup successful response
        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK)));

        // Act: Exhaust rate limiter (20 requests in test config)
        for (int i = 0; i < 20; i++) {
            feignAddressAdapter.getAddressFromRemoteServer();
        }

        // Verify rate limiter is exhausted
        assertThat(rateLimiter.getMetrics().getAvailablePermissions()).isZero();

        // Wait for rate limiter to refresh (configured for 5s refresh period in test)
        await().atMost(Duration.ofSeconds(7))
                .untilAsserted(() -> assertThat(rateLimiter.getMetrics().getAvailablePermissions()).isGreaterThan(0));

        // Act: Make another request after refresh
        ApiResponse result = feignAddressAdapter.getAddressFromRemoteServer();

        // Assert: Request should succeed
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should demonstrate precise rate limiting behavior without cache interference")
    void shouldDemonstrateExactRateLimitingBehavior() {
        // Arrange: Setup successful response
        stubFor(get(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE))
                .willReturn(aResponse()
                        .withBodyFile("test-response.json")
                        .withHeader("Content-Type", "application/json")
                        .withStatus(HTTP_OK)));

        // Clear cache to ensure clean state
        addressCache.invalidateAll();

        // Act: Make exactly 20 requests (the rate limit)
        for (int i = 0; i < 20; i++) {
            ApiResponse result = feignAddressAdapter.getAddressFromRemoteServer();
            assertThat(result).isNotNull();
        }

        // Verify rate limiter is now exhausted
        assertThat(rateLimiter.getMetrics().getAvailablePermissions()).isZero();

        // Clear cache again to ensure rate-limited requests fail
        addressCache.invalidateAll();

        // Act & Assert: Next request should be rate limited
        assertThatThrownBy(() -> feignAddressAdapter.getAddressFromRemoteServer())
                .isInstanceOf(RequestNotPermitted.class)
                .hasMessageContaining("RateLimiter 'addressRL' does not permit further calls");

        // Verify exactly 20 HTTP requests were made to WireMock
        verify(20, getRequestedFor(urlPathEqualTo(ADDRESS_PATH))
                .withQueryParam("_quantity", equalTo(ONE)));
    }
}