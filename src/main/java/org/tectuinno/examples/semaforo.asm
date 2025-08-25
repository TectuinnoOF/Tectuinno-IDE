ini:
	addi x2,x0,1
	lui x20,0x10000
	addi x5,x0,1
	sw x5,0(x20)
	call delay
	call parp
	add x5,x5,x5
	sw x5,0(x20)
	call delay2
	add x5,x5,x5
	sw x5,0(x20)
	call delay
	jal x0,ini

delay:	lui x19,0x3000
tec:	sub x19,x19,x2
	beq x19,x0,salir
	jal x0,tec

delay2:	lui x19,0x1000
tec2:	sub x19,x19,x2
	beq x19,x0,salir
	jal x0,tec2
	
parp:	addi x6,x0,5
parp2:	sw x0,0(x20)
delay3:	lui x4,1000
tec3:	sub x4,x4,x2
	beq x4,x0,parp3
	jal x0, tec3
parp3:	sw x5,0(x20)
delay4:	lui x4,1000
tec4:	sub x4,x4,x2
	beq x4,x0,cont
	jal x0,tec4

cont:	sub x6,x6,x2
	beq x6,x0,salir
	jal x0,parp2

salir:	sw x5,0(x20)
	ret