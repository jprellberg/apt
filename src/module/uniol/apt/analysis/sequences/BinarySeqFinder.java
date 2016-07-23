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

package uniol.apt.analysis.sequences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.analysis.connectivity.Connectivity;

/**
 * Finds maximal binary subsequences in a LTS. The can either be returned as a
 * whole or a listener can be called as soon as each sequence is found. This is
 * useful for short-circuiting applications, i.e. as soon as a bad sequence
 * matching some pattern is found the process can be aborted.
 *
 * @author Jonas Prellberg
 */
public class BinarySeqFinder {

	private List<BinarySeqExpression> sequences;
	private Set<String> uniqueSequences;
	private Function<BinarySeqExpression, Boolean> sequenceConsumer;
	private boolean limitResult;

	/**
	 * Shortcut for {@code BinarySeqFinder(true)}.
	 *
	 * @see #BinarySeqFinder(boolean)
	 */
	public BinarySeqFinder() {
		this(true);
	}

	/**
	 * Creates a new {@link BinarySeqFinder}. Depending on the boolean
	 * parameter the returned results when calling one of the
	 * {@code getSequence} methods will be different. If set to true the
	 * result will only contain sequences that contain at least two letters
	 * and whose corresponding regular expressions (normalized to labels a
	 * and b) are unique, i.e. the following sequences would be equal to
	 * each other and therefore only one of them would be in the result:
	 *
	 * <pre>
	 * s0 --a-> s1 --b-> s2
	 * s0 --a-> s1 --c-> s2
	 * s0 --c-> s1 --a-> s2
	 * s3 --a-> s4 --c-> s5
	 * </pre>
	 *
	 * @param limitResult
	 *                if true the result is limited to structurally unique
	 *                sequences with at least two letters
	 */
	public BinarySeqFinder(boolean limitResult) {
		this.limitResult = limitResult;
		this.uniqueSequences = new HashSet<>();
	}

	/**
	 * Starts the sequence search algorithm on the given LTS and collects
	 * the results in a list. Please note that the result list may contain
	 * duplicates under certain circumstances. These duplicates belong to
	 * different modified copies of the transition system and therefore do
	 * not equal each other from a Java perspective.
	 *
	 * @param input
	 *                input transition system
	 * @return all found maximal binary sequences, possibly with duplicates
	 */
	public List<BinarySeqExpression> getSequences(TransitionSystem input) {
		sequences = new ArrayList<>();
		run(input);
		return sequences;
	}

	/**
	 * Starts the sequence search algorithm on the given LTS and calls the
	 * consumer function each time a maximal binary sequence is found. If
	 * the consumer function returns false the search is aborted. Please
	 * note that results may be given to the consumer multiple times on
	 * certain circumstances.
	 *
	 * @param input
	 *                input transition system
	 * @param seqConsumer
	 *                consumer for found sequences
	 */
	public void getSequences(TransitionSystem input, Function<BinarySeqExpression, Boolean> seqConsumer) {
		sequenceConsumer = seqConsumer;
		run(input);
	}

	/**
	 * Calls the main algorithm with all label pair combinations.
	 *
	 * @param transitionSystem
	 *                input transition system
	 */
	private void run(TransitionSystem transitionSystem) {
		List<String> alphabet = new ArrayList<>(transitionSystem.getAlphabet());
		for (int i = 0; i < alphabet.size(); i++) {
			for (int j = i + 1; j < alphabet.size(); j++) {
				try {
					String a = alphabet.get(i);
					String b = alphabet.get(j);
					// Run will be called for all {a, b}
					// combinations
					run(transitionSystem, a, b);
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	/**
	 * Main algorithm. The approach is to modify the LTS so that only edges
	 * with labels a or b remain and remove all states with more than one
	 * outgoing edge. Then strongly connected components are used to
	 * identify paths and cycles. The root components that have no incoming
	 * edges are used as starting points for path generation.
	 *
	 * @param ts
	 *                input LTS
	 * @param a
	 *                first label
	 * @param b
	 *                second label
	 * @throws InterruptedException
	 *                 thrown if requested by consumer function
	 */
	private void run(TransitionSystem transitionSystem, String a, String b) throws InterruptedException {
		log("Searching LTS limited to {%s, %s}%n", a, b);

		// Copy transition system so we can modify it without affecting
		// the input
		TransitionSystem ts = new TransitionSystem(transitionSystem);

		/*
		 * Modify the LTS to be (S', →, T, ?). S' is S without all
		 * states that have more than one outgoing {a, b} arc. This is
		 * because this algorithm looks for binary paths over {a, b} in
		 * the LTS where any state that is part of the path may not have
		 * an {a, b} arc to a state that is not part of the path. The
		 * removed states therefore could not have been part of a path
		 * anyways. Be aware that S' could possibly not contain s0.
		 */
		removeStates(ts, a, b);

		/*
		 * Modify the LTS to be (S', →', {a, b}, ?). →' is → keeping
		 * only the arcs labeled with {a, b} that are connected to
		 * states in S'
		 */
		removeArcs(ts, a, b);

		/*
		 * The modified transition system is now LTS' = (S', →', {a, b},
		 * ?). Note that s0 may not actually exist in S' anymore but
		 * this does not matter for the remaining algorithm. LTS'
		 * contains only paths and cycles because there are no cycles in
		 * LTS' that contain a branch leaving the cycle. This is because
		 * LTS' has only labels {a, b} and all states that have more
		 * than one outgoing arc were removed.
		 *
		 * Compute strongly connected components of LTS'. Nodes on a
		 * path will end up alone in a component while cycles will end
		 * up in their own components. SSC is a set of sets of nodes.
		 */
		Set<? extends Set<State>> ssc = Connectivity.getStronglyConnectedComponents(ts);

		// Find all SSCs that are cycles.
		Set<Set<State>> sscCycles = findCycleComponents(ssc);

		// Find all SSCs that have no incoming arcs because only maximal
		// paths are interesting.
		Set<Set<State>> sscRoots = findRootComponents(ssc);

		// Find paths as regular expressions r = vw* with ¬(|v| = 0 and
		// |w| = 0) starting from ROOTS
		for (Set<State> component : sscRoots) {
			BinarySeqExpression binseq;
			if (component.size() == 1) {
				// This component is a single state that is the
				// starting point of a path.
				binseq = findFromPathComponent(component, sscCycles);
			} else {
				/*
				 * |C| > 1. This component is a cycle and also
				 * has no branches to leave it. Pick random
				 * state from C and follow the path until all
				 * states have been visited.
				 */
				binseq = findFromCycleComponent(component);
			}
			/*
			 * Output sequence unless it's empty or if limitResult
			 * is set if it is not a single letter word.
			 */
			if ((!limitResult && !binseq.isEmpty())
					|| (limitResult && !binseq.isEmptyOrSingleLetterWord())) {
				yield(binseq);
			}
		}
	}

	/**
	 * Given a cycle component create a {@link BinarySeqExpression}
	 * representing that cycle. A cycle component has no outgoing edges to
	 * another component.
	 *
	 * @param component
	 *                cycle component
	 * @return PathExpression of the form w* with w being one run through
	 *         the cycle
	 */
	private BinarySeqExpression findFromCycleComponent(Set<State> component) {
		// Pick any state as source.
		State source = component.iterator().next();
		List<Arc> w = followCycle(source);
		return BinarySeqExpression.w(w);
	}

	/**
	 * Given a path component create a {@link BinarySeqExpression}
	 * representing that path. A path component's outgoing edge may lead to
	 * another path component or a cycle component.
	 *
	 * @param component
	 *                path component
	 * @param sscCycles
	 *                a set of all SSCs that are cycles
	 * @return PathExpression of the form vw* with |v| ≠ 0 or |w| ≠ 0
	 */
	private BinarySeqExpression findFromPathComponent(Set<State> component, Set<Set<State>> sscCycles) {
		List<Arc> v = new ArrayList<>();
		List<Arc> w = new ArrayList<>();

		State first = component.iterator().next();
		Set<Arc> postset = first.getPostsetEdges();
		if (postset.isEmpty()) {
			return BinarySeqExpression.empty();
		}

		State currState = first;
		do {
			assert postset.size() == 1;
			Arc nextArc = postset.iterator().next();
			State nextState = nextArc.getTarget();
			if (!isPartOfCycleComponent(nextState, sscCycles)) {
				v.add(nextArc);
				currState = nextState;
			} else {
				v.add(nextArc);
				w = followCycle(nextState);
				return BinarySeqExpression.vw(v, w);
			}
			postset = currState.getPostsetEdges();
		} while (!postset.isEmpty());

		return BinarySeqExpression.v(v);
	}

	/**
	 * @param source
	 *                any state that is part of a cycle
	 * @return all arcs encountered on a single run through the cycle
	 *         starting at source
	 */
	private List<Arc> followCycle(State source) {
		List<Arc> cycle = new ArrayList<>();
		State currState = source;
		do {
			Set<Arc> postset = currState.getPostsetEdges();
			assert postset.size() == 1;
			Arc curr = postset.iterator().next();
			cycle.add(curr);
			currState = curr.getTarget();
		} while (!currState.equals(source));
		return cycle;
	}

	/**
	 * @param query
	 *                state that will be searched for
	 * @param sscCycles
	 *                a set of all SSCs that are cycles
	 * @return true if the given query state is part of any SSC that is a
	 *         cycle component
	 */
	private boolean isPartOfCycleComponent(State query, Set<Set<State>> sscCycles) {
		for (Set<State> component : sscCycles) {
			if (component.contains(query)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find all SSCs that are
	 * <code>ROOTS = { C | C ∈ SSC and ¬∃s ∈ S', t ∈ {a, b}, c ∈ C: (s ∉ C and (s, t, c) ∈ →')}</code>
	 * .
	 *
	 * @param ssc
	 *                all strongly connected components
	 * @return a subset of ssc that contains all components that have no
	 *         incoming edges for outside the component itself
	 */
	private Set<Set<State>> findRootComponents(Set<? extends Set<State>> ssc) {
		Set<Set<State>> roots = new HashSet<>();
		for (Set<State> component : ssc) {
			boolean isRootComponent = true;
			for (State state : component) {
				Set<State> preset = state.getPresetNodes();
				// If the preset is not empty and any node from
				// the preset is not part of the component
				// itself the component is no root
				if (!preset.isEmpty() && !component.containsAll(preset)) {
					isRootComponent = false;
					break;
				}
			}
			if (isRootComponent) {
				roots.add(component);
			}
		}
		return roots;
	}

	/**
	 * Find all SSCs that are
	 * <code>CYCLES = { C | C ∈ SSC and |C| > 1 }</code>.
	 *
	 * @param ssc
	 *                all strongly connected components
	 * @return a subset of ssc that contains all components that are cycles
	 */
	private Set<Set<State>> findCycleComponents(Set<? extends Set<State>> ssc) {
		Set<Set<State>> cycles = new HashSet<>();
		for (Set<State> component : ssc) {
			if (component.size() > 1) {
				cycles.add(component);
			}
		}
		return cycles;
	}

	/**
	 * Modifies the given LTS by removing all states that have multiple
	 * outgoing edges with labels a or b.
	 *
	 * @param ts
	 *                LTS
	 * @param a
	 *                first label
	 * @param b
	 *                second label
	 */
	private void removeStates(TransitionSystem ts, String a, String b) {
		List<State> toRemove = new ArrayList<>();
		for (State s : ts.getNodes()) {
			if (hasMultipleOutgoingArcsWithLabels(s, a, b)) {
				toRemove.add(s);
			}
		}
		for (State s : toRemove) {
			ts.removeState(s);
		}
	}

	/**
	 * @param s
	 *                state
	 * @param a
	 *                first label
	 * @param b
	 *                second label
	 * @return true if state s has more than one outgoing edge with label a
	 *         or b
	 */
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

	/**
	 * Modifies the given LTS by removing all edges with the given labels a
	 * or b.
	 *
	 * @param ts
	 *                LTS
	 * @param a
	 *                first label
	 * @param b
	 *                second label
	 */
	private void removeArcs(TransitionSystem ts, String a, String b) {
		List<Arc> toRemove = new ArrayList<>();
		for (Arc arc : ts.getEdges()) {
			if (!arc.getLabel().equals(a) && !arc.getLabel().equals(b)) {
				toRemove.add(arc);
			}
		}
		for (Arc arc : toRemove) {
			ts.removeArc(arc);
		}
	}

	/**
	 * Processes a result by either saving it or calling the consumer.
	 *
	 * @param binseq
	 *                result
	 * @throws InterruptedException
	 *                 thrown if requested by consumer function
	 */
	private void yield(BinarySeqExpression binseq) throws InterruptedException {
		boolean isUnique = true;
		if (limitResult && !binseq.isEmptyOrSingleLetterWord()) {
			String key = binseq.toRegExpString("a", "b");
			isUnique = uniqueSequences.add(key);
		}

		log("Found sequence %s (%s) (unique=%s)%n",
				binseq.toRegExpString("a", "b"),
				binseq.toStateArcString(),
				isUnique
		);

		if (sequences != null && isUnique) {
			sequences.add(binseq);
		}
		if (sequenceConsumer != null && isUnique) {
			if (!sequenceConsumer.apply(binseq)) {
				throw new InterruptedException();
			}
		}
	}

	private void log(String format, Object... args) {
		// System.out.printf(format, args);
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
