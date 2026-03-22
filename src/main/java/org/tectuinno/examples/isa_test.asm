/* ---------------------------
           BASE
        --------------------------- */
        lui x10,0x10000       /* direccion LEDs */
 
        /* ---------------------------
           CARGA DE VALORES
        --------------------------- */
        addi x1,x0,10         /* x1 = 10 */
        addi x2,x0,5          /* x2 = 5 */
 
        /* ---------------------------
           OPERACIONES ARITMETICAS
        --------------------------- */
        add x3,x1,x2          /* x3 = 15 */
        sub x4,x1,x2          /* x4 = 5 */
 
        /* ---------------------------
           LOGICAS
        --------------------------- */
        and x5,x3,x4          /* x5 = 15 & 5 = 5 */
        or  x6,x3,x4          /* x6 = 15 | 5 = 15 */
        xor x7,x3,x4          /* x7 = 15 ^ 5 = 10 */
 
        /* ---------------------------
           SHIFTS
        --------------------------- */
        sll x8,x4,x2          /* 5 << 5 = 160 */
        srl x9,x8,x2          /* 160 >> 5 = 5 */
        sra x11,x8,x2         /* igual comportamiento */
 
        /* ---------------------------
           COMPARACIONES
        --------------------------- */
        slt x12,x2,x1         /* 5 < 10 → 1 */
        slti x13,x1,20        /* 10 < 20 → 1 */
 
        /* ---------------------------
           BRANCH
        --------------------------- */
        beq x12,x13,ok_branch /* ambos = 1 → salta */
 
        addi x14,x0,0         /* no entra aquí */
 
ok_branch:
        blt x2,x1,ok_blt      /* 5 < 10 → salta */
 
        addi x14,x0,0
 
ok_blt:
 
        /* ---------------------------
           RESULTADO FINAL
        --------------------------- */
        /* construimos valor final */
        add x15,x5,x7         /* 5 + 10 = 15 */
        add x15,x15,x12       /* 15 + 1 = 16 */
        add x15,x15,x13       /* 16 + 1 = 17 */
 
        /* limitar a 6 bits */
        andi x15,x15,0x3F     /* 17 = 010001 */
 
        /* LEDs activos en 0 → invertir */
        xori x15,x15,0x03F     /* invertir bits */
 
        /* ---------------------------
           SALIDA A LEDs
        --------------------------- */
        sw x15,0(x10)
 
fin:
        jal x0,fin