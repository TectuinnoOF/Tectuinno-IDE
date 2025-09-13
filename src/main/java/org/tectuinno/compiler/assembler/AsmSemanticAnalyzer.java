package org.tectuinno.compiler.assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
				.append(expected).append(" ARGS | ").append(recived).append("FUERON RECIBIDOS... EN: ").append(instr)
                .append(" <<<<<").toString();
	}

	private String errorArgs(Token instr, String message) {
		errorCounter++;
		return new StringBuilder().append(">>ERROR DETECTADO: ").append(message).append(" EN ").
                append(instr).append(" <<<<<").toString();
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

	private boolean check(TokenType type) {
		if (isAtEnd())
			return false;
		// this.consolePanel.getTerminalPanel().writteIn("\n\t>>>Parametro: " +
		// type.toString() + " | Comparar: " + peek().getType().toString() + "\n");
		return peek().getType() == type;
	}

	private boolean match(TokenType type) {
		if (check(type)) {
			advance();
			return true;
		}
		return false;
	}	

	/**
	 * Validates instruction semantic using logical arguments (no commas/parens).
	 * Accepts declared labels as LABEL tokens
	 * 
	 * @param instr
	 * @param args
	 */
	private void validateInstruction(Token instr, List<Token> args) {

		final String name = instr.getValue();

		final int n = args.size();
		final Predicate<Integer> isReg = i -> args.get(i).getType() == TokenType.REGISTER;
		final Predicate<Integer> isImm = i -> args.get(i).getType() == TokenType.IMMEDIATE;
		final Predicate<Integer> isLbl = i -> args.get(i).getType() == TokenType.LABEL;

		switch (name) {
		case ADDI:
		case SLTI:
		case ORI:
		case ANDI:
			if (n != 3 || !isReg.test(0) || !isReg.test(1) || !isImm.test(2)) {
				errors.add(this.errorArgs(instr, 3, n));
			}
			break;
		case ADD:
		case SUB:
		case SLT:
		case OR:
		case AND:
			if (n != 3 || !isReg.test(0) || !isReg.test(1) || !isReg.test(2)) {
				errors.add(this.errorArgs(instr, 3, n));
			}
			break;
		case LUI:
			if (n != 2 || !isReg.test(0) || !isImm.test(1)) {
				errors.add(this.errorArgs(instr, 2, n));
			}
			break;
		case LW:
		case SW:
			if (n != 3 || !isReg.test(0) || !isImm.test(1) || !isReg.test(2)) {
				errors.add(this.errorArgs(instr, 3, n));
			}
			break;
		case BEQ:
			if (n != 3 || !isReg.test(0) || !isReg.test(1) || !isLbl.test(2)) {
				errors.add(this.errorArgs(instr,"BEQ espera: rs1, rs2, etiqueta"));
			}
			break;
		case JAL:
			if ((n == 1 && isLbl.test(0)) || (n == 2 && isReg.test(0) && isLbl.test(1))) {

			} else {
				errors.add(errorArgs(instr, 1, n));
			}
			break;
		case JALR:
			if (n != 3 || !isReg.test(0) || !isReg.test(1) || !isImm.test(2)) {
                errors.add(errorArgs(instr, 3, n));
            }
            break;
		case CALL:
			if (!(n == 1 && isLbl.test(0))) {
                errors.add(errorArgs(instr,"CALL espera 1 etiqueta como argumento"));
            }
            break;
		case RET:
			if (n != 0) {
                errors.add(errorArgs(instr,"Ret no espera ningún argumento"));
            }
            break;
		default:
			errors.add(this.errorArgs(instr,name + " | Instrucción u objeto desconocido...."));
			break;
		}

	}
	
	
	private void parseInstruction() {

		Token instr = this.previous();
		List<Token> args = this.colletArgumentTokens();		
		List<Token> logicArgs = this.toLogicalArgs(args);
		validateInstruction(instr, logicArgs);
	}
	
	private List<Token> colletArgumentTokens(){
		
		List<Token> args = new ArrayList<Token>();
		while (check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.LEFTPAREN)
				|| check(TokenType.UNKNOWN)) {
			
			if (check(TokenType.REGISTER) || check(TokenType.IMMEDIATE) || check(TokenType.UNKNOWN)) {
	            args.add(advance());
			}else if(match(TokenType.LEFTPAREN)) {
				args.add(previous());				
				if(check(TokenType.REGISTER)) args.add(advance());
				if(match(TokenType.RIGHTPAREN)) args.add(previous());
				
			}
			
			match(TokenType.COMMA);
			
		}
		
		return args;
	}
	
	private List<Token> toLogicalArgs(List<Token> raw){
		List<Token> out = new ArrayList<Token>();
		for(Token t: raw) {
			if (t.getType() == TokenType.COMMA || t.getType() == TokenType.LEFTPAREN
					|| t.getType() == TokenType.RIGHTPAREN) {
				continue;
			}
			out.add(normalizeLabelToken(t));
		}
		return out;
	}
	
	private Token normalizeLabelToken(Token t) {
		if (t.getType() == TokenType.UNKNOWN && declaredLabels.containsKey(t.getValue())) {
			return new Token(TokenType.LABEL, t.getValue(), t.getPosition(), t.getLine(), t.getColumn());
		}
		return t;
	}

	private void parseLine() {

		Token current = peek();

		if (match(TokenType.COMMENT))
			return;

		if (match(TokenType.LABEL)) {
			if (match(TokenType.INSTRUCTION)) {
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
					errors.add(this.errorArgs(token,"Etiqueta duplicada...."));
				} else {
					declaredLabels.put(name, token);
				}
			}
		}
	}

	private void analizerFinishResult(long start, long finish) {

		double time = (double) ((finish - start) / 1000);

		this.consolePanel.getTerminalPanel().writteIn("\n======================================\n");
		this.consolePanel.getTerminalPanel().writteIn(">> analisis finalizado\n");

		if (this.errors.size() >= 0) {
			this.errors.stream().forEach(er -> {
				consolePanel.getTerminalPanel().writteIn(er + "\n");
			});
		}

		String finishMessage = new StringBuilder().append("\n======================================\n")
				.append("Tokens analizados: ").append(this.tokens.size()).append("\nErrores semanticos detectados: ")
				.append(errorCounter).append("\nTiempo: ").append(time)
				.append("\n======================================\n").toString();

		this.consolePanel.getTerminalPanel().writteIn(finishMessage);
	}

	public boolean analize() throws Exception {

		long start = System.currentTimeMillis();

		this.consolePanel.getTerminalPanel().writteIn("\n======================================\n");
		this.consolePanel.getTerminalPanel().writteIn(">> Analizando Semantica");

		this.collectLabels();
		this.position = 0;
		errorCounter = 0;

		while (!isAtEnd()) {
			this.parseLine();
		}

		long finish = System.currentTimeMillis();

		this.analizerFinishResult(start, finish);
		
		return errorCounter <= 0;

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
