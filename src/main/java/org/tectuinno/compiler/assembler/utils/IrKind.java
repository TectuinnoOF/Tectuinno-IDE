package org.tectuinno.compiler.assembler.utils;

/**
 * Defines the logical kind of an intermediate representation line
 * generated during the assembler pipeline.
 *
 * <p>Each IR line can represent one of the following categories:</p>
 * <ul>
 *     <li>{@link #INSTRUCTION}: an executable CPU instruction</li>
 *     <li>{@link #DATA}: a data definition directive such as {@code .word} or {@code .asciz}</li>
 *     <li>{@link #DIRECTIVE}: a control directive such as {@code .text}, {@code .data}, or {@code .global}</li>
 * </ul>
 *
 * @author Pablo
 * @version 1.0
 * @since 1.2.1.1
 */
public enum IrKind {
	/**
     * Represents a standard executable instruction that must be encoded
     * into machine code.
     */
    INSTRUCTION,

    /**
     * Represents a data-producing directive that generates bytes in memory.
     */
    DATA,

    /**
     * Represents a control or metadata directive that does not directly
     * generate machine code bytes.
     */
    DIRECTIVE
}
