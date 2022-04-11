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

actual class TelemetryCore : Telemetry {
    actual companion object {
        actual fun startUsage(): Long {
            TODO("Not yet implemented")
        }

        actual fun endUsage(usage: Long): Benchmark {
            TODO("Not yet implemented")
        }
    }
}