/* programa básico - apagar leds */
inicio:
	lui x10,0x10000	/* Carga el valor alto (0x10000) en x10. x10 = 0x10000000 (Dirección base de los LEDs/Salida) */
	addi x2,x0,0		/* x2 = 0. Carga el valor cero en x2 (valor para apagar todos los LEDs) */
	sw x2,0(x10)		/* Escribe el valor de x2 (0) en la dirección de los LEDs (x10). Apaga los LEDs */
	jal x0,inicio		/* Salta incondicionalmente a 'inicio'. Crea un bucle infinito para mantener el estado */