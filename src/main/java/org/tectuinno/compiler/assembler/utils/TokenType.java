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
 * Enumeration of all possible types of tokens recognized by the Tectuinno RISC-V assembler lexer.
 * <p>
 * These types are used to classify fragments of assembly code during lexical analysis.
 * </p>
 * 
 * @author Tectuinno
 * @version 1.0
 * @since 2025-07-12
 */
public enum TokenType {
	/**
     * Represents an instruction keyword (e.g., {@code addi}, {@code lw}, {@code jalr}).
     */
    INSTRUCTION,

    /**
     * Represents a register (e.g., {@code x0} to {@code x31}).
     */
    REGISTER,

    /**
     * Represents a label declaration in the code (e.g., {@code loop:} or {@code _start:}).
     */
    LABEL,

    /**
     * Represents an immediate value (e.g., numeric literals such as {@code 10}, {@code 0xFF}).
     */
    IMMEDIATE,

    /**
     * Represents a block comment
     */
    COMMENT,

    /**
     * Represents a comma (used to separate instruction arguments).
     */
    COMMA,

    /**
     * Represents a colon, typically after labels.
     */
    COLON,
    
    /**
     * Represents a left parenthesis {@code (}, used in memory address expressions.
     */
    LEFTPAREN,
    
    /**
     * Represents a right parenthesis {@code )}, used in memory address expressions.
     */
    RIGHTPAREN,

    /**
     * Represents any unrecognized or invalid token.
     */
    UNKNOWN
}
