/**
 * Copyright (c) 2022 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
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
package org.angelos.telemetry

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Benchmark measures performance in comparison to expectations.
 *
 * @constructor Create empty Benchmark
 */
@OptIn(ExperimentalTime::class)
abstract class Benchmark {
    fun test(repetitions: Int, block: () -> Unit): Datum {
        check(repetitions > 0)
        val duration = measureTime {
            repeat(repetitions) {
                block()
            }
        }
        return Datum(duration.inWholeMicroseconds)
    }
}