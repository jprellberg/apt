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

import java.util.ArrayList;
import java.util.List;

import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;

public class Decision {

	private final State state;

	private final List<Arc> postset;

	private final boolean setLetter;

	private int branchIndex;

	private boolean partOfLongerSequence;

	public Decision(State state) {
		this(state, false);
	}

	public Decision(State state, boolean setLetter) {
		this.state = state;
		this.postset = new ArrayList<>(state.getPostsetEdges());
		this.setLetter = setLetter;
		this.branchIndex = 0;
		this.partOfLongerSequence = false;
	}

	public State getState() {
		return state;
	}

	public Arc getArc() {
		return postset.get(branchIndex);
	}

	public boolean hasUnvisitedArcs() {
		return branchIndex < postset.size();
	}

	public int getBranchIndex() {
		return branchIndex;
	}

	public void increaseBranchIndex() {
		branchIndex += 1;
	}

	public boolean hasSetLetter() {
		return setLetter;
	}

	public boolean isPartOfLongerSequence() {
		return partOfLongerSequence;
	}

	public void setPartOfLongerSequence(boolean partOfLongerSequence) {
		this.partOfLongerSequence = partOfLongerSequence;
	}

	@Override
	public String toString() {
		return "Decision [state=" + state + ", branchIndex=" + branchIndex + ", setLetter=" + setLetter + "]";
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
