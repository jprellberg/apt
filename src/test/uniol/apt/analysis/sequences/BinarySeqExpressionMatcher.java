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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import uniol.apt.adt.ts.Arc;

/**
 * Matcher that compares {@link BinarySeqExpression} by the contained state and
 * arc id strings instead of using their equality functions as those will not
 * work if the states and arcs belong to different LTS as may be the case with
 * some implementations. Also handles ambiguities in w* expressions as they may
 * begin in any state that is part of the described cycle but are still equal.
 *
 * @author Jonas Prellberg
 */
public class BinarySeqExpressionMatcher extends TypeSafeMatcher<BinarySeqExpression> {

	private final BinarySeqExpression expected;

	public BinarySeqExpressionMatcher(BinarySeqExpression expected) {
		this.expected = expected;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(expected.toStateArcString());
	}

	@Override
	protected boolean matchesSafely(BinarySeqExpression actual) {
		if (expected.getV().isEmpty() && !expected.getW().isEmpty() && actual.getV().isEmpty()
				&& !actual.getW().isEmpty()) {
			// Both expressions are cycles w*. Compare them by
			// comparing the state IDs contained in the cycle.
			Set<String> expectedIds = getStateIds(expected.getW());
			Set<String> actualIds = getStateIds(actual.getW());
			return expectedIds.equals(actualIds);
		} else {
			String expectedStr = expected.toStateArcString();
			String actualStr = actual.toStateArcString();
			return expectedStr.equals(actualStr);
		}
	}

	private Set<String> getStateIds(List<Arc> arcs) {
		Set<String> result = new HashSet<>();
		for (Arc arc : arcs) {
			result.add(arc.getSourceId());
		}
		return result;
	}

	/**
	 * Returns a matcher that will compare {@link BinarySeqExpression}
	 * against the given expected result.
	 */
	public static BinarySeqExpressionMatcher binSeqMatches(BinarySeqExpression expected) {
		return new BinarySeqExpressionMatcher(expected);
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
