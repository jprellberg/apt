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

import org.testng.annotations.Test;

import uniol.apt.adt.ts.TransitionSystem;

/**
 * Tests for {@link CheckSolvability}.
 *
 * @author Jonas Prellberg
 */
public class CheckSolvabilityTest {

	@Test
	public void testSolvable() {
		TransitionSystem ts = new TransitionSystem();
		ts.createState("s0");
		ts.createState("s1");
		ts.createState("s2");
		ts.createState("s6");
		ts.createArc("s0", "s1", "a");
		ts.createArc("s1", "s2", "b");
		ts.createArc("s2", "s1", "a");
		ts.setInitialState("s0");

		CheckSolvability checkSolvability = new CheckSolvability(ts);
		assertThat(checkSolvability.isSolvable(), is(equalTo(true)));
	}

	@Test
	public void testContainsPatternI() {
		TransitionSystem ts = new TransitionSystem();
		ts.createState("s0");
		ts.createState("s1");
		ts.createState("s2");
		ts.createState("s3");
		ts.createState("s4");
		ts.createState("s5");
		ts.createState("s6");
		ts.createArc("s0", "s1", "a");
		ts.createArc("s1", "s2", "b");
		ts.createArc("s2", "s3", "b");
		ts.createArc("s3", "s4", "b");
		ts.createArc("s4", "s5", "b");
		ts.createArc("s5", "s6", "b");
		ts.createArc("s6", "s4", "a");
		ts.setInitialState("s0");

		// Path regex is abbb(bba)*. When unrolled two times it matches
		// pattern I.

		CheckSolvability checkSolvability = new CheckSolvability(ts);
		assertThat(checkSolvability.isSolvable(), is(equalTo(false)));
		assertThat(checkSolvability.getUnsolvableSequenceAsRegex(), is(equalTo("abbb(bba)*")));
	}

	@Test
	public void testContainsPatternII() {
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
		ts.createArc("s0", "s1", "c");
		ts.createArc("s1", "s2", "d");
		ts.createArc("s2", "s3", "c");
		ts.createArc("s3", "s4", "c");
		ts.createArc("s4", "s5", "c");
		ts.createArc("s5", "s6", "c");
		ts.createArc("s6", "s7", "d");
		ts.createArc("s7", "s8", "c");
		ts.createArc("s8", "s8", "c");
		ts.setInitialState("s0");

		// Path regex is cdccccdc(c)*. When unrolled 5 times it matches
		// pattern II.

		CheckSolvability checkSolvability = new CheckSolvability(ts);
		assertThat(checkSolvability.isSolvable(), is(equalTo(false)));
		assertThat(checkSolvability.getUnsolvableSequenceAsRegex(), is(equalTo("cdccccdc(c)*")));
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
