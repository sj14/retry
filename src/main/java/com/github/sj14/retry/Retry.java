package com.github.sj14.retry;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Different methods for retrying code on Exceptions or Throwables.
 */
public class Retry {

    /**
     * Retry specified operation, defaulting to 5 retry attempts.
     *
     * @param retryOperation
     * @throws Exception
     */
    public static void onException(RetryOperation retryOperation) throws Exception {
        onException(5, retryOperation);
    }

    /**
     *
     * @param maxAttempts
     * @param retryOperation
     * @throws Exception
     */
    public static void onException(int maxAttempts, RetryOperation retryOperation) throws Exception {
        onException(maxAttempts, null, retryOperation);
    }

    /**
     *
     * @param maxAttempts how often should we try again when an exception is thrown?
     * @param whitelist don't retry if one of those exceptions is thrown.
     * @param retryOperation the code which should be retried.
     * @throws Exception of the last attempt, originated from the code of the retry operation.
     */
    public static void onException(int maxAttempts, Collection<Class<? extends Exception>> whitelist, RetryOperation retryOperation) throws Exception {
        if (whitelist != null) {
            // Java can't convert this automatically?
            ArrayList<Class<? extends Throwable>> wl = new ArrayList<>(whitelist);
            retry(maxAttempts, wl, true, retryOperation);
        }
        retry(maxAttempts, null, true, retryOperation);
    }


    /**
     * Retry specified operation, defaulting to 5 retry attempts.
     *
     * @param retryOperation
     * @throws Throwable
     */
    public static void onThrowable(RetryOperation retryOperation) throws Exception {
        onThrowable(5, retryOperation);
    }

    /**
     * Suitable for tests (e.g. junit's assert functions throws errors and not exceptions).
     * Throwable will catch both, errors and exceptions
     * but shouldn't be used in "normal" code of the service.
     *
     * @param maxAttempts
     * @param retryOperation
     * @throws Throwable
     */
    public static void onThrowable(int maxAttempts, RetryOperation retryOperation) throws Exception {
        onThrowable(maxAttempts, null, retryOperation);
    }

    /**
     * Suitable for tests (e.g. junit's assert functions throws errors and not exceptions).
     * Throwable will catch both, errors and exceptions
     * but shouldn't be used in "normal" code of the service.
     *
     * @param maxAttempts how often should we try again when an exception is thrown?
     * @param whitelist don't retry if one of those exceptions is thrown.
     * @param retryOperation the code which should be retried.
     * @throws Throwable of the last attempt, originated from the code of the retry operation.
     */
    public static void onThrowable(int maxAttempts, Collection<Class<? extends Throwable>> whitelist, RetryOperation retryOperation) throws Exception {
        retry(maxAttempts, whitelist, false, retryOperation);
    }

    private static void retry(int maxAttempts, Collection<Class<? extends Throwable>> whitelist, boolean whitelistErrors, RetryOperation retryOperation) throws Exception {
        for (int attempt = 1; ; attempt++) {
            try {
                retryOperation.doIt(attempt);
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
