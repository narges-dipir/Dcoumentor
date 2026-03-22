package app.narges.documentor.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class DocumentorMacrobenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupColdAndWarm() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.COLD,
            setupBlock = { pressHome() },
        ) {
            startActivityAndWait()
        }

        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(StartupTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            setupBlock = { pressHome() },
        ) {
            startActivityAndWait()
        }
    }

    @Test
    fun listScrollAndPagination() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), TraceSectionMetric("list_scroll_pagination")),
            iterations = 5,
            setupBlock = {
                startActivityAndWait()
            },
        ) {
            device.waitForIdle()
            scrollList()
            clickTextIfExists("Load More")
            device.waitForIdle()
            scrollList()
        }
    }

    @Test
    fun openCountAndSave() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), TraceSectionMetric("open_count_save")),
            iterations = 5,
            setupBlock = {
                startActivityAndWait()
            },
        ) {
            clickTextIfExists("Red Apple")
            setTextAt(0, "77")
            clickTextIfExists("save")
            device.waitForIdle()
        }
    }

    @Test
    fun createArticleAndSave() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), TraceSectionMetric("create_article_save")),
            iterations = 5,
            setupBlock = {
                startActivityAndWait()
            },
        ) {
            clickTextIfExists("+")
            val articleNumber = ARTICLE_COUNTER.incrementAndGet().toString()
            setTextAt(0, "Benchmark $articleNumber")
            setTextAt(1, articleNumber)
            clickTextIfExists("save")
            device.waitForIdle()
        }
    }

    @Test
    fun resumeListFlow() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), TraceSectionMetric("resume_list")),
            iterations = 5,
            setupBlock = {
                startActivityAndWait()
            },
        ) {
            clickTextIfExists("+")
            device.pressBack()
            device.waitForIdle()
            pressHome()
            startActivityAndWait()
            device.waitForIdle()
        }
    }

    @Test
    fun errorRetryPath() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric(), TraceSectionMetric("error_retry")),
            iterations = 5,
            setupBlock = {
                startActivityAndWait()
            },
        ) {
            val retryClicked = clickTextIfExists("Retry")
            if (!retryClicked) {
                clickTextIfExists("+")
                setTextAt(0, "Red Apple")
                setTextAt(1, "1000001")
                clickTextIfExists("save")
                device.pressBack()
            }
            device.waitForIdle()
        }
    }

    private fun MacrobenchmarkScope.clickTextIfExists(text: String): Boolean {
        val node = device.wait(Until.findObject(By.text(text)), FIND_TIMEOUT_MS) ?: return false
        node.click()
        device.waitForIdle()
        return true
    }

    private fun MacrobenchmarkScope.setTextAt(index: Int, value: String) {
        val fields = device.findObjects(By.clazz("android.widget.EditText"))
        if (index < fields.size) {
            fields[index].text = value
            device.waitForIdle()
        }
    }

    private fun MacrobenchmarkScope.scrollList() {
        val list = device.findObject(By.scrollable(true))
        if (list != null) {
            list.scroll(Direction.DOWN, 1.0f)
        } else {
            val width = device.displayWidth
            val height = device.displayHeight
            device.swipe(width / 2, (height * 0.8).toInt(), width / 2, (height * 0.2).toInt(), 20)
        }
        device.waitForIdle()
    }

    private companion object {
        const val PACKAGE_NAME = "app.narges.documentor"
        const val FIND_TIMEOUT_MS = 3_000L
        val ARTICLE_COUNTER = AtomicInteger(8_000_000)
    }
}
