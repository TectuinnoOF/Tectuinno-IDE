package org.tectuinno.compiler.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tectuinno.compiler.assembler.encode.AsmEncoder;
import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;
import org.tectuinno.compiler.assembler.encode.DataEncoder;

/**
 * Second pass: encodes each IR line to a 32-bit RV32I instruction word.
 */
public class AsmSecondPass {

	/**
	 * Encapsulates the result of the second assembler pass.
	 *
	 * @param encoded encoded output lines
	 * @param errors  encoding errors detected during the pass
	 */
	public record Result(List<EncoderIrLine> encoded, List<String> errors) {
	}

	private final List<? extends org.tectuinno.compiler.assembler.IrLine> lines;
	private final Map<String, Integer> labelAddrs;

	public AsmSecondPass(List<? extends IrLine> lines, Map<String, Integer> labelAddrs) {
		this.lines = lines;
		this.labelAddrs = labelAddrs;
	}

	/*
	 * private static List<Token> normalizeArgs(List<Token> raw, Map<String,Integer>
	 * labels) { List<Token> out = new ArrayList<>(raw.size()); for (Token t : raw)
	 * { TokenType tt = t.getType(); if (tt == TokenType.COMMA || tt ==
	 * TokenType.LEFTPAREN || tt == TokenType.RIGHTPAREN) { continue; // descartamos
	 * puntación } if (tt == TokenType.UNKNOWN && labels.containsKey(t.getValue()))
	 * { out.add(new Token(TokenType.LABEL, t.getValue(), t.getPosition(),
	 * t.getLine(), t.getColumn())); continue; } out.add(t); } return out; }
	 */

	public Result run() {
		List<EncoderIrLine> out = new ArrayList<>();
		List<String> errors = new ArrayList<>();

		for (IrLine line : lines) {
			try {
				EncoderIrLine encodedLine = encodeLine(line);
				out.add(encodedLine);
			} catch (Exception ex) {
				ex.printStackTrace(System.err);
				errors.add("Encode error at 0x%08X: %s".formatted(line.pc(), ex.getMessage()));
				out.add(errorPlaceholder(line));
			}
		}

		return new Result(out, errors);
	}

	private EncoderIrLine encodeLine(IrLine line) {
		return switch (line.kind()) {
		case INSTRUCTION -> encodeInstructionLine(line);
		case DATA -> encodeDataLine(line);
		case DIRECTIVE -> encodeDirectiveLine(line);
		};
	}

	private EncoderIrLine encodeInstructionLine(IrLine line) {
		List<Token> logicArgs = normalizeInstructionArgs(line.args(), labelAddrs);

		int word = AsmEncoder.encode(line.mnemonic().toLowerCase(), line.pc(), logicArgs, labelAddrs);

		byte[] bytes = toLittleEndianBytes(word);

		return new EncoderIrLine(line.pc(), line.labelOpt(), line.kind(), line.mnemonic(), logicArgs,
				line.originalText(), bytes, AsmEncoder.toHex32(word), word);

	}

	private EncoderIrLine encodeDataLine(IrLine line) {
		List<Token> logicArgs = normalizeDataArgs(line.args());
		byte[] bytes = DataEncoder.encode(line.mnemonic().toLowerCase(), logicArgs);
		String hex = DataEncoder.toHex(bytes);

		return new EncoderIrLine(line.pc(), line.labelOpt(), line.kind(), line.mnemonic(), logicArgs,
				line.originalText(), bytes, hex, null);
	}

	private EncoderIrLine encodeDirectiveLine(IrLine line) {
		return new EncoderIrLine(line.pc(), line.labelOpt(), line.kind(), line.mnemonic(), line.args(),
				line.originalText(), new byte[0], "", null);
	}

	private EncoderIrLine errorPlaceholder(IrLine line) {
		return new EncoderIrLine(line.pc(), line.labelOpt(), line.kind(), line.mnemonic(), line.args(),
				line.originalText(), new byte[0], "ERROR", null);
	}

	private static List<Token> normalizeInstructionArgs(List<Token> raw, Map<String, Integer> labels) {
		List<Token> out = new ArrayList<>(raw.size());

		for (Token token : raw) {
			TokenType type = token.getType();

			if (type == TokenType.COMMA || type == TokenType.LEFTPAREN || type == TokenType.RIGHTPAREN) {
				continue;
			}

			if (type == TokenType.UNKNOWN && labels.containsKey(token.getValue())) {
				out.add(new Token(TokenType.LABEL, token.getValue(), token.getPosition(), token.getLine(),
						token.getColumn()));
				continue;
			}

			out.add(token);
		}

		return out;
	}

	private static List<Token> normalizeDataArgs(List<Token> raw) {
		List<Token> out = new ArrayList<>(raw.size());

		for (Token token : raw) {
			if (token.getType() == TokenType.COMMA) {
				continue;
			}

			out.add(token);
		}

		return out;
	}

	private static byte[] toLittleEndianBytes(int word) {
		return new byte[] { (byte) (word & 0xFF), (byte) ((word >>> 8) & 0xFF), (byte) ((word >>> 16) & 0xFF),
				(byte) ((word >>> 24) & 0xFF) };
	}
}
