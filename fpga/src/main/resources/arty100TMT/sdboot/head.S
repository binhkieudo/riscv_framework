// See LICENSE.Sifive for license details.
#include "platform.h"

  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  li s2, 0x8  
  csrw mie, s2
  li s1, 0
  csrr s2, mhartid
  beq s1, s2, _boot
  j _exit
_boot:
  li sp, (CORE0_PRIVATE_MEM + 0xFF0)
  csrr a0, mhartid
  call main
_exit:
  csrr a0, mhartid
  la a1, dtb // dtb address for next level bootloader
  li s1, MEMORY_MEM_ADDR
  jr s1
  wfi
_loopback:
  j _exit

  .section .dtb
  .align 3
dtb:
