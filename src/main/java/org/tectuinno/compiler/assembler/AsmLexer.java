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

public class AsmLexer {

	private final String source;
	private int position;

	public AsmLexer(String source) {
		this.source = source;
		this.position = 0;
	}

	private Token readNumber() {
		int start = position;
		while (!isAtEnd() && Character.isDigit(peek())) {
			advance();
		}
		String value = source.substring(start, position);
		return new Token(TokenType.IMMEDIATE, value, start);
	}

	private boolean isInstruction(String value) {
		for (String instr : AsmSyntaxDictionary.INSTRUCTIONS) {
			if (instr.equals(value)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRegister(String value) {
		for (String reg : AsmSyntaxDictionary.REGISTERS) {
			if (reg.equals(value)) {
				return true;
			}
		}
		return false;
	}

	private boolean isAtEnd() {
		return position >= source.length();
	}

	private char peek() {
		return source.charAt(position);
	}

	private char peekNext() {
		if (position + 1 >= source.length())
			return '\0';
		return source.charAt(position + 1);
	}

	private void advance() {
		position++;
	}

	private Token readIdentifierOrInstruction() {
		int start = position;
		while (!isAtEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
			advance();
		}
		String value = source.substring(start, position);

		if (isRegister(value)) {
			return new Token(TokenType.REGISTER, value, start);
		} else if (isInstruction(value)) {
			return new Token(TokenType.INSTRUCTION, value, start);
		} else {
			return new Token(TokenType.LABEL, value, start);
		}
	}

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
			} else {
				tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(current), position));
				advance();
			}
		}

		return tokens;
	}

}
