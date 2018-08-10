package bloomfilter

import org.apache.flink.api.java.tuple.Tuple2
import org.junit.Test
import kotlin.test.assertEquals

class CustomMetricSpaceTest {

    // HIer müsste man nochmal prüfen, um die korrektheit der werte zu verstehen, aber ich verstehe nicht so 100% was true matches und was matches machen, da ich beide dateien gleich habe und er trotzdem 0 als antwort gibt
    @Test
    fun testWithNoPivotMetrics(){
        val metric = CustomMetricSpace()
        val results = metric.calculateFilter("src/test/resources/data", PivotFinderType.FFT, 0, 1.0)
        // Matching time cannot be tested
        assertEquals(6, results.first.toInt())
        assertEquals(6, results.second.toInt())
    }

    @Test
    fun testWith2PivotMetrics(){
        val metric = CustomMetricSpace()
        val results = metric.calculateFilter("src/test/resources/data", PivotFinderType.FFT, 2, 1.0)
        assertEquals(6, results.first.toInt())
        assertEquals(6, results.second.toInt())
    }

    @Test
    fun testWith4PivotMetrics(){
        val metric = CustomMetricSpace()
        val results = metric.calculateFilter("src/test/resources/data", PivotFinderType.FFT, 4, 1.0)
        assertEquals(6, results.first.toInt())
        assertEquals(6, results.second.toInt())
    }
}