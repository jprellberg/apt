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

package uniol.apt.analysis.exception;

import uniol.apt.module.exception.ModuleException;

/**
 * A PreconditionFailedException is thrown when the input to an analysis does not satisfy the analysis' precondition.
 * @author Uli Schlachter, vsp
 */
public class PreconditionFailedException extends ModuleException {
	// Chosen by fair dice roll. Guaranteed to be random.
	public static final long serialVersionUID = 4L;

	/**
	 * Constructor creates a new PreconditionFailedException with message @message.
	 * @param message A string containing a describing message.
	 */
	public PreconditionFailedException(String message) {
		super(message);
	}

	/**
	 * Constructor creates a new PreconditionFailedException with a message @message and a cause @cause.
	 * @param message A string containing a describing message.
	 * @param cause The cause for this exception as Throwable.
	 */
	public PreconditionFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
