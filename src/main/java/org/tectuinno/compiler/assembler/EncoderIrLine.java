package org.tectuinno.compiler.assembler;

import java.util.List;

import org.tectuinno.compiler.assembler.utils.Token;

/** Enriched IR line holding the encoded machine word. */
public record EncoderIrLine(
		int pc,                 // address
        String labelOpt,        // optional label
        String mnemonic,        // mnemonic
        List<Token> args,       // logical args (REGISTER / IMMEDIATE / LABEL)
        String originalText,    // original source line
        int word,               // 32-bit encoded instruction
        String hex              // "0x%08X") {

) {}
