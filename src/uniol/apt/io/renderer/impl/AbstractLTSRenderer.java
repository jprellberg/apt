/*-
 * APT - Analysis of Petri Nets and labeled Transition systems
 * Copyright (C) 2015  vsp
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

package uniol.apt.io.renderer.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.StringBuilderWriter;

import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.io.renderer.LTSRenderer;
import uniol.apt.io.renderer.RenderException;

/**
 * Abstract base class for labeled transition system renderers
 *
 * @author vsp
 */
public abstract class AbstractLTSRenderer implements LTSRenderer {
	public void renderFile(TransitionSystem ts, String filename) throws RenderException, IOException {
		renderFile(ts, new File(filename));
	}

	public void renderFile(TransitionSystem ts, File file) throws RenderException, IOException {
		render(ts, FileUtils.openOutputStream(file));
	}

	public void render(TransitionSystem ts, OutputStream os) throws RenderException, IOException {
		render(ts, new BufferedWriter(new OutputStreamWriter(os, "UTF-8")));
	}

	public String render(TransitionSystem ts) throws RenderException {
		Writer writer = new StringBuilderWriter();
		try {
			render(ts, writer);
		} catch (IOException e) {
			// A StringWriter shouldn't throw IOExceptions
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120
