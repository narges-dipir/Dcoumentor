package app.narges.documentor.core.common.policy

import app.narges.documentor.core.result.ErrorState

fun ErrorState.isRetryable(): Boolean {
    return when (this) {
        ErrorState.NetworkUnavailable,
        ErrorState.Timeout,
        is ErrorState.HttpServer,
        ErrorState.RateLimited,
        is ErrorState.Unknown -> true

        is ErrorState.HttpClient,
        ErrorState.Unauthorized,
        ErrorState.Forbidden,
        ErrorState.NotFound,
        ErrorState.Serialization,
        ErrorState.Database,
        ErrorState.Validation,
        ErrorState.Cancelled -> false
    }
}

object RetryPolicy {
    const val MAX_SERVER_RETRIES: Int = 12
    const val IMMEDIATE_RETRY_COUNT: Int = 5
    const val INCREMENTAL_DELAY_MS: Long = 500L
    const val MAX_DELAY_MS: Long = 5_000L
}

fun incrementalRetryDelayMs(
    retryNumber: Int,
    immediateRetryCount: Int = RetryPolicy.IMMEDIATE_RETRY_COUNT,
    incrementalDelayMs: Long = RetryPolicy.INCREMENTAL_DELAY_MS,
    maxDelayMs: Long = RetryPolicy.MAX_DELAY_MS,
): Long {
    if (retryNumber <= immediateRetryCount) return 0L
    val multiplier = (retryNumber - immediateRetryCount).toLong()
    return (multiplier * incrementalDelayMs).coerceAtMost(maxDelayMs)
}
