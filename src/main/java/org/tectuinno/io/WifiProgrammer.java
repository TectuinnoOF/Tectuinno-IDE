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

public class WifiProgrammer {
	
    private static final int CHUNK = 2048;
    
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
    
    public void send(String host, int port, byte[] payload, int timeoutMs, IntConsumer onProgress) throws IOException, PayloadTooLargeException{
    	
    	byte[] completeMessage = this.prepareMessage("Tectuinno", payload, this.checksum(payload));
    	
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
    
    private byte[] prepareMessage(String handShake,byte[] payload, int checksum) throws PayloadTooLargeException{
    	
    	if(payload.length > 0xFFFF) {
    		throw new PayloadTooLargeException(payload.length);
    	}
    	
    	//this pointer is needed to alocate the data in the final message array
    	int position = 0;
    	
    	//Convert the first message in bytearray
    	byte[] byteHandShake = handShake.getBytes();
    	
    	//converting the size to a byte data
    	int size = payload.length;
    	byte sizeHigh = (byte) ((size >> 8) & 0xff);
    	byte sizeLow = (byte) (size & 0xff);    	    	
    	
    	//the new completed payload included the handshake message and the code
    	byte[] completePayload = new byte[byteHandShake.length + 2 + payload.length + 1];
    	
    	//then, we concat the data in just one array
    	System.arraycopy(byteHandShake, 0, completePayload, position, byteHandShake.length);
    	position += byteHandShake.length;
    	completePayload[position++] = sizeHigh;
    	completePayload[position++] = sizeLow;    	    	
    	System.arraycopy(payload, 0, completePayload, position, payload.length);
    	position += payload.length;
    	completePayload[position] = (byte) checksum;
    	    	
    	
    	return completePayload;
    }
    
    private int checksum(byte[] payload) {
    	int sum = 0;
    	
    	for(byte b : payload) {
    		sum += (b & 0xff);
    	}
    	
    	return sum & 0xff;
    }
	
}
