/*
 * This file is part of Tectuinno IDE.
 *
 * Tectuinno IDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * As a special exception, you may use this file as part of a free software
 * library without restriction. Specifically, if other files instantiate
 * templates or use macros or inline functions from this file, or you compile
 * this file and link it with other files to produce an executable, this
 * file does not by itself cause the resulting executable to be covered by
 * the GNU General Public License. This exception does not however
 * invalidate any other reasons why the executable file might be covered by
 * the GNU General Public License.
 *
 * Copyright 2025 Tectuinno Team (https://github.com/tectuinno)
 */

package org.tectuinno.compiler.assembler;

import java.util.ArrayList;
import java.util.List;

import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;

/**
 * 
 */
public final class AsmFirstPass {

	private final List<Token> tokens;
	private int position = 0;
	private int pc;

	public AsmFirstPass(List<Token> tokens, int startPc) {
		this.tokens = tokens;
		this.pc = startPc;
	}

	// --- token stream helpers ---
	private boolean isAtEnd() {
		return position >= tokens.size();
	}

	private Token peek() {
		return tokens.get(position);
	}

	private Token previous() {
		return tokens.get(position - 1);
	}

	private boolean check(TokenType type) {
		return !isAtEnd() && peek().getType() == type;
	}

	private Token advance() {
		if (!isAtEnd())
			position++;
		return previous();
	}

	private boolean match(TokenType type) {
		if (check(type)) {
			advance();
			return true;
		}
		return false;
	}

	public static final class Result {
		public final SymbolTable symbols;
		public final List<IrLine> lines;

		Result(SymbolTable s, List<IrLine> l) {
			this.symbols = s;
			this.lines = l;
		}
	}

	public Result run() {
		SymbolTable symbols = new SymbolTable();
		List<IrLine> lines = new ArrayList<>();

		while (!isAtEnd()) {
			if (match(TokenType.COMMENT))
				continue;

			// One or more labels can appear before an instruction
			String lastLabel = null;
			while (match(TokenType.LABEL)) {
				String raw = previous().getValue(); // e.g. "inicio:"
				String name = raw.endsWith(":") ? raw.substring(0, raw.length() - 1) : raw;
				symbols.define(name, pc);
				lastLabel = name; // keep the last one; if instruction follows, attach for listing
			}

			if (match(TokenType.INSTRUCTION)) {
				Token instr = previous();
				List<Token> args = collectArgs();
				String mnemonic = instr.getValue();
				String original = rebuildOriginal(mnemonic, args);
				lines.add(new IrLine(pc, lastLabel, mnemonic, args, original));
				pc += 4;
				continue;
			}

			// If the token is none of the above, advance to avoid stalling.
			advance();
		}

		return new Result(symbols, lines);
	}

	/**
	 * Collects raw argument tokens until no more arg-starters are found
	 * (REGISTER/IMMEDIATE/UNKNOWN or parenthesized).
	 */
	private List<Token> collectArgs() {
		List<Token> args = new ArrayList<>();

		while (check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.LEFTPAREN)
				|| check(TokenType.UNKNOWN)) {
			// Atom: reg/imm/unknown
			if (check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.UNKNOWN)) {
				args.add(advance());
			} else if (match(TokenType.LEFTPAREN)) {
				// Capture (reg)
				args.add(previous()); // '('
				if (check(TokenType.REGISTER))
					args.add(advance());
				if (match(TokenType.RIGHTPAREN))
					args.add(previous()); // ')'
			}
			// Optional comma
			if (match(TokenType.COMMA)) {
				args.add(previous()); // keep comma in IR; useful to rebuild original text
			}
		}
		return args;
	}

	/** Rebuild a readable "mnemonic arg1, arg2, ..." string from tokens. */
	private String rebuildOriginal(String mnemonic, List<Token> args) {
		StringBuilder sb = new StringBuilder(mnemonic).append(' ');
		for (int i = 0; i < args.size(); i++) {
			Token t = args.get(i);
			sb.append(t.getValue());
			if (i + 1 < args.size())
				sb.append(' ');
		}
		return sb.toString().trim();
	}

}
