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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.TransitionSystem;

/**
 * Test for {@link BinarySeqExpression}.
 *
 * @author Jonas Prellberg
 */
public class BinarySeqExpressionTest {

	private List<Arc> ab;
	private List<Arc> bb;
	private List<Arc> db;
	private List<Arc> bd;

	@BeforeClass
	public void before() {
		TransitionSystem ts = new TransitionSystem();

		ts.createState("s0");
		ts.createState("s1");
		ts.createState("s2");
		ts.createState("s3");
		ts.createState("s4");
		ts.createState("s5");
		ts.createState("s6");
		ts.createState("s7");
		ts.createState("s8");
		ts.createState("s9");
		ts.createState("s10");
		ts.createState("s11");

		Arc a1 = ts.createArc("s0", "s1", "a");
		Arc a2 = ts.createArc("s1", "s2", "b");
		ab = Arrays.asList(a1, a2);

		Arc a3 = ts.createArc("s3", "s4", "b");
		Arc a4 = ts.createArc("s4", "s5", "b");
		bb = Arrays.asList(a3, a4);

		Arc a5 = ts.createArc("s6", "s7", "d");
		Arc a6 = ts.createArc("s7", "s8", "b");
		db = Arrays.asList(a5, a6);

		Arc a7 = ts.createArc("s9", "s10", "b");
		Arc a8 = ts.createArc("s10", "s11", "d");
		bd = Arrays.asList(a7, a8);
	}

	@Test
	public void testRegularExpressionsV() {
		BinarySeqExpression binseq = BinarySeqExpression.v(ab);
		assertThat(binseq.toRegExpString(), is(equalTo("ab")));
	}

	@Test
	public void testRegularExpressionsW() {
		BinarySeqExpression binseq = BinarySeqExpression.w(ab);
		assertThat(binseq.toRegExpString(), is(equalTo("(ab)*")));
	}

	@Test
	public void testRegularExpressionsVW() {
		BinarySeqExpression binseq = BinarySeqExpression.vw(ab, bb);
		assertThat(binseq.toRegExpString(), is(equalTo("ab(bb)*")));
	}

	@Test
	public void testRegularExpressionsRegularizedV1() {
		BinarySeqExpression binseq = BinarySeqExpression.v(db);
		assertThat(binseq.toRegExpString("a", "b"), is(equalTo("ab")));
	}

	@Test
	public void testRegularExpressionsRegularizedV2() {
		BinarySeqExpression binseq = BinarySeqExpression.v(bb);
		assertThat(binseq.toRegExpString("a", "b"), is(equalTo("aa")));
	}

	@Test
	public void testRegularExpressionsRegularizedW() {
		BinarySeqExpression binseq = BinarySeqExpression.w(db);
		assertThat(binseq.toRegExpString("a", "b"), is(equalTo("(ab)*")));
	}

	@Test
	public void testRegularExpressionsRegularizedVW() {
		BinarySeqExpression binseq = BinarySeqExpression.vw(db, bd);
		assertThat(binseq.toRegExpString("a", "b"), is(equalTo("ab(ba)*")));
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
