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

package uniol.apt.analysis.sequences.old;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import uniol.apt.adt.exception.StructureException;
import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;

/**
 * Checks a LTS for PN solvability by checking subsequences of the LTS against a
 * pattern.
 */
public class BinarySequences {

	private final TransitionSystem ts;
	private final int iterDepth;

	private final Queue<Sequence> queue;

	public BinarySequences(TransitionSystem ts, int iterDepth) {
		this.ts = ts;
		this.iterDepth = iterDepth;
		this.queue = new LinkedList<>();
	}

	public void run() {
		queue.add(new Sequence(0, new Decision(ts.getInitialState())));
		while (!queue.isEmpty()) {
			Sequence seq = queue.peek();
			Decision dec = seq.getLastDecision();

			if (seq.getDecisions().size() - seq.getDepth() > iterDepth) {
				yield(seq.getDecisions());
				Sequence newSeq = new Sequence(seq.getDepth() + seq.getDecisions().size(), seq.getA(),
						seq.getB(), new Decision(dec.getState(), dec.hasSetLetter()));
				queue.add(newSeq);
				seq.removeLastDecision();
				seq.getLastDecision().increaseBranchIndex();
				continue;
			}

			if (!dec.hasUnvisitedArcs()) {
				System.out.println(dec + " has no more unvisited arcs.");
				if (!dec.isPartOfLongerSequence()) {
					yield(seq.getDecisions());
				}
				if (dec.hasSetLetter()) {
					if (seq.getB() != null) {
						seq.setB(null);
						System.out.println("Resetting letter B");
					} else if (seq.getA() != null) {
						seq.setA(null);
						System.out.println("Resetting letter A");
					}
				}
				System.out.println("Removing that decision: " + dec);
				seq.removeLastDecision();
				if (seq.getDecisions().isEmpty()) {
					System.out.println("No decisions left in sequence. Removing from queue.");
					queue.remove(seq);
				}
			} else {
				Arc arc = dec.getArc();
				String label = arc.getLabel();
				System.out.println("Current decision & label: " + dec + " & " + label);

				boolean setLetter = false;
				if (seq.getA() == null) {
					System.out.println("Setting letter A = " + label);
					seq.setA(label);
					setLetter = true;
				} else if (seq.getA() != label && seq.getB() == null) {
					if (!containsBadBranch(seq.getDecisions(), label)) {
						System.out.println("Setting letter B = " + label);
						seq.setB(label);
						setLetter = true;

					} else if (!wasStartingPoint(dec.getState())) {
						dec.increaseBranchIndex();
						Decision newDec = new Decision(dec.getState());
						setWasStartingPoint(dec.getState(), true);
						queue.add(new Sequence(seq.getDepth() + seq.getDecisions().size(), newDec));
						System.out.println("Add to queue (1): " + dec.getState());
						continue;
					}
				}

				dec.increaseBranchIndex();
				if ((label.equals(seq.getA()) || label.equals(seq.getB()))
						&& !containsBadBranch(dec, seq.getA(), seq.getB())) {
					dec.setPartOfLongerSequence(true);
					Decision newDec = new Decision(arc.getTarget(), setLetter);
					seq.getDecisions().add(newDec);
					System.out.println("Making decision: " + newDec);

//					queue.remove(seq);
//					seq.increaseDepth();
//					queue.add(seq);
				} else if (!wasStartingPoint(dec.getState())) {
					Decision newDec = new Decision(dec.getState());
					setWasStartingPoint(dec.getState(), true);
					queue.add(new Sequence(seq.getDepth() + seq.getDecisions().size(), newDec));
					System.out.println("Add to queue (2): " + dec.getState());
				}
			}
		}
	}

	private boolean wasStartingPoint(State state) {
		try {
			return (boolean) state.getExtension("wasStartingPoint");
		} catch (StructureException e) {
			return false;
		}
	}

	private void setWasStartingPoint(State state, boolean val) {
		state.putExtension("wasStartingPoint", val);
	}

	private boolean containsBadBranch(Decision decision, String a, String b) {
		int count = 0;
		Set<Arc> arcs = decision.getState().getPostsetEdges();
		for (Arc arc : arcs) {
			if (arc.getLabel().equals(a) || arc.getLabel().equals(b)) {
				count += 1;
			}
			if (count > 1) {
				return true;
			}
		}
		return false;
	}

	private boolean containsBadBranch(List<Decision> decisions, String badLabel) {
		// Leave out last element as it is the one with the given label
		for (int i = 0; i < decisions.size() - 1; i++) {
			Decision dec = decisions.get(i);
			if (!dec.getState().getPostsetEdgesByLabel(badLabel).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void yield(List<Decision> decisions) {
		String states = "";
		String word = "";
		for (int i = 0; i < decisions.size(); i++) {
			Decision dec = decisions.get(i);
			states += dec.getState().getId() + " ";

			if (i < decisions.size() - 1) {
				Set<Arc> post = dec.getState().getPostsetEdges();
				for (Arc arc : post) {
					if (arc.getTarget().equals(decisions.get(i + 1).getState())) {
						word += arc.getLabel();
					}
				}
			}
		}
		System.out.println(">>>>>>>> " + states + " = " + word);
	}

	public boolean isSolvable() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getUnsolvableSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUnsolvableSequenceStates() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		TransitionSystem ts = new TransitionSystem();

		State s0 = ts.createState();
		State s1 = ts.createState();
		State s2 = ts.createState();
		State s3 = ts.createState();

		ts.setInitialState(s0);

		ts.createArc(s0, s1, "a");
		ts.createArc(s1, s2, "b");
		ts.createArc(s1, s3, "c");
		ts.createArc(s2, s0, "a");

		BinarySequences bs = new BinarySequences(ts, 5);
		bs.run();
	}

//	public static void main(String[] args) {
//		TransitionSystem ts = new TransitionSystem();
//
//		State s0 = ts.createState();
//		State s1 = ts.createState();
//		State s2 = ts.createState();
//		State s3 = ts.createState();
//		State s4 = ts.createState();
//		State s5 = ts.createState();
//		State s6 = ts.createState();
//
//		ts.setInitialState(s0);
//
//		ts.createArc(s0, s1, "b");
//		ts.createArc(s0, s4, "c");
//		ts.createArc(s0, s6, "a");
//		ts.createArc(s1, s2, "a");
//		ts.createArc(s2, s3, "a");
//		ts.createArc(s4, s5, "d");
//		ts.createArc(s5, s3, "c");
//
//		BinarySequences bs = new BinarySequences(ts, 10);
//		bs.run();
//	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
