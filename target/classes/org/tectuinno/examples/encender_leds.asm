/* programa básico - encender leds */
inicio:
	lui x10,0x10000	/* Carga el valor alto (0x10000) en x10. x10 = 0x10000000 (Dirección base de los LEDs/Salida) */
	addi x2,x0,0xAA	/* x2 = 0xAA. Carga el patrón de bits (1010 1010) en x2 */
	sw x2,0(x10)		/* Escribe el valor de x2 (0xAA) en la dirección de los LEDs (x10). Enciende los LEDs con el patrón */
	jal x0,inicio		/* Salta incondicionalmente a 'inicio'. Crea un bucle infinito para mantener el patrón de encendido */