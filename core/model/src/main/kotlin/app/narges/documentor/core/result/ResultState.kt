package app.narges.documentor.core.result

sealed interface ResultState<out T> {
    data object Loading : ResultState<Nothing>

    sealed interface Success<out T> : ResultState<T> {
        data class Data<T>(
            val value: T,
        ) : Success<T>

        data class Message(
            val message: String,
        ) : Success<Nothing>

        data object Empty : Success<Nothing>
    }

    data class Error(
        val type: ErrorState,
        val message: String? = null,
        val cause: Throwable? = null,
    ) : ResultState<Nothing>
}
