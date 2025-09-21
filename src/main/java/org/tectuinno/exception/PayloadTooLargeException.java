package org.tectuinno.exception;


public class PayloadTooLargeException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public PayloadTooLargeException(int size) {
		super("el tamaño del mensaje: " + size + "Supera el limite de 2 bytes");
	}
	
	public PayloadTooLargeException(String message) {
		super(message);
	}
	
	public PayloadTooLargeException() {}
}
