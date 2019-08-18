package com.github.sj14.retry;

/**
 * The interface the retryable code has to implement.
 * (based on https://stackoverflow.com/a/13240586)
 */
public interface Retryable {

   /**
    * Retryable code to execute.
    * @param attempt counts the current attempt, starting at 1.
    * @throws Exception
    */
   void doIt(int attempt) throws Exception;
}
