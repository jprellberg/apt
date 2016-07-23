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

	/**
	 * Creates a new instance of {@link CheckSolvability} that operates on
	 * the given transition system. Invoking this constructor starts the
	 * check process and the results can be retrieved using the other class
	 * methods.
	 *
	 * @param transitionSystem
	 *                input LTS
	 */
	public CheckSolvability(TransitionSystem transitionSystem) {
		this(transitionSystem, new BinarySeqFinder(), new BinaryWordMatcher());
	}

	/**
	 * Creates a new instance of {@link CheckSolvability} that operates on
	 * the given transition system. Invoking this constructor starts the
	 * check process and the results can be retrieved using the other class
	 * methods.
	 *
	 * @param transitionSystem
	 *                input LTS
	 * @param binarySequenceFinder
	 *                {@link BinarySeqFinder} dependency
	 * @param binaryWordMatcher
	 *                {@link BinaryWordMatcher} dependency
	 */
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

	private Boolean apply(BinarySeqExpression binSeq) {
		if (isUnsolvableBinarySeq(binSeq)) {
			offendingExpression = binSeq;
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checks if the given {@link BinarySeqExpression} makes the LTS
	 * unsolvable
	 *
	 * @param binSeq
	 *                binary sequence expression vw*
	 * @return true if the expression makes the LTS Petri net unsolvable
	 */
	private boolean isUnsolvableBinarySeq(BinarySeqExpression binSeq) {
		// If pattern I is found in a string does not change when
		// unrolling more than two times.
		String unrolled2 = binSeq.toWord("a", "b", 2);
		char[] unrolled2Chars = unrolled2.toCharArray();
		if (binaryWordMatcher.containsPatternI(unrolled2Chars)
				|| binaryWordMatcher.containsPatternII(unrolled2Chars)) {
			return true;
		}

		/*
		 * Now only pattern II is left to check but it requires infinite
		 * unrolling for an exhaustive check. For now just unroll 50
		 * times. TODO devise a proper scheme to check this exhaustively
		 */
		String unrolledInf = binSeq.toWord("a", "b", 50);
		char[] unrolledInfChars = unrolledInf.toCharArray();
		return binaryWordMatcher.containsPatternII(unrolledInfChars);
	}

	/**
	 * Returns true if the LTS was found to be Petri net solvable.
	 */
	public boolean isSolvable() {
		return offendingExpression == null;
	}

	/**
	 * Returns the regular expression that represents a path through the LTS
	 * that makes it Petri net unsolvable if {@link #isSolvable()} returned
	 * false.
	 *
	 * @throws IllegalStateException
	 *                 thrown if the LTS was solvable
	 */
	public String getUnsolvableSequenceAsRegex() {
		if (offendingExpression != null) {
			return offendingExpression.toRegExpString();
		} else {
			throw new IllegalStateException(
					"The LTS was solvable so there is no offending sequence regular expression.");
		}
	}

	/**
	 * Returns a string representation of a path through the LTS that makes
	 * it Petri net unsolvable if {@link #isSolvable()} returned false.
	 *
	 * @throws IllegalStateException
	 *                 thrown if the LTS was solvable
	 */
	public String getUnsolvableSequenceAsArcs() {
		if (offendingExpression != null) {
			return offendingExpression.toStateArcString();
		} else {
			throw new IllegalStateException("The LTS was solvable so there is no offending sequence.");
		}
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
