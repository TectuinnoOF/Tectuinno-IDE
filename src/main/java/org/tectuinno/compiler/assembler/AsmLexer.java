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
import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;

/**
 * Performs lexical analysis on Tectuinno RISC‑V assembly source code.
 * <p>
 * This lexer reads the input character by character and produces a list of
 * {@link Token}, classifying each fragment as instructions, registers, labels,
 * immediates, comments, parentheses, commas, or unknown tokens.
 * </p>
 * <p>
 * It relies on definitions from
 * {@link org.tectuinno.view.assembler.utils.AsmSyntaxDictionary}, ensuring
 * consistency with syntax highlighting and later parser stages.
 * </p>
 *
 * <p>
 * <strong>Token types produced include:</strong>
 * </p>
 * <ul>
 * <li>{@link TokenType#INSTRUCTION}</li>
 * <li>{@link TokenType#REGISTER}</li>
 * <li>{@link TokenType#LABEL}</li>
 * <li>{@link TokenType#IMMEDIATE}</li>
 * <li>{@link TokenType#COMMENT}</li>
 * <li>{@link TokenType#LEFT_PAREN}, {@link TokenType#RIGHT_PAREN}</li>
 * <li>{@link TokenType#COMMA}, {@link TokenType#COLON}</li>
 * <li>{@link TokenType#UNKNOWN} for unrecognized tokens</li>
 * </ul>
 *
 * @author Tectuinno
 * @version 1.0
 * @since 2025-07-12
 */
public class AsmLexer {

	private final String source;
	private int position;

	/**
	 * Constructs the lexer for the given source code.
	 *
	 * @param source the raw assembler source text to tokenize; must not be
	 *               {@code null}
	 */
	public AsmLexer(String source) {
		this.source = source;
		this.position = 0;
	}

	/**
	 * Extracts a numeric token from the source code, starting at the current
	 * position.
	 * <p>
	 * This method supports both decimal and hexadecimal formats:
	 * </p>
	 * <ul>
	 * <li>Hexadecimal numbers must start with the prefix {@code 0x} and may contain
	 * digits from {@code 0-9} and letters {@code A-F/a-f}.</li>
	 * <li>Decimal numbers consist only of digits {@code 0-9}.</li>
	 * </ul>
	 *
	 * @return a {@link Token} of type {@link TokenType#IMMEDIATE} representing the
	 *         parsed numeric value.
	 */
	private Token readNumber() {
		int start = position;

		if (!isAtEnd() && peek() == '0' && peekNext() == 'x') {
			advance(); // 0 /*Reading hex values as inmediate*/
			advance(); // x

			while (!isAtEnd() && isHexDigit(peek())) {
				advance();
			}

			String hexValue = source.substring(start, position);
			return new Token(TokenType.IMMEDIATE, hexValue, start);
		}

		/**
		 * lopp to reading a normal Number, immediate value for registers in teha asm
		 * code
		 */
		while (!isAtEnd() && Character.isDigit(peek())) {
			advance();
		}
		String value = source.substring(start, position);
		return new Token(TokenType.IMMEDIATE, value, start);
	}

	/**
	 * Checks whether a given character is a valid hexadecimal digit.
	 * <p>
	 * A character is considered a hexadecimal digit if it is:
	 * <ul>
	 * <li>A decimal digit between '0' and '9'</li>
	 * <li>A lowercase letter between 'a' and 'f'</li>
	 * <li>An uppercase letter between 'A' and 'F'</li>
	 * </ul>
	 *
	 * @param c the character to check
	 * @return {@code true} if the character is a valid hexadecimal digit;
	 *         {@code false} otherwise
	 */
	private boolean isHexDigit(char c) {
		return Character.isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}
	
	/**
	 * Checks whether a given string matches any of the supported assembler instruction mnemonics.
	 * <p>
	 * The method compares the provided string against the list of valid instructions
	 * defined in {@link AsmSyntaxDictionary#INSTRUCTIONS}.
	 *
	 * @param value the string to check
	 * @return {@code true} if the string is a recognized instruction; {@code false} otherwise
	 */
	private boolean isInstruction(String value) {
		for (String instr : AsmSyntaxDictionary.INSTRUCTIONS) {
			if (instr.equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks whether a given string matches any of the defined register names.
	 * <p>
	 * The method compares the provided string against the list of registers
	 * defined in {@link AsmSyntaxDictionary#REGISTERS}.
	 *
	 * @param value the string to check
	 * @return {@code true} if the string is a valid register name; {@code false} otherwise
	 */
	private boolean isRegister(String value) {
		for (String reg : AsmSyntaxDictionary.REGISTERS) {
			if (reg.equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks whether the lexer has reached the end of the source code.
	 *
	 * @return {@code true} if all characters have been processed; {@code false} otherwise
	 */
	private boolean isAtEnd() {
		return position >= source.length();
	}
	
	/**
	 * Returns the current character in the source without advancing the position.
	 *
	 * @return the current character being analyzed
	 * @throws StringIndexOutOfBoundsException if called when at the end of input
	 */
	private char peek() throws IndexOutOfBoundsException{
		return source.charAt(position);
	}
	
	/**
	 * Returns the next character in the source without advancing the position.
	 *
	 * @return the next character, or {@code '\0'} if there is no next character (end of source)
	 */
	private char peekNext() {
		if (position + 1 >= source.length())
			return '\0';
		return source.charAt(position + 1);
	}
	
	/**
	 * Advances the current position by one character.
	 * <p>
	 * This should be used after successfully reading or consuming a character.
	 */
	private void advance() {
		position++;
	}
	
	/**
	 * Reads an identifier from the source code and determines its type.
	 * <p>
	 * This method handles three possible scenarios:
	 * <ul>
	 *   <li><b>Labels</b>: Identifiers followed by a colon ':' (e.g., {@code start:})</li>
	 *   <li><b>Instructions</b>: Mnemonics defined in {@link AsmSyntaxDictionary#INSTRUCTIONS}</li>
	 *   <li><b>Registers</b>: Names like {@code x0}, {@code x1}, etc., defined in {@link AsmSyntaxDictionary#REGISTERS}</li>
	 *   <li><b>Unknown</b>: Any other identifier not matching the previous categories</li>
	 * </ul>
	 *
	 * @return a {@link Token} representing the identified element, including its type and position in the source
	 */
	private Token readIdentifierOrInstruction() {
		int start = position;
		while (!isAtEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
			advance();
		}

		// Verificar si termina con dos puntos para considerar etiqueta
		if (!isAtEnd() && peek() == ':') {
			advance();
			String label = source.substring(start, position);
			return new Token(TokenType.LABEL, label, start);
		}

		String value = source.substring(start, position);

		if (isRegister(value)) {
			return new Token(TokenType.REGISTER, value, start);
		} else if (isInstruction(value)) {
			return new Token(TokenType.INSTRUCTION, value, start);
		} else {
			// No reconocido
			return new Token(TokenType.UNKNOWN, value, start);
		}
	}
	
	/**
	 * Reads a multi-line comment from the source code.
	 * <p>
	 * Comments in this assembler language start with {@code /*} and end with {@code *&#47;}.
	 * The lexer advances through the characters until the closing sequence is found.
	 * If the end of the source is reached before the closing, the comment is returned as-is.
	 *
	 * @return a {@link Token} of type {@link TokenType#COMMENT} representing the full comment block
	 */
	private Token readComment() {
		int start = position;
		advance(); // '/'
		advance(); // '*'
		while (!isAtEnd() && !(peek() == '*' && peekNext() == '/')) {
			advance();
		}
		if (!isAtEnd()) {
			advance(); // '*'
			advance(); // '/'
		}
		String value = source.substring(start, position);
		return new Token(TokenType.COMMENT, value, start);
	}

	/**
	 * Tokenizes the source code into a list of {@link Token}.
	 *
	 * This method scans the source sequentially, identifies token types using
	 * character classification and syntax definitions, and returns the complete
	 * list.
	 *
	 * @return a list of tokens found in the source
	 */
	public List<Token> tokenize() {
		List<Token> tokens = new ArrayList<>();

		while (!isAtEnd()) {
			char current = peek();

			if (Character.isWhitespace(current)) {
				advance(); // Ignore spaces and newlines
			} else if (current == '/') {
				if (peekNext() == '*') {
					tokens.add(readComment());
				} else {
					tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(current), position));
					advance();
				}
			} else if (Character.isLetter(current) || current == '_') {
				tokens.add(readIdentifierOrInstruction());
			} else if (Character.isDigit(current)) {
				tokens.add(readNumber());
			} else if (current == ',') {
				tokens.add(new Token(TokenType.COMMA, ",", position));
				advance();
			} else if (current == ':') {
				tokens.add(new Token(TokenType.COLON, ":", position));
				advance();
			} else if (current == '(') {
				tokens.add(new Token(TokenType.LEFTPAREN, "(", position));
				advance();
			} else if (current == ')') {
				tokens.add(new Token(TokenType.RIGHTPAREN, ")", position));
				advance();
			} else {
				tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(current), position));
				advance();
			}
		}

		return tokens;
	}

}
