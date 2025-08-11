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

import org.tectuinno.view.MainWindow;



public class App {
    public static void main(String[] args) {
        
    	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	
    }
}

/**
 * Some asm example codes
 * */
/*
/*Este es otro ejemplo*/
/*
ini:
	addi x7,x0,1
	addi x4,x0,0x04aa
	lui x2,0x10000

	sw x4,0(x2)  
    	call delay
	addi x4,x0,0x0255
   	sw x4,0(x2)  
    	call delay
	jal x0,ini
    
delay:
	lui x5,1000
tec:    sub x5, x5,x7
    	beq x5,x0,salir
	jal x0,tec
salir:	ret
*/
/*
 
 Otro ejemplo de codigo ensamblador:
 
 
 
 */



 