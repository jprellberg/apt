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

import uniol.apt.adt.ts.TransitionSystem;

/**
 * Checks a LTS for PN solvability by checking subsequences of the LTS against a
 * pattern.
 *
 * @author Jonas Prellberg
 */
public class CheckSolvability {

	private final BinarySeqFinder binarySeqFinder;
	private final BinaryWordMatcher binaryWordMatcher;

	private BinarySeqExpression offendingExpression;

	public CheckSolvability(TransitionSystem transitionSystem) {
		this(transitionSystem, new BinarySeqFinder(), new BinaryWordMatcher());
	}

	public CheckSolvability(TransitionSystem transitionSystem, BinarySeqFinder binarySequenceFinder,
			BinaryWordMatcher binaryWordMatcher) {
		this.binarySeqFinder = binarySequenceFinder;
		this.binaryWordMatcher = binaryWordMatcher;
		this.binarySeqFinder.getSequences(transitionSystem, new Function<BinarySeqExpression, Boolean>() {
			@Override
			public Boolean apply(BinarySeqExpression t) {
				return CheckSolvability.this.apply(t);
			}
		});
	}

	private Boolean apply(BinarySeqExpression pathExpression) {
		// Unroll cycles 10 times until a better solution for the check is found
		String word = pathExpression.toWord("a", "b", 10);
		if (binaryWordMatcher.isUnsolvableBinaryWord(word)) {
			offendingExpression = pathExpression;
			return false;
		} else {
			return true;
		}
	}

	public boolean isSolvable() {
		return offendingExpression == null;
	}

	public String getUnsolvableSequenceAsRegex() {
		return offendingExpression.toRegExpString();
	}

	public String getUnsolvableSequenceAsArcs() {
		return offendingExpression.toStateArcString();
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
