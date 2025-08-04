package org.tectuinno.compiler.assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;
import org.tectuinno.view.component.ResultConsolePanel;

import static org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary.*;

/**
 * Semantic analyzer for Tectuinno RISC-V assembler.
 * <p>
 * This analyzer performs validations after the syntax analysis stage,
 * including:
 * <ul>
 * <li>Checking for valid instruction argument counts</li>
 * <li>Verifying the existence of label references</li>
 * <li>Ensuring correct types of arguments per instruction</li>
 * <li>Detecting duplicate label declarations</li>
 * </ul>
 * </p>
 * 
 * @author Tectuinno
 * @version 1.0
 * @since 2025-07-12
 */
public class AsmSemanticAnalyzer {

	private final List<Token> tokens;
	private Map<String, Token> declaredLabels;
	private List<String> errors;
	private int position;
	private ResultConsolePanel consolePanel;
	private static int errorCounter;

	public AsmSemanticAnalyzer(List<Token> tokens, ResultConsolePanel consolePanel) {
		this.tokens = tokens;
		this.declaredLabels = new HashMap<String, Token>();
		this.errors = new ArrayList<String>();
		this.consolePanel = consolePanel;
		this.position = 0;
		errorCounter = 0;
	}

	public AsmSemanticAnalyzer() {
		this.declaredLabels = new HashMap<String, Token>();
		this.errors = new ArrayList<String>();
		this.tokens = null;
		this.position = 0;
		errorCounter = 0;
	}

	private String errorArgs(Token instr, int expected, int recived) {
		errorCounter++;
		return new StringBuilder().append(">>ERROR DETECTADO EN: ").append(instr.getValue()).append(" | SE ESPERABA: ")
				.append(expected).append(" ARGS | ").append(recived).append("FUERON RECIBIDOS... <<<<<").toString();
	}

	private String errorArgs(String message) {
		errorCounter++;
		return new StringBuilder().append(">>ERROR DETECTADO: ").append(message).append(" QUE ESTÁS HACIENDO ? <<<<<")
				.toString();
	}

	private boolean isAtEnd() {
		return this.position >= tokens.size();
	}

	private Token peek() {
		return this.tokens.get(position);
	}

	private Token advance() {
		if (!isAtEnd())
			position++;
		return this.previous();
	}

	private Token previous() {
		return this.tokens.get(this.position - 1);
	}

	private boolean check(TokenType... types) {
		
		if (isAtEnd()) return false;
		
		TokenType current = peek().getType();
		
		for(TokenType type : types) if(type == current) return true;
		
		return false;
	}
	
	private boolean check(TokenType type) {
	    if (isAtEnd()) return false;
	    //this.consolePanel.getTerminalPanel().writteIn("\n\t>>>Parametro: " + type.toString() +  " | Comparar: " + peek().getType().toString() + "\n");
	    return peek().getType() == type;
	}

	private boolean match(TokenType type) {
		if (check(type)) {
			advance();
			return true;
		}
		return false;
	}

	private void verifyLabel(Token token) {
		String name = token.getValue();
		if (!declaredLabels.containsKey(name)) {
			errors.add(this.errorArgs(name + "No se reconoce como etiqueta u objeto declarado"));
		}
	}

	private void validateInstruction(Token instr, List<Token> args) {

		String name = instr.getValue();

		switch (name) {
		case ADDI:			
		case SLTI:			
		case ORI:			
		case ANDI:
			if (args.size() != 5) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				errors.add(this.errorArgs(instr, 3, args.size()));
			}
			break;
		case ADD:			
		case SUB:			
		case SLT:
		case OR:
		case AND:
			if (args.size() != 5) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				errors.add(this.errorArgs(instr, 3, args.size()));
			}
			break;
		case LUI:
			if (args.size() != 3) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				errors.add(this.errorArgs(instr, 2, args.size()));
			}
			break;
		case LW:			
		case SW:
			if (args.size() != 6) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				errors.add(this.errorArgs(instr, 2, args.size()));
			}
			break;
		case BEQ:
			if (args.size() != 5 || args.get(4).getType() != TokenType.UNKNOWN) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				errors.add("BEQ espera: rs1, rs2, etiqueta");
			} else {
				verifyLabel(args.get(4));
			}
			break;
		case JAL:
			if (args.size() == 1 && args.get(0).getType() == TokenType.UNKNOWN) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				verifyLabel(args.get(0));
			}
			if (args.size() == 3 && args.get(2).getType() == TokenType.UNKNOWN) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				verifyLabel(args.get(2));
			}
			break;
		case JALR:			
		case CALL:
			if (args.size() != 1 || args.get(0).getType() != TokenType.UNKNOWN) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				errors.add(this.errorArgs("CALL espera 1 etiqueta como argumento"));
			} else {
				verifyLabel(args.get(0));
			}
			break;
		case RET:
			if (!args.isEmpty()) {
				this.consolePanel.getTerminalPanel().writteIn("\nargs "+args.size() + "\n");
				this.errors.add(this.errorArgs("Ret no espera ningún argumento"));
			}
			break;
		default:
			errors.add(this.errorArgs("Instrucción u objeto desconocido...."));
			break;
		}

	}

	private void parseInstruction() {
		Token instr = this.previous();
		List<Token> args = new ArrayList<Token>();
		while (check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.LEFTPAREN)
				|| check(TokenType.UNKNOWN)) {
			args.add(advance());
			if (match(TokenType.LEFTPAREN)) {
				args.add(previous());
				if (check(TokenType.REGISTER))
					args.add(advance());
				if (check(TokenType.RIGHTPAREN))
					args.add(previous());
			}
			match(TokenType.COMMA);
		}

		validateInstruction(instr, args);
	}

	private void parseLine() {

		Token current = peek();

		if (match(TokenType.COMMENT))
			return;

		if (match(TokenType.LABEL)) {
			if (check(TokenType.INSTRUCTION)) {
				this.parseInstruction();
			}
			return;
		}
		if (match(TokenType.INSTRUCTION)) {
			this.parseInstruction();
			return;
		}

		advance();

	}

	private void collectLabels() {
		for (Token token : this.tokens) {
			if (token.getType() == TokenType.LABEL) {
				String name = token.getValue().replace(":", "");
				if (declaredLabels.containsKey(name)) {
					errors.add(this.errorArgs("Etiqueta duplicada...."));
				} else {
					declaredLabels.put(name, token);
				}
			}
		}
	}
	
	private void analizerFinishResult(long start, long finish) {
		
		double time = (double) ((finish - start)/1000);
		
		this.consolePanel.getTerminalPanel().writteIn("\n======================================\n");
		this.consolePanel.getTerminalPanel().writteIn(">> analisis finalizado\n");
		
		
		
		if(this.errors.size() >= 0) {
			this.errors.stream().forEach(er -> {
				consolePanel.getTerminalPanel().writteIn(er + "\n");
			});
		}
		
		String finishMessage = new StringBuilder()
				.append("\n======================================\n")
				.append("Tokens analizados: ").append(this.tokens.size())
				.append("\nErrores semanticos detectados: ").append(errorCounter)
				.append("\nTiempo: ").append(time)
				.append("\n======================================\n")
				.toString();
		
		this.consolePanel.getTerminalPanel().writteIn(finishMessage);
	}

	public void analize() throws Exception {
		
		long start = System.currentTimeMillis();
		
		this.consolePanel.getTerminalPanel().writteIn("\n======================================\n");
		this.consolePanel.getTerminalPanel().writteIn(">> Iniciando depuración");
		
		this.collectLabels();
		this.position = 0;
		errorCounter = 0;

		while (!isAtEnd()) {
			this.parseLine();
		}
		
		long finish = System.currentTimeMillis();
		
		this.analizerFinishResult(start, finish);
		
	}

	public Map<String, Token> getDeclaredLabels() {
		return declaredLabels;
	}

	public void setDeclaredLabels(Map<String, Token> declaredLabels) {
		this.declaredLabels = declaredLabels;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
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

}
