ini:
	lui x1, 0x10000
	addi x2, x0, 1
	addi x3, x0, 5
loop:
	sw x2, 0(x1)
	addi x4, x0, 0
high_delay: 
	addi x4, x4, 1
	slti x5, x4, 500
	beq x5, x0, low
	jal x0, high_delay
low: 
	addi x6, x0, 0
	sw x6, 0(x1)
	addi x4, x0, 0
low_delay:
	addi x4, x4, 1
	slti x5, x4, 500
	beq x5, x0, next
	jal x0, low_delay
next: 
	addi x3, x3, -1
	beq x3, x0, fin
	jal x0, loop
fin: 
	jal x0, fin