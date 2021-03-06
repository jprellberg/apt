/*-
 * APT - Analysis of Petri Nets and labeled Transition systems
 * Copyright (C) 2012-2013  Members of the project group APT
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

package uniol.apt.analysis.cycles.lts;

/**
 * Factory for {@link ComputeSmallestCycles} instances
 * @author vsp, Uli Schlachter
 */
public enum ComputeSmallestCyclesAlgorithms {
	FLOYD_WARSHALL {
		@Override
		public ComputeSmallestCycles getInstance() {
			return new ComputeSmallestCyclesFloydWarshall();
		}
	},
	DFS {
		@Override
		public ComputeSmallestCycles getInstance() {
			return new ComputeSmallestCyclesDFS();
		}
	},
	JOHNSON {
		@Override
		public ComputeSmallestCycles getInstance() {
			return new ComputeSmallestCyclesJohnson();
		}
	};

	public abstract ComputeSmallestCycles getInstance();

	/**
	 * Get the default algorithm.
	 * @return The default algorithm
	 */
	public static ComputeSmallestCyclesAlgorithms getDefaultAlgorithm() {
		return JOHNSON;
	}

	/**
	 * Get a description of the meaning of the char parameter
	 * @return description
	 */
	public static String getAlgorithmCharDescription() {
		return "Select the algorithm to use. Possible options are"
			+ " a depth-first search algorithm ('dfs');"
			+ " an adapted Floyd-Warshall algorithm ('floyd_warshall');"
			+ " Johnson's algorithm ('johnson')";
	}
}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
