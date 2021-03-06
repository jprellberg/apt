/*-
 * APT - Analysis of Petri Nets and labeled Transition systems
 * Copyright (C) 2015  Uli Schlachter
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

package uniol.apt.util;

import java.util.Arrays;

import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/** @author Uli Schlachter */
@SuppressWarnings("unchecked") // I hate generics
public class EquivalenceRelationTest {
	EquivalenceRelation<Integer> getOddEven(int limit) {
		EquivalenceRelation<Integer> result = new EquivalenceRelation<>();
		for (int i = 2; i <= limit; i++)
			result.joinClasses(i % 2, i);
		return result;
	}

	EquivalenceRelation<Integer> getSomePrimes() {
		int[] primes = { 2, 3, 5, 7, 11, 13, 17, 19 };
		EquivalenceRelation<Integer> result = new EquivalenceRelation<>();
		for (int i = 0; i <= 20; i++)
			if (Arrays.binarySearch(primes, i) >= 0)
				result.joinClasses(2, i);
			else
				result.joinClasses(1, i);
		return result;
	}

	@Test
	public void testEmptyRelation() {
		EquivalenceRelation<String> relation = new EquivalenceRelation<>();

		assertThat(relation, emptyIterable());
		assertThat(relation, hasSize(0));
		assertThat(relation.isEquivalent("a", "b"), is(false));
		assertThat(relation.getClass("a"), contains("a"));
	}

	@Test
	public void testIterateAfterChecks() {
		EquivalenceRelation<String> relation = new EquivalenceRelation<>();

		assertThat(relation.isEquivalent("a", "b"), is(false));
		assertThat(relation.isEquivalent("c", "b"), is(false));
		assertThat(relation.getClass("a"), contains("a"));
		assertThat(relation.getClass("c"), contains("c"));

		assertThat(relation, emptyIterable());
		assertThat(relation, hasSize(0));
	}

	@Test
	public void testReflexive() {
		EquivalenceRelation<String> relation = new EquivalenceRelation<>();

		assertThat(relation.isEquivalent("a", "a"), is(true));
	}

	@Test
	public void testTwoElements() {
		EquivalenceRelation<String> relation = new EquivalenceRelation<>();
		assertThat(relation.joinClasses("a", "b"), containsInAnyOrder("a", "b"));

		assertThat(relation.isEquivalent("a", "b"), is(true));
		assertThat(relation.isEquivalent("b", "a"), is(true));
		assertThat(relation.isEquivalent("c", "b"), is(false));
		assertThat(relation.getClass("a"), containsInAnyOrder("a", "b"));
		assertThat(relation.getClass("c"), contains("c"));
		assertThat(relation.getClass("b"), sameInstance(relation.getClass("a")));
		assertThat(relation, contains(containsInAnyOrder("a", "b")));
		assertThat(relation, hasSize(1));
	}

	@Test
	public void testThreeElements() {
		EquivalenceRelation<String> relation = new EquivalenceRelation<>();
		assertThat(relation.joinClasses("a", "b"), containsInAnyOrder("a", "b"));
		assertThat(relation.joinClasses("a", "c"), containsInAnyOrder("a", "b", "c"));

		assertThat(relation.isEquivalent("c", "b"), is(true));
		assertThat(relation.getClass("a"), containsInAnyOrder("a", "b", "c"));
		assertThat(relation, contains(containsInAnyOrder("a", "b", "c")));
		assertThat(relation, hasSize(1));
	}

	@Test
	public void testTwoClasses() {
		EquivalenceRelation<String> relation = new EquivalenceRelation<>();
		assertThat(relation.joinClasses("a", "b"), containsInAnyOrder("a", "b"));
		assertThat(relation.joinClasses("c", "d"), containsInAnyOrder("c", "d"));

		assertThat(relation.isEquivalent("c", "b"), is(false));
		assertThat(relation.getClass("a"), containsInAnyOrder("a", "b"));
		assertThat(relation, containsInAnyOrder(
					containsInAnyOrder("a", "b"),
					containsInAnyOrder("c", "d")));
		assertThat(relation, hasSize(2));
	}

	@Test
	public void testFourElements() {
		EquivalenceRelation<String> relation = new EquivalenceRelation<>();
		assertThat(relation.joinClasses("a", "b"), containsInAnyOrder("a", "b"));
		assertThat(relation.joinClasses("c", "d"), containsInAnyOrder("c", "d"));
		assertThat(relation.joinClasses("a", "d"), containsInAnyOrder("a", "b", "c", "d"));

		assertThat(relation.isEquivalent("c", "b"), is(true));
		assertThat(relation.getClass("a"), containsInAnyOrder("a", "b", "c", "d"));
		assertThat(relation, contains(containsInAnyOrder("a", "b", "c", "d")));
		assertThat(relation, hasSize(1));
	}

	@Test
	public void testEqualsEmpty() {
		EquivalenceRelation<String> relation1 = new EquivalenceRelation<>();
		EquivalenceRelation<String> relation2 = new EquivalenceRelation<>();
		assertThat(relation1, equalTo(relation2));
		assertThat(relation1.hashCode(), equalTo(relation2.hashCode()));
	}

	@Test
	public void testEqualsNonEmpty() {
		EquivalenceRelation<String> relation1 = new EquivalenceRelation<>();
		EquivalenceRelation<String> relation2 = new EquivalenceRelation<>();
		relation1.joinClasses("a", "b");
		relation2.joinClasses("b", "a");
		assertThat(relation1, equalTo(relation2));
		assertThat(relation1.hashCode(), equalTo(relation2.hashCode()));
	}

	@Test
	public void testRefineEmpty() {
		EquivalenceRelation<Integer> primes = getSomePrimes();
		EquivalenceRelation<Integer> empty = new EquivalenceRelation<>();
		assertThat(primes.refine(empty), equalTo(empty));
	}

	@Test
	public void testEmptyRefine() {
		EquivalenceRelation<Integer> primes = getSomePrimes();
		EquivalenceRelation<Integer> empty = new EquivalenceRelation<>();
		assertThat(empty.refine(primes), sameInstance(empty));
	}

	@Test
	public void testRefineSelf() {
		EquivalenceRelation<Integer> primes = getSomePrimes();
		assertThat(primes.refine(primes), sameInstance(primes));
	}

	@Test
	public void testRefine() {
		EquivalenceRelation<Integer> primes = getSomePrimes();
		EquivalenceRelation<Integer> numbers = getOddEven(20);
		EquivalenceRelation<Integer> refined = primes.refine(numbers);

		assertThat(refined.isEquivalent(3, 7), is(true));
		assertThat(refined.isEquivalent(1, 5), is(false));
		assertThat(refined.isEquivalent(11, 9), is(false));
		assertThat(refined.isEquivalent(11, 13), is(true));
		assertThat(refined.isEquivalent(15, 9), is(true));
	}
}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
