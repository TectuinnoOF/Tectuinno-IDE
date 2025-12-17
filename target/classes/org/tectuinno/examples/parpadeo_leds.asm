/* programa básico - parpadeo de leds */
lui x10, 0x10000	/* x10 = 0x10000000. Dirección base de los LEDs */
addi x2,x0,0xAA		/* x2 = 0xAA (Patrón 1: 1010 1010) */
addi x3,x0,0x55		/* x3 = 0x55 (Patrón 2: 0101 0101) */

ciclo:	sw x2,0(x10)	/* Escribe el Patrón 1 (0xAA) en los LEDs */
	jal x1,delay	/* Llama a la subrutina 'delay', guardando la dirección de retorno en x1 (ra) */
	sw x3,0(x10)	/* Escribe el Patrón 2 (0x55) en los LEDs */
	jal x1,delay	/* Llama a la subrutina 'delay', guardando la dirección de retorno en x1 (ra) */
	jal x0,ciclo	/* Salta incondicionalmente a 'ciclo' para repetir el parpadeo */
	

delay:	lui x31,100	/* Inicializa el contador de retardo (x31). x31 = 0x64000 */
resta:	addi x31,x31,-1	/* Decrementa el contador x31 */
	beq x31,x0,salir	/* Si x31 == 0, salta a 'salir' (fin del delay) */
	jal x0,resta	/* Continúa el bucle de retardo */

salir:	jalr x0, x1,0	/* Retorna: Salta a la dirección almacenada en x1 (ra), con desplazamiento 0 */