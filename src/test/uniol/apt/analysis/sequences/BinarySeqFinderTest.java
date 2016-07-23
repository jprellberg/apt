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

	private TransitionSystem tsA;
	private Arc a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;

	private TransitionSystem tsB;
	private Arc b1, b2, b3, b4;

	@BeforeClass
	public void before() {
		binarySeqFinder = new BinarySeqFinder(true);

		tsA = new TransitionSystem();
		tsA.createState("s0");
		tsA.createState("s1");
		tsA.createState("s2");
		tsA.createState("s3");
		tsA.createState("s4");
		tsA.createState("s5");
		tsA.createState("s6");
		tsA.createState("s7");
		tsA.createState("s8");
		a1 = tsA.createArc("s0", "s1", "a");
		a2 = tsA.createArc("s0", "s2", "b");
		a3 = tsA.createArc("s1", "s3", "b");
		a4 = tsA.createArc("s1", "s4", "d");
		a5 = tsA.createArc("s2", "s6", "c");
		a6 = tsA.createArc("s3", "s5", "b");
		a7 = tsA.createArc("s4", "s5", "a");
		a8 = tsA.createArc("s5", "s0", "a");
		a9 = tsA.createArc("s6", "s7", "d");
		a10 = tsA.createArc("s7", "s8", "d");
		a11 = tsA.createArc("s8", "s6", "c");
		tsA.setInitialState("s0");

		tsB = new TransitionSystem();
		tsB.createState("s0");
		tsB.createState("s1");
		tsB.createState("s2");
		tsB.createState("s3");
		b1 = tsB.createArc("s0", "s1", "c");
		b2 = tsB.createArc("s1", "s2", "d");
		b3 = tsB.createArc("s2", "s3", "d");
		b4 = tsB.createArc("s3", "s1", "c");
		tsB.setInitialState("s0");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetSequences1() {
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
		List<BinarySeqExpression> seqs = binarySeqFinder.getSequences(tsB);
		assertThat(seqs, contains(
			// s0 c (s1 d s2 d s3 c s1)*
			binSeqMatches(BinarySeqExpression.vw(Arrays.asList(b1), Arrays.asList(b2, b3, b4)))
		));
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
