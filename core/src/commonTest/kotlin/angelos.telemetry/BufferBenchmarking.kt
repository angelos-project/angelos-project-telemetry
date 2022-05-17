/**
 * Copyright (c) 2021-2022 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package angelos.telemetry

import org.angelos.telemetry.Benchmark
import org.angelos.telemetry.Data
import org.angelos.telemetry.Datum
import org.angelos.telemetry.Gauge
import kotlin.random.Random.Default.nextBytes
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.measureTime

enum class PerformanceTest(val test: Int) {
    GET_BYTE(0),
    GET_UBYTE(1),
    GET_CHAR(2),
    GET_SHORT(3),
    GET_USHORT(4),
    GET_INT(5),
    GET_UINT(6),
    GET_LONG(7),
    GET_ULONG(8),
    GET_FLOAT(9),
    GET_DOUBLE(10),
    SET_BYTE(11),
    SET_UBYTE(12),
    SET_CHAR(13),
    SET_SHORT(14),
    SET_USHORT(15),
    SET_INT(16),
    SET_UINT(17),
    SET_LONG(18),
    SET_ULONG(19),
    SET_FLOAT(20),
    SET_DOUBLE(21);

    companion object {
        fun getsAsList() = listOf(
            GET_BYTE, GET_UBYTE, GET_CHAR, GET_SHORT, GET_USHORT,
            GET_INT, GET_UINT, GET_LONG, GET_ULONG, GET_FLOAT, GET_DOUBLE
        )

        fun setsAsList() = listOf(
            SET_BYTE, SET_UBYTE, SET_CHAR, SET_SHORT, SET_USHORT,
            SET_INT, SET_UINT, SET_LONG, SET_ULONG, SET_FLOAT, SET_DOUBLE
        )

        fun allAsList() = getsAsList() + setsAsList()
    }
}

/**
 * Buffer benchmark is a test setup for the sole purpose of benchmarking different data swapping
 * operations for the sake of optimization, and offers an internal standard to compare buffer
 * data speeds. It is important to know which implementation of data copying that is the fastest
 * for each target and of type: native or heap memory.
 *
 * The sake of these tests are not general unit testing, but should be generally ignored by measuring setups.
 *
 * @constructor Create empty Buffer benchmark
 */

class BufferBenchmarking: Benchmark() {
    private val reads = Gauge()
    private val bytes = Gauge()

    private val refElements = 3_000_000
    private val refDataSize = refElements * Buffer.LONG_SIZE
    private val refData = ByteArray(refDataSize).also { nextBytes(it) }

    /**
     * Setup benchmarking by randomly shuffling the reference buffer.
     */
    @BeforeTest
    fun setUp() {
        refData.shuffle()
    }

    fun reverseEndianness(buf: Buffer) = when {
        buf.endian.isBig() -> Endianness.LITTLE_ENDIAN
        buf.endian.isLittle() -> Endianness.BIG_ENDIAN
        else -> error("Only big or little endian will do")
    }

    fun profile(buf: Buffer): Data {
        val dataMap = mutableMapOf<Int, Datum>()
        PerformanceTest.getsAsList().forEach {
            buf.rewind()
            dataMap[it.test] = measureGet(it, buf)}
        if(buf is MutableBuffer)
            PerformanceTest.setsAsList().forEach {
                buf.rewind()
                dataMap[it.test] = measureSet(it, buf)}
        return Data(0, dataMap.toMap())
    }

    fun measureGet(mark: PerformanceTest, buf: Buffer): Data {
        var numLoops: Int = 0
        val time = when(mark) {
            PerformanceTest.GET_BYTE -> test(1) {
                numLoops = refElements / Buffer.BYTE_SIZE
                for (idx in 0..numLoops)
                    buf.getNextByte()
            }
            PerformanceTest.GET_UBYTE -> test(1) {
                numLoops = refElements / Buffer.UBYTE_SIZE
                for (idx in 0..numLoops)
                    buf.getNextUByte()
            }
            PerformanceTest.GET_CHAR -> test(1) {
                numLoops = refElements / Buffer.CHAR_SIZE
                for (idx in 0..numLoops)
                    buf.getNextChar()
            }
            PerformanceTest.GET_SHORT -> test(1) {
                numLoops = refElements / Buffer.SHORT_SIZE
                for (idx in 0..numLoops)
                    buf.getNextShort()
            }
            PerformanceTest.GET_USHORT -> test(1) {
                numLoops = refElements / Buffer.USHORT_SIZE
                for (idx in 0..numLoops)
                    buf.getNextUShort()
            }
            PerformanceTest.GET_INT -> test(1) {
                numLoops = refElements / Buffer.INT_SIZE
                for (idx in 0..numLoops)
                    buf.getNextInt()
            }
            PerformanceTest.GET_UINT -> test(1) {
                numLoops = refElements / Buffer.UINT_SIZE
                for (idx in 0..numLoops)
                    buf.getNextUInt()
            }
            PerformanceTest.GET_LONG -> test(1) {
                numLoops = refElements / Buffer.LONG_SIZE
                for (idx in 0..numLoops)
                    buf.getNextLong()
            }
            PerformanceTest.GET_ULONG -> test(1) {
                numLoops = refElements / Buffer.ULONG_SIZE
                for (idx in 0..numLoops)
                    buf.getNextULong()
            }
            PerformanceTest.GET_FLOAT -> test(1) {
                numLoops = refElements / Buffer.FLOAT_SIZE
                for (idx in 0..numLoops)
                    buf.getNextFloat()
            }
            PerformanceTest.GET_DOUBLE -> test(1) {
                numLoops = refElements / Buffer.DOUBLE_SIZE
                for (idx in 0..numLoops)
                    buf.getNextDouble()
            }
            else -> error("Only GET_* tests")
        }
        return Data(mark.test.toLong(), mapOf(
            0 to time,
            1 to Datum(numLoops.toLong()),
            2 to Datum(buf.position.toLong())
        ))
    }

    fun measureSet(mark: PerformanceTest, buf: MutableBuffer): Data {
        var numLoops: Int = 0
        val time = when(mark) {
            PerformanceTest.SET_BYTE -> test(1) {
                numLoops = refElements / Buffer.BYTE_SIZE
                for (idx in 0..numLoops)
                    buf.setNextByte(-54)
            }
            PerformanceTest.SET_UBYTE -> test(1) {
                numLoops = refElements / Buffer.UBYTE_SIZE
                for (idx in 0..numLoops)
                    buf.setNextUByte(216u)
            }
            PerformanceTest.SET_CHAR -> test(1) {
                numLoops = refElements / Buffer.CHAR_SIZE
                for (idx in 0..numLoops)
                    buf.setNextChar('ö')
            }
            PerformanceTest.SET_SHORT -> test(1) {
                numLoops = refElements / Buffer.SHORT_SIZE
                for (idx in 0..numLoops)
                    buf.setNextShort(-14_543)
            }
            PerformanceTest.SET_USHORT -> test(1) {
                numLoops = refElements / Buffer.USHORT_SIZE
                for (idx in 0..numLoops)
                    buf.setNextUShort(45_603u)
            }
            PerformanceTest.SET_INT -> test(1) {
                numLoops = refElements / Buffer.INT_SIZE
                for (idx in 0..numLoops)
                    buf.setNextInt(2_134_504_956)
            }
            PerformanceTest.SET_UINT -> test(1) {
                numLoops = refElements / Buffer.UINT_SIZE
                for (idx in 0..numLoops)
                    buf.setNextUInt(3_543_234_876u)
            }
            PerformanceTest.SET_LONG -> test(1) {
                numLoops = refElements / Buffer.LONG_SIZE
                for (idx in 0..numLoops)
                    buf.setNextLong(-342_598_345_743_509_723L)
            }
            PerformanceTest.SET_ULONG -> test(1) {
                numLoops = refElements / Buffer.ULONG_SIZE
                for (idx in 0..numLoops)
                    buf.setNextULong(4_502_348_923_032_498_475uL)
            }
            PerformanceTest.SET_FLOAT -> test(1) {
                numLoops = refElements / Buffer.FLOAT_SIZE
                for (idx in 0..numLoops)
                    buf.setNextFloat(0.4359685F)
            }
            PerformanceTest.SET_DOUBLE -> test(1) {
                numLoops = refElements / Buffer.DOUBLE_SIZE
                for (idx in 0..numLoops)
                    buf.setNextDouble(-0.892384774029876)
            }
            else -> error("Only SET_* tests")
        }
        return Data(mark.test.toLong(), mapOf(
            0 to time,
            1 to Datum(numLoops.toLong()),
            2 to Datum(buf.position.toLong())
        ))
    }

    fun printSimpleReport(data: Data, rdata: Data) {
        println("| Ops | Mb / s | M Calls / s |")
        println("| :--- | ---: | ---: |")

        for (mark in PerformanceTest.allAsList()) {
            val m = (data.points[mark.test] as Data).points
            val t: Float = 1_000_000.0F / m[0]!!.measure
            println("| N-$mark | ${m[2]!!.measure * t / 1_000_000.0F} | ${m[1]!!.measure * t / 1_000_000.0F} |")
        }
        for (mark in PerformanceTest.allAsList()){
            val m = (data.points[mark.test] as Data).points
            val t: Float = 1_000_000.0F / m[0]!!.measure
            println("| R-$mark | ${m[2]!!.measure * t / 1_000_000.0F} | ${m[1]!!.measure * t / 1_000_000.0F} |")
        }
    }

    fun printCompoundReport(info: List<Triple<String, Data, Data>>): String {
        val output = StringBuilder()

        output.append("| Ops | Mb/s | Mio/s |")
        for (num in 0 until info.size-1)
            output.append(" X | Mb/s | Mio/s |")
        output.appendLine()

        output.append("| :--- | ---: | ---: |")
        for (num in 0 until info.size-1)
            output.append(" :---: | ---: | ---: |")
        output.appendLine()

        for (mark in PerformanceTest.allAsList()) {
            output.append("| P-$mark |")
            var m = (info[0].second.points[mark.test] as Data).points
            var t: Float = 1_000_000.0F / m[0]!!.measure
            val p = m[2]!!.measure * t / 1_000_000.0F
            output.append(" $p | ${m[1]!!.measure * t / 1_000_000.0F} |")

            for (r in 1 until info.size) {
                if(info[r].second.points[mark.test] == null){
                    output.append(" - | - | - |")
                } else {
                    m = (info[r].second.points[mark.test] as Data).points
                    t = 1_000_000.0F / m[0]!!.measure
                    val p2 = m[2]!!.measure * t / 1_000_000.0F
                    output.append(" ${p2 / p} | $p2 | ${m[1]!!.measure * t / 1_000_000.0F} |")
                }
            }
            output.appendLine()
        }

        for (mark in PerformanceTest.allAsList()) {
            output.append("| R-$mark |")
            var m = (info[0].third.points[mark.test] as Data).points
            var t: Float = 1_000_000.0F / m[0]!!.measure
            val p = m[2]!!.measure * t / 1_000_000.0F
            output.append(" $p | ${m[1]!!.measure * t / 1_000_000.0F} |")

            for (r in 1 until info.size) {
                if(info[r].third.points[mark.test] == null) {
                    output.append(" - | - | - |")
                } else {
                    m = (info[r].third.points[mark.test] as Data).points
                    t = 1_000_000.0F / m[0]!!.measure
                    val p2 = m[2]!!.measure * t / 1_000_000.0F
                    output.append(" ${p2 / p} | $p2 | ${m[1]!!.measure * t / 1_000_000.0F} |")
                }
            }
            output.appendLine()
        }
        return output.toString()
    }

    fun runBuffer(buf: Buffer): Triple<String, Data, Data> {
        val nd = profile(buf)
        buf.endian = reverseEndianness(buf)
        val rd = profile(buf)
        //return Triple(buf::class.qualifiedName.toString(), nd, rd)
        return Triple("buf::class.qualifiedName.toString()", nd, rd)
    }

    @Test
    fun runBenchmark() {
        val info = mutableListOf<Triple<String, Data, Data>>()
        info.add(runBuffer(refMutableBufferOf(refData.copyOf())))
        info.add(runBuffer(byteBufferOf(refData.copyOf())))
        println(info)
        println(printCompoundReport(info))
    }

    @Test
    fun runArrayLoopSpeed(){
        var duration: Duration
        val to = ByteArray(refDataSize)

        duration = profile {
            for(idx in 0 until refDataSize)
                to[idx] = refData[idx]
        }
        println("ByteArray with for-loop: $duration")

        duration = profile {
            refData.copyInto(to)
        }
        println("ByteArray with copyInto: $duration")

        duration = profile {
            var idx = 0
            while(idx < refDataSize) {
                to[idx] = refData[idx]
                idx += 1
            }
        }
        println("ByteArray with while-loop: $duration")
    }

    @Test
    fun runByteBufferReadSpeed() {
        var duration: Duration
        val ref = refMutableBufferOf(refData.copyOf())
        val buf = byteBufferOf(refData.copyOf())

        duration = profile {
            for(idx in 0 until ref.size / Buffer.ULONG_SIZE)
                ref.getNextULong()
            ref.rewind()
        }
        println("ReferenceMutableBuffer.getNextULong(): $duration")

        duration = profile {
            for(idx in 0 until buf.size / Buffer.ULONG_SIZE)
                buf.getNextULong()
            buf.rewind()
        }
        println("ByteBuffer.getNextULong(): $duration")

        /*
                duration = measureGet(PerformanceTest.GET_ULONG, ref).points[0]!!.measure
        println("ReferenceMutableBuffer.getNextULong(): $duration")

        duration = measureGet(PerformanceTest.GET_ULONG, buf).points[0]!!.measure
        println("ByteBuffer.getNextULong(): $duration")
         */
    }

    companion object {
        /**
         * Buffer reference size.
         */
        @OptIn(ExperimentalTime::class)
        fun profile(mark: () -> Unit): Duration {
            return TimeSource.Monotonic.measureTime {
                for (idx in 0 until 10)
                    mark()
            }
        }
    }
}

/**
> Task :buffer:jsLegacyNodeTest
ByteArray with copyInto: 10.664247ms
ByteArray with for-loop: 100.255529ms
ByteArray with while-loop: 102.860198ms

> Task :buffer:jvmTest
ByteArray with copyInto: 9.079690ms
ByteArray with for-loop: 37.620716ms
ByteArray with while-loop: 23.020329ms

> Task :buffer:nativeTest
ByteArray with copyInto: 10.540988ms
ByteArray with for-loop: 980.785599ms
ByteArray with while-loop: 1.186091390s
 */

/**
> Task :buffer:jvmTest
ReferenceMutableBuffer.getNextULong(): 101.085030ms
ByteBuffer.getNextULong(): 134.790772ms

> Task :buffer:nativeTest
ReferenceMutableBuffer.getNextULong(): 8.840421552s
ByteBuffer.getNextULong(): 8.799817260s
 */