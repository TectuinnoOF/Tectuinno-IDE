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

import java.util.ArrayList;

import org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary;
import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;
import org.tectuinno.view.component.ResultConsolePanel;

public class AsmParser {

	private final List<Token> tokens;
	private int position;
	// Removed unused fields to satisfy strict compilation settings
	private ResultConsolePanel consolePanel;
	private static int errorCounter;
	private final java.util.List<AnalysisError> errors;

	public AsmParser(List<Token> tokens, int position) {
		super();
		this.tokens = tokens;
		this.position = position;
		// Removed unused line/column tracking initialization
		this.errors = new ArrayList<>();
	}

	public AsmParser(List<Token> tokens) {
		super();
		this.tokens = tokens;
		this.position = 0;
		errorCounter = 0;
		this.errors = new ArrayList<>();
	}

	private void error(Token token, String message) {
		String errorMessage = "Error de sintaxis: " + token + ": " + message + "\n\r";
		errorCounter++;
		this.errors.add(new AnalysisError(token.getLine(), token.getColumn(), errorMessage.trim(), AnalysisError.Severity.ERROR));
		System.err.println(errorMessage);
		if (this.consolePanel != null) {
			this.consolePanel.getTerminalPanel().writteIn(errorMessage);
		}
		advance();
	}

	private Token previous() {
		return this.tokens.get(position - 1);
	}

	private boolean isAtEnt() {
		return position >= this.tokens.size();
	}

	private Token advance() {
		if (!isAtEnt())
			position++;
		return previous();
	}

	private Token peek() {
		return tokens.get(position);
	}

	private boolean check(TokenType type) {
		if (isAtEnt())
			return false;
		return peek().getType() == type;
	}

	private boolean match(TokenType... types) {

		for (TokenType type : types) {
			if (check(type)) {
				advance();
				return true;
			}
		}

		return false;

	}

	private void parseArgument() {

		/* if(match(TokenType.REGISTER) || match(TokenType.IMMEDIATE)) return; */

		// case when match with pattern i(reg)
		if (check(TokenType.IMMEDIATE)) {

			advance(); // rebace the immediate

			if (match(TokenType.LEFTPAREN)) {

				if (!match(TokenType.REGISTER)) {
					error(peek(), "Se esperaba un registro");
					return;
				}

				if (!match(TokenType.RIGHTPAREN)) {
					error(peek(), "Se esperaba ')'");
					return;
				}
			}

			return;
		}

		if (match(TokenType.REGISTER))
			return;

		if (match(TokenType.LEFTPAREN)) {
			if (!match(TokenType.REGISTER)) {
				error(peek(), "Se esperaba un registro dentro de paréntesis");
				return;
			}
			if (!match(TokenType.RIGHTPAREN)) {
				error(peek(), "Falta el paréntesis de cierre ')'");
				return;
			}
			return;
		}

		if (check(TokenType.UNKNOWN)) {
			advance();
			return;
		}

		error(peek(), "Argumento inválido");
	}

	private void parseArguments() {
		if (check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.LEFTPAREN)) {
			do {
				parseArgument();
			} while (match(TokenType.COMMA));
		}

		if (check(TokenType.UNKNOWN)) {
			advance();
			return;
		}
	}		

	private void parseInstruction() {
		parseArguments();
	}
	
	/**
	 * Parses the operand of the {@code .global} directive.
	 *
	 * <p>Expected syntax:</p>
	 * <pre>
	 * .global symbol
	 * </pre>
	 *
	 * <p>At this stage, the parser only validates that a valid symbol token exists
	 * after the directive. Symbol existence and visibility rules belong to the
	 * semantic analysis phase.</p>
	 *
	 * @param directive the directive token being parsed
	 */
	private void parseGlobalDirectiveArgument(Token directive) {
		
	    if (isAtEnt()) {
	        error(directive, "Expected symbol after " + directive.getValue());
	        return;
	    }

	    if (check(TokenType.UNKNOWN) || check(TokenType.LABEL)) {
	        advance();
	        return;
	    }

	    error(peek(), "Expected symbol after " + directive.getValue());
	    
	}
	
	/**
	 * Parses the operand of the {@code .section} directive.
	 *
	 * <p>Expected syntax:</p>
	 * <pre>
	 * .section .text
	 * .section .data
	 * </pre>
	 *
	 * <p>This method validates only that a directive token follows {@code .section}.
	 * The semantic analyzer should later decide whether the section name is allowed.</p>
	 *
	 * @param directive the directive token being parsed
	 */
	private void parseSectionDirectiveArgument(Token directive) {
	    if (isAtEnt()) {
	        error(directive, "Expected section name after " + directive.getValue());
	        return;
	    }

	    if (check(TokenType.DIRECTIVE)) {
	        advance();
	        return;
	    }

	    error(peek(), "Expected section name after " + directive.getValue());
	}
	
	/**
	 * Parses the argument list of a data definition directive such as
	 * {@code .word}, {@code .byte}, or {@code .half}.
	 *
	 * <p>Expected syntax:</p>
	 * <pre>
	 * .word 10
	 * .word 1, 2, 3
	 * .byte 0x10, 0x20
	 * .half 100
	 * </pre>
	 *
	 * <p>Only immediate values are accepted at this stage.</p>
	 *
	 * @param directive the directive token being parsed
	 */
	private void parseDataDirectiveArguments(Token directive) {
	    if (!match(TokenType.IMMEDIATE, TokenType.CHAR)) {
	        error(peek(), "Expected immediate value after " + directive.getValue());
	        return;
	    }

	    while (match(TokenType.COMMA)) {
	        if (!match(TokenType.IMMEDIATE,TokenType.CHAR)) {
	            error(peek(), "Expected immediate or char value after comma in " + directive.getValue());
	            return;
	        }
	    }
	}
	
	/**
	 * Parses the argument of the {@code .asciz} directive.
	 *
	 * <p>Expected syntax:</p>
	 * <pre>
	 * .asciz "Hello"
	 * </pre>
	 *
	 * <p>The lexer must already provide the string literal as a {@link TokenType#STRING}
	 * token. String encoding and null-termination are responsibilities of later phases.</p>
	 *
	 * @param directive the directive token being parsed
	 */
	private void parseAscizDirectiveArgument(Token directive) {
	    if (!match(TokenType.STRING)) {
	        error(peek(), "Expected string literal after " + directive.getValue());
	    }
	}
	
	/**
	 * Parses an assembler directive and validates its syntactic structure.
	 *
	 * @implNote
	 * This method performs only syntactic validation. Semantic validation
	 * (such as section validity or value ranges) must be handled later.
	 */
	private void parseDirective() {
	    Token directive = previous();
	    String value = directive.getValue();

	    switch (value) {

	        case AsmSyntaxDictionary.TEXT:
	        case AsmSyntaxDictionary.DATA:
	            // No arguments
	            return;

	        case AsmSyntaxDictionary.GLOBAL:
	            parseGlobalDirectiveArgument(directive);
	            return;

	        case AsmSyntaxDictionary.SECTION:
	            parseSectionDirectiveArgument(directive);
	            return;

	        case AsmSyntaxDictionary.WORD:
	        case AsmSyntaxDictionary.BYTE:
	        case AsmSyntaxDictionary.HALF:
	            parseDataDirectiveArguments(directive);
	            return;

	        case AsmSyntaxDictionary.ASCIZ:
	            parseAscizDirectiveArgument(directive);
	            return;

	        default:
	            error(directive, "Unsupported directive: " + value);
	    }
	}
	
	private void parseLines() {

		if (match(TokenType.COMMENT))
			return;
		
		if (match(TokenType.DIRECTIVE)) {
	        parseDirective();
	        return;
	    }

		if (match(TokenType.LABEL)) {
			if (check(TokenType.INSTRUCTION)) {
				parseInstruction();
			}
			return;
		}

		if (match(TokenType.INSTRUCTION)) {
			parseArguments();
			return;
		}

		error(peek(), "Linea no válida");
	}

	private void parserFinishResults(long start, long finish) {
		double time = (double) ((finish - start) / 1000);
		String finishMessage = new StringBuilder()
				.append("\n============== Analisis de sintaxis ============================\n")
				.append("Tokens analizados: ").append(this.tokens.size())
				.append("\nErrores sintacticos detectados: ").append(errorCounter)
				.append("\nTiempo: ").append(time)
				.append("\n================================================================\n")
				.toString();
		if (this.consolePanel != null) {
			this.consolePanel.getTerminalPanel().writteIn(finishMessage);
		}
	}

	public boolean parseProgram() throws Exception {
		long start = System.currentTimeMillis();
		while (!isAtEnt()) {
			parseLines();
		}
		long finish = System.currentTimeMillis();

		this.parserFinishResults(start, finish);

		return errorCounter <= 0;

	}

	public java.util.List<AnalysisError> getErrors() {
		return java.util.List.copyOf(this.errors);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public void setResultConsolePanel(ResultConsolePanel consolePanel) {
		this.consolePanel = consolePanel;
	}

	public ResultConsolePanel getConsolePanel() {
		return this.consolePanel;
	}

}
