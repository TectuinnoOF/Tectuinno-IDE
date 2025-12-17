lui x10,0x10000	/* Carga el valor alto (0x10000) en x10. x10 = 0x10000000 (Dirección base de los LEDs) */
		/* y rellena los 12 bits restantes con 0's*/
addi x6,x0,34		/* x6 = 34. Define el límite superior de la secuencia */
addi x2, x0, 1		/* x2 = 1. Primer término de la secuencia (F(n-1)) */
addi x3,x0,0		/* x3 = 0. Segundo término de la secuencia (F(n-2)) */
addi x4,x0,0		/* x4 = 0. Variable para almacenar el nuevo término (F(n)) */
loop:
	sw x4,0(x10)		/* Escribe el término actual de Fibonacci (x4) en los LEDs (dirección x10) */
	call delay		/* Llama a la subrutina de retardo (delay) */
	add x4,x2,x3		/* Calcula el siguiente término: x4 = x2 + x3 (F(n) = F(n-1) + F(n-2)) */
	add x3,x0,x2		/* Actualiza el término F(n-2): x3 = x2 (El antiguo F(n-1) se convierte en F(n-2)) */
	add x2,x0,x4		/* Actualiza el término F(n-1): x2 = x4 (El nuevo F(n) se convierte en F(n-1)) */
	slt x7,x6,x4		/* Compara: x7 = 1 si x6 < x4 (si límite < nuevo término). Esto verifica si x4 > 34 */
	beq x7,x0,loop	/* Si x7 == 0 (es decir, x4 <= 34), salta a 'loop' para continuar la secuencia */
	jal x0,fin		/* Si x4 > 34, salta incondicionalmente a 'fin' */
fin: 	
	jal x0,fin		/* Bucle infinito de terminación */

delay:
	lui x31, 1000		/* Carga 1000 en los bits altos de x31 (x31 se inicializa a 0x3E800000, un valor grande para el delay) */
	addi x11, x0, 3	/* x11 = 3. Inicializa el contador para 3 parpadeos (parpadeo ocurre 3 veces) */
rest:
	addi x31, x31, -1	/* Decrementa el contador de retardo x31 */
	beq x31,x0,parp	/* Si x31 == 0, salta a 'parp' para iniciar el parpadeo */
	jal x0,rest		/* Salta a 'rest' para continuar el bucle de retardo */

salir: 	ret		/* Retorna de la subrutina (en este caso, regresa al 'loop' principal después del parpadeo) */

parp:
	sw x0,0(x10)		/* Escribe 0 en los LEDs (los apaga) */
delay2:
	lui x30,1000		/* Inicializa un contador de retardo (x30) */
rest2:
	addi x30,x30,-1	/* Decrementa el contador x30 */
	beq x30,x0,parp2	/* Si x30 == 0, salta a 'parp2' */
	jal x0, rest2		/* Continúa el bucle de retardo */
parp2:
	sw x4,0(x10)		/* Escribe el término de Fibonacci actual (x4) en los LEDs (los enciende de nuevo) */
delay3:
	lui x30,1000		/* Inicializa otro contador de retardo (x30) */
rest3:
	addi x30, x30, -1	/* Decrementa el contador x30 */
	beq x30,x0,contador	/* Si x30 == 0, salta a 'contador' */
	jal x0,rest3		/* Continúa el bucle de retardo */
contador:
	addi x11, x11, -1	/* Decrementa el contador de parpadeos x11 */
	beq x11,x0,salir	/* Si x11 == 0, salta a 'salir' (termina el parpadeo y retorna) */
	jal x0,parp		/* Si x11 > 0, salta a 'parp' para un nuevo ciclo de parpadeo */