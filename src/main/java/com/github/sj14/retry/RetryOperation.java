package com.github.sj14.retry;

/**
 * based on https://stackoverflow.com/a/13240586
 */
public interface RetryOperation {

   /**
    * Retryable code to execute.
    * @param attempt counts the current attempt, starting at 0.
    * @throws Exception
    */
   void doIt(int attempt) throws Exception;
}
