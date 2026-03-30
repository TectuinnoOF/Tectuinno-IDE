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

import org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary;
import org.tectuinno.compiler.assembler.utils.IrKind;
import org.tectuinno.compiler.assembler.utils.SymbolTable;
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

	/**
	 * Encapsulates the result of the first assembler pass.
	 *
	 * @param symbols symbol table populated with discovered labels
	 * @param lines   generated intermediate representation lines
	 */
	public record Result(SymbolTable symbols, List<IrLine> lines) {
	}

	/** List of tokens produced by the lexer for the source assembly file. */
	private final List<Token> tokens;

	/** Current reading position within the token list. */
	private int position;	

	private int textPc;
	private int dataPc;

	private String currentSection;	

	/**
	 * Creates a first-pass processor with default section bases.
	 *
	 * <p>
	 * Both sections start at address {@code 0x00000000}.
	 * </p>
	 *
	 * @param tokens token stream produced by the lexer
	 */
	public AsmFirstPass(List<Token> tokens) {
		this(tokens, 0, 0);
	}

	/**
	 * Creates a first-pass processor with explicit section bases.
	 *
	 * @param tokens token stream produced by the lexer
	 * @param textStartPc initial address for the text section
	 * @param dataStartPc initial address for the data section
	 */
	public AsmFirstPass(List<Token> tokens, int textStartPc, int dataStartPc) {
		this.tokens = tokens;
		this.position = 0;
		this.textPc = textStartPc;
		this.dataPc = dataStartPc;
		this.currentSection = AsmSyntaxDictionary.TEXT;
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
			String lastLabel = collectLabels(symbols);

			if (match(TokenType.INSTRUCTION)) {
				lines.add(buildInstructionLine(lastLabel));
				continue;
			}

			if (match(TokenType.DIRECTIVE)) {
				lines.add(buildDirectiveOrDataLine(lastLabel));
				continue;
			}

			// If the token is none of the above, advance to avoid stalling.
			advance();
		}

		return new Result(symbols, lines);
	}

	/**
	 * Builds an IR line for a directive or data-producing directive.
	 *
	 * @param label optional label attached to the directive
	 * @return directive/data IR line
	 */
	private IrLine buildDirectiveOrDataLine(String label) {

		Token directive = previous();
		String mnemonic = directive.getValue();
		List<Token> args = collectDirectiveArgsByMnemonic(mnemonic);
		String original = rebuildOriginal(mnemonic, args);
		int pc = currentPc();

		IrKind kind = isDataDirective(mnemonic) ? IrKind.DATA : IrKind.DIRECTIVE;

		applyDirectiveSideEffects(mnemonic, args);

		return new IrLine(pc, label, kind, mnemonic, args, original);

	}

	/**
	 * Applies side effects produced by a directive.
	 *
	 * <p>
	 * Side effects may include:
	 * </p>
	 * <ul>
	 * <li>changing the active section</li>
	 * <li>advancing the section address counter</li>
	 * <li>ignoring metadata-only directives</li>
	 * </ul>
	 *
	 * @param mnemonic directive mnemonic
	 * @param args     directive arguments
	 */
	private void applyDirectiveSideEffects(String mnemonic, List<Token> args) {

		switch (mnemonic) {
		case AsmSyntaxDictionary.TEXT:
			currentSection = AsmSyntaxDictionary.TEXT;
			return;

		case AsmSyntaxDictionary.DATA:
			currentSection = AsmSyntaxDictionary.DATA;
			return;

		case AsmSyntaxDictionary.SECTION:
			processSectionDirective(args);
			return;

		case AsmSyntaxDictionary.GLOBAL:
			return;

		case AsmSyntaxDictionary.WORD:
			advanceCurrentPc(countDataItems(args) * 4);
			return;

		case AsmSyntaxDictionary.HALF:
			advanceCurrentPc(countDataItems(args) * 2);
			return;

		case AsmSyntaxDictionary.BYTE:
			advanceCurrentPc(countDataItems(args));
			return;

		case AsmSyntaxDictionary.ASCIZ:
			advanceCurrentPc(computeAscizSize(args));
			return;

		default:
			return;
		}

	}

	/**
	 * Computes the memory size required by an {@code asciz} directive.
	 *
	 * <p>
	 * Size = string length + null terminator.
	 * </p>
	 *
	 * @param args directive argument list
	 * @return number of bytes required
	 */
	private int computeAscizSize(List<Token> args) {

		if (args.isEmpty()) {
			return 1;
		}

		Token first = args.get(0);

		if (first.getType() != TokenType.STRING) {
			return 1;
		}

		return first.getValue().length() + 1;

	}

	/**
	 * Counts the number of data items in a directive argument list.
	 *
	 * <p>
	 * Only value-carrying tokens are counted. Commas are ignored.
	 * </p>
	 *
	 * @param args directive argument list
	 * @return number of data elements
	 */
	private int countDataItems(List<Token> args) {

		int count = 0;

		for (Token token : args) {
			if (token.getType() == TokenType.IMMEDIATE || token.getType() == TokenType.CHAR) {
				count++;
			}
		}

		return count;

	}

	/**
	 * Processes the operand of the {@code section} directive.
	 *
	 * <p>
	 * Only {@code text} and {@code data} are handled in this phase. Unsupported
	 * section names are ignored.
	 * </p>
	 *
	 * @param args directive arguments
	 */
	private void processSectionDirective(List<Token> args) {

		if (args.isEmpty()) {
			return;
		}

		String sectionName = args.get(0).getValue();

		if (AsmSyntaxDictionary.TEXT.equals(sectionName)) {
			currentSection = AsmSyntaxDictionary.TEXT;
			return;
		}

		if (AsmSyntaxDictionary.DATA.equals(sectionName)) {
			currentSection = AsmSyntaxDictionary.DATA;
		}

	}

	/**
	 * Indicates whether the given directive produces bytes in memory.
	 *
	 * @param mnemonic directive mnemonic
	 * @return {@code true} if the directive generates data bytes
	 */
	private boolean isDataDirective(String mnemonic) {

		return AsmSyntaxDictionary.WORD.equals(mnemonic)
				|| AsmSyntaxDictionary.BYTE.equals(mnemonic)
				|| AsmSyntaxDictionary.HALF.equals(mnemonic)
				|| AsmSyntaxDictionary.ASCIZ.equals(mnemonic);

	}
	
	/**
	 * Collects directive arguments according to the directive kind.
	 *
	 * @param mnemonic directive mnemonic
	 * @return collected directive arguments
	 */
	private List<Token> collectDirectiveArgsByMnemonic(String mnemonic) {
		if (AsmSyntaxDictionary.SECTION.equals(mnemonic)) {
			return collectSectionArgs();
		}

		if (AsmSyntaxDictionary.GLOBAL.equals(mnemonic)) {
			return collectGlobalArgs();
		}

		if (AsmSyntaxDictionary.ASCIZ.equals(mnemonic)) {
			return collectAscizArgs();
		}

		if (AsmSyntaxDictionary.WORD.equals(mnemonic)
				|| AsmSyntaxDictionary.BYTE.equals(mnemonic)
				|| AsmSyntaxDictionary.HALF.equals(mnemonic)) {
			return collectDataArgs();
		}

		return List.of();
	}
	
	/**
	 * Collects arguments for the {@code asciz} directive.
	 *
	 * @return collected arguments
	 */
	private List<Token> collectAscizArgs() {
		List<Token> args = new ArrayList<>();

		if (!isAtEnd() && check(TokenType.STRING)) {
			args.add(advance());
		}

		return args;
	}
	
	/**
	 * Collects arguments for the {@code section} directive.
	 *
	 * @return collected arguments
	 */
	private List<Token> collectSectionArgs() {
		List<Token> args = new ArrayList<>();

		if (!isAtEnd() && check(TokenType.DIRECTIVE)) {
			args.add(advance());
		}

		return args;
	}
	
	/**
	 * Collects arguments for the {@code global} directive.
	 *
	 * @return collected arguments
	 */
	private List<Token> collectGlobalArgs() {
		List<Token> args = new ArrayList<>();

		if (!isAtEnd() && check(TokenType.UNKNOWN)) {
			args.add(advance());
		}

		return args;
	}
	
	/**
	 * Collects arguments for data directives such as {@code word}, {@code byte},
	 * and {@code half}.
	 *
	 * @return collected arguments
	 */
	private List<Token> collectDataArgs() {
		List<Token> args = new ArrayList<>();

		while (!isAtEnd()) {
			if (check(TokenType.IMMEDIATE) || check(TokenType.CHAR)) {
				args.add(advance());
				consumeOptionalComma(args);
				continue;
			}

			break;
		}

		return args;
	}
	
	

	/**
	 * Builds an IR line for a standard executable instruction.
	 * 
	 * @param label optional label attached to the instruction
	 * @return
	 */
	private IrLine buildInstructionLine(String label) {

		Token instruction = previous();
		List<Token> args = collectInstructionArgs();
		String mnemonic = instruction.getValue();
		String original = rebuildOriginal(mnemonic, args);
		int pc = currentPc();

		this.advanceCurrentPc(4);

		return new IrLine(pc, label, IrKind.INSTRUCTION, mnemonic, args, original);
	}

	/**
	 * Advances the address counter of the active section.
	 *
	 * @param size number of bytes to advance
	 */
	private void advanceCurrentPc(int size) {

		if (size <= 0) {
			return;
		}

		if (isDataSection()) {
			dataPc += size;
			return;
		}

		textPc += size;

	}

	/**
	 * Indicates whether the active section is {@code data}.
	 *
	 * @return {@code true} if the current section is data
	 */
	private boolean isDataSection() {

		return AsmSyntaxDictionary.DATA.equals(currentSection);

	}

	/**
	 * Collects operands belonging to an instruction.
	 *
	 * <p>
	 * Supported token types:
	 * </p>
	 * <ul>
	 * <li>{@link TokenType#REGISTER}</li>
	 * <li>{@link TokenType#IMMEDIATE}</li>
	 * <li>{@link TokenType#UNKNOWN}</li>
	 * <li>{@link TokenType#LABEL}</li>
	 * <li>parenthesized register addressing</li>
	 * </ul>
	 *
	 * @return collected instruction arguments
	 */
	private List<Token> collectInstructionArgs() {

		List<Token> args = new ArrayList<>();

		while (!isAtEnd()) {

			if (check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.UNKNOWN)){
				args.add(advance());
				consumeOptionalComma(args);
				continue;
			}

			if (match(TokenType.LEFTPAREN)) {
				args.add(previous());

				if (check(TokenType.REGISTER)) {
					args.add(advance());
				}

				if (match(TokenType.RIGHTPAREN)) {
					args.add(previous());
				}

				consumeOptionalComma(args);
				continue;
			}

			break;
		}

		return args;

	}

	/**
	 * Consumes a trailing comma if present and appends it to the current argument
	 * list.
	 *
	 * @param args argument list being built
	 */
	private void consumeOptionalComma(List<Token> args) {

		if (!match(TokenType.COMMA)) {
			return;
		}

		args.add(previous());

	}

	/**
	 * Collects one or more consecutive label declaration an defines them in the
	 * symbol table using the current section address
	 * 
	 * @param symbols -> symbol table beign populated
	 * @return the last label found before the current logical address, or
	 *         {@code null}
	 */
	private String collectLabels(SymbolTable symbols) {

		String lastLabel = null;

		while (match(TokenType.LABEL)) {
			String raw = previous().getValue();
			String name = raw.endsWith(":") ? raw.substring(0, raw.length() - 1) : raw;

			symbols.define(name, currentPc());
			lastLabel = name;
		}

		return lastLabel;
	}

	/**
	 * Returns the current logical address of the active section.
	 *
	 * @return current section address
	 */
	private int currentPc() {

		return isDataSection() ? dataPc : textPc;

	}

	
	/**
	 * Rebuilds a readable source-like representation from a mnemonic and its
	 * argument list.
	 *
	 * @param mnemonic instruction or directive mnemonic
	 * @param args     argument list
	 * @return reconstructed source line
	 */
	private String rebuildOriginal(String mnemonic, List<Token> args) {

		if (args == null || args.isEmpty()) {
			return mnemonic;
		}

		StringBuilder sb = new StringBuilder(mnemonic);

		for (Token arg : args) {
			if (arg.getType() == TokenType.COMMA) {
				sb.append(arg.getValue());
				continue;
			}

			if (arg.getType() == TokenType.STRING) {
				sb.append(' ').append('"').append(arg.getValue()).append('"');
				continue;
			}

			if (arg.getType() == TokenType.CHAR) {
				sb.append(' ').append('\'').append(arg.getValue()).append('\'');
				continue;
			}

			sb.append(' ').append(arg.getValue());
		}

		return sb.toString().replace(" ,", ",");

	}

}
