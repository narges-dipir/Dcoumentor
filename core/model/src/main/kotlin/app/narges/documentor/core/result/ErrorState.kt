package app.narges.documentor.core.result

sealed interface ErrorState {
    data object NetworkUnavailable : ErrorState
    data object Timeout : ErrorState
    data class HttpClient(val code: Int? = null) : ErrorState
    data class HttpServer(val code: Int? = null) : ErrorState
    data object Unauthorized : ErrorState
    data object Forbidden : ErrorState
    data object NotFound : ErrorState
    data object RateLimited : ErrorState
    data object Serialization : ErrorState
    data object Database : ErrorState
    data object Validation : ErrorState
    data object Cancelled : ErrorState
    data class Unknown(val throwable: Throwable? = null) : ErrorState
}
