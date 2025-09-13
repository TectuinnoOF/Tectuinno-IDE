package org.tectuinno.compiler.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tectuinno.compiler.assembler.encode.AsmEncoder;
import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;

/**
 * Second pass: encodes each IR line to a 32-bit RV32I instruction word.
 */
public class AsmSecondPass {

	public record Result(List<EncoderIrLine> encoded, List<String> errors) {}

    private final List<? extends org.tectuinno.compiler.assembler.IrLine> lines;
    private final Map<String,Integer> labelAddrs;

    public AsmSecondPass(List<? extends org.tectuinno.compiler.assembler.IrLine> lines,
                         Map<String,Integer> labelAddrs) {
        this.lines = lines;
        this.labelAddrs = labelAddrs;
    }
    
    private static List<Token> normalizeArgs(List<Token> raw, Map<String,Integer> labels) {
        List<Token> out = new ArrayList<>(raw.size());
        for (Token t : raw) {
            TokenType tt = t.getType();
            if (tt == TokenType.COMMA || tt == TokenType.LEFTPAREN || tt == TokenType.RIGHTPAREN) {
                continue; // descartamos puntación
            }
            if (tt == TokenType.UNKNOWN && labels.containsKey(t.getValue())) {
                out.add(new Token(TokenType.LABEL, t.getValue(), t.getPosition(), t.getLine(), t.getColumn()));
                continue;
            }
            out.add(t);
        }
        return out;
    }
    
    public Result run() {
        List<EncoderIrLine> out = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (var line : lines) {
            try {
            	
            	List<Token> logicArgs = normalizeArgs(line.args(), labelAddrs);
            	
                // Normalize mnemonic (lowercase) and encode
                int word = AsmEncoder.encode(
                        line.mnemonic().toLowerCase(),
                        line.pc(),
                        logicArgs,
                        labelAddrs
                );
                out.add(new EncoderIrLine(
                        line.pc(), line.labelOpt(), line.mnemonic(),
                        line.args(), line.originalText(), word, AsmEncoder.toHex32(word)
                ));
            } catch (Exception ex) {
            	ex.printStackTrace(System.err);
                errors.add("Encode error at 0x%08X: %s".formatted(line.pc(), ex.getMessage()));
                // Optionally push a placeholder to keep table aligned
                out.add(new EncoderIrLine(
                        line.pc(), line.labelOpt(), line.mnemonic(),
                        line.args(), line.originalText(), 0, "ERROR"
                ));
            }
        }
        return new Result(out, errors);
    }
	
}
