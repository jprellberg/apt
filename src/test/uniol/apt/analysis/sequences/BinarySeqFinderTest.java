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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static uniol.apt.analysis.sequences.BinarySeqExpressionMatcher.*;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.TransitionSystem;

/**
 * Tests for {@link BinarySeqFinder}.
 *
 * @author Jonas Prellberg
 */
public class BinarySeqFinderTest {

	private BinarySeqFinder binarySeqFinder;

	@BeforeClass
	public void before() {
		binarySeqFinder = new BinarySeqFinder(true);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetSequences1() {
		TransitionSystem tsA = new TransitionSystem();
		tsA.createState("s0");
		tsA.createState("s1");
		tsA.createState("s2");
		tsA.createState("s3");
		tsA.createState("s4");
		tsA.createState("s5");
		tsA.createState("s6");
		tsA.createState("s7");
		tsA.createState("s8");
		Arc a1 = tsA.createArc("s0", "s1", "a");
		Arc a2 = tsA.createArc("s0", "s2", "b");
		Arc a3 = tsA.createArc("s1", "s3", "b");
		Arc a4 = tsA.createArc("s1", "s4", "d");
		Arc a5 = tsA.createArc("s2", "s6", "c");
		Arc a6 = tsA.createArc("s3", "s5", "b");
		Arc a7 = tsA.createArc("s4", "s5", "a");
		Arc a8 = tsA.createArc("s5", "s0", "a");
		Arc a9 = tsA.createArc("s6", "s7", "d");
		Arc a10 = tsA.createArc("s7", "s8", "d");
		Arc a11 = tsA.createArc("s8", "s6", "c");
		tsA.setInitialState("s0");

		List<BinarySeqExpression> seqs = binarySeqFinder.getSequences(tsA);
		assertThat(seqs, containsInAnyOrder(
			// s1 b s3 b s5
			binSeqMatches(BinarySeqExpression.v(Arrays.asList(a3, a6))),
			// s4 a s5 a s0 a s1
			binSeqMatches(BinarySeqExpression.v(Arrays.asList(a7, a8, a1))),
			// (s0 a s1 d s4 a s5 a s0)*
			binSeqMatches(BinarySeqExpression.w(Arrays.asList(a1, a4, a7, a8))),
			// s6 d s7 d s10 is not part of the result because it has the same structure as s1 b s3 b s5
			// binSeqMatches(BinarySeqExpression.v(Arrays.asList(a9, a10))),
			// s0 b s2 c s6
			binSeqMatches(BinarySeqExpression.v(Arrays.asList(a2, a5))),
			// s2 c (s6 d s7 d s10 c s6)*
			binSeqMatches(BinarySeqExpression.vw(Arrays.asList(a5), Arrays.asList(a9, a10, a11)))
		));
	}

	@Test
	public void testGetSequences2() {
		TransitionSystem ts = new TransitionSystem();
		ts.createState("s0");
		ts.createState("s1");
		ts.createState("s2");
		ts.createState("s3");
		Arc a1 = ts.createArc("s0", "s1", "c");
		Arc a2 = ts.createArc("s1", "s2", "d");
		Arc a3 = ts.createArc("s2", "s3", "d");
		Arc a4 = ts.createArc("s3", "s1", "c");
		ts.setInitialState("s0");

		List<BinarySeqExpression> seqs = binarySeqFinder.getSequences(ts);
		assertThat(seqs, contains(
			// s0 c (s1 d s2 d s3 c s1)*
			binSeqMatches(BinarySeqExpression.vw(Arrays.asList(a1), Arrays.asList(a2, a3, a4)))
		));
	}

	@Test
	public void testGetSequences3() {
		TransitionSystem ts = new TransitionSystem();
		ts.createState("s0");
		ts.createState("s1");
		Arc a1 = ts.createArc("s0", "s1", "a");
		Arc a2 = ts.createArc("s1", "s1", "b");
		ts.setInitialState("s0");

		List<BinarySeqExpression> seqs = binarySeqFinder.getSequences(ts);
		assertThat(seqs, contains(
			// s0 a (s1 b s1)*
			binSeqMatches(BinarySeqExpression.vw(Arrays.asList(a1), Arrays.asList(a2)))
		));
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
