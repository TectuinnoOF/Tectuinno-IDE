
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