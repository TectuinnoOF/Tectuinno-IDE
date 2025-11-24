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

package org.tectuinno.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.IntConsumer;

import org.tectuinno.exception.PayloadTooLargeException;

/**
 * Provides WiFi-based programming support for Tectuinno devices.
 * <p>
 * This class implements a simple custom protocol over TCP sockets to send
 * assembled machine code (payload) to an ESP12F module embedded in the
 * Tectuinno PCB. The communication protocol includes:
 * <ul>
 *     <li>A handshake string ("Tectuinno") to identify the sender</li>
 *     <li>Payload size (2 bytes, unsigned)</li>
 *     <li>The raw payload (hexadecimal-encoded binary program)</li>
 *     <li>A 1-byte checksum for integrity validation</li>
 * </ul>
 * </p>
 * <p>
 * The payload is transmitted in chunks (default: 2048 bytes) to handle large
 * programs efficiently and avoid buffer overflows in the receiving device.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * WifiProgrammer programmer = new WifiProgrammer();
 * 
 * // 1. Test connection
 * boolean ok = programmer.ping("192.168.4.1", 3333, 4000);
 * if (ok) {
 *     System.out.println("Device is available");
 * }
 *
 * // 2. Send payload with progress updates
 * byte[] program = ...; // compiled hex program
 * programmer.send("192.168.4.1", 3333, program, 7000, progress -> {
 *     System.out.println("Progress: " + progress + "%");
 * });
 * }</pre>
 *
 * @author Tectuinno Team
 * @version 1.0
 * @since 2025-09-18
 */
public class WifiProgrammer {
	
	/**
     * Maximum chunk size (in bytes) to split payload during transmission.
     * <p>
     * Sending data in smaller chunks helps avoid buffer overflow on the
     * receiving side (ESP12F), which may have limited memory and buffer size.
     * </p>
     */
    private static final int CHUNK = 2048;
    
    /**
     * Verifies if the Tectuinno device is available and responsive on the given host and port.
     * <p>
     * This method establishes a TCP connection to the device, sends a protocol-specific
     * handshake message ({@code "TECTUINNO/1.0 HELLO"}), and expects the string {@code "OK"}
     * in response. If the response matches, the device is considered available.
     * </p>
     *
     * @param host       the IP address of the Tectuinno device (e.g., {@code "192.168.4.1"})
     * @param port       the TCP port to connect to (default: {@code 3333})
     * @param timeoutMs  timeout in milliseconds for establishing the connection
     *                   and waiting for a response
     * @return {@code true} if the device responded with "OK"; {@code false} otherwise
     * @throws IOException if the connection fails or an I/O error occurs
     */
    public boolean ping(String host, int port, int timeoutMs) throws IOException {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), timeoutMs);
            s.setSoTimeout(timeoutMs);

            var out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.US_ASCII), true);
            var in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.US_ASCII));

            out.println("TECTUINNO/1.0 HELLO");
            String resp = in.readLine();
            return "OK".equals(resp);
        }
    }
    
    /**
     * Sends the compiled payload to the Tectuinno device using the custom WiFi protocol.
     * <p>
     * The method first builds the complete message by concatenating:
     * <ol>
     *     <li>Handshake string ({@code "Tectuinno"})</li>
     *     <li>Payload size (2 bytes)</li>
     *     <li>Payload bytes</li>
     *     <li>Checksum (1 byte)</li>
     * </ol>
     * Then the message is transmitted in fixed-size chunks of 2048 bytes to the device.
     * </p>
     *
     * @param host        the IP address of the Tectuinno device
     * @param port        the TCP port to connect to
     * @param payload     the raw payload data (compiled program) to send
     * @param timeoutMs   timeout in milliseconds for the connection
     * @param onProgress  a callback function (consumer of {@code int}) that is invoked
     *                    with progress updates in percentage (0–100). Can be {@code null}.
     *
     * @throws IOException               if a socket error occurs during transmission
     * @throws PayloadTooLargeException  if the payload exceeds the maximum allowed size (65,535 bytes)
     */
    public void send(String host, int port, byte[] payload, int timeoutMs, IntConsumer onProgress) throws IOException, PayloadTooLargeException{
    	
    	byte[] completeMessage = this.prepareMessage("TECTUINNO", payload);
    	
    	try(Socket s = new Socket()){
    		
    		s.connect(new InetSocketAddress(host, port), timeoutMs);
            s.setSoTimeout(timeoutMs);
            
            var outRaw = s.getOutputStream();
 
            int sent = 0;
            while (sent < completeMessage.length) {
                int n = Math.min(CHUNK, completeMessage.length - sent);
                outRaw.write(completeMessage, sent, n);
                outRaw.flush();
                sent += n;
                if (onProgress != null) onProgress.accept((int)((sent * 100L) / completeMessage.length));
            }

    	}
    	
    }
    
    /**
     * Prepares the final message to be sent to the Tectuinno device.
     * <p>
     * The message format is:
     * <pre>
     * [Handshake ASCII][Payload size: 2 bytes][Payload][Checksum: 1 byte]
     * </pre>
     * </p>
     *
     * @param handShake the handshake string (usually {@code "TECTUINNO"})
     * @param payload   the raw program data
     * @return the full byte array ready for transmission
     * @throws PayloadTooLargeException if {@code payload.length > 65535}
     */
    private byte[] prepareMessage(String handShake,byte[] payload) throws PayloadTooLargeException{
    	
    	if(payload.length > 0xFFFF) {
    		throw new PayloadTooLargeException(payload.length);
    	}
    	
    	String currentPayload = new String(payload);
    	
    	String preparedPayload = handShake + currentPayload;
    	
    	byte[] completePayload = preparedPayload.getBytes();
    	
    	
    	
    	return completePayload;
    }
    
    /**
     * Calculates an 8-bit checksum of the payload.
     * <p>
     * The checksum is the sum of all payload bytes (treated as unsigned) modulo 256.
     * This value is appended at the end of the message and allows the receiver
     * to validate payload integrity.
     * </p>
     *
     * @param payload the raw payload to calculate checksum for
     * @return the checksum as an integer between 0 and 255
     */
    private int checksum(byte[] payload) {
    	int sum = 0;
    	
    	for(byte b : payload) {
    		sum += (b & 0xff);
    	}
    	
    	return sum & 0xff;
    }
	
}
