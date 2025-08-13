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

/**
 * Builds the human-readable disassembly/assembly listing.
 */
public final class AsmListingFormatter {

	private AsmListingFormatter() {}

    /** Builds a table with columns: Address | Label | Instruction | Hex (empty for now). */
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
