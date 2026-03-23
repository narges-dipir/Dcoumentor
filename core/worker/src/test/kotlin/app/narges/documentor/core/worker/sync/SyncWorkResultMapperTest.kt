package app.narges.documentor.core.worker.sync

import androidx.work.ListenableWorker
import app.narges.documentor.core.result.ErrorState
import app.narges.documentor.core.result.ResultState
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncWorkResultMapperTest {

    @Test
    fun `map returns success for success state`() {
        val result = SyncWorkResultMapper.map(ResultState.Success.Data(Unit))

        assertTrue(result is ListenableWorker.Result.Success)
    }

    @Test
    fun `map returns retry for retryable error`() {
        val result = SyncWorkResultMapper.map(
            ResultState.Error(type = ErrorState.NetworkUnavailable, message = "offline"),
        )

        assertTrue(result is ListenableWorker.Result.Retry)
    }

    @Test
    fun `map returns failure for non retryable error`() {
        val result = SyncWorkResultMapper.map(
            ResultState.Error(type = ErrorState.Validation, message = "bad payload"),
        )

        assertTrue(result is ListenableWorker.Result.Failure)
    }
}
