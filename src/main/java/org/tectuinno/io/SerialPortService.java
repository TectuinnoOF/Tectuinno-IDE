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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;

public final class SerialPortService {

    private SerialPortService() {

    }

    public static List<PortInfo> listAvailablePorts() {
        SerialPort[] ports = SerialPort.getCommPorts();
        List<PortInfo> out = new ArrayList<>(ports.length);
        for (SerialPort p : ports) {
            String desc = p.getDescriptivePortName();
            // Ocultar puertos Bluetooth (e.g., "Serie estándar sobre el vínculo Bluetooth")
            if (desc != null && desc.toLowerCase().contains("bluetooth")) {
                continue;
            }
            out.add(new PortInfo(p.getSystemPortName(), desc));
        }
        return out;
    }

    public static void sendBytes(String systemPortName, int baud, byte[] data) throws IOException {
        SerialPort port = SerialPort.getCommPort(systemPortName);
        port.setComPortParameters(baud, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (!port.openPort()) {

            throw new IOException("No se pudo abrir el puerto: " + systemPortName);

        }
        try {
            int wrote = port.writeBytes(data, data.length);
            if (wrote != data.length) {
                throw new IOException("Escritura incompleta: " + wrote + " de " + data.length + " bytes.");
            }
            port.flushIOBuffers();
        } finally {
            port.closePort();
        }
    }

}
