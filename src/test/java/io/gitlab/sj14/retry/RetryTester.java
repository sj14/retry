package io.gitlab.sj14.retry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class RetryTester {

  @Test
  public void testRetryExceptionSuccess() throws Exception {
    Retry.onException(3, attempt -> {
        // First 2 tries fail,
        // third and last try succeeds.
        if (attempt < 2) {
          throw new Exception();
      }
    });
  }

  @Test
  public void testRetryExceptionFail() {
    assertThrows(Exception.class, ()-> Retry.onException(3, attempt -> {
      throw new Exception();
    }));
  }

  @Test
  public void testRetryExceptionOnThrowableFail() {
    assertThrows(Error.class, ()-> Retry.onException(3, attempt -> {
      // should rethrow the Error immediately, as we are only retrying on Exceptions
      if (attempt >= 1) {
        fail();
      }
      throw new AssertionError();
    }));
  }

  @Test
  public void testRetryThrowableSuccess() throws Throwable {
    Retry.onThrowable(3, attempt -> {
      // First 2 tries fail,
      // third and last try succeeds.
      if (attempt < 2) {
        throw new AssertionError();
      }
    });
  }

  @Test
  public void testRetryThrowableOnExceptionSuccess() throws Throwable {
    Retry.onThrowable(3, attempt -> {
      // First 2 tries fail,
      // third and last try succeeds.
      if (attempt < 2) {
        throw new Exception();
      }
    });
  }

  @Test
  public void testRetryThrowableFail() {
    assertThrows(Error.class, ()-> Retry.onThrowable(3, attempt -> {
      // throw Error instead of Exception
      throw new Error();
    }));
  }

  @Test
  public void testRetryThrowableOnExceptionFail() {
    assertThrows(Exception.class, ()-> Retry.onThrowable(3, attempt -> {
      throw new Exception();
    }));
  }
}
