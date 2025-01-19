package com.mj.apigateway.config;

import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

@Configuration
public class GatewayConfig {

	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults()).build());
	}
//	@Bean
//	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
//		return factory -> factory
//				.configureDefault(id -> new Resilience4JConfigBuilder(id)
//						.circuitBreakerConfig(CircuitBreakerConfig.custom().failureRateThreshold(50)
//								// Open circuit if 50% of requests fail
//								.slidingWindowType(SlidingWindowType.COUNT_BASED) // COUNT_BASED or TIME_BASED
//								.slidingWindowSize(10) // Last 10 calls will be used to calculate the failure rate
//								.minimumNumberOfCalls(5) // Minimum 5 calls before evaluating the circuit state
//								.waitDurationInOpenState(Duration.ofSeconds(5))
//								// Wait 5 seconds before transitioning to HALF_OPEN
//								.permittedNumberOfCallsInHalfOpenState(3)
//								// Allow 3 calls in HALF_OPEN state to test the service
//								.recordExceptions(IOException.class, TimeoutException.class)
//								// Exceptions to treat as failures
//								.ignoreExceptions(IllegalArgumentException.class) // Exceptions to ignore
//								.build())
//						.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4))
//								// Timeout duration for the calls
//								.build())
//						.build());
//	}

}
