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

package uniol.apt.ui.impl;

import static org.apache.commons.collections4.ListUtils.union;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import uniol.apt.module.Module;
import uniol.apt.module.ModuleOutputSpec;
import uniol.apt.module.exception.NoSuchTransformationException;
import uniol.apt.module.impl.ModuleUtils;
import uniol.apt.module.impl.OptionalParameter;
import uniol.apt.module.impl.Parameter;
import uniol.apt.module.impl.ReturnValue;
import uniol.apt.ui.AptReturnValueTransformation;
import uniol.apt.ui.ParametersTransformer;
import uniol.apt.ui.ReturnValueTransformation;

/**
 * Some utility methods to simplify work with the module system.
 *
 * @author Renke Grunwald
 */
public class UIUtils {

	private UIUtils() {
	}

	/**
	 * Returns a usage string describing how to use the module.
	 *
	 * @param module
	 *                module in question
	 * @param parametersTransformer
	 *                transformer for getting descriptions of the string
	 *                transformations
	 * @return usage string
	 * @throws NoSuchTransformationException
	 *                 thrown if there is no applicable transformation for a
	 *                 parameter
	 */
	public static String getModuleUsage(Module module, ParametersTransformer parametersTransformer)
			throws NoSuchTransformationException {
		List<Parameter> parameters = ModuleUtils.getParameters(module);
		List<OptionalParameter<?>> optionalParameters = ModuleUtils.getOptionalParameters(module);
		List<ReturnValue> fileReturnValues = getModuleFileReturnValues(module);

		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("Usage: apt %s", module.getName());

		for (Parameter parameter : parameters) {
			formatter.format(" <%s>", parameter.getName());
		}

		for (Parameter parameter : optionalParameters) {
			formatter.format(" [<%s>]", parameter.getName());
		}

		for (ReturnValue fileReturnValue : fileReturnValues) {
			formatter.format(" [<%s>]", fileReturnValue.getName());
		}
		sb.append("\n");

		for (Parameter parameter : parameters) {
			formatter.format("  %-10s %s%n", parameter.getName(), parameter.getDescription());
		}
		for (OptionalParameter<?> parameter : optionalParameters) {
			Object def = parameter.getDefaultValueString();
			String extra = "";
			if (def != null)
				extra = String.format(" The default value is '%s'.", def);
			formatter.format("  %-10s %s%s%n", parameter.getName(), parameter.getDescription(), extra);
		}
		for (ReturnValue value : fileReturnValues) {
			formatter.format("  %-10s Optional file name for writing the output to%n", value.getName());
		}

		formatter.format("\n%s", module.getLongDescription());

		boolean printedHeader = false;
		for (Parameter parameter : union(parameters, optionalParameters)) {
			String desc = parametersTransformer.getTransformationDescription(parameter.getKlass());
			if ("".equals(desc))
				continue;

			if (!printedHeader)
				formatter.format("%n%nFormat descriptions of parameters:");
			printedHeader = true;
			formatter.format("%n%n%s:%n%s", parameter.getName(), desc);
		}

		formatter.close();
		return sb.toString();
	}

	/**
	 * Returns true if the given return value may be written to a file. This
	 * is determined by checking for the file property and type annotations.
	 */
	public static boolean isFileReturnValue(ReturnValue retVal) {
		ReturnValueTransformation<?> transform = AptReturnValuesTransformer.INSTANCE
				.getTransformation(retVal.getKlass());
		if (transform == null) {
			return false;
		}
		AptReturnValueTransformation annotation = transform.getClass()
				.getAnnotation(AptReturnValueTransformation.class);
		return annotation.fileDestination() || retVal.hasProperty(ModuleOutputSpec.PROPERTY_FILE);
	}

	/**
	 * Returns a list of all return values that may be written to a file.
	 *
	 * @param module
	 *                module in question
	 * @return list of return values with file property or type annotation
	 */
	public static List<ReturnValue> getModuleFileReturnValues(Module module) {
		List<ReturnValue> result = new ArrayList<>();
		for (ReturnValue retVal : ModuleUtils.getReturnValues(module)) {
			if (isFileReturnValue(retVal)) {
				result.add(retVal);
			}
		}
		return result;
	}

}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
