package com.github.sj14.retry;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class RetryTest {

    @Test
    void onExceptionSuccess() throws Exception {
        AtomicInteger attempts = new AtomicInteger();

        Retry.onException(3, attempt -> {
            attempts.set(attempt);

            // First 2 tries fail,
            // third and last try succeeds.
            if (attempt <= 2) {
                throw new Exception();
            }
        });

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onExceptionFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(IndexOutOfBoundsException.class, () -> Retry.onException(3, attempt -> {
            attempts.set(attempt);
            throw new IndexOutOfBoundsException();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onExceptionWithThrowableFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(AssertionError.class, () -> Retry.onException(3, attempt -> {
            attempts.set(attempt);

            // should rethrow the Error immediately, as we are only retrying on Exceptions
            throw new AssertionError();
        }));

        if (attempts.get() != 1) {
            fail();
        }
    }

    @Test
    void onExceptionWithWhitelistFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(IndexOutOfBoundsException.class, () -> Retry.onException(3, Arrays.asList(IndexOutOfBoundsException.class), attempt -> {
            attempts.set(attempt);

            // should rethrow the Exception immediately, as it's whitelisted
            throw new IndexOutOfBoundsException();
        }));

        if (attempts.get() != 1) {
            fail();
        }
    }

    @Test
    void onExceptionWithWhitelistWrongFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(NegativeArraySizeException.class, () -> Retry.onException(3, Arrays.asList(IndexOutOfBoundsException.class), attempt -> {
            attempts.set(attempt);

            throw new NegativeArraySizeException();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onExceptionWithWhitelistNoInheritedException() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(IndexOutOfBoundsException.class, () -> Retry.onException(3, Arrays.asList(RuntimeException.class), attempt -> {
            attempts.set(attempt);

            // IndexOutOfBoundsException extends RuntimeException,
            // but we don't want to whitelist inherited classes,
            // thus, it should use 3 attempts.
            throw new IndexOutOfBoundsException();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onThrowableSuccess() throws Throwable {
        AtomicInteger attempts = new AtomicInteger();

        Retry.onThrowable(3, attempt -> {
            attempts.set(attempt);

            // First 2 tries fail,
            // third and last try succeeds.
            if (attempt <= 2) {
                throw new AssertionError();
            }
        });

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onThrowableWithExceptionSuccess() throws Throwable {
        AtomicInteger attempts = new AtomicInteger();

        Retry.onThrowable(3, attempt -> {
            attempts.set(attempt);

            // First 2 tries fail,
            // third and last try succeeds.
            if (attempt <= 2) {
                throw new Exception();
            }
        });

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onThrowableFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(AssertionError.class, () -> Retry.onThrowable(3, attempt -> {
            attempts.set(attempt);

            // throw Error instead of Exception
            throw new AssertionError();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onThrowableWithExceptionFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(Exception.class, () -> Retry.onThrowable(3, attempt -> {
            attempts.set(attempt);
            throw new Exception();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void onThrowableWithWhitelistFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(AssertionError.class, () -> Retry.onThrowable(3, Arrays.asList(AssertionError.class), attempt -> {
            attempts.set(attempt);

            // should rethrow the Exception immediately, as it's whitelisted
            throw new AssertionError();
        }));

        if (attempts.get() != 1) {
            fail();
        }
    }

    @Test
    void onThrowableWithWhitelistWrongFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(InstantiationError.class, () -> Retry.onThrowable(3, Arrays.asList(AssertionError.class), attempt -> {
            attempts.set(attempt);
            throw new InstantiationError();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

}
