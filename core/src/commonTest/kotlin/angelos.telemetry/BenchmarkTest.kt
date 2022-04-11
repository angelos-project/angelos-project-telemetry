package angelos.telemetry

import kotlin.test.Test
import kotlin.test.assertNotNull

class BenchmarkTest {

    @Test
    fun measure() {
        assertNotNull(Benchmark.measure {
            val data = mutableListOf<String>()
            for(idx in 0..10_000){
                data.add("Number $idx")
            }
        })
    }
}