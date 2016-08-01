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

package uniol.apt.analysis.undirectedcycles;

import java.util.ArrayList;
import java.util.List;

/**
 * Result class that holds cycles.
 */
public class Cycles {

	private final List<List<String>> cycles;

	public Cycles() {
		this.cycles = new ArrayList<>();
	}

	public List<List<String>> getCycles() {
		return cycles;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (List<String> cycle : cycles) {
			sb.append(System.lineSeparator());
			sb.append(cycle.toString());
		}
		return sb.toString();
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
