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
import javax.swing.UIManager;

import org.tectuinno.view.StartingWindow;




public class App {
    public static void main(String[] args) {
        
    	try {    		
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	}catch (Exception e) {
			e.printStackTrace(System.err);
		}
    	
    	EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartingWindow frame = new StartingWindow();
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
 
ini:
    lui x7, 0x10000    
    addi x3,x0,1
    sw x3,0(x7)    
    call delay
    call parp
    add x3,x3,x3
    sw x3, 0(x7)
	call delay2
    addi x3,x0,4
    sw x3,0(x7)
    call delay
    jal x0,ini

delay:
    addi x5, x0, 1 
    lui x6, 0x3000

tec:
    sub x6, x6, x5         
    beq x6, x0, salir  
    jal x0,tec

delay2:
    addi x5, x0, 1
    lui x6, 0x2000
tec2:
    sub x6, x6, x5          
    beq x6, x0, salir
    jal x0,tec2

parp:
    addi x8,x0,5
    addi x5,x0,1
parp2:
    addi x9,x0,0
    sw x9,0(x7)
delay3:
    lui x2,1000
salir:

==================================================
==================================================
 
 ini:
    lui x7, 0x10000    
    addi x3,x0,-1
    sw x3,0(x7)    
    call delay
    call parp
    add x3,x3,x3
    sw x3, 0(x7)
	call delay2
    addi x3,x0,4
    sw x3,0(x7)
    call delay
    jal x0,ini

delay:
    addi x5, x0, -1 
    lui x6, 0x3000

tec:
    sub x6, x6, x5         
    beq x6, x0, salir  
    jal x0,tec

delay2:
    addi x5, x0, 1
    lui x6, 0x2000
tec2:
    sub x6, x6, x5          
    beq x6, x0, salir
    jal x0,tec2

parp:
    addi x8,x0,5
    addi x5,x0,1
parp2:
    addi x9,x0,0
    sw x9,0(x7)
delay3:
    lui x2,1000
salir:
 
 */



 