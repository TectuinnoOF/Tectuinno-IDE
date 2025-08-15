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

package org.tectuinno.compiler.assembler.encode;

import static org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary.*;
import java.util.Map;

final class InstructionTable {
	
	private InstructionTable() {}
	
	// funct7=0x20 is used by SUB, others 0x00
    static final Map<String, InstructionDef> DEF = Map.ofEntries(
        // R-type
        Map.entry(ADD,  new InstructionDef(Format.R, 0x33, 0x0, 0x00)),
        Map.entry(SUB,  new InstructionDef(Format.R, 0x33, 0x0, 0x20)),
        Map.entry(SLT,  new InstructionDef(Format.R, 0x33, 0x2, 0x00)),
        Map.entry(AND,  new InstructionDef(Format.R, 0x33, 0x7, 0x00)),
        Map.entry(OR,   new InstructionDef(Format.R, 0x33, 0x6, 0x00)),

        // I-type
        Map.entry(ADDI, new InstructionDef(Format.I, 0x13, 0x0, 0x00)),
        Map.entry(ANDI, new InstructionDef(Format.I, 0x13, 0x7, 0x00)),
        Map.entry(ORI,  new InstructionDef(Format.I, 0x13, 0x6, 0x00)),
        Map.entry("jalr", new InstructionDef(Format.I, 0x67, 0x0, 0x00)), // por si lo invocas directo
        Map.entry("lw",   new InstructionDef(Format.I, 0x03, 0x2, 0x00)),

        // S-type
        Map.entry("sw",   new InstructionDef(Format.S, 0x23, 0x2, 0x00)),

        // B-type
        Map.entry(BEQ,  new InstructionDef(Format.B, 0x63, 0x0, 0x00)),

        // U-type
        Map.entry(LUI,  new InstructionDef(Format.U, 0x37, 0x0, 0x00)),

        // J-type
        Map.entry(JAL,  new InstructionDef(Format.J, 0x6F, 0x0, 0x00))
    );

}
