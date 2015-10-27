/*-
 * APT - Analysis of Petri Nets and labeled Transition systems
 * Copyright (C) 2014-2015  vsp
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

package uniol.apt.io.parser.impl;

import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import org.apache.commons.collections4.MapUtils;

import uniol.apt.adt.exception.DatastructureException;
import uniol.apt.adt.ts.Arc;
import uniol.apt.adt.ts.State;
import uniol.apt.adt.ts.TransitionSystem;
import uniol.apt.io.parser.LTSParser;
import uniol.apt.io.parser.ParseException;

/**
 * apt format parser
 *
 * @author vsp
 */
public class AptLTSParser extends AbstractLTSParser implements LTSParser {
	private static class NameDescStateLabelListener extends AptLTSFormatBaseListener
			implements AptLTSFormatListener {
		private final TransitionSystem ts;
		private final Map<String, Map<String, Object>> labelOpts;
		private Map<String, Object> curOpts;
		private int initCount;

		private NameDescStateLabelListener(TransitionSystem ts, Map<String, Map<String, Object>> labelOpts) {
			this.ts        = ts;
			this.labelOpts = labelOpts;
			this.initCount = 0;
		}

		@Override
		public void exitTs(AptLTSFormatParser.TsContext ctx) {
			if (this.initCount < 1) {
				throw new ParseCancellationException(new ParseException("Initial state not found"));
			}
		}

		@Override
		public void exitName(AptLTSFormatParser.NameContext ctx) {
			String str = ctx.STR().getText();
			this.ts.setName(str.substring(1, str.length() - 1));
		}

		@Override
		public void exitDescription(AptLTSFormatParser.DescriptionContext ctx) {
			String str = ctx.txt.getText();
			this.ts.putExtension("description", str.substring(1, str.length() - 1));
		}

		@Override
		public void enterOpts(AptLTSFormatParser.OptsContext ctx) {
			this.curOpts = new HashMap<>();
		}

		@Override
		public void exitOption(AptLTSFormatParser.OptionContext ctx) {
			assert this.curOpts != null;

			Object val = ctx.ID().getText();

			if (ctx.STR() != null) {
				String str = ctx.STR().getText();
				val = str.substring(1, str.length() - 1);
			} else if (ctx.INT() != null) {
				val = Integer.parseInt(ctx.INT().getText());
			}

			this.curOpts.put(ctx.ID().getText(), val);
		}

		@Override
		public void exitState(AptLTSFormatParser.StateContext ctx) {
			State s = this.ts.createState(ctx.idi().getText());

			if (this.curOpts == null)
				return;

			// Extensible really needs a putExtensions method ...
			for (Map.Entry<String, Object> entry : this.curOpts.entrySet()) {
				if ("initial".equals(entry.getKey())) {
					if (this.initCount++ > 0) {
						throw new ParseCancellationException(new ParseException(
								"Multiple states are marked as initial state."));
					}
					this.ts.setInitialState(s);
				} else {
					s.putExtension(entry.getKey(), entry.getValue());
				}
			}

			this.curOpts = null;
		}

		@Override
		public void exitLabel(AptLTSFormatParser.LabelContext ctx) {
			this.labelOpts.put(ctx.idi().getText(), MapUtils.emptyIfNull(this.curOpts));
			this.curOpts = null;
		}
	}

	private static class ArcListener extends AptLTSFormatBaseListener implements AptLTSFormatListener {
		private final TransitionSystem ts;
		private final Map<String, Map<String, Object>> labelOpts;

		private ArcListener(TransitionSystem ts, Map<String, Map<String, Object>> labelOpts) {
			this.ts        = ts;
			this.labelOpts = labelOpts;
		}

		@Override
		public void exitArc(AptLTSFormatParser.ArcContext ctx) {
			Map<String, Object> extensions = this.labelOpts.get(ctx.labell.getText());
			if (extensions == null) {
				throw new ParseCancellationException(new ParseException("Unknown label found"));
			}
			Arc a = this.ts.createArc(ctx.src.getText(), ctx.dest.getText(), ctx.labell.getText());

			// Extensible really needs a putExtensions method ...
			for (Map.Entry<String, Object> entry : extensions.entrySet()) {
				a.putExtension(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public TransitionSystem parseLTS(InputStream is) throws ParseException {
		CharStream input;
		try {
			input             = new ANTLRInputStream(is);
		} catch (IOException ex) {
			throw new ParseException(ex.getMessage(), ex);
		}
		AptLTSFormatLexer lexer   = new AptLTSFormatLexer(input);
		lexer.removeErrorListeners(); // don't spam on stderr
		lexer.addErrorListener(new ThrowingErrorListener());
		CommonTokenStream tokens  = new CommonTokenStream(lexer);
		AptLTSFormatParser parser = new AptLTSFormatParser(tokens);
		parser.removeErrorListeners(); // don't spam on stderr
		parser.addErrorListener(new ThrowingErrorListener());
		parser.setBuildParseTree(true);
		ParseTree tree;
		try {
			tree              = parser.ts();
		} catch (ParseCancellationException ex) {
			throw new ParseException(ex.getMessage(), ex);
		}
		TransitionSystem ts       = new TransitionSystem();
		Map<String, Map<String, Object>> labelOpts = new HashMap<>();
		try {
			ParseTreeWalker.DEFAULT.walk(new NameDescStateLabelListener(ts, labelOpts), tree);
			ParseTreeWalker.DEFAULT.walk(new ArcListener(ts, labelOpts), tree);
		} catch (DatastructureException | ParseCancellationException ex) {
			throw new ParseException(ex.getMessage(), ex);
		}

		return ts;
	}
}

// vim: ft=java:noet:sw=8:sts=8:ts=8:tw=120