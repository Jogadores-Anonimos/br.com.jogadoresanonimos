package br.com.jogadoresanonimos.common.ratelimit;

import br.com.jogadoresanonimos.common.exception.RateLimitedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RateLimiterService {

    private static final Duration IDLE_TTL = Duration.ofMinutes(30);

    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final Clock clock = Clock.systemUTC();

    public void check(String key, int capacity, Duration window) {
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(capacity, window, clock));
        if (!bucket.tryConsume()) {
            throw new RateLimitedException("Muitas tentativas, aguarde antes de tentar novamente", bucket.secondsUntilRefill());
        }
    }

    @Scheduled(fixedRate = 10, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    void limparBucketsExpirados() {
        buckets.entrySet().removeIf(entry -> entry.getValue().expirado(IDLE_TTL));
    }
}
