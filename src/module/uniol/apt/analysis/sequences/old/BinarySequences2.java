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

package uniol.apt.analysis.sequences.old;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uniol.apt.adt.exception.StructureException;
import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.util.Pair;

/**
 * Checks a LTS for PN solvability by checking subsequences of the LTS against a
 * pattern.
 */
public class BinarySequences2 {

	private final TransitionSystem transitionSystem;
	private final int visitLimit;

	public BinarySequences2(TransitionSystem transitionSystem, int visitLimit) {
		this.transitionSystem = transitionSystem;
		this.visitLimit = visitLimit;
	}

	public void run() {
		List<String> alphabet = new ArrayList<>(transitionSystem.getAlphabet());
		for (int i = 0; i < alphabet.size(); i++) {
			for (int j = i + 1; j < alphabet.size(); j++) {
				TransitionSystem ts = new TransitionSystem(transitionSystem);
				String a = alphabet.get(i);
				String b = alphabet.get(j);
				run(ts, a, b);
			}
		}
	}

	private void run(TransitionSystem ts, String a, String b) {
		System.out.printf("Running on sub-lts with alphabet {%s, %s}%n", a, b);
		Set<State> origins = new HashSet<>();

		// Add states that became origins by removing states (and their outgoing arcs)
		origins.addAll(removeStates(ts, a, b));

		// Add states that became origins by removing arcs not labeled with {a, b}
		origins.addAll(removeArcs(ts, a, b));

		keepSourcesRemoveSinks(origins);

		// Add initial state unless it was removed or has no more
		// outgoing arcs
		try {
			// LTS still has s0.
			State s0 = ts.getInitialState();
			if (!s0.getPostsetEdges().isEmpty()) {
				origins.add(s0);
			}
		} catch (StructureException e) {
			// Do not add s0 if it does not exist.
		}

		// Perform DFS
		for (State origin : origins) {
			dfs(origin);
		}
	}

	private List<State> removeStates(TransitionSystem ts, String a, String b) {
		List<State> origins = new ArrayList<>();
		List<State> toRemove = new ArrayList<>();
		for (State s : ts.getNodes()) {
			if (hasMultipleOutgoingArcsWithLabels(s, a, b)) {
				toRemove.add(s);
				origins.addAll(s.getPostsetNodes());
			}
		}
		for (State s : toRemove) {
			ts.removeState(s);
		}
		// Make sure that no removed nodes are part of origins
		// This can happen due to iteration order
		origins.removeAll(toRemove);
		return origins;
	}

	private boolean hasMultipleOutgoingArcsWithLabels(State s, String a, String b) {
		int count = 0;
		for (Arc arc : s.getPostsetEdges()) {
			if (arc.getLabel().equals(a) || arc.getLabel().equals(b)) {
				count += 1;
				if (count > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private List<State> removeArcs(TransitionSystem ts, String a, String b) {
		List<State> origins = new ArrayList<>();
		List<Arc> toRemove = new ArrayList<>();
		for (Arc arc : ts.getEdges()) {
			if (!arc.getLabel().equals(a) && !arc.getLabel().equals(b)) {
				toRemove.add(arc);
				origins.add(arc.getTarget());
			}
		}
		for (Arc arc : toRemove) {
			ts.removeArc(arc);
		}
		return origins;
	}

	private void keepSourcesRemoveSinks(Set<State> newOrigins) {
		List<State> toRemove = new ArrayList<>();
		for (State s : newOrigins) {
			if (!s.getPresetEdges().isEmpty() || s.getPostsetEdges().isEmpty()) {
				toRemove.add(s);
			}
		}
		newOrigins.removeAll(toRemove);
	}

	private void dfs(State origin) {
		Deque<Pair<State, List<State>>> stack = new LinkedList<>();
		stack.push(pair(origin));
		while (!stack.isEmpty()) {
			Pair<State, List<State>> curr = stack.pop();
			State s = curr.getFirst();
			List<State> p = curr.getSecond();
			incVisitCount(s);
			if (s.getPostsetEdges().isEmpty()) {
				yield(p);
			} else {
				for (State post : s.getPostsetNodes()) {
					if (getVisitCount(post) <= visitLimit) {
						List<State> newPath = new ArrayList<>(p);
						newPath.add(post);
						stack.push(pair(post, newPath));
					} else {
						yield(p);
					}
				}
			}
		}
	}

	private static final String EXT_KEY_VISIT_COUNT = "VISIT_COUNT";

	private int getVisitCount(State s) {
		if (s.hasExtension(EXT_KEY_VISIT_COUNT)) {
			return (Integer) s.getExtension(EXT_KEY_VISIT_COUNT);
		} else {
			return 0;
		}
	}

	private void incVisitCount(State s) {
		if (s.hasExtension(EXT_KEY_VISIT_COUNT)) {
			Integer count = (Integer) s.getExtension(EXT_KEY_VISIT_COUNT);
			s.putExtension(EXT_KEY_VISIT_COUNT, count + 1);

		} else {
			s.putExtension(EXT_KEY_VISIT_COUNT, 1);
		}
	}

	private Pair<State, List<State>> pair(State state) {
		List<State> path = new ArrayList<State>();
		path.add(state);
		return new Pair<State, List<State>>(state, path);
	}

	private Pair<State, List<State>> pair(State state, List<State> path) {
		return new Pair<State, List<State>>(state, path);
	}

	private void yield(List<State> path) {
		System.out.print(">>>> ");
		for (State s : path) {
			System.out.print(s.getId() + " ");
		}
		System.out.println();
	}

	public boolean isSolvable() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getUnsolvableSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUnsolvableSequenceStates() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		TransitionSystem ts = new TransitionSystem();

		State s0 = ts.createState();
		State s1 = ts.createState();
		State s2 = ts.createState();
		State s3 = ts.createState();
		State s4 = ts.createState();

		ts.setInitialState(s0);

		ts.createArc(s0, s1, "a");
		ts.createArc(s0, s2, "a");
		ts.createArc(s1, s2, "b");
		ts.createArc(s1, s3, "c");
		ts.createArc(s2, s0, "a");
		ts.createArc(s3, s4, "d");

		BinarySequences2 bs = new BinarySequences2(ts, 4);
		bs.run();
	}

//	 public static void main(String[] args) {
//	 TransitionSystem ts = new TransitionSystem();
//
//	 State s0 = ts.createState();
//	 State s1 = ts.createState();
//	 State s2 = ts.createState();
//	 State s3 = ts.createState();
//	 State s4 = ts.createState();
//	 State s5 = ts.createState();
//	 State s6 = ts.createState();
//
//	 ts.setInitialState(s0);
//
//	 ts.createArc(s0, s1, "b");
//	 ts.createArc(s0, s4, "c");
//	 ts.createArc(s0, s6, "a");
//	 ts.createArc(s1, s2, "a");
//	 ts.createArc(s2, s3, "a");
//	 ts.createArc(s4, s5, "d");
//	 ts.createArc(s5, s3, "c");
//
//	 BinarySequences2 bs = new BinarySequences2(ts, 10);
//	 bs.run();
//	 }

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
