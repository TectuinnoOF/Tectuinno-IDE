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
 * Performs the first pass of the assembler process.
 * <p>
 * This class reads a tokenized assembly source and:
 * <ul>
 * <li>Identifies and stores labels with their corresponding memory
 * addresses</li>
 * <li>Builds an intermediate representation ({@link IrLine}) for each
 * instruction</li>
 * <li>Increments the program counter ({@code pc}) for each instruction
 * encountered</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <table border="1">
 * <caption>AsmFirstPass Responsibilities</caption>
 * <tr>
 * <th>Method</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link #run()}</td>
 * <td>Executes the first pass, producing a symbol table and a list of
 * intermediate lines</td>
 * </tr>
 * <tr>
 * <td>{@link #collectArgs()}</td>
 * <td>Collects argument tokens (registers, immediates, unknowns, or
 * parenthesized values)</td>
 * </tr>
 * <tr>
 * <td>{@link #rebuildOriginal(String, List)}</td>
 * <td>Rebuilds a readable source line from mnemonic and argument tokens</td>
 * </tr>
 * </table>
 *
 * @author Pablo
 * @version 1.0
 * @since 2025-08-14
 */
public final class AsmFirstPass {

	/** List of tokens produced by the lexer for the source assembly file. */
	private final List<Token> tokens;

	/** Current reading position within the token list. */
	private int position = 0;

	/** Current program counter (memory address) being processed. */
	private int pc;

	public AsmFirstPass(List<Token> tokens, int startPc) {
		this.tokens = tokens;
		this.pc = startPc;
	}

	/**
	 * Checks if the parser has reached the end of the token stream.
	 *
	 * @return {@code true} if all tokens have been processed; {@code false}
	 *         otherwise
	 */
	private boolean isAtEnd() {
		return position >= tokens.size();
	}

	/**
	 * Retrieves the current token without advancing the position.
	 *
	 * @return the current {@link Token}
	 */
	private Token peek() {
		return tokens.get(position);
	}

	/**
	 * Retrieves the previous token.
	 *
	 * @return the previous {@link Token}
	 */
	private Token previous() {
		return tokens.get(position - 1);
	}

	/**
	 * Checks if the current token matches a given type.
	 *
	 * @param type the {@link TokenType} to check against
	 * @return {@code true} if the token matches; {@code false} otherwise
	 */
	private boolean check(TokenType type) {
		return !isAtEnd() && peek().getType() == type;
	}

	/**
	 * Advances the token position by one and returns the previous token.
	 *
	 * @return the previous {@link Token}
	 */
	private Token advance() {
		if (!isAtEnd())
			position++;
		return previous();
	}

	/**
	 * If the current token matches the given type, consumes it and returns
	 * {@code true}.
	 *
	 * @param type the {@link TokenType} to check for
	 * @return {@code true} if a match was found and consumed; {@code false}
	 *         otherwise
	 */
	private boolean match(TokenType type) {
		if (check(type)) {
			advance();
			return true;
		}
		return false;
	}

	/**
	 * Represents the result of the first assembler pass, including the symbol table
	 * and IR lines.
	 */
	public static final class Result {

		/** The symbol table with label definitions and addresses. */
		public final SymbolTable symbols;

		/** The list of intermediate representation lines generated. */
		public final List<IrLine> lines;

		Result(SymbolTable s, List<IrLine> l) {
			this.symbols = s;
			this.lines = l;
		}
	}

	/**
	 * Executes the first assembler pass.
	 * <p>
	 * This method:
	 * <ul>
	 * <li>Parses labels and stores them in the symbol table</li>
	 * <li>Identifies instructions and their arguments</li>
	 * <li>Builds intermediate representation lines</li>
	 * </ul>
	 *
	 * @return a {@link Result} containing the symbol table and list of IR lines
	 */
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
	 * Collects raw argument tokens until no more argument starters are found.
	 * <p>
	 * Recognized argument types:
	 * <ul>
	 * <li>{@link TokenType#REGISTER}</li>
	 * <li>{@link TokenType#IMMEDIATE}</li>
	 * <li>{@link TokenType#UNKNOWN}</li>
	 * <li>Parenthesized register values</li>
	 * </ul>
	 *
	 * @return a list of {@link Token} objects representing the arguments
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

	/**
	 * Rebuilds a readable {@code "mnemonic arg1, arg2, ..."} string from tokens.
	 *
	 * @param mnemonic the instruction mnemonic
	 * @param args     the list of argument tokens
	 * @return a reconstructed assembly line as a string
	 */
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
