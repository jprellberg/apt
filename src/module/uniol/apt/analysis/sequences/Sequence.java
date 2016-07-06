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

import java.util.ArrayList;
import java.util.List;

public class Sequence implements Comparable<Sequence> {

	private int depth;

	private String a;

	private String b;

	private final List<Decision> decisions;

	public Sequence(int depth, Decision initialDecision) {
		this(depth, null, null, initialDecision);
	}

	public Sequence(int depth, String a, String b, Decision initialDecision) {
		this.depth = depth;
		this.a = a;
		this.b = b;
		this.decisions = new ArrayList<>();
		this.decisions.add(initialDecision);
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void increaseDepth() {
		depth += 1;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public List<Decision> getDecisions() {
		return decisions;
	}

	public Decision getLastDecision() {
		return decisions.get(decisions.size() - 1);
	}

	public Decision removeLastDecision() {
		return decisions.remove(decisions.size() - 1);
	}

	@Override
	public int compareTo(Sequence o) {
		return depth - o.depth;
	}

	@Override
	public String toString() {
		return "Sequence [depth=" + depth + ", a=" + a + ", b=" + b + ", decisions=" + decisions + "]";
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
