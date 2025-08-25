ini:
	addi x7,x0,1

	addi x4,x0,0x04aa
	lui x2, 0x10000  /*envia a los leds */
	sw x4,0(x2)  /*envia a leds*/
    call delay
	addi x4,x0,0x0255
   	sw x4,0(x2)  /*envia a leds*/
    call delay

	jal x0,ini
    
delay:
		lui x5,1000
tec:    sub x5, x5,x7
    	beq x5,x0,salir
		jal x0,tec
salir:	ret