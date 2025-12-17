/* programa básico - cambio de leds con entradas */
lui x10,0x10000		/* Carga el valor alto en x10. x10 = 0x10000000 (Dirección base de los LEDs) */
lui x4,0x40000			/* Carga el valor alto en x4. x4 = 0x40000000 (Dirección base de la entrada)*/
addi x2,x0,0xFF		/* x2 = 0xFF (Valor para encender todos los LEDs) */
addi x3,x0,1			/* x3 = 1 (Valor para encender solo el primer LED) */

main: 	lw x5,0(x4)		/* Carga la palabra de la dirección de entrada (x4) en el registro x5 */
	andi x6,x5,1		/* x6 = x5 AND 1. Aísla el bit menos significativo de la entrada (bit 0) */
			/* Si el bit 0 de x5 es 1, x6 = 1. Si es 0, x6 = 0 */
	beq x6,x0,mostrar0	/* Si x6 == 0 (entrada baja/apagada), salta a 'mostrar0' (apagar/primer LED) */
	jal x0,mostrar1	/* Si x6 != 0 (entrada alta/encendida), salta a 'mostrar1' (todos los LEDs) */

mostrar0:	sw x3,0(x10)		/* Escribe el valor x3 (1) en los LEDs (enciende el primer LED) */
	jal x0,main		/* Salta incondicionalmente a 'main' para leer la entrada de nuevo */

mostrar1:	sw x2,0(x10)		/* Escribe el valor x2 (0xFF) en los LEDs (enciende todos los LEDs) */
	jal x0,main		/* Salta incondicionalmente a 'main' para leer la entrada de nuevo */