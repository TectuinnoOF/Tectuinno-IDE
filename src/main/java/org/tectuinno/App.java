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

package org.tectuinno;

import java.awt.EventQueue;
import org.tectuinno.config.FlatlafManager;
import org.tectuinno.io.LoggerInfoManager;
import org.tectuinno.view.StartingWindow;

public class App {

	public static void main(String[] args) {
		
		LoggerInfoManager.writteInInfoLogTxt("Inicializando Tectuinno IDE");
		
		FlatlafManager.enableOnlySoftwareRendering();
		FlatlafManager.setUpFlatlafLookAndFell();
		FlatlafManager.setupBorderRadiusArcs();
		FlatlafManager.paintComponents();
		FlatlafManager.colorizeWarningDialogs();
		FlatlafManager.colorizeJFileChooser();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoggerInfoManager.writteInInfoLogTxt("Inicializando interfaz");
					StartingWindow frame = new StartingWindow();
					frame.setVisible(true);
					LoggerInfoManager.writteInInfoLogTxt("Interzar iniciada");
				} catch (Exception e) {
					e.printStackTrace();
					LoggerInfoManager.writteInErrorLogTxtr(e.getMessage(), e.fillInStackTrace());
				}
			}
		});
	}

}

/**
 * prueba con directivas
.section .data
array: .word 'h','o','l','a'

.text
_main:
lui x2,0x10000
addi x3,x0,0x12f
add x4,x2,x3
 */

/*
main:
lui x3,0x10000
add x4,x7,x2

sub x9,x7,x2

.data
array: addi x5,x4,0x19f
*/


