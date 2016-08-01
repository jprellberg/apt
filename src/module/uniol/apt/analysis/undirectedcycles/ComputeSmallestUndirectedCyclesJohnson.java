/*-
 * APT - Analysis of Petri Nets and labeled Transition systems
 * Copyright (C) 2015 vsp
 *               2016 Jonas Prellberg
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.util.Pair;

/**
 * Compute smallest cycles or cycles which do not contain any state twice while
 * disregarding edge directions using a modified version of Johnson's algorithm.
 *
 * <p>This implementation is based on
 * "Finding All the Elementary Circuits of a Directed Graph" from Donald B.
 * Johnson in SIAM J. Comput., 4(1), 77–84. (8 pages) (DOI: 10.1137/0204007).
 *
 * @author vsp, Jonas Prellberg
 */
class ComputeSmallestUndirectedCyclesJohnson {

	/**
	 * Returns the adjacencies lists of all states greater* than the given
	 * minState. Greater* is defined by the arbitrary state ordering imposed
	 * by the given list of states.
	 */
	private List<Collection<Pair<String, Integer>>> constructAdjacencies(TransitionSystem ts, List<State> states,
			int minState) {
		List<Collection<Pair<String, Integer>>> adjacencies = new ArrayList<>();
		for (int i = 0; i < states.size(); i++) {
			adjacencies.add(new ArrayList<Pair<String, Integer>>());
		}
		for (int i = minState; i < states.size(); i++) {
			State curr = states.get(i);
			Set<Arc> postset = new HashSet<>();
			postset.addAll(curr.getPostsetEdges());
			postset.addAll(curr.getPresetEdges());
			for (Arc arc : postset) {
				Integer target;
				if (arc.getTarget().equals(curr)) {
					target = states.indexOf(arc.getSource());
				} else {
					target = states.indexOf(arc.getTarget());
				}
				assert target != -1;
				adjacencies.get(i).add(new Pair<>(arc.getLabel(), target));
			}
		}
		return adjacencies;
	}

	/**
	 * Do a DFS for circles going through s. We are currently in state
	 * 'state'. This is called CIRCUIT() in the paper.
	 */
	static private class DoDfs {
		private final List<Collection<Pair<String, Integer>>> adjacencies;
		private final List<State> states;
		private final int s;
		private final Deque<String> sStack;
		private final boolean[] blocked;
		private final List<Set<Integer>> b;
		private final Cycles cycles;

		public DoDfs(List<Collection<Pair<String, Integer>>> adjacencies, List<State> states, int s,
				Cycles cycles) {
			this.adjacencies = adjacencies;
			this.states = states;
			this.s = s;
			this.cycles = cycles;

			this.blocked = new boolean[states.size()];
			Arrays.fill(blocked, s, states.size(), false);

			this.b = new ArrayList<>();
			for (int i = 0; i < states.size(); i++) {
				if (i >= s)
					b.add(new HashSet<Integer>());
				else
					// Not part of the graph, should not be
					// accessed
					b.add(null);
			}

			this.sStack = new ArrayDeque<>();

			doDfs(s);

			assert sStack.isEmpty();
		}

		private boolean doDfs(int state) {
			boolean foundCycle = false;

			blocked[state] = true;
			sStack.addLast(states.get(state).getId());
			for (Pair<String, Integer> arc : adjacencies.get(state)) {
				if (arc.getSecond() == s) {
					// cycle found
					List<String> sCycle = new ArrayList<>(sStack);
					cycles.getCycles().add(sCycle);
					foundCycle = true;
				} else if (!blocked[arc.getSecond()]) {
					foundCycle |= doDfs(arc.getSecond());
				}
			}
			sStack.removeLast();

			if (foundCycle) {
				unblock(state, blocked, b);
			} else {
				for (Pair<String, Integer> arc : adjacencies.get(state)) {
					b.get(state).add(arc.getSecond());
				}
			}

			return foundCycle;
		}
	}

	static private void unblock(int state, boolean[] blocked, List<Set<Integer>> b) {
		blocked[state] = false;
		for (Integer prev : b.get(state)) {
			if (blocked[prev])
				unblock(prev, blocked, b);
		}
		b.set(state, new HashSet<Integer>());
	}

	public Cycles getCycles(TransitionSystem ts) {
		List<State> states = new ArrayList<>(ts.getNodes());
		Cycles cycles = new Cycles();

		int s = 0;
		while (s < states.size()) {
			List<Collection<Pair<String, Integer>>> adjacencies = constructAdjacencies(ts, states, s);

			// As a side effect, this adds cycles through s to
			// 'cycles'
			new DoDfs(adjacencies, states, s, cycles);
			s++;
		}

		return cycles;
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
