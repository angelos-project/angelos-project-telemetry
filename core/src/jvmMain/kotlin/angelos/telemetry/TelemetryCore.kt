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
package angelos.telemetry

import java.lang.System

actual class TelemetryCore : Telemetry {
    actual companion object{
        init {
            System.loadLibrary("jni-telemetry")
        }

        actual fun startUsage(): Long = start_usage()

        @JvmStatic
        private external fun start_usage(): Long

        actual fun endUsage(usage: Long): Benchmark = end_usage(usage)

        @JvmStatic
        private external fun end_usage(start: Long): Benchmark
    }
}