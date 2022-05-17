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
 * A quantifiable that can be measured with takeMeasurement(). Measurements may be organized hierarchically.
 * An object that should be gauged for telemetry must implement this.
 *
 * @constructor Create empty Quantifiable
 */
interface Quantifiable {

    /**
     * Take measurement of all meters.
     *
     * @return data
     */
    fun takeMeasurement(): Data
}