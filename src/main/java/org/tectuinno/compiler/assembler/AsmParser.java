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

import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;
import org.tectuinno.view.component.ResultConsolePanel;

public class AsmParser {

	private final List<Token> tokens;
	private int position;
	private ResultConsolePanel consolePanel;
	private static int errorCounter;

	public AsmParser(List<Token> tokens, int position) {
		super();
		this.tokens = tokens;
		this.position = position;
	}
	
	public AsmParser(List<Token> tokens) {
		super();
		this.tokens = tokens;
		this.position = 0;
		errorCounter = 0;
	}
	
	private void error(Token token, String message) {
		String errorMessage = "Error de sintaxis: " + token + ": " + message + "\n\r";
		System.err.println(errorMessage);
		this.consolePanel.getTerminalPanel().writteIn(errorMessage);
		errorCounter ++;
		advance();
	}
	
	private Token previous() {
		return this.tokens.get(position - 1);
	}
	
	private boolean isAtEnt() {
		return position >= this.tokens.size();
	}
	
	private Token advance() {
		if(!isAtEnt()) {
			position ++;
		}
		return previous();
	}
	
	private Token peek() {
		return tokens.get(position);
	}
	
	private boolean check(TokenType type) {
		if(isAtEnt()) return false;
		return peek().getType() == type;
	}
	
	private boolean match(TokenType... types) {		
		
		for(TokenType type : types) {
			if(check(type)) {
				advance();
				return true;
			}
		}
		
		return false;
					
	}
	
	private void parseArgument() {
		
		/*if(match(TokenType.REGISTER) || match(TokenType.IMMEDIATE)) return;*/
		
		//case when match with pattern i(reg)		
		if(check(TokenType.IMMEDIATE)) {
			
			advance(); //rebace the immediate
			
			if(match(TokenType.LEFTPAREN)) {
				
				if(!match(TokenType.REGISTER)) {
					error(peek(), "Se esperaba un registro");
					return;
				}
				
				if(!match(TokenType.RIGHTPAREN)) {
					error(peek(), "Se esperaba ')'");
					return;
				}
			}
			
			return;
		}
		
		if(match(TokenType.REGISTER)) return;
				
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
	    
	    if(check(TokenType.UNKNOWN)){
	    	advance();
	    	return;
	    }
		
		error(peek(), "Argumento inválido");
	}

	private void parseArguments() {
		if(check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.LEFTPAREN)) {
			do {
				parseArgument();
			}while(match(TokenType.COMMA));
		}
		
		if(check(TokenType.UNKNOWN)) {
			advance();
			return;
		}
	}
	
	private void parseInstruction() {
		parseArguments();
	}
	
	private void parseLines() {
		
		if(match(TokenType.COMMENT)) return;
		
		if(match(TokenType.LABEL)) {
			if(check(TokenType.INSTRUCTION)) {
				parseInstruction();
			}
			return;
		}
		
		if(match(TokenType.INSTRUCTION)) {
			parseArguments();
			return;
		}
		
		error(peek(), "Linea no válida");
	}		
	
	private void parserFinishResults(long start, long finish) {
		double time = (double) ((finish - start)/1000);
		String finishMessage = new StringBuilder()
				.append("\n============== Analisis de sintaxis ============================\n")
				.append("Tokens analizados: ").append(this.tokens.size())
				.append("\nErrores sintacticos detectados: ").append(errorCounter)
				.append("\nTiempo: ").append(time)
				.append("\n================================================================\n")
				.toString();
		this.consolePanel.getTerminalPanel().writteIn(finishMessage);
	}
	
	public void parseProgram() {
		long start = System.currentTimeMillis();
		while (!isAtEnt()) {			
			parseLines();			
		}
		long finish = System.currentTimeMillis();
		
		this.parserFinishResults(start, finish);
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
