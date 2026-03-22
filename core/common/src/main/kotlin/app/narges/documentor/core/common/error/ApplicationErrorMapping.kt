package app.narges.documentor.core.common.error

import app.narges.documentor.core.result.ErrorState
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

fun Throwable.toAppError(): ErrorState = when (this) {
    is SocketTimeoutException -> ErrorState.Timeout
    is IOException -> ErrorState.NetworkUnavailable
    is HttpException -> code().toHttpError()
    is CancellationException -> ErrorState.Cancelled
    else -> ErrorState.Unknown(this)
}

fun Int.toHttpError(): ErrorState = when (this) {
    401 -> ErrorState.Unauthorized
    403 -> ErrorState.Forbidden
    404 -> ErrorState.NotFound
    429 -> ErrorState.RateLimited
    in 400..499 -> ErrorState.HttpClient(this)
    in 500..599 -> ErrorState.HttpServer(this)
    else -> ErrorState.Unknown()
}
