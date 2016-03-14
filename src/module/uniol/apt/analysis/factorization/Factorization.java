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

package uniol.apt.analysis.factorization;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.analysis.connectivity.Connectivity;
import uniol.apt.analysis.gdiam.GeneralDiamond;
import uniol.apt.analysis.labelseparation.LabelSeparation;
import uniol.apt.analysis.labelseparation.TransitionSystemFilter;
import uniol.apt.util.PowerSet;

/**
 * A class to test if a LTS is a product. It allows to compute the factors as
 * well.
 *
 * @author Jonas Prellberg
 *
 */
public class Factorization {

	private final TransitionSystem ts;

	private TransitionSystem factor1;
	private TransitionSystem factor2;

	/**
	 * Creates a new Factorization instance that examines the given TS.
	 *
	 * @param ts
	 *                the transition system to factorize
	 */
	public Factorization(TransitionSystem ts) {
		this.ts = ts;
	}

	/**
	 * Returns the first factor, if any.
	 *
	 * @return the first factor or null
	 */
	public TransitionSystem getFactor1() {
		return factor1;
	}

	/**
	 * Returns the second factor, if any.
	 *
	 * @return the second factor or null
	 */
	public TransitionSystem getFactor2() {
		return factor2;
	}

	/**
	 * Tries to factorize the TS and returns if it was possible.
	 *
	 * @return true, if a factorization was found
	 */
	public boolean factorize() {
		for (Collection<String> item : PowerSet.powerSet(ts.getAlphabet())) {
			// Skip trivial solutions.
			if (item.isEmpty() || item.size() == ts.getAlphabet().size()) {
				continue;
			}
			if (isGdiamAndSeparated(item)) {
				createFactors(item);
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates factors with the given set of labels. It is assumed that the
	 * set of labels (T') is a T'-separation of the TS and the TS is a
	 * T'-gdiam.
	 *
	 * @param item
	 *                a set of labels
	 */
	private void createFactors(Collection<String> item) {
		// t1 ∪ t2 = Σ
		Set<String> t1 = new HashSet<>(item);
		Set<String> t2 = new HashSet<>(ts.getAlphabet());
		t2.removeAll(item);

		factor1 = createFactor(t1);
		factor2 = createFactor(t2);
	}

	/**
	 * Creates and returns a single factor with the given label set.
	 *
	 * @param labels
	 *                the label set for this factor
	 * @return the factor TS
	 */
	private TransitionSystem createFactor(Set<String> labels) {
		String initId = ts.getInitialState().getId();
		TransitionSystem result = new TransitionSystem();
		// Copy states that are generally reachable from the initial
		// state with the label set.
		for (State state : getGenerallyReachableFromInitialStateByLabels(labels)) {
			result.createState(state.getId());
		}
		// Set initial state.
		result.setInitialState(initId);
		// Copy arcs that start and end in the correct state set and
		// have the correct label.
		for (Arc arc : ts.getEdges()) {
			if (labels.contains(arc.getLabel()) && result.containsState(arc.getSourceId())
					&& result.containsState(arc.getTargetId())) {
				result.createArc(arc.getSourceId(), arc.getTargetId(), arc.getLabel());
			}
		}
		return result;
	}

	private Set<State> getGenerallyReachableFromInitialStateByLabels(Set<String> labels) {
		TransitionSystem modTs = TransitionSystemFilter.retainArcsByLabel(ts, labels);
		return Connectivity.getWeaklyConnectedComponent(modTs.getInitialState());
	}

	private boolean isGdiamAndSeparated(Collection<String> tPrime) {
		Set<String> set = new HashSet<>(tPrime);
		return GeneralDiamond.isGdiam(ts, set) && LabelSeparation.isSeparated(ts, set);
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
