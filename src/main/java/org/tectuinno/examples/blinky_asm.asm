ini:
	addi x7,x0,1
	lui x2 ,  0x10000  /* x1  = 1000 0x3E8 */

    lui x4,0xAAAAA
	addi x4,x4,0x04aa //x4=AAAAA4AA

	sw x4,1(x2)

    call delay

    jal x0,ini
    
delay:
		addi x5,x0,100
tec:    sub x5, x5,x7
    	beq x5,x0,salir
		jal x0,tec
salir:	ret