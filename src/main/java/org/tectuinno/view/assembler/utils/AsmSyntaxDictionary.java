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

package org.tectuinno.view.assembler.utils;

/**
 * Provides a centralized dictionary of reserved elements used in Tectuinno's RISC-V assembler. <br> 
 *<p>
 * This utility class defines the supported instruction set, register identifiers,<br>
 * and regular expression patterns for syntax highlighting and lexical analysis<br>
 * in the assembler editor or compiler components of the Tectuinno IDE.<br>
 *
 * It is used in conjunction with syntax highlighters, parsers, and validators<br>
 * to identify keywords, registers, and label declarations in user-written assembly code.<br>
 *
 * This class is not intended to be instantiated.<br>
 * </p>
 */
public final class AsmSyntaxDictionary {
	
	/**
     * A list of supported base RISC-V instructions for the Tectuinno assembler.<br>
     * 
     * These include arithmetic, logic, memory, branch, and jump instructions.
     * Future expansions may include the full RV32I and additional extensions.
     */
	public static final String[] INSTRUCTIONS = {			
			"lw", 
			"addi",
			"slti",
			"ori",
			"andi",
			"sw",
			"add",
			"sub",
			"slt",
			"or",
			"and",
			"beq",
			"jal",
			"jalr",
			"lui"
	};
	
	/**
     * A list of all 32 general-purpose registers available in the Tectuinno Core.
     * 
     * These follow the RISC-V convention: x0 to x31.
     */
	public static final String[] REGISTERS = {
		"x0", "x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9", "x10", "x11",
		"x12", "x13", "x14", "x15", "x16", "x16", "x17", "x18", "x19", "x20",
		"x21", "x22", "x23", "x24", "x25", "x26", "x27", "x28", "x29", "x30", "x31"
	};
	
	/**
     * Regular expression pattern for matching supported instructions.
     * 
     * Matches full words only (e.g. "\\b(add|sub|lw)\\b").
     */
	public static final String INSTRUCTION_PATTERN = "\\b(" + String.join("|", INSTRUCTIONS) + ")\\b";
	
	/**
     * Regular expression pattern for matching general-purpose registers.
     * 
     * Matches x0 to x31 as full words.
     */
	public static final String REGISTER_PATTERN = "\\b(" + String.join("|", REGISTERS) + ")\\b";
	
	/**
     * Regular expression pattern for matching label declarations in assembly.
     * 
     * Labels must begin with a letter or underscore and end with a colon.
     * Example matches: "_start:", "loop:"
     */
	public static final String TAGS_PATTERN = "\\b[a-zA-Z_][a-zA-Z0-9_]*\\:";
	
	private AsmSyntaxDictionary() {
		// this is an utility class, therefore prevent the instantiation whit a private constructor
	}
	
}
