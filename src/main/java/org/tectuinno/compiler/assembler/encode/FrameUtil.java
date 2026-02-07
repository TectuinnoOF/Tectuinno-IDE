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

package org.tectuinno.compiler.assembler.encode;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.tectuinno.compiler.assembler.EncoderIrLine;

public final class FrameUtil {
	
	private FrameUtil() {}
	
	public static byte[] buildLittleEndianFrame(List<EncoderIrLine> encodedLines) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(encodedLines.size() * 4);

        for (EncoderIrLine ln : encodedLines) {
            String hx = ln.hex(); // esperado: "0xXXXXXXXX" o "ERROR"
            if (hx == null || "ERROR".equalsIgnoreCase(hx)) {               
                continue;
            }
            // Más robusto con unsigned:
            int word = (int) Long.parseUnsignedLong(hx.substring(2), 16);
            byte[] le = AsmEncoder.toLittleEndian(word);
            out.write(le, 0, le.length);
        }
        
        byte[] payload = out.toByteArray();
        byte[] header = "TECTUINNO".getBytes(StandardCharsets.US_ASCII);
        
        byte[] frame = new byte[header.length + payload.length];
        
        System.arraycopy(header, 0, frame, 0, header.length);
        System.arraycopy(payload, 0, frame, header.length, payload.length);
        
        return frame;
    }
	
	 public static String toHex(byte[] frame, boolean conEspacios) {
	        StringBuilder sb = new StringBuilder(frame.length * (conEspacios ? 3 : 2));
	        for (int i = 0; i < frame.length; i++) {
	            sb.append(String.format("%02X", frame[i] & 0xFF));
	            if (conEspacios && i + 1 < frame.length) sb.append(' ');
	        }
	        return sb.toString();
	    }
	
}
