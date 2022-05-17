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

import kotlin.math.absoluteValue

class Counter(private var data: Long = 0): Meter() {

    /**
     * Make a reading of the gauge.
     *
     * @return current measurement
     */
    override fun reading(): Datum = Datum(data)

    /**
     * Increment the count with a certain value.
     *
     * @param value
     */
    fun increment(value: Int) {
        check(value >= 0)
        data += value
    }
}