package io.gitlab.sj14.retry;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class RetryTester {

    @Test
    void testOnExceptionSuccess() throws Exception {
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
    void testOnExceptionFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(Exception.class, () -> Retry.onException(3, attempt -> {
            attempts.set(attempt);
            throw new Exception();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void testOnExceptionWithThrowableFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(Error.class, () -> Retry.onException(3, attempt -> {
            attempts.set(attempt);

            // should rethrow the Error immediately, as we are only retrying on Exceptions
            throw new AssertionError();
        }));

        if (attempts.get() != 1) {
            fail();
        }
    }

    @Test
    void testOnExceptionWithWhitelistFail() {
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
    void testOnExceptionWithWhitelistWrongFail() {
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
    void testOnThrowableSuccess() throws Throwable {
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
    void testOnThrowableWithExceptionSuccess() throws Throwable {
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
    void testOnThrowableFail() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(Error.class, () -> Retry.onThrowable(3, attempt -> {
            attempts.set(attempt);

            // throw Error instead of Exception
            throw new Error();
        }));

        if (attempts.get() != 3) {
            fail();
        }
    }

    @Test
    void testOnThrowableWithExceptionFail() {
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
    void testOnThrowableWithWhitelistFail() {
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
    void testOnThrowableWithWhitelistWrongFail() {
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
