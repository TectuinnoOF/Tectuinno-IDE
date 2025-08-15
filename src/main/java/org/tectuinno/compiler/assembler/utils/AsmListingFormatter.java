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

import java.util.List;

import org.tectuinno.compiler.assembler.IrLine;

/**
 * Utility class that builds a human-readable assembly listing from intermediate
 * representation lines ({@link IrLine}).
 * <p>
 * The listing contains the following columns:
 * <ul>
 *     <li><b>Dirección</b> - The memory address of the instruction in hexadecimal format</li>
 *     <li><b>Etiqueta</b> - An optional label associated with the instruction</li>
 *     <li><b>Instrucción</b> - The original assembly instruction text</li>
 *     <li><b>Hex</b> - The machine code in hexadecimal (empty in first pass)</li>
 * </ul>
 *
 * <h2>Key Features:</h2>
 * <table border="1">
 *   <caption>AsmListingFormatter Responsibilities</caption>
 *   <tr>
 *     <th>Method</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #buildListing(List)}</td>
 *     <td>Generates a formatted table from a list of {@link IrLine} objects</td>
 *   </tr>
 * </table>
 *
 * @author Pablo
 * @version 1.0
 * @since 2025-08-14
 */
public final class AsmListingFormatter {

	private AsmListingFormatter() {}

	/**
     * Builds a formatted table with columns:
     * <b>Dirección | Etiqueta | Instrucción | Hex</b>.
     * <p>
     * In the first assembler pass, the <b>Hex</b> column is left empty, to be filled in the second pass.
     *
     * @param lines the list of {@link IrLine} objects representing assembled lines
     * @return a {@code String} containing the formatted assembly listing
     */
    public static String buildListing(List<IrLine> lines) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s %-10s %-30s %s%n", "Dirección", "Etiqueta", "Instrucción", "Hex"));
        sb.append("--------------------------------------------------------------------------------\n");
        for (IrLine ln : lines) {
            String addr = String.format("%08X", ln.pc());
            String label = ln.labelOpt() == null ? "" : ln.labelOpt();
            String instr = ln.originalText(); // already rebuilt (e.g., "addi x7, x0, 1")
            String hex = ""; // will be filled in second pass
            sb.append(String.format("%-12s %-10s %-30s %s%n", addr, label, instr, hex));
        }
        return sb.toString();
    }
	
}
