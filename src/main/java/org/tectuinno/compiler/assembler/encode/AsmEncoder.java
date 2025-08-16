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

import java.util.List;
import java.util.Map;
import org.tectuinno.compiler.assembler.utils.Token;
import static org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary.*;

public final class AsmEncoder {

	/** Encodes one line into a 32-bit instruction word. */
	public static int encode(String mnemonic, int pc, List<Token> args, Map<String, Integer> labelAddrs) {
		mnemonic = mnemonic.toLowerCase();
		// Handle pseudos first
		if (CALL.equals(mnemonic))
			return encodeCALL(pc, args, labelAddrs);
		if (RET.equals(mnemonic))
			return encodeRET();

		InstructionDef def = InstructionTable.DEF.get(mnemonic);
		if (def == null)
			throw new IllegalArgumentException("Unknown instruction: " + mnemonic);

		return switch (def.format()) {
		case R -> encodeR(def, args);
		case I -> encodeI(mnemonic, def, pc, args, labelAddrs);
		case S -> encodeS(def, args);
		case B -> encodeB(def, pc, args, labelAddrs);
		case U -> encodeU(def, args);
		case J -> encodeJ(def, pc, args, labelAddrs);
		};
	}

	// ----------------- Helpers (parsing) -----------------

	/** Parses register token like "x7" -> 7. */
	private static int regNum(Token t) {
		// if (t.getType() != TokenType.REGISTER) throw new
		// IllegalArgumentException("Expected register: " + t);
		String v = t.getValue().trim().toLowerCase();
		int idx = v.startsWith("x") ? 1 : 0;
		// if (!v.startsWith("x")) throw new IllegalArgumentException("Register must
		// start with 'x': " + v);

		try {
			return Integer.parseInt(v.substring(idx));
		} catch (Exception er) {
			er.printStackTrace(System.err);
			return 0;
		}

	}

	/**
	 * Parses immediates in dec/hex/bin with sign (e.g., -12, 0xFF, -0x10, 0b1010).
	 */
	private static int immVal(Token t) {

		try {
			String s = t.getValue().trim();
			return Integer.decode(s);
		} catch (Exception er) {
			er.printStackTrace(System.err);
			return 0;
		}
	}

	/** Resolves a declared label token to absolute address. */
	private static int labelAddr(Token t, Map<String, Integer> labelAddrs) {
		// if (t.getType() != TokenType.LABEL) throw new
		// IllegalArgumentException("Expected label: " + t);
		// if (addr == null) throw new IllegalArgumentException("Undeclared label: " +
		// t.getValue());

		try {
			Integer addr = labelAddrs.get(t.getValue());
			return addr;
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			return 0;
		}
	}

	// ----------------- Encoders per format -----------------

	/** R-type: rd, rs1, rs2 */
	private static int encodeR(InstructionDef def, List<Token> args) {

		// if (args.size() != 3) throw new IllegalArgumentException("R-type expects 3
		// args");
		int rd = regNum(args.get(0));
		int rs1 = regNum(args.get(1));
		int rs2 = regNum(args.get(2));
		return (def.funct7() << 25) | (rs2 << 20) | (rs1 << 15) | (def.funct3() << 12) | (rd << 7) | def.opcode();
	}

	/**
	 * I-type: (addi/andi/ori) rd, rs1, imm | (lw) rd, imm(rs1) | (jalr) rd, rs1,
	 * imm
	 */
	private static int encodeI(String mnemonic, InstructionDef def, int pc, List<Token> args,
			Map<String, Integer> labels) {
		if (ADDI.equals(mnemonic) || ANDI.equals(mnemonic) || ORI.equals(mnemonic) || JALR.equals(mnemonic)) {
			// if (args.size() != 3) throw new IllegalArgumentException(mnemonic + " expects
			// 3 args");
			int rd = regNum(args.get(0));
			int rs1 = regNum(args.get(1));
			int imm = imm12(immVal(args.get(2)));
			return (imm << 20) | (rs1 << 15) | (def.funct3() << 12) | (rd << 7) | def.opcode();
		}

		if (LW.equals(mnemonic)) {
			// logical args form you already produce: rd, imm, baseReg
			// if (args.size() != 3) throw new IllegalArgumentException("lw expects: rd,
			// imm, baseReg");
			int rd = regNum(args.get(0));
			int imm = imm12(immVal(args.get(1)));
			int rs1 = regNum(args.get(2));
			return (imm << 20) | (rs1 << 15) | (def.funct3() << 12) | (rd << 7) | def.opcode();
		}

		// throw new IllegalArgumentException("Unsupported I-type mnemonic: " +
		// mnemonic);
		return 0;
	}

	/** S-type: sw rs2, imm(rs1) -> logical args: rs2, imm, rs1 */
	private static int encodeS(InstructionDef def, List<Token> args) {
		// if (args.size() != 3) throw new IllegalArgumentException("sw expects: rs2,
		// imm, baseReg");
		int rs2 = regNum(args.get(0));
		int imm = imm12(immVal(args.get(1)));
		int rs1 = regNum(args.get(2));
		int imm11_5 = (imm >> 5) & 0x7F;
		int imm4_0 = imm & 0x1F;
		return (imm11_5 << 25) | (rs2 << 20) | (rs1 << 15) | (def.funct3() << 12) | (imm4_0 << 7) | def.opcode();
	}

	/** B-type: beq rs1, rs2, label (PC-relative) */
	private static int encodeB(InstructionDef def, int pc, List<Token> args, Map<String, Integer> labels) {
		// if (args.size() != 3) throw new IllegalArgumentException("beq expects: rs1,
		// rs2, label");
		int rs1 = regNum(args.get(0));
		int rs2 = regNum(args.get(1));
		int target = labelAddr(args.get(2), labels);
		int off = target - pc;
		// if ((off & 0x1) != 0) throw new IllegalArgumentException("Branch offset must
		// be 2-byte aligned");
		// 12-bit signed immediate (in bytes). We check range in bytes:
		// if (off < -4096 || off > 4094) throw new IllegalArgumentException("Branch
		// offset out of range: " + off);
		int bit12 = (off >> 12) & 0x1;
		int bit11 = (off >> 11) & 0x1;
		int bits10_5 = (off >> 5) & 0x3F;
		int bits4_1 = (off >> 1) & 0xF;
		int immEnc = (bit12 << 31) | (bits10_5 << 25) | (bits4_1 << 8) | (bit11 << 7);
		return immEnc | (rs2 << 20) | (rs1 << 15) | (def.funct3() << 12) | def.opcode();
	}

	/** U-type: lui rd, imm20 (imm goes to bits 31..12) */
	private static int encodeU(InstructionDef def, List<Token> args) {
		// if (args.size() != 2) throw new IllegalArgumentException("lui expects: rd,
		// imm20");
		int rd = regNum(args.get(0));
		int imm = imm20(immVal(args.get(1)));
		return (imm << 12) | (rd << 7) | def.opcode();
	}

	/** J-type: jal label | jal rd, label (PC-relative) */
	private static int encodeJ(InstructionDef def, int pc, List<Token> args, Map<String, Integer> labels) {
		int rd = 0;
		int target = 0;
		if (args.size() == 1) { // jal label
			rd = 0;
			target = labelAddr(args.get(0), labels);
		} else if (args.size() == 2) { // jal rd, label
			rd = regNum(args.get(0));
			target = labelAddr(args.get(1), labels);
		} // else throw new IllegalArgumentException("jal expects: label or rd, label");

		int off = target - pc;
		if ((off & 0x1) != 0)
			throw new IllegalArgumentException("Jump offset must be 2-byte aligned");
		if (off < -(1 << 20) || off > ((1 << 20) - 2))
			throw new IllegalArgumentException("Jump offset out of range: " + off);

		int bit20 = (off >> 20) & 0x1;
		int bits10_1 = (off >> 1) & 0x3FF;
		int bit11 = (off >> 11) & 0x1;
		int bits19_12 = (off >> 12) & 0xFF;

		return (bit20 << 31) | (bits19_12 << 12) | (bit11 << 20) | (bits10_1 << 21) | (rd << 7) | def.opcode();
	}

	// ----------------- Pseudos -----------------

	/** call label → jal x1, label */
	private static int encodeCALL(int pc, List<Token> args, Map<String, Integer> labels) {
		/*
		 * if (args.size() != 1 || args.get(0).getType() != TokenType.LABEL) throw new
		 * IllegalArgumentException("call expects one label");
		 */
		InstructionDef jal = InstructionTable.DEF.get(JAL);
		int rd = 1; // ra = x1
		int target = labelAddr(args.get(0), labels);
		int off = target - pc;
		// if ((off & 1) != 0) throw new IllegalArgumentException("Jump offset must be
		// 2-byte aligned");
		// if (off < -(1<<20) || off > ((1<<20)-2)) throw new
		// IllegalArgumentException("Jump offset out of range: " + off);

		int bit20 = (off >> 20) & 0x1;
		int bits10_1 = (off >> 1) & 0x3FF;
		int bit11 = (off >> 11) & 0x1;
		int bits19_12 = (off >> 12) & 0xFF;

		return (bit20 << 31) | (bits19_12 << 12) | (bit11 << 20) | (bits10_1 << 21) | (rd << 7) | jal.opcode();
	}

	/** ret → jalr x0, x1, 0 */
	private static int encodeRET() {
		int opcode = 0x67, funct3 = 0x0, rd = 0, rs1 = 1, imm = 0;
		return (imm << 20) | (rs1 << 15) | (funct3 << 12) | (rd << 7) | opcode;
	}

	// ----------------- Bit-range helpers -----------------

	/** Clamp to 12-bit signed immediate (I/S/B formats use 12-bit signed). */
	private static int imm12(int v) {
		// if (v < -2048 || v > 2047) throw new IllegalArgumentException("12-bit
		// immediate out of range: " + v);
		return v & 0xFFF;
	}

	/** Clamp to 20-bit unsigned immediate (U-type) */
	private static int imm20(int v) {
		// if (v < 0 || v > 0xFFFFF) throw new IllegalArgumentException("20-bit
		// immediate out of range: " + v);
		return v & 0xFFFFF;
	}

	/** Returns 4 LE bytes for a 32-bit word. */
	public static byte[] toLittleEndian(int word) {
		return new byte[] { (byte) (word & 0xFF), (byte) ((word >> 8) & 0xFF), (byte) ((word >> 16) & 0xFF),
				(byte) ((word >> 24) & 0xFF) };
	}

	/** Hex "0x%08X". */
	public static String toHex32(int word) {
		return String.format("0x%08X", word);
	}

}
