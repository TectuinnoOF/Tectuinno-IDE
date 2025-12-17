/* Secuencia */

lui x5,0x10000		/* x5 = 0x10000000. Dirección base de los LEDs */
addi x2,x0,1		/* x2 = 1. Inicializa el contador de la secuencia */
addi x10,x0,31		/* x10 = 31. Define el límite superior de la secuencia */
sw x2,0(x5)		/* Escribe el valor inicial (1) en los LEDs */
call delay		/* Llama a la subrutina de retardo */
loop:
addi x2,x2,1		/* x2 = x2 + 1. Incrementa el contador */
sw x2,0(x5)		/* Escribe el nuevo valor de la secuencia (x2) en los LEDs */
call delay		/* Llama a la subrutina de retardo */
slt x3,x10,x2		/* Compara: x3 = 1 si x10 < x2 (si 31 < contador). Verifica si se alcanzó el límite */
beq x3,x0,loop		/* Si x3 == 0 (x2 <= 31), salta a 'loop' para continuar la secuencia */
jal x0,fin		/* Si x3 != 0 (x2 > 31), salta incondicionalmente a 'fin' */
fin: jal x0,fin		/* Bucle infinito de terminación */

delay: lui x31,1000	/* Inicializa el contador de retardo (x31). x31 = 0x3E800000 */
rest: addi x31,x31,-1	/* Decrementa el contador x31 */
beq x31,x0,salir	/* Si x31 == 0, salta a 'salir' (fin del delay) */
jal x0,rest		/* Continúa el bucle de retardo */
salir: ret		/* Retorna a la instrucción después del 'call' */