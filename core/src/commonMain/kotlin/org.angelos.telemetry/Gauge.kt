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

/**
 * Gauge is a meter for a measuring point.
 *
 * @constructor Create empty Gauge
 */
class Gauge(private var data: Long = 0): Meter() {

    /**
     * Make a reading of the gauge.
     *
     * @return current measurement
     */
    override fun reading(): Datum = Datum(data)

    /**
     * Read the count of the gauge and reset to zero.
     *
     * @return current measurement
     */
    fun count(): Datum {
        val reading = reading()
        reset()
        return reading
    }

    /**
     * Updates the gauge by adding a value.
     *
     * @param value
     */
    fun update(value: Int) {
        data += value
    }

    /**
     * Adjusts the gauge by changing to a new value.
     *
     * @param value
     */
    fun adjust(value: Long) {
        data = value
    }

    /**
     * Resets the gauge.
     *
     * @return
     */
    fun reset() {
        adjust(0)
    }
}