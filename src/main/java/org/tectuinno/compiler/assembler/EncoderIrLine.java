package org.tectuinno.compiler.assembler;

import java.util.List;

import org.tectuinno.compiler.assembler.utils.IrKind;
import org.tectuinno.compiler.assembler.utils.Token;

/** Enriched IR line holding the encoded machine word. */
public record EncoderIrLine(
		int pc,                 // address
        String labelOpt,        // optional label
        IrKind kind,
        String mnemonic,        // mnemonic
        List<Token> args,       // logical args (REGISTER / IMMEDIATE / LABEL)
        String originalText,    // original source line
        byte[] bytes,        
        String hex,              // "0x%08X") {
        Integer wordOpt

) {
	
	public EncoderIrLine{
		bytes = bytes == null ? new byte[0] : bytes.clone();
	}
	
	public int size() {
		return bytes.length;
	}
	
	public boolean emitsBytes() {
		return bytes.length > 0;
	}
}
