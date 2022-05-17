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
 * Record is an initial data that can be measured.
 *
 * @param T state data type
 * @property value the state value
 * @constructor Create empty State
 */
class Record<T: Any>(val value: T): Meter() {

    override fun reading(): Datum {
        return Datum(value.hashCode().toLong())
    }
}