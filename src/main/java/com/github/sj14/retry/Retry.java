package com.github.sj14.retry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Different methods for retrying code on Exceptions or Throwables.
 */
public class Retry {

    private Retry() {
        // not instantiable, only static methods so far
    }

    /**
     * Retry specified operation, defaulting to 5 retry attempts.
     *
     * @param retryable
     * @throws Exception
     */
    public static void onException(Retryable retryable) throws Exception {
        onException(5, retryable);
    }

    /**
     *
     * @param maxAttempts
     * @param retryable
     * @throws Exception
     */
    public static void onException(int maxAttempts, Retryable retryable) throws Exception {
        onException(maxAttempts, null, retryable);
    }

    /**
     *
     * @param maxAttempts how often should we try again when an exception is thrown?
     * @param whitelist don't retry if one of those exceptions is thrown.
     * @param retryable the code which should be retried.
     * @throws Exception of the last attempt, originated from the code of the retry operation.
     */
    public static void onException(int maxAttempts, Collection<Class<? extends Exception>> whitelist, Retryable retryable) throws Exception {
        ArrayList<Class<? extends Throwable>> wl = null;

        if (whitelist != null) {
            // Java can't convert this automatically?
            wl = new ArrayList<>(whitelist);
        }

        retry(maxAttempts, wl, true, retryable);
    }


    /**
     * Retry specified operation, defaulting to 5 retry attempts.
     *
     * @param retryable
     * @throws Throwable
     */
    public static void onThrowable(Retryable retryable) throws Exception {
        onThrowable(5, retryable);
    }

    /**
     * Suitable for tests (e.g. junit's assert functions throws errors and not exceptions).
     * Throwable will catch both, errors and exceptions
     * but shouldn't be used in "normal" code of the service.
     *
     * @param maxAttempts
     * @param retryable
     * @throws Throwable
     */
    public static void onThrowable(int maxAttempts, Retryable retryable) throws Exception {
        onThrowable(maxAttempts, null, retryable);
    }

    /**
     * Suitable for tests (e.g. junit's assert functions throws errors and not exceptions).
     * Throwable will catch both, errors and exceptions
     * but shouldn't be used in "normal" code of the service.
     *
     * @param maxAttempts how often should we try again when an exception is thrown?
     * @param whitelist don't retry if one of those exceptions is thrown.
     * @param retryable the code which should be retried.
     * @throws Throwable of the last attempt, originated from the code of the retry operation.
     */
    public static void onThrowable(int maxAttempts, Collection<Class<? extends Throwable>> whitelist, Retryable retryable) throws Exception {
        retry(maxAttempts, whitelist, false, retryable);
    }

    private static void retry(int maxAttempts, Collection<Class<? extends Throwable>> whitelist, boolean whitelistErrors, Retryable retryable) throws Exception {
        for (int attempt = 1; ; attempt++) {
            try {
                retryable.doIt(attempt);
                break; // call was successful
            } catch (Throwable t) {

                // don't retry Errors (using onException method)
                if (whitelistErrors && t instanceof Error) {
                    throw t;
                }

                // don't retry on whitelisted classes
                if (whitelist != null) {
                    for (Class<?> w : whitelist) {
                        if (w.equals(t.getClass())) {
                            throw t;
                        }
                    }
                }

                if (attempt < maxAttempts) {
                    // exponential backoff before trying again
                    exponentialSleep(attempt);
                    continue;
                }

                // reached max. attempts
                throw t;
            }
        }
    }

    // using 6: limit max wait time (2^6 *100ms =  6,4 seconds in the last attempt)
    // using 7: limit max wait time (2^7 *100ms = 12,8 seconds in the last attempt)
    // based on https://docs.aws.amazon.com/en_us/general/latest/gr/api-retries.html
    private static void exponentialSleep(int count) throws InterruptedException {
        Thread.sleep(((long) Math.pow(2, count) * 100L));
    }
}
