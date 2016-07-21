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

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uniol.apt.adt.ts.TransitionSystem;

/**
 * Tests for {@link BinarySeqFinder}.
 *
 * @author Jonas Prellberg
 */
public class BinarySeqFinderTest {

	private BinarySeqFinder binarySeqFinder;
	private TransitionSystem ts;

	@BeforeClass
	public void before() {
		binarySeqFinder = new BinarySeqFinder();
		ts = new TransitionSystem();

		ts.createState("s0");
		ts.createState("s1");
		ts.createState("s2");
		ts.createState("s3");
		ts.createState("s4");
		ts.createState("s5");
		ts.createArc("s0", "s1", "a");
		ts.createArc("s1", "s2", "d");
		ts.createArc("s2", "s3", "d");
		ts.createArc("s3", "s4", "a");
		ts.createArc("s4", "s5", "a");

		ts.createState("s6");
		ts.createState("s7");
		ts.createArc("s2", "s6", "b");
		ts.createArc("s6", "s7", "b");
		ts.createArc("s7", "s2", "c");

		ts.setInitialState("s0");
	}

	@Test
	public void testGetSequences() {
		List<BinarySeqExpression> seqs = binarySeqFinder.getSequences(ts);
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
