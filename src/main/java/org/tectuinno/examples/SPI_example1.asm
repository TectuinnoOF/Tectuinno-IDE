ini: addi x2,x0,1
    addi x10, x0,0 /*contador*/
     
    lui x3,0xAAAAA   /*dato parte alta*/
    addi x3,x3,0x2AA /*dato parte baja*/
    lui x4,0x20000   /*dir SPI*/
    sw x3,1(x4)      /*envia x3 al 20000001*/

    lui x5,0x10000
    sw x10,0(x5)  /*envia contador al port_out*/

l1: lw x6,0(x4)
    andi x6,x6,1
    beq x6,x2,l1
    call delay

    sw x3,1(x4)      /*envia x3 al 20000001*/
    addi x10,x10,1
    sw x10,0(x5)  /*envia contador al port_out*/

l2: lw x6,0(x4)
    andi x6,x6,1
    beq x6,x2,l2
    call delay

    sw x3,1(x4)      /*envia x3 al 20000001*/
    addi x10,x10,1
    sw x10,0(x5)  /*envia contador al port_out*/

l3: lw x6,0(x4)
    andi x6,x6,1
    beq x6,x2,l3
    call delay

    sw x3,1(x4)      /*envia x3 al 20000001*/
    addi x10,x10,1
    sw x10,0(x5)  /*envia contador al port_out*/
l4: lw x6,0(x4)
    andi x6,x6,1
    beq x6,x2,l4
    call delay

    jal x0,ini

delay:
    addi x31,x0,50
t1: sub x31,x31,x2
    beq x31,x0,salir
    jal x0,t1
salir: ret