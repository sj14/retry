[![](https://jitpack.io/v/sj14/retry.svg)](https://jitpack.io/#sj14/retry)

# Introduction

`retry` is a library to retry some specific code when an Exception or an Error is thrown. This might be helpful when dealing with distributed systems and a first call didn't bring success.  
My first attempts in researching a solution for this were not very satisfying. Most times you only could retry the whole method or just use a simple loop. But I wanted something more granular and more extensible without copying the same loops over and over again.  
The closest solution I found was the "enterprisy" Stackoverflow answer by [ach](https://stackoverflow.com/a/13240586) - which lost against the simple loop as the correct answer ;-). This is a slightly enhanced version of the retry functionality with Java 8 compatibility.

# Installation

Include the `Retry` library to your project with [JitPack (Retry)](https://jitpack.io/#sj14/retry).

# Examples

## Exceptions

For **Exceptions** only. Use this in your "normal" application code.

```java
// some code
// ...
Retry.onException(5, attempt -> {
    // Code which will be retried.
    // A test with Assert.assertNull(new Object()) will immediately fail
    // as the assertion throws an Error and not an Exception,
    // but we are calling 'onException' instead of 'onThrowable'.
});
// some more code
// ...
```

With default number of retries (5):

```java
Retry.onException(attempt -> {
    // code
})
```

Whitelisting a specific Exception class. If the specified Exception was raised, it won't retry and immediately rethrow the Exception.

```java
Retry.onException(3, Arrays.asList(ClassNotFoundException.class), attempt -> {
    // code
})
```

## Throwables

For **Throwables** (Exceptions and Errors). You shouldn't use this in "normal" application code, only in tests. Otherwise, it's the same as `Retry.onException(..)`.

```java
// some code
// ...
Retry.onThrowable(5, attempt -> {
    // Code which will be retried.
    // A test with Assert.assertNull(new Object()) will retry
    // the code inside here until no Exception or Error is thrown
    // or the maximum attempts of 5 are exceeded.
});
// some more code
// ...
```
