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
 * Meter is an abstract base class for every thing measurable for telemetry.
 *
 * @constructor Create empty Meter
 */
abstract class Meter {

    /**
     * Reading a datum as a measurement from the meter, gauge, counter, record, state, etc.
     *
     * @return datum measurement
     */
    abstract fun reading(): Datum
}