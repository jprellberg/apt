/*-
 * APT - Analysis of Petri Nets and labeled Transition systems
 * Copyright (C) 2016 Jonas Prellberg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package uniol.apt.analysis.sequences;

/**
 * Simple function interface since Java 7 does not have it. Can be removed once
 * the project upgrades to Java 8.
 *
 * @author Jonas Prellberg
 *
 * @param <T>
 *                argument type
 * @param <U>
 *                result type
 */
public interface Function<T, U> {

	/**
	 * Applies this function to the given argument.
	 *
	 * @param t
	 *                the function argument
	 * @return the function result
	 */
	U apply(T t);

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
