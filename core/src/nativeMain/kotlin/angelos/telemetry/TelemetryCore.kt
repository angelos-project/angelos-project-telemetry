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

import kotlinx.cinterop.*
import platform.posix.RUSAGE_SELF
import platform.posix.free
import platform.posix.getrusage
import platform.posix.rusage

actual class TelemetryCore : Telemetry {
    actual companion object {
        actual fun startUsage(): Long = memScoped {
            val usage = nativeHeap.alloc<rusage>()
            getrusage(RUSAGE_SELF, usage.ptr)
            return usage.ptr.toLong()
        }

        actual fun endUsage(usage: Long): Benchmark = memScoped {
            val end = nativeHeap.alloc<rusage>()
            getrusage(RUSAGE_SELF, end.ptr)
            val start = usage.toCPointer<rusage>()!!.pointed
            val bm = Benchmark((((
                    end.ru_utime.tv_sec * 1000000 + end.ru_utime.tv_usec) - (
                    start.ru_utime.tv_sec * 1000000 + start.ru_utime.tv_usec)) + ((
                    end.ru_stime.tv_sec * 1000000 + end.ru_stime.tv_usec) - (
                    start.ru_stime.tv_sec * 1000000 + start.ru_stime.tv_usec))),
                end.ru_maxrss - start.ru_maxrss,
                end.ru_inblock - start.ru_inblock,
                end.ru_oublock - start.ru_oublock
            )
            free(start.ptr)
            free(end.ptr)
            return bm
        }
    }
}