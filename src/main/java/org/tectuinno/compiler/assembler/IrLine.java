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

import java.util.List;

import org.tectuinno.compiler.assembler.utils.Token;

/**
 * Represents an intermediate representation (IR) of a single assembled line.
 * <p>
 * This record stores:
 * <ul>
 *     <li>The program counter (address) of the instruction</li>
 *     <li>An optional label associated with this line</li>
 *     <li>The instruction mnemonic</li>
 *     <li>The list of arguments as {@link Token} objects</li>
 *     <li>The original text of the source line</li>
 * </ul>
 *
 * <h2>Usage example:</h2>
 * <pre>{@code
 * List<Token> args = List.of(new Token("x1"), new Token("x2"));
 * IrLine line = new IrLine(0x004, "LOOP", "ADD", args, "ADD x1, x2");
 * 
 * System.out.println("Mnemonic: " + line.mnemonic());
 * System.out.println("PC: " + line.pc());
 * }</pre>
 *
 * <h2>Field descriptions:</h2>
 * <table border="1">
 *   <caption>IrLine Fields</caption>
 *   <tr>
 *     <th>Field</th>
 *     <th>Type</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #pc()}</td>
 *     <td>{@code int}</td>
 *     <td>The program counter (address) of the instruction in memory</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #labelOpt()}</td>
 *     <td>{@code String}</td>
 *     <td>Optional label name; may be {@code null} if no label is present</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #mnemonic()}</td>
 *     <td>{@code String}</td>
 *     <td>The instruction mnemonic (e.g., ADD, SUB, JMP)</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #args()}</td>
 *     <td>{@code List<Token>}</td>
 *     <td>Arguments (operands) for the instruction, represented as tokens</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #originalText()}</td>
 *     <td>{@code String}</td>
 *     <td>The exact source line as written by the user</td>
 *   </tr>
 * </table>
 *
 * @param pc           the program counter (address) of the instruction
 * @param labelOpt     optional label name (may be {@code null} or empty if not present)
 * @param mnemonic     the instruction mnemonic
 * @param args         the list of instruction arguments as {@link Token} objects
 * @param originalText the original text of the source line
 *
 * @since 2025-08-14
 * @version 1.0
 * @see Token
 */
public record IrLine(int pc, String labelOpt, String mnemonic, List<Token> args, String originalText) {
}
