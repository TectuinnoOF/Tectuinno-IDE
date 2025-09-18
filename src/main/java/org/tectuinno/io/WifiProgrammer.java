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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.IntConsumer;

public class WifiProgrammer {

	private static final int DEFAULT_TIMEOUT_MS = 5000;
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
    
    public void send(String host, int port, byte[] payload, int timeoutMs, IntConsumer onProgress) throws IOException{
    	
    	try(Socket s = new Socket()){
    		
    		s.connect(new InetSocketAddress(host, port), timeoutMs);
            s.setSoTimeout(timeoutMs);
            
            var outTxt = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.US_ASCII));
            var inTxt  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.US_ASCII));
            var outRaw = s.getOutputStream();
    		
            outTxt.write("TECTUINNO/1.0 HELLO\n"); outTxt.flush();
            if (!"OK".equals(inTxt.readLine())) throw new IOException("HELLO failed");
            
            outTxt.write("SEND " + payload.length + "\n"); outTxt.flush();
            if (!"READY".equals(inTxt.readLine())) throw new IOException("Device not READY");
            
            int sent = 0;
            while (sent < payload.length) {
                int n = Math.min(CHUNK, payload.length - sent);
                outRaw.write(payload, sent, n);
                outRaw.flush();
                sent += n;
                if (onProgress != null) onProgress.accept((int)((sent * 100L) / payload.length));
            }

            outTxt.write("\nEND\n"); outTxt.flush();
            String resp = inTxt.readLine();
            if (!"OK".equals(resp)) throw new IOException("Device error: " + resp);
            
    	}
    	
    }
	
}
