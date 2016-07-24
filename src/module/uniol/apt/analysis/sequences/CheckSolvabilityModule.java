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

package uniol.apt.analysis.sequences;

import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.module.AbstractModule;
import uniol.apt.module.AptModule;
import uniol.apt.module.Category;
import uniol.apt.module.Module;
import uniol.apt.module.ModuleInput;
import uniol.apt.module.ModuleInputSpec;
import uniol.apt.module.ModuleOutput;
import uniol.apt.module.ModuleOutputSpec;
import uniol.apt.module.exception.ModuleException;

/**
 * Module that tests a LTS for PN solvability by checking subsequences of the
 * LTS against a pattern.
 *
 * @author Jonas Prellberg
 */
@AptModule
public class CheckSolvabilityModule extends AbstractModule implements Module {

	private static final int DEFAULT_UNROLL_DEPTH = 50;

	@Override
	public String getName() {
		return "checksolvability";
	}

	@Override
	public void require(ModuleInputSpec inputSpec) {
		inputSpec.addParameter("lts", TransitionSystem.class, "The LTS that should be examined");
		inputSpec.addOptionalParameterWithDefault("unrollDepth",
			Integer.class,
			DEFAULT_UNROLL_DEPTH,
			String.valueOf(DEFAULT_UNROLL_DEPTH),
			"Amount of cycle unrolls that will be performed before pattern matching."
		);
	}

	@Override
	public void provide(ModuleOutputSpec outputSpec) {
		outputSpec.addReturnValue("solvable", Boolean.class, ModuleOutputSpec.PROPERTY_SUCCESS);
		outputSpec.addReturnValue("offendingStructure", String.class);
		outputSpec.addReturnValue("offendingSeqRegex", String.class);
	}

	@Override
	public void run(ModuleInput input, ModuleOutput output) throws ModuleException {
		TransitionSystem ts = input.getParameter("lts", TransitionSystem.class);
		Integer unrollDepth = input.getParameter("unrollDepth", Integer.class);

		CheckSolvability checker = new CheckSolvability(ts, unrollDepth);

		output.setReturnValue("solvable", Boolean.class, checker.isSolvable());
		if (!checker.isSolvable()) {
			output.setReturnValue("offendingStructure", String.class,
					checker.getUnsolvableSequenceAsArcs());
			output.setReturnValue("offendingSeqRegex", String.class,
					checker.getUnsolvableSequenceAsRegex());

		}
	}

	@Override
	public String getShortDescription() {
		return "Check if any subword of the LTS is PN unsolvable";
	}

	@Override
	public String getLongDescription() {
		return "Searches for binary sequences in the LTS that make the LTS Petri net unsolvable. "
			+ "The considered binary sequences are always of the form vw* with the possiblity "
			+ "of v=empty or w=empty but not both. To decide if a sequence makes the LTS unsolvable "
			+ "patten matching is used. In order to do the matching some sequences with a "
			+ "non-empty w must be unrolled. The optional parameter allows to configure "
			+ "how many unrolls will be performed before matching. Independently from that "
			+ "setting at least two unrolls will be performed to check cases that can be checked "
			+ "exhaustively. For a completely exhaustive check an infinite number of unrolls "
			+ "would have to be performed so the setting determines how thorough the check is.";
	}

	@Override
	public Category[] getCategories() {
		return new Category[] { Category.LTS };
	}
}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
