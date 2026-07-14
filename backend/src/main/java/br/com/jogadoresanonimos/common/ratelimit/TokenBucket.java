package br.com.jogadoresanonimos.common.ratelimit;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class TokenBucket {

    private final int capacity;
    private final Duration refillWindow;
    private final Clock clock;
    private final AtomicReference<State> state;

    public TokenBucket(int capacity, Duration refillWindow, Clock clock) {
        this.capacity = capacity;
        this.refillWindow = refillWindow;
        this.clock = clock;
        this.state = new AtomicReference<>(new State(capacity, clock.instant()));
    }

    public boolean tryConsume() {
        while (true) {
            State current = state.get();
            State refilled = refill(current);
            if (refilled.tokens <= 0) {
                return state.compareAndSet(current, refilled) || tryConsume();
            }
            State next = new State(refilled.tokens - 1, refilled.windowStart);
            if (state.compareAndSet(current, next)) {
                return true;
            }
        }
    }

    public boolean expirado(Duration idleTtl) {
        Instant windowStart = state.get().windowStart;
        return Duration.between(windowStart, clock.instant()).compareTo(refillWindow.plus(idleTtl)) >= 0;
    }

    public long secondsUntilRefill() {
        State current = refill(state.get());
        if (current.tokens > 0) {
            return 0;
        }
        Instant nextWindow = current.windowStart.plus(refillWindow);
        return Math.max(0, Duration.between(clock.instant(), nextWindow).getSeconds());
    }

    private State refill(State current) {
        Instant now = clock.instant();
        if (Duration.between(current.windowStart, now).compareTo(refillWindow) >= 0) {
            return new State(capacity, now);
        }
        return current;
    }

    private record State(int tokens, Instant windowStart) {
    }
}
