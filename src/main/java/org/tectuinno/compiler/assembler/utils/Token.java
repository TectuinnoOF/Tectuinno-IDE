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

package org.tectuinno.compiler.assembler.utils;

/**
 * Represents a lexical token produced during the analysis of assembly source code.
 * <p>
 * Each token contains a {@link TokenType} indicating its classification,
 * the raw text value matched in the source, and its starting position in the input.
 * </p>
 * 
 * <p>Example: <br>
 * {@code Token(INSTRUCTION, "addi", 42)}</p>
 * 
 * @author Tectuinno
 * @version 1.0
 * @since 2025-07-12
 */
public class Token {
	
	/** The category or type of the token (e.g., INSTRUCTION, REGISTER, COMMENT). */
    private final TokenType type;

    /** The exact text value matched from the source code. */
    private final String value;

    /** The character position in the input where this token begins. */
    private final int position;

    /** The character line in the input where this tokens begins */
    private final int line;

    /** The character column in the input where this token begins */
    private final int column;
    
    /**
     * Constructs a new {@code Token} with the specified type, value, and position.
     *
     * @param type the type of token (e.g., instruction, label)
     * @param value the matched string from the input
     * @param position the starting position in the input string
     * @param line the line location of the input character
     * @param column column to find character
     */
	public Token(TokenType type, String value, int position, int line, int column) {
		this.type = type;
		this.value = value;
		this.position = position;
        this.line = line;
        this.column = column;

	}

	/**
     * Returns the {@link TokenType} of this token.
     *
     * @return the token type
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Returns the string value matched in the input.
     *
     * @return the token's text
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the index in the input where this token starts.
     *
     * @return the starting character index of the token
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns the line number in the input where this token starts.
     *
     * @return the line number of the token
     */
    public int getLine(){
        return line;
    }

    /**
     * Returns the column number in the input where this token starts.
     *
     * @return the column number of the token
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns a string representation of the token for debugging and display.
     * Format: {@code Token(TYPE, "value", position)}
     *
     * @return a formatted string describing the token
     */
	@Override
	public String toString() {
		return String.format("Token (%s, \"%s\", %d, linea: %d, columna: %d)", type, value, position,line,column);
	}
}
