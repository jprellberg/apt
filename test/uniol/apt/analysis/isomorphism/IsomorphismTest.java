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

package uniol.apt.analysis.isomorphism;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static uniol.apt.BestNetCollection.*;
import static uniol.apt.TestNetsForIsomorphism.*;
import static uniol.apt.TestTSCollection.getSingleStateTS;
import static uniol.apt.TestTSCollection.getSingleStateTSWithLoop;

import java.io.IOException;
import org.apache.commons.collections4.BidiMap;

import org.testng.annotations.Test;

import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.adt.ts.State;

/**
 * Collection of nets to test the isomorphism-module
 *
 * notation:
 * weak isomorphism: isomorphism, that ignores labels
 * strong isomorphism: isomorphism, that doesn't ignore labels
 *
 * @author Maike Schwammberger
 */
public class IsomorphismTest {

	/**
	 * Test strong isomorphism (which includes weak isomorphism)
	 *
	 * @param pn1
	 * @param pn2
	 */
	private BidiMap<State, State> testIsomorphism(TransitionSystem lts1, TransitionSystem lts2) {
		IsomorphismLogic logic1 = new IsomorphismLogic(lts1, lts2, false);
		assertTrue(logic1.isIsomorphic());
		IsomorphismLogic logic2 = new IsomorphismLogic(lts1, lts2, true);
		assertTrue(logic2.isIsomorphic());
		assertTrue(logic1.getIsomorphism().equals(logic2.getIsomorphism()));
		return logic1.getIsomorphism();
	}

	/**
	 * Test weak isomorphism, but non-strong isomorphism.
	 *
	 * @param pn1
	 * @param pn2
	 */
	private BidiMap<State, State> testWeakIsomorphism(TransitionSystem lts1, TransitionSystem lts2) {
		IsomorphismLogic logic1 = new IsomorphismLogic(lts1, lts2, false);
		assertTrue(logic1.isIsomorphic());
		IsomorphismLogic logic2 = new IsomorphismLogic(lts1, lts2, true);
		assertFalse(logic2.isIsomorphic());
		assertTrue(logic2.getIsomorphism().isEmpty());
		return logic1.getIsomorphism();
	}

	/**
	 * Test not non-weak isomorphism (which includes non-strong isomorphism)
	 *
	 * @param pn1
	 * @param pn2
	 */
	private void testNonWeakIsomorphism(TransitionSystem lts1, TransitionSystem lts2) {
		IsomorphismLogic logic = new IsomorphismLogic(lts1, lts2, false);
		assertFalse(logic.isIsomorphic());
		assertTrue(logic.getIsomorphism().isEmpty());
		logic = new IsomorphismLogic(lts1, lts2, true);
		assertFalse(logic.isIsomorphic());
		assertTrue(logic.getIsomorphism().isEmpty());
	}

	//Tests for strong isomorphic nets:

	@Test
	public void testIsomorphicWithItSelf() throws IOException {
		testIsomorphism(getTs3A(), getTs3A());
	}

	@Test
	public void testIsomorphicNets1() throws IOException {
		testIsomorphism(getTs3A(), getTs3B());
	}

	/**
	 * The nets IsoNet1A and IsoNet1B are identically,
	 * except that the labels are exchanged.
	 *
	 * @throws IOException
	 * @throws ModuleException
	 */
	@Test
	public void testIsomorphicNets2() throws IOException {
		testIsomorphism(getIsoTs1A(), getIsoTs1B());
	}

	@Test
	public void testIsomorphicNets3() throws IOException {
		testIsomorphism(getIsoTs2A(), getIsoTs2B());
	}

	// Tests for non isomorphic nets (neither weak nor strong isomorphism):

	/**
	 * A net with one transition and no place isn't isomorphic to another
	 * net with one place and no transition.
	 *
	 * @throws IOException
	 * @throws ModuleException
	 */
	@Test
	public void testEmptyNet() throws IOException {
		testNonWeakIsomorphism(getSingleStateTS(), getSingleStateTSWithLoop());
	}

	@Test
	public void testNonIsomorphicNets1() throws IOException {
		testNonWeakIsomorphism(getTs1A(), getTs1B());
	}

	@Test
	public void testNonIsomorphicNets2() throws IOException {
		testNonWeakIsomorphism(getTs2A(), getTs2B());
	}

	@Test
	public void testNonIsomorphicNets3() throws IOException {
		testNonWeakIsomorphism(getTs4A(), getTs4B());
	}

	//Tests for (non) weak isomorphism

	/**
	 * Test to check, if the initial nodes are mapped correctly
	 * (IsoNet3A and IsoNet4a have isomorphic reachability graphs,
	 * but their initial nodes don't map to each other)
	 *
	 * @throws IOException
	 * @throws ModuleException
	 */
	@Test
	public void testNonIsomorphicNets3Own() throws IOException {
		testWeakIsomorphism(getIsoTs3A(), getIsoTs3B());
	}

	/**
	 * IsoNet4A and IsoNet4B are "almost isomorphic". There is just a single edge of difference
	 * and that edge is not easily reachable, but needs four firings before it appears.
	 *
	 * @throws IOException
	 * @throws ModuleException
	 */
	@Test
	public void testNonIsomorphicNets4() throws IOException {
		testNonWeakIsomorphism(getIsoTs4A(), getIsoTs4B());
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
