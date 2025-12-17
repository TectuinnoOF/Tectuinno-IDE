/* Semáforo */

ini:
	addi x2,x0,1		/* x2 = 1. Valor usado para restar en los contadores de retardo */
	lui x20,0x10000	/* x20 = 0x10000000. Dirección base de los LEDs */
	addi x5,x0,1		/* x5 = 1 (Luz Verde. Asumiendo que el bit 0 es el LED Verde) */
	sw x5,0(x20)		/* Enciende el LED Verde (x5 = 1) */
	call delay		/* Llama a retardo largo */
	call parp		/* Llama a la subrutina de parpadeo (parpadea la luz actual, x5=1) */
	add x5,x5,x5		/* x5 = x5 + x5. Realiza un desplazamiento a la izquierda: x5 = 2 (Luz Amarilla) */
	sw x5,0(x20)		/* Enciende el LED Amarillo (x5 = 2) */
	call delay2		/* Llama a retardo corto */
	add x5,x5,x5		/* x5 = x5 + x5. Realiza un desplazamiento a la izquierda: x5 = 4 (Luz Roja) */
	sw x5,0(x20)		/* Enciende el LED Rojo (x5 = 4) */
	call delay		/* Llama a retardo largo */
	jal x0,ini		/* Salta incondicionalmente a 'ini' para repetir la secuencia */

delay:	lui x19,0x3000		/* Inicializa el contador de retardo largo (x19). x19 = 0x30000000 */
tec:	sub x19,x19,x2	/* x19 = x19 - 1. Decrementa el contador */
	beq x19,x0,salir	/* Si x19 == 0, salta a 'salir' (fin del delay) */
	jal x0,tec		/* Continúa el bucle de retardo */

delay2:	lui x19,0x1000		/* Inicializa el contador de retardo corto (x19). x19 = 0x10000000 */
tec2:	sub x19,x19,x2	/* x19 = x19 - 1. Decrementa el contador */
	beq x19,x0,salir	/* Si x19 == 0, salta a 'salir' (fin del delay) */
	jal x0,tec2		/* Continúa el bucle de retardo */
	
parp:	addi x6,x0,5		/* x6 = 5. Inicializa el contador de ciclos de parpadeo */
parp2:	sw x0,0(x20)		/* Apaga los LEDs (x0 = 0) */
delay3:	lui x4,1000		/* Inicializa contador de retardo para la fase de apagado del parpadeo (x4) */
tec3:	sub x4,x4,x2		/* Decrementa x4 */
	beq x4,x0,parp3	/* Si x4 == 0, salta a 'parp3' */
	jal x0, tec3		/* Continúa el bucle de retardo */
parp3:	sw x5,0(x20)		/* Enciende el LED con el valor actual (x5) */
delay4:	lui x4,1000		/* Inicializa contador de retardo para la fase de encendido del parpadeo (x4) */
tec4:	sub x4,x4,x2		/* Decrementa x4 */
	beq x4,x0,cont	/* Si x4 == 0, salta a 'cont' */
	jal x0,tec4		/* Continúa el bucle de retardo */

cont:	sub x6,x6,x2		/* x6 = x6 - 1. Decrementa el contador de parpadeos */
	beq x6,x0,salir	/* Si x6 == 0, salta a 'salir' (termina el parpadeo) */
	jal x0,parp2		/* Si x6 > 0, salta a 'parp2' para un nuevo ciclo de parpadeo */

salir:	sw x5,0(x20)		/* Asegura que el LED quede encendido al terminar el parpadeo */
	ret		/* Retorna a la instrucción después del 'call' */